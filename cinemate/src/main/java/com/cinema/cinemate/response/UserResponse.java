package com.cinema.cinemate.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

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
    private String image;
    private String phoneNumber;
    private Integer score;
    private String status;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
