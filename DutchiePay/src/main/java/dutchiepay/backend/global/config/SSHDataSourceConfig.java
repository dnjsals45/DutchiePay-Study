package dutchiepay.backend.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Profile("local")
@Configuration
@RequiredArgsConstructor
public class SSHDataSourceConfig {
    private final SshTunnelingInitializer initializer;

    @Value("${DB_URL}")
    private String url;

    @Value("${DB_USER}")
    private String username;

    @Value("${DB_PASSWORD}")
    private String password;

    @Value("${DB_DRIVER}")
    private String driverClassName;

    @Bean(name="dataSource")
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        initializer.buildSshConnection();

        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }
}
