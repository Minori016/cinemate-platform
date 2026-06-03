package com.cinema.cinemate.controller.auth;

import com.cinema.cinemate.entity.User;
import com.cinema.cinemate.request.IntrospectRequest;
import com.cinema.cinemate.request.UserLoginRequest;
import com.cinema.cinemate.request.UserRegisterRequest;
import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.response.AuthenticationResponse;
import com.cinema.cinemate.response.IntrospectResponse;
import com.cinema.cinemate.response.UserResponse;
import com.cinema.cinemate.service.AuthenticationService;
import com.cinema.cinemate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
        private final UserService userService;
        private final AuthenticationService authenticationService;

        @PostMapping("/register")
        ApiResponse<UserResponse> register(@RequestBody @Valid UserRegisterRequest request) {
                return ApiResponse.<UserResponse>builder()
                                .result(userService.createUser(request))
                                .build();
        }

        @PostMapping("/login")
        ApiResponse<AuthenticationResponse> login(@RequestBody UserLoginRequest request) {
                // Authenticate user
                User user = userService.authenticate(request);

                // Generate JWT token
                String token = authenticationService.generateToken(user);

                // Return token response
                return ApiResponse.<AuthenticationResponse>builder()
                                .result(AuthenticationResponse.builder()
                                                .token(token)
                                                .authenticated(true)
                                                .build())
                                .build();
        }

        @PostMapping("/introspect")
        ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
                IntrospectResponse result = authenticationService.introspect(request);
                return ApiResponse.<IntrospectResponse>builder()
                                .result(result)
                                .build();
        }
}
