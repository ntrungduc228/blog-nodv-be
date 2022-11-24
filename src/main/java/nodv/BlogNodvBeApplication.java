package nodv;

import nodv.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication()
@EnableConfigurationProperties(AppProperties.class)
public class BlogNodvBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogNodvBeApplication.class, args);
    }
}

