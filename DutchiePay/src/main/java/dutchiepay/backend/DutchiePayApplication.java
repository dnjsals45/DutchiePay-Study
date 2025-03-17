package dutchiepay.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DutchiePayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DutchiePayApplication.class, args);
    }

}
