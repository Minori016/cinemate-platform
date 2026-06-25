package com.cinema.cinemate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity ánh xạ bảng "users" trong database.
 *
 * Lưu trữ toàn bộ thông tin người dùng bao gồm:
 * - Thông tin tài khoản (email, password, username)
 * - Thông tin cá nhân (fullName, dayOfBirth, gender, identityCard, phoneNumber, address)
 * - Trạng thái tài khoản (status: ACTIVE / LOCKED)
 * - Quan hệ với bảng roles thông qua bảng trung gian user_roles
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    // --- Thông tin tài khoản ---

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", unique = true)
    private String username;

    // --- Thông tin cá nhân ---

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "day_of_birth")
    private LocalDate dayOfBirth;

    @Column(name = "gender")
    private String gender;

    /** Số CMND / CCCD */
    @Column(name = "identity_card", length = 20)
    private String identityCard;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    /** Địa chỉ */
    @Column(name = "address")
    private String address;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    // --- Trạng thái & điểm ---

    /** Điểm tích lũy của thành viên */
    @Column(name = "score")
    @Builder.Default
    private Integer score = 0;

    /**
     * Trạng thái tài khoản: ACTIVE hoặc LOCKED.
     * Nếu LOCKED, user sẽ không thể đăng nhập (AC-03 Login).
     */
    @Column(name = "status")
    private String status;

    // --- Quan hệ với Role ---

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Staff staff;

    // --- Thời gian ---

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
