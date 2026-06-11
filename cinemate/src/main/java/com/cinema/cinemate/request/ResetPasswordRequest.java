package com.cinema.cinemate.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO nhận token và password mới để reset mật khẩu.
 */
@Data
public class ResetPasswordRequest {

    @NotBlank(message = "INVALID_KEY")
    private String token;

    @NotBlank(message = "INVALID_PASSWORD")
    @Size(min = 8, message = "INVALID_PASSWORD")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "WEAK_PASSWORD"
    )
    private String newPassword;

    @NotBlank(message = "INVALID_KEY")
    private String confirmPassword;
}
