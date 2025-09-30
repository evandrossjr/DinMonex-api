package com.essjr.DinMonex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DinMonexApplication {

	public static void main(String[] args) {
		SpringApplication.run(DinMonexApplication.class, args);
	}

}
