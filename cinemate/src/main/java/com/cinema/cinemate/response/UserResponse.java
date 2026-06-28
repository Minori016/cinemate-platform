package com.cinema.cinemate.response;

import lombok.*;
import com.cinema.cinemate.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO trả về thông tin user cho client.
 * Bao gồm tất cả thông tin cá nhân + roles + trạng thái tài khoản.
 * Lưu ý: KHÔNG bao giờ trả về password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID uuid;
    private String email;
    private String fullName;
    private String username;
    private LocalDate dayOfBirth;
    private String gender;
    private String identityCard;
    private String phoneNumber;
    private String address;
    private String image;
    private Integer score;
    private UserStatus status;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private java.math.BigDecimal salary;
    private java.util.UUID cinemaId;
    private String cinemaName;
    private Boolean isFirstLogin;
}
