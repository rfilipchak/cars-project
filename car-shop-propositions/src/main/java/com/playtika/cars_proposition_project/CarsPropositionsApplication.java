package com.playtika.cars_proposition_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CarsPropositionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarsPropositionsApplication.class, args);
	}

}
