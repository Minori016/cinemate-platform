package com.cinema.cinemate.controller.user;

import com.cinema.cinemate.request.ChangePasswordRequest;
import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.response.UserResponse;
import com.cinema.cinemate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // ============ ENDPOINTS FOR CURRENT USER (ALL ROLES) ============

    @GetMapping("/myinfo")
    public ApiResponse<UserResponse> getMyInfo(@AuthenticationPrincipal Jwt jwt) {
        String userIdStr = jwt.getClaim("userId");
        UUID userId = UUID.fromString(userIdStr);
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @PostMapping("/myinfo/change-password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal Jwt jwt,
                                            @RequestBody @Valid ChangePasswordRequest request) {
        String email = jwt.getSubject();
        userService.changePassword(email, request);
        return ApiResponse.<Void>builder()
                .message("Password has been changed successfully.")
                .build();
    }

    // ============ ADMIN ONLY ENDPOINTS ============

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable UUID userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ApiResponse<UserResponse> getUserByEmail(@PathVariable String email) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByEmail(email))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted successfully")
                .build();
    }
}
