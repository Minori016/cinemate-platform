package com.cinema.cinemate.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO nhận email để gửi yêu cầu quên mật khẩu.
 */
@Data
public class ForgotPasswordRequest {

    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;
}
