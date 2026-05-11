package com.salon.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan("com.salon.manager.infrastructure.persistence.entity")
public class SalonManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalonManagerApplication.class, args);
    }

}
