package com.feniksovich.bankcards;

import com.feniksovich.bankcards.config.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(SecurityProperties.class)
@SpringBootApplication
public class BankCardsApplication {

	static void main(String[] args) {
		SpringApplication.run(BankCardsApplication.class, args);
	}

}
