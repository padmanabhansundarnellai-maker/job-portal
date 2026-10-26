package com.portfolio.jobportal.config;

import com.portfolio.jobportal.model.Role;
import com.portfolio.jobportal.model.User;
import com.portfolio.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (!users.existsByEmail("admin@jobportal.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@jobportal.com");
            admin.setPassword(encoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            users.save(admin);
        }
    }
}
