package com.cinema.cinemate.response;

import lombok.*;

/**
 * DTO trả về sau khi gửi yêu cầu quên mật khẩu.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordResponse {
    private String message;
    private boolean sent;
}
