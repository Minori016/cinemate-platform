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

/**
 * Controller xử lý Authentication: Đăng ký, Đăng nhập, Kiểm tra token.
 *
 * Tất cả endpoint trong controller này đều public (không cần JWT),
 * được cấu hình tại SecurityConfig (/auth/**).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    /**
     * API Đăng ký tài khoản mới (User Story 1).
     *
     * POST /auth/register
     *
     * Luồng xử lý:
     * 1. Validate tất cả field bắt buộc (AC-02) — thực hiện tự động bởi @Valid
     * 2. Kiểm tra password == confirmPassword (AC-03) — xử lý trong UserService
     * 3. Hash password và lưu user (AC-06) — xử lý trong UserService
     * 4. Log sự kiện đăng ký (AC-05) — xử lý trong UserService
     * 5. Trả về thông tin user đã tạo → Frontend redirect đến trang login (AC-04)
     *
     * @param request DTO chứa thông tin đăng ký (đã validate qua @Valid)
     * @return ApiResponse chứa UserResponse
     */
    @PostMapping("/register")
    ApiResponse<UserResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    /**
     * API Đăng nhập (User Story 2).
     *
     * POST /auth/login
     *
     * Luồng xử lý:
     * 1. Validate email + password trong database (AC-02)
     * 2. Kiểm tra tài khoản có bị khóa không (AC-03)
     * 3. Tạo JWT token chứa roles — Frontend dùng để redirect theo role (AC-04):
     *    - ADMIN  → Admin Dashboard
     *    - STAFF  → Staff Interface
     *    - MEMBER → Member Homepage
     *
     * @param request DTO chứa email và password
     * @return ApiResponse chứa JWT token + trạng thái authenticated
     */
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody UserLoginRequest request) {
        // Xác thực user (kiểm tra credentials + trạng thái tài khoản)
        User user = userService.authenticate(request);

        // Tạo JWT token chứa userId, email, roles
        String token = authenticationService.generateToken(user);

        // Trả về token cho client
        return ApiResponse.<AuthenticationResponse>builder()
                .result(AuthenticationResponse.builder()
                        .token(token)
                        .authenticated(true)
                        .build())
                .build();
    }

    /**
     * API kiểm tra token còn hợp lệ không.
     *
     * POST /auth/introspect
     *
     * Dùng để frontend kiểm tra session hiện tại có còn valid không
     * trước khi thực hiện các thao tác cần xác thực.
     *
     * @param request DTO chứa token cần kiểm tra
     * @return ApiResponse chứa kết quả valid (true/false)
     */
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        IntrospectResponse result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
