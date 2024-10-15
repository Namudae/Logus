package com.logus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class) //security auto 비활성화
@EnableJpaAuditing
public class LogusApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogusApplication.class, args);
	}

}
