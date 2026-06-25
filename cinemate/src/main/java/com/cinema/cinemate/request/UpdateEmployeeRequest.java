package com.cinema.cinemate.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * DTO nhận dữ liệu cập nhật thông tin nhân viên từ Admin/Manager.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequest {

    /** URL hình ảnh nhân viên (tùy chọn) */
    private String image;

    /** Mật khẩu mới (Tùy chọn) */
    private String password;

    /** Xác nhận mật khẩu mới (Tùy chọn) */
    private String confirmPassword;

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

    /** Vai trò của nhân viên: STAFF hoặc MANAGER */
    @NotBlank(message = "ROLE_REQUIRED")
    private String role;

    /** Trạng thái tài khoản: ACTIVE hoặc LOCKED */
    @NotBlank(message = "STATUS_REQUIRED")
    private String status;
}
