package com.example.springbooteksapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot EKS API.
 */
@SpringBootApplication
public class SpringBootEksApiApplication {

    protected SpringBootEksApiApplication() {
    }

    public static void main(final String[] args) {
        SpringApplication.run(SpringBootEksApiApplication.class, args);
    }
}
