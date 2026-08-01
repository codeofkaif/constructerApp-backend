package com.AdilProject.constructerApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class ConstructerAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConstructerAppApplication.class, args);
	}

}

