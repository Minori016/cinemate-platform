package com.cinema.cinemate.controller.user;

import com.cinema.cinemate.request.ChangePasswordRequest;
import com.cinema.cinemate.request.ProfileUpdateRequest;
import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.response.UserResponse;
import com.cinema.cinemate.response.ScoreHistoryResponse;
import com.cinema.cinemate.service.UserService;
import com.cinema.cinemate.service.ScoreHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
        private final UserService userService;
        private final ScoreHistoryService scoreHistoryService;

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

        @PutMapping("/myinfo")
        public ApiResponse<UserResponse> updateMyProfile(@AuthenticationPrincipal Jwt jwt,
                        @RequestBody @Valid ProfileUpdateRequest request) {
                String userIdStr = jwt.getClaim("userId");
                UUID userId = UUID.fromString(userIdStr);
                List<String> rolesList = jwt.getClaim("roles");
                java.util.Set<String> roles = rolesList != null ? new HashSet<>(rolesList)
                                : java.util.Collections.emptySet();

                UserResponse updatedUser = userService.updateProfile(userId, request, userId, roles);
                return ApiResponse.<UserResponse>builder()
                                .message("Update information successfully")
                                .result(updatedUser)
                                .build();
        }

        @PostMapping(value = "/myinfo/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ApiResponse<UserResponse> uploadAvatar(@AuthenticationPrincipal Jwt jwt,
                        @RequestParam("file") MultipartFile file) {
                String userIdStr = jwt.getClaim("userId");
                UUID userId = UUID.fromString(userIdStr);
                List<String> rolesList = jwt.getClaim("roles");
                java.util.Set<String> roles = rolesList != null ? new HashSet<>(rolesList)
                                : java.util.Collections.emptySet();

                UserResponse updatedUser = userService.uploadAvatar(userId, file, userId, roles);
                return ApiResponse.<UserResponse>builder()
                                .message("Avatar uploaded successfully")
                                .result(updatedUser)
                                .build();
        }

        @GetMapping("/myinfo/score-history")
        public ApiResponse<List<ScoreHistoryResponse>> getMyScoreHistory(
                        @AuthenticationPrincipal Jwt jwt,
                        @RequestParam("fromDate") String fromDateStr,
                        @RequestParam("toDate") String toDateStr,
                        @RequestParam("type") String type) {
                String userIdStr = jwt.getClaim("userId");
                UUID userId = UUID.fromString(userIdStr);
                List<ScoreHistoryResponse> result = scoreHistoryService.getMyScoreHistory(userId, fromDateStr, toDateStr, type);
                return ApiResponse.<List<ScoreHistoryResponse>>builder()
                                .result(result)
                                .build();
        }

}
