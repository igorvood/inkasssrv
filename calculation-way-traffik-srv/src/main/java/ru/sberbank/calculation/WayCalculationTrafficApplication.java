package ru.sberbank.calculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WayCalculationTrafficApplication {

    public static void main(String[] args) {
        SpringApplication.run(WayCalculationTrafficApplication.class, args);
    }
}
