package com.macys.mirakl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = { "com.macys" })
public class MiraklOrchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiraklOrchApplication.class, args);
	}
}
