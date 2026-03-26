package com.github.curriculeon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		// Allow tests to opt-out of starting the full Spring context by setting
		// the system property "skip.spring.boot" or environment variable "SKIP_SPRING".
		if ("true".equalsIgnoreCase(System.getProperty("skip.spring.boot")) ||
			"true".equalsIgnoreCase(System.getenv("SKIP_SPRING"))) {
			return;
		}

		SpringApplication.run(DemoApplication.class, args);
	}
}