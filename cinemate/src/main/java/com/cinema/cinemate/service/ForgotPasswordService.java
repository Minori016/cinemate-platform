package com.cinema.cinemate.service;

import com.cinema.cinemate.entity.PasswordResetToken;
import com.cinema.cinemate.entity.User;
import com.cinema.cinemate.repository.PasswordResetTokenRepository;
import com.cinema.cinemate.repository.UserRepository;
import com.cinema.cinemate.request.ForgotPasswordRequest;
import com.cinema.cinemate.request.ResetPasswordRequest;
import com.cinema.cinemate.response.ForgotPasswordResponse;
import com.cinema.cinemate.enums.ErrorCode;
import com.cinema.cinemate.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service xử lý nghiệp vụ quên mật khẩu.
 * Tách riêng để giữ UserService gọn gàng.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationService authenticationService;

    /**
     * Gửi yêu cầu quên mật khẩu.
     *
     * Luồng xử lý:
     * 1. Tìm user theo email
     * 2. Xóa token cũ nếu có
     * 3. Tạo JWT reset token
     * 4. Lưu token vào DB
     * 5. Gửi email cho user
     *
     * Lưu ý bảo mật: Luôn trả thành công dù email có tồn tại hay không,
     * để tránh lộ thông tin user trong hệ thống.
     */
    @Transactional
    public ForgotPasswordResponse requestReset(ForgotPasswordRequest request) {
        String email = request.getEmail().trim();

        userRepository.findByEmail(email).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUserUuid(user.getUuid());

            String resetToken = authenticationService.generatePasswordResetToken(user);

            PasswordResetToken prt = PasswordResetToken.builder()
                    .token(resetToken)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusMinutes(15))
                    .used(false)
                    .build();
            passwordResetTokenRepository.save(prt);

            emailService.sendPasswordResetEmail(email, resetToken);

            log.info("PASSWORD_RESET_REQUEST | email={} | timestamp={}",
                    email, LocalDateTime.now());
        });

        return ForgotPasswordResponse.builder()
                .message("If the email exists, a reset link has been sent.")
                .sent(true)
                .build();
    }

    /**
     * Reset mật khẩu bằng token.
     *
     * Luồng xử lý:
     * 1. Tìm token trong DB
     * 2. Kiểm tra token đã được sử dụng chưa
     * 3. Kiểm tra token đã hết hạn chưa
     * 4. Kiểm tra password khớp với confirm password
     * 5. Cập nhật password mới cho user
     * 6. Đánh dấu token đã sử dụng
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new AppException(ErrorCode.RESET_TOKEN_INVALID));

        if (resetToken.getUsed()) {
            throw new AppException(ErrorCode.RESET_TOKEN_USED);
        }

        if (resetToken.isExpired()) {
            throw new AppException(ErrorCode.RESET_TOKEN_EXPIRED);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("PASSWORD_RESET_COMPLETED | email={} | timestamp={}",
                user.getEmail(), LocalDateTime.now());
    }
}
