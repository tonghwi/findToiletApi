package com.toilet.public_toilet_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PublicToiletApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublicToiletApiApplication.class, args);
	}

}
