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
}
