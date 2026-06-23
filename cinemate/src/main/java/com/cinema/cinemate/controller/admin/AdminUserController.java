package com.cinema.cinemate.controller.admin;

import com.cinema.cinemate.request.ProfileUpdateRequest;
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

/**
 * Controller xử lý nghiệp vụ User management cho Admin.
 *
 * Chỉ user có role ADMIN mới được truy cập các endpoint trong controller này.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

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
    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@AuthenticationPrincipal Jwt jwt,
                    @PathVariable UUID userId,
                    @RequestBody @Valid ProfileUpdateRequest request) {
        String adminIdStr = jwt.getClaim("userId");
        UUID adminId = UUID.fromString(adminIdStr);
        List<String> rolesList = jwt.getClaim("roles");
        java.util.Set<String> roles = rolesList != null ? new java.util.HashSet<>(rolesList)
                        : java.util.Collections.emptySet();

        UserResponse updatedUser = userService.updateProfile(userId, request, adminId, roles);
        return ApiResponse.<UserResponse>builder()
                .message("Update information successfully")
                .result(updatedUser)
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
