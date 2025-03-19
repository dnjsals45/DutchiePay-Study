package dutchiepay.backend.global.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
@Profile("local")
public class SshTunnelingInitializer {
    @Value("${ssh.host}")
    private String host;
    @Value("${ssh.user}")
    private String user;
    @Value("${ssh.port}")
    private int sshPort;
    @Value("${ssh.password}")
    private String password;
    @Value("${ssh.database-host}")
    private String databaseHost;
    @Value("${ssh.database-port}")
    private int databasePort;

    private Session session;

    @PreDestroy
    public void closeSSH() {
        if (session != null && session.isConnected())
            session.disconnect();
    }

    public void buildSshConnection() {
        Integer forwardedPort = null;
        try {
            log.debug("{}@{}:{}:{} with privateKey",user, host, sshPort, databasePort);
            log.info("start ssh tunneling..");
            JSch jSch = new JSch();
            log.info("creating ssh session");
            session = jSch.getSession(user, host, sshPort);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();  // ssh 연결
            log.info("success connecting ssh connection ");

            log.info("start forwarding");
            session.setPortForwardingL(3307, databaseHost, databasePort);
            log.info("successfully connected to database");
        } catch (JSchException e){
            this.closeSSH();
            e.printStackTrace();
            log.error("fail to make ssh tunneling : {}", e.getMessage());
        }
    }
}
