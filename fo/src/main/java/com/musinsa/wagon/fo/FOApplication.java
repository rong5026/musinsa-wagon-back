package com.musinsa.wagon.fo;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.musinsa.wagon")
@EnableJpaRepositories(basePackages = "com.musinsa.wagon.core.repository")
@EntityScan(basePackages = "com.musinsa.wagon.core")
public class FOApplication {

    public static void main(String[] args) {
        SpringApplication.run(FOApplication.class, args);
    }

    @PostConstruct
    void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
