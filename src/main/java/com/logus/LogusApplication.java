package com.logus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class LogusApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogusApplication.class, args);
	}

}