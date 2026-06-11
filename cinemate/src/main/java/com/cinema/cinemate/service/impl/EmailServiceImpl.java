package com.cinema.cinemate.service.impl;

import com.cinema.cinemate.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation gửi email thực tế qua SMTP.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Cinemate - Password Reset Request");
        message.setText(buildEmailBody(resetToken));

        mailSender.send(message);
        log.info("Password reset email sent to: {}", toEmail);
    }

    private String buildEmailBody(String resetToken) {
        return """
                Hello,

                You requested a password reset for your Cinemate account.

                Use the token below to reset your password:
                %s

                This token will expire in 15 minutes.

                If you did not request this, please ignore this email.
                """.formatted(resetToken);
    }
}
