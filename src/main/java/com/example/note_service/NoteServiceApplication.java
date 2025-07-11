package com.example.note_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.note_service.repository")
public class NoteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoteServiceApplication.class, args);
	}

}
