package com.cinema.cinemate.configuration;

import com.cinema.cinemate.entity.Role;
import com.cinema.cinemate.entity.User;
import com.cinema.cinemate.entity.UserRole;
import com.cinema.cinemate.entity.ScoreHistory;
import com.cinema.cinemate.repository.RoleRepository;
import com.cinema.cinemate.repository.UserRepository;
import com.cinema.cinemate.repository.ScoreHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
    private final ScoreHistoryRepository scoreHistoryRepository;

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
                    .status("ACTIVE")
                    .score(150)
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
                    .status("ACTIVE")
                    .score(150)
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

        // 4. Seed Score History for admin and manager
        userRepository.findByEmail("admin@cinemate.com").ifPresent(this::seedScoreHistoryForUser);
        userRepository.findByEmail("manager@cinemate.com").ifPresent(this::seedScoreHistoryForUser);
    }

    private void seedScoreHistoryForUser(User user) {
        if (!scoreHistoryRepository.existsByUser(user)) {
            log.info("Seeding score history for user: {}", user.getEmail());
            scoreHistoryRepository.save(ScoreHistory.builder()
                    .user(user)
                    .type("EARN")
                    .amount(22)
                    .movieName("Spider-man: Brand New Day")
                    .date(LocalDateTime.of(2026, 6, 15, 12, 30, 0))
                    .build());
            scoreHistoryRepository.save(ScoreHistory.builder()
                    .user(user)
                    .type("SPEND")
                    .amount(50)
                    .movieName("Sweet Combo (Bắp nước)")
                    .date(LocalDateTime.of(2026, 6, 14, 9, 15, 0))
                    .build());
            scoreHistoryRepository.save(ScoreHistory.builder()
                    .user(user)
                    .type("EARN")
                    .amount(18)
                    .movieName("Lớp Học Ám Sát: Giờ Của Chúng Ta")
                    .date(LocalDateTime.of(2026, 6, 13, 10, 15, 0))
                    .build());
            scoreHistoryRepository.save(ScoreHistory.builder()
                    .user(user)
                    .type("EARN")
                    .amount(13)
                    .movieName("Kumanthong Ác Quỷ Dẫn Đường")
                    .date(LocalDateTime.of(2026, 6, 8, 14, 0, 0))
                    .build());
            scoreHistoryRepository.save(ScoreHistory.builder()
                    .user(user)
                    .type("SPEND")
                    .amount(100)
                    .movieName("Vé 2D Doraemon: Bản Tình Ca Đất Nước")
                    .date(LocalDateTime.of(2026, 6, 5, 16, 45, 0))
                    .build());
            scoreHistoryRepository.save(ScoreHistory.builder()
                    .user(user)
                    .type("EARN")
                    .amount(25)
                    .movieName("Lật Mặt 7: Một Điều Ước")
                    .date(LocalDateTime.of(2026, 5, 29, 11, 0, 0))
                    .build());

            // Update user score to 150 to match
            user.setScore(150);
            userRepository.save(user);
        }
    }
}

