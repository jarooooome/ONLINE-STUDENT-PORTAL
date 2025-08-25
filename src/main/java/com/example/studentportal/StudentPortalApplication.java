package com.example.studentportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class StudentPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentPortalApplication.class, args);
	}

	@Bean
	public CommandLineRunner init() {
		return args -> {
			try {
				// Create uploads directory if it doesn't exist
				Files.createDirectories(Paths.get("./uploads"));
				System.out.println("Uploads directory created successfully");
			} catch (IOException e) {
				System.err.println("Failed to create uploads directory: " + e.getMessage());
			}
		};
	}
}