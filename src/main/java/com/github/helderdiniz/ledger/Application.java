package com.github.helderdiniz.ledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.github.helderdiniz"})
public class Application {

    static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
