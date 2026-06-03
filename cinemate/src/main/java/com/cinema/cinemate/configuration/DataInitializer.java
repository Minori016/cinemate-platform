package com.cinema.cinemate.configuration;

import com.cinema.cinemate.entity.Role;
import com.cinema.cinemate.entity.User;
import com.cinema.cinemate.entity.UserRole;
import com.cinema.cinemate.repository.RoleRepository;
import com.cinema.cinemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing default roles and admin user...");

        // 1. Initialize Roles
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name("ADMIN")
                                .description("Administrator Role")
                                .build()
                ));

        roleRepository.findByName("STAFF")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name("STAFF")
                                .description("Staff Role")
                                .build()
                ));

        roleRepository.findByName("MEMBER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name("MEMBER")
                                .description("Default Member Role")
                                .build()
                ));

        // 2. Initialize Admin User if none exists
        if (!userRepository.existsByEmail("admin@cinemate.com")) {
            User admin = User.builder()
                    .email("admin@cinemate.com")
                    .password(passwordEncoder.encode("Admin@123456"))
                    .fullName("System Administrator")
                    .username("admin")
                    .status("ACTIVE")
                    .score(0)
                    .build();

            UserRole userRole = UserRole.builder()
                    .user(admin)
                    .role(adminRole)
                    .build();

            admin.getUserRoles().add(userRole);
            userRepository.save(admin);
            log.info("Default Admin account created: admin@cinemate.com / Admin@123456");
        } else {
            log.info("Admin account already exists.");
        }
    }
}
