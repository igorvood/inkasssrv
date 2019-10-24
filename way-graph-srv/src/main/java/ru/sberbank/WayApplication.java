package ru.sberbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WayApplication {
    public static void main(String[] args) {
        SpringApplication.run(WayApplication.class, args);
    }

}
