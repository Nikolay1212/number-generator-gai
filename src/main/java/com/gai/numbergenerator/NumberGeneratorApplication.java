package com.gai.numbergenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class NumberGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NumberGeneratorApplication.class, args);
    }

}
