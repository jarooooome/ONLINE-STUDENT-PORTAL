package com.example.studentportal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.studentportal.model.User;
import com.example.studentportal.repository.UserRepository;

@Configuration
public class AdminSetupConfig {

    @Bean
    CommandLineRunner addDefaultAdmin(UserRepository userRepository) {
        return args -> {
            // Check if admin already exists
            if (userRepository.findByEmail("monkey.jarome@email.com").isEmpty()) {
                User admin = new User();
                admin.setId(1L); // optional if using auto-generated IDs
                admin.setActive(true);
                admin.setContactNumber("09953234284");
                admin.setEmail("monkey.jarome@email.com");
                admin.setFirstName("Jarome");
                admin.setLastName("Urriza");
                admin.setPassword("$2a$12$9jLwkediy7lY.akSwVx6zefi22rdtDs2f9egj1OthMJWq8GAm3Jyy"); // already hashed
                admin.setRole("ADMIN");

                userRepository.save(admin);
                System.out.println("✅ Default admin created: monkey.jarome@email.com");
            }
        };
    }
}
