package com.cinema.cinemate.service;

/**
 * Service gửi email.
 */
public interface EmailService {

    /**
     * Gửi email chứa link/token reset password.
     *
     * @param toEmail   email người nhận
     * @param resetToken token reset (JWT)
     */
    void sendPasswordResetEmail(String toEmail, String resetToken);

    /**
     * Gửi email thông báo tạo tài khoản thành công cho nhân viên.
     *
     * @param toEmail   email người nhận
     * @param username  tên đăng nhập
     * @param tempPassword mật khẩu tạm thời
     */
    void sendEmployeeAccountCreationEmail(String toEmail, String username, String tempPassword);
}
