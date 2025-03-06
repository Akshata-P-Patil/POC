package com.saft.pack_generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PackGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PackGeneratorApplication.class, args);
	}

}
