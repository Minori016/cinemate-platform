package com.cinema.cinemate.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
    private String token;
    private boolean authenticated;
    private Boolean isFirstLogin;
}
