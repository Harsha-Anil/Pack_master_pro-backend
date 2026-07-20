package com.packagemaster.service.service;

import com.packagemaster.service.model.AdminUser;
import com.packagemaster.service.repository.AdminUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DbSeeder implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;

    public DbSeeder(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (adminUserRepository.count() == 0) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            AdminUser admin = AdminUser.builder()
                    .username("admin")
                    .passwordHash(encoder.encode("admin@123"))
                    .createdAt(Instant.now())
                    .build();
            adminUserRepository.save(admin);
            System.out.println("Default admin user seeded.");
        }
    }
}
