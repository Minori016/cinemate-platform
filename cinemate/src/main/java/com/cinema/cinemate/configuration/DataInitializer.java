package com.cinema.cinemate.configuration;

import com.cinema.cinemate.entity.Role;
import com.cinema.cinemate.entity.User;
import com.cinema.cinemate.entity.UserRole;
import com.cinema.cinemate.repository.CinemaRepository;
import com.cinema.cinemate.repository.RoleRepository;
import com.cinema.cinemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.cinema.cinemate.enums.UserStatus;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
    private final CinemaRepository cinemaRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing database extensions...");
        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS unaccent;");
            log.info("Successfully enabled 'unaccent' extension.");
        } catch (Exception e) {
            log.warn("Could not create 'unaccent' extension. Please ensure it is created manually in PostgreSQL. Error: {}", e.getMessage());
        }

        log.info("Initializing default roles, admin, and manager users...");

        // 1. Initialize Roles
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name("ADMIN")
                                .description("Administrator Role")
                                .build()
                ));

        Role managerRole = roleRepository.findByName("MANAGER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name("MANAGER")
                                .description("Manager Role")
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
                    .status(UserStatus.ACTIVE)
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

        // 3. Initialize Manager User if none exists
        if (!userRepository.existsByEmail("manager@cinemate.com")) {
            User manager = User.builder()
                    .email("manager@cinemate.com")
                    .password(passwordEncoder.encode("Manager@123456"))
                    .fullName("Cinema Manager")
                    .username("manager")
                    .status(UserStatus.ACTIVE)
                    .score(0)
                    .build();

            UserRole userRole = UserRole.builder()
                    .user(manager)
                    .role(managerRole)
                    .build();

            manager.getUserRoles().add(userRole);
            userRepository.save(manager);
            log.info("Default Manager account created: manager@cinemate.com / Manager@123456");
        } else {
            log.info("Manager account already exists.");
        }

        // 4. Initialize Cinemas if they don't exist
        log.info("Initializing default cinemas...");
        seedCinema("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11", "Cinemate HQ", "123 Main St, Quận 1, TP. Hồ Chí Minh", "1900 1234");
        seedCinema("b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22", "Cinemate Q7", "458 Nguyễn Thị Thập, Phường Tân Quy, Quận 7, TP. Hồ Chí Minh", "1900 1238");
        seedCinema("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33", "Cinemate Bình Tân", "1 Đường Số 17A, Phường Bình Trị Đông B, Quận Bình Tân, TP. Hồ Chí Minh", "1900 1239");
        seedCinema("d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44", "Cinemate Quận 9", "50 Lê Văn Việt, Phường Hiệp Phú, Quận 9, TP. Hồ Chí Minh", "1900 1240");
        seedCinema("e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55", "Cinemate Bình Thạnh", "156 Xô Viết Nghệ Tĩnh, Phường 26, Quận Bình Thạnh, TP. Hồ Chí Minh", "1900 1241");
        seedCinema("f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66", "Cinemate Cần Thơ", "1 Hòa Bình, Phường Tân An, Quận Ninh Kiều, TP. Cần Thơ", "1900 1242");
    }

    private void seedCinema(String id, String name, String address, String hotline) {
        if (!cinemaRepository.existsByName(name)) {
            jdbcTemplate.update(
                "INSERT INTO cinemas (id, name, address, hotline, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())",
                java.util.UUID.fromString(id), name, address, hotline
            );
            log.info("Seeded cinema using JdbcTemplate: {}", name);
        }
    }
}

