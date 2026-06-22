package com.cinema.cinemate.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO nhận dữ liệu đăng ký từ client.
 *
 * Bao gồm tất cả các field bắt buộc theo User Story:
 * Account(username), Password, Confirm Password, Full Name,
 * Date of Birth, Sex(gender), Email, Phone Number.
 *
 * Tất cả field đều được validate — nếu rỗng sẽ trả lỗi (AC-02).
 */
@Data
public class UserRegisterRequest {

    // --- Thông tin tài khoản ---

    /** Tên tài khoản (account) */
    @NotBlank(message = "USERNAME_REQUIRED")
    private String username;

    /** Email đăng nhập — phải đúng định dạng email */
    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "INVALID_EMAIL")
    private String email;

    /**
     * Mật khẩu — tối thiểu 8 ký tự, phải chứa chữ hoa, chữ thường, số và ký tự đặc
     * biệt
     */
    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, message = "INVALID_PASSWORD")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "WEAK_PASSWORD")
    private String password;

    /** Xác nhận mật khẩu — phải trùng khớp với password (AC-03) */
    @NotBlank(message = "CONFIRM_PASSWORD_REQUIRED")
    private String confirmPassword;

    // --- Thông tin cá nhân ---

    /** Họ và tên đầy đủ */
    @NotBlank(message = "FULLNAME_REQUIRED")
    private String fullName;

    /** Ngày sinh */
    @NotNull(message = "BIRTHDAY_REQUIRED")
    private LocalDate dayOfBirth;

    /** Giới tính (Male / Female / Other) */
    @NotBlank(message = "GENDER_REQUIRED")
    private String gender;

    /** Số điện thoại */
    @NotBlank(message = "PHONE_REQUIRED")
    private String phoneNumber;
}
