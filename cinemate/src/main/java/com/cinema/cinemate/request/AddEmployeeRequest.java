package com.cinema.cinemate.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import java.time.LocalDate;

/**
 * DTO nhận dữ liệu tạo nhân viên từ Admin/Manager.
 *
 * Bao gồm tất cả field theo AC-01:
 * - Image (upload file)
 * - Account (username)
 * - Password & Confirm Password
 * - Date of Birth
 * - Sex (gender)
 * - Employee Name (fullName)
 * - Identity Card
 * - Email
 * - Phone Number
 * - Address
 *
 * Validate tất cả field bắt buộc theo AC-02.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddEmployeeRequest {

    // --- Hình ảnh ---

    /** File hình ảnh nhân viên */
    private byte[] image;

    // --- Thông tin tài khoản ---

    /** Tên tài khoản (Account) - phải unique (AC-02) */
    @NotBlank(message = "USERNAME_REQUIRED")
    @Size(min = 3, max = 28, message = "Account name must be between 3 and 28 characters")
    private String username;

    /** Mật khẩu */
    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, max = 28, message = "Password must be between 8 and 28 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "WEAK_PASSWORD")
    private String password;

    /** Xác nhận mật khẩu - phải khớp với Password (AC-02) */
    @NotBlank(message = "CONFIRM_PASSWORD_REQUIRED")
    private String confirmPassword;

    // --- Thông tin cá nhân ---

    /** Ngày sinh */
    @NotNull(message = "BIRTHDAY_REQUIRED")
    private LocalDate dayOfBirth;

    /** Giới tính (Male / Female / Other) */
    @NotBlank(message = "GENDER_REQUIRED")
    private String gender;

    /** Họ và tên nhân viên */
    @NotBlank(message = "FULLNAME_REQUIRED")
    @Size(max = 28, message = "Employee name must not exceed 28 characters")
    private String fullName;

    /** Số CMND / CCCD */
    @NotBlank(message = "IDENTITY_CARD_REQUIRED")
    @Size(max = 28, message = "Identity card must not exceed 28 characters")
    private String identityCard;

    /** Email */
    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "INVALID_EMAIL")
    private String email;

    /** Số điện thoại */
    @NotBlank(message = "PHONE_REQUIRED")
    @Size(max = 28, message = "Phone number must not exceed 28 characters")
    private String phoneNumber;

    /** Địa chỉ */
    @NotBlank(message = "ADDRESS_REQUIRED")
    @Size(max = 28, message = "Address must not exceed 28 characters")
    private String address;

    // --- Phân quyền ---

    /**
     * Vai trò của nhân viên: STAFF hoặc MANAGER.
     * AC-06: Chỉ Admin/Manager mới được tạo employee.
     */
    @NotBlank(message = "ROLE_REQUIRED")
    private String role;
}
