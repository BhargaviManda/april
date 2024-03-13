package com.mounika.bha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class BhaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BhaApplication.class, args);
	}

}
