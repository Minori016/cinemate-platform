package com.cinema.cinemate.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "OTP_REQUIRED")
    private String otp;

    @NotBlank(message = "TOKEN_REQUIRED")
    private String otpToken;

    @NotBlank(message = "PASSWORD_REQUIRED")
    private String currentPassword;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 6, message = "PASSWORD_LENGTH_INVALID")
    private String newPassword;

    @NotBlank(message = "PASSWORD_REQUIRED")
    private String confirmPassword;
}
