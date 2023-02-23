package com.digitalmoneyhouse.iamservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class IamServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IamServiceApplication.class, args);
	}

}
