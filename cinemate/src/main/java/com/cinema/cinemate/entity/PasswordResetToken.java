package com.cinema.cinemate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity lưu trữ token reset password.
 * Mỗi token chỉ sử dụng được 1 lần và có thời hạn 15 phút.
 */
@Entity
@Table(name = "password_reset_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    /** Token dạng JWT */
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    /** Thời điểm hết hạn */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** Đã được sử dụng chưa */
    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** User sở hữu token này */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }
}
