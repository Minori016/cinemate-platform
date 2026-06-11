package com.cinema.cinemate.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO nhận dữ liệu cập nhật thông tin cá nhân từ client.
 * Bao gồm các field bắt buộc: Account (username), Full Name, Password, Date of Birth, Sex (gender), Email, Identity Card, Phone Number, Address.
 * Các field được validated bằng Jakarta Validation (AC-02).
 */
@Data
public class ProfileUpdateRequest {

    /** Tên tài khoản (account) */
    @NotBlank(message = "INVALID_KEY")
    private String username;

    /** Email đăng nhập */
    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    /** Mật khẩu mới/hiện tại — tối thiểu 8 ký tự, phải chứa chữ hoa, chữ thường, số và ký tự đặc biệt */
    @NotBlank(message = "INVALID_PASSWORD")
    @Size(min = 8, message = "INVALID_PASSWORD")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "WEAK_PASSWORD"
    )
    private String password;

    /** Họ và tên đầy đủ */
    @NotBlank(message = "INVALID_KEY")
    private String fullName;

    /** Ngày sinh */
    @NotNull(message = "INVALID_KEY")
    private LocalDate dayOfBirth;

    /** Giới tính (Sex) */
    @NotBlank(message = "INVALID_KEY")
    private String gender;

    /** Số CMND / CCCD */
    @NotBlank(message = "INVALID_KEY")
    private String identityCard;

    /** Số điện thoại */
    @NotBlank(message = "INVALID_KEY")
    private String phoneNumber;

    /** Địa chỉ */
    @NotBlank(message = "INVALID_KEY")
    private String address;
}
