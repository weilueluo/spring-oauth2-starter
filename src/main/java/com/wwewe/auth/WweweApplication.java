package com.wwewe.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WweweApplication {

	public static void main(String[] args) {
		SpringApplication.run(WweweApplication.class, args);
	}

}
