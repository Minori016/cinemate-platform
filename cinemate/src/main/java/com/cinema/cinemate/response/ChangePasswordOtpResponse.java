package com.cinema.cinemate.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordOtpResponse {
    private String otpToken;
    private String message;
}
