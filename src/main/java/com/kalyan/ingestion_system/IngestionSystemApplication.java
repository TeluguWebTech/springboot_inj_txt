package com.kalyan.ingestion_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IngestionSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngestionSystemApplication.class, args);
	}

}
