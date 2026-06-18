package com.cinema.cinemate.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Tập trung quản lý tất cả mã lỗi của hệ thống.
 * Mỗi ErrorCode gồm: code (số), message (hiển thị cho client), HTTP status.
 *
 * Quy ước đánh số:
 *   - 9999       : Lỗi chưa phân loại
 *   - 1001-1009  : Lỗi liên quan đến user/auth
 *   - 1011-1019  : Lỗi validation input
 */
@Getter
public enum ErrorCode {
    // === Lỗi hệ thống ===
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),

    // === Lỗi nghiệp vụ User ===
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(1022, "Username already exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),

    // === Lỗi Authentication & Authorization ===
    UNAUTHENTICATED(1006, "Email/password is invalid. Please try again!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1008, "Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1009, "Token has expired", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED(1010, "Account has been locked!", HttpStatus.FORBIDDEN),
    TOKEN_CREATION_FAILED(1013, "Failed to create authentication token", HttpStatus.INTERNAL_SERVER_ERROR),

    // === Lỗi Validation Input ===
    INVALID_EMAIL(1011, "Invalid email address", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    WEAK_PASSWORD(1012, "Password must contain uppercase, lowercase, digit and special character", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(1014, "Password and Confirm Password do not match", HttpStatus.BAD_REQUEST),

    // === Lỗi Forgot Password ===
    RESET_TOKEN_INVALID(1015, "Reset token is invalid", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_EXPIRED(1016, "Reset token has expired", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_USED(1017, "Reset token has already been used", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1018, "Email address not found in the system", HttpStatus.NOT_FOUND),

    // === Lỗi Change Password ===
    INVALID_OTP(1019, "OTP is invalid or has expired", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1020, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_CURRENT(1021, "New password cannot be the same as the current password", HttpStatus.BAD_REQUEST),

    // === Lỗi Movies ===
    MOVIE_NOT_FOUND(2001, "Movie not found", HttpStatus.NOT_FOUND),
    MOVIE_ALREADY_EXISTS(2002, "Movie with this title already exists", HttpStatus.BAD_REQUEST),
    INVALID_DATE_RANGE(2003, "From date must be before or equal to to date", HttpStatus.BAD_REQUEST),
    GENRE_NOT_FOUND(2004, "One or more genres not found", HttpStatus.NOT_FOUND),
    COUNTRY_NOT_FOUND(2005, "One or more countries not found", HttpStatus.NOT_FOUND),
    CINEMA_ROOM_NOT_FOUND(2006, "Cinema room not found", HttpStatus.NOT_FOUND),
    SHOWTIME_CONFLICT(2007, "Showtime conflicts with existing schedule in this room", HttpStatus.CONFLICT),
    POSTER_UPLOAD_FAILED(2008, "Failed to upload poster image", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}
