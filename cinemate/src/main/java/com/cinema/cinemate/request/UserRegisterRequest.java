package com.cinema.cinemate.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO nhận dữ liệu đăng ký từ client.
 *
 * Bao gồm tất cả các field bắt buộc theo User Story:
 * Account(username), Password, Confirm Password, Full Name,
 * Date of Birth, Sex(gender), Email, Identity Card, Phone Number, Address.
 *
 * Tất cả field đều được validate — nếu rỗng sẽ trả lỗi (AC-02).
 */
@Data
public class UserRegisterRequest {

    // --- Thông tin tài khoản ---

    /** Tên tài khoản (account) */
    @NotBlank(message = "INVALID_KEY")
    private String username;

    /** Email đăng nhập — phải đúng định dạng email */
    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    /** Mật khẩu — tối thiểu 8 ký tự, phải chứa chữ hoa, chữ thường, số và ký tự đặc biệt */
    @NotBlank(message = "INVALID_PASSWORD")
    @Size(min = 8, message = "INVALID_PASSWORD")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "WEAK_PASSWORD"
    )
    private String password;

    /** Xác nhận mật khẩu — phải trùng khớp với password (AC-03) */
    @NotBlank(message = "INVALID_KEY")
    private String confirmPassword;

    // --- Thông tin cá nhân ---

    /** Họ và tên đầy đủ */
    @NotBlank(message = "INVALID_KEY")
    private String fullName;

    /** Ngày sinh */
    @NotNull(message = "INVALID_KEY")
    private LocalDate dayOfBirth;

    /** Giới tính (Male / Female / Other) */
    @NotBlank(message = "INVALID_KEY")
    private String gender;

    /** Số điện thoại */
    @NotBlank(message = "INVALID_KEY")
    private String phoneNumber;
}
