package com.example.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Bank Transaction System.
 *
 * @SpringBootApplication enables:
 *   - @Configuration       → marks this as a config class
 *   - @EnableAutoConfiguration → auto-configures Spring based on dependencies
 *   - @ComponentScan       → scans all @Component, @Service, @Repository, @Controller
 */
@SpringBootApplication
public class BankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }
}
