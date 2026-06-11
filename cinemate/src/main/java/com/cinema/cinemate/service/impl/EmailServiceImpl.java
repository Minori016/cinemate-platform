package com.cinema.cinemate.service.impl;

import com.cinema.cinemate.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Implementation gửi email thực tế qua SMTP bằng giao diện HTML.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject("Cinemate - Đặt lại mật khẩu");
            
            String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;
            
            String htmlBody = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #000000; color: #ffffff; padding: 0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
                    .header { background-color: #E50914; padding: 40px 20px; text-align: center; }
                    .header h1 { margin: 0; font-size: 32px; color: #ffffff; font-weight: 900; letter-spacing: 2px;}
                    .header p { margin: 10px 0 0 0; font-size: 18px; color: #ffdcdc; }
                    .content { padding: 30px; background-color: #141414; }
                    .content p { font-size: 16px; line-height: 1.6; color: #e5e5e5; }
                    .btn-container { text-align: center; margin: 35px 0; }
                    .btn { display: inline-block; padding: 15px 35px; background-color: #E50914; color: #ffffff !important; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 4px; }
                    .warning-box { background-color: #2b2b2b; border-left: 4px solid #E50914; padding: 15px; margin: 20px 0; }
                    .warning-box p { margin: 0; font-size: 14px; color: #cccccc; }
                    .link-box { word-wrap: break-word; font-size: 14px; margin-top: 20px; color: #E50914;}
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #888888; background-color: #000000; border-top: 1px solid #333333; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>C I N E M A T E</h1>
                        <p>Đặt lại mật khẩu</p>
                    </div>
                    <div class="content">
                        <p>Xin chào,</p>
                        <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn trên <strong>Cinemate</strong>.</p>
                        <p>Nhấn vào nút bên dưới để đặt lại mật khẩu:</p>
                        <div class="btn-container">
                            <a href="%s" class="btn">Đặt lại mật khẩu</a>
                        </div>
                        <div class="warning-box">
                            <p><strong>Lưu ý:</strong> Link này sẽ hết hạn sau <strong>15 phút</strong>. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
                        </div>
                        <p>Nếu nút không hoạt động, bạn có thể copy đường link sau vào trình duyệt:</p>
                        <p class="link-box"><a href="%s" style="color: #E50914;">%s</a></p>
                    </div>
                    <div class="footer">
                        <p>© 2026 Cinemate Team. Email này được gửi tự động, vui lòng không trả lời.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetLink, resetLink, resetLink);
            
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Password reset HTML email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}", toEmail, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject("Cinemate - Mã xác nhận đổi mật khẩu");
            
            String htmlBody = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #000000; color: #ffffff; padding: 0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
                    .header { background-color: #E50914; padding: 40px 20px; text-align: center; }
                    .header h1 { margin: 0; font-size: 32px; color: #ffffff; font-weight: 900; letter-spacing: 2px;}
                    .header p { margin: 10px 0 0 0; font-size: 18px; color: #ffdcdc; }
                    .content { padding: 30px; background-color: #141414; }
                    .content p { font-size: 16px; line-height: 1.6; color: #e5e5e5; }
                    .otp-box { text-align: center; margin: 30px 0; padding: 20px; background-color: #2b2b2b; border: 2px dashed #E50914; border-radius: 8px; font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #E50914; }
                    .warning-box { background-color: #2b2b2b; border-left: 4px solid #E50914; padding: 15px; margin: 20px 0; }
                    .warning-box p { margin: 0; font-size: 14px; color: #cccccc; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #888888; background-color: #000000; border-top: 1px solid #333333; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>C I N E M A T E</h1>
                        <p>Xác nhận đổi mật khẩu</p>
                    </div>
                    <div class="content">
                        <p>Xin chào,</p>
                        <p>Chúng tôi nhận được yêu cầu đổi mật khẩu cho tài khoản của bạn trên <strong>Cinemate</strong>.</p>
                        <p>Mã xác nhận (OTP) của bạn là:</p>
                        <div class="otp-box">%s</div>
                        <div class="warning-box">
                            <p><strong>Lưu ý:</strong> Mã OTP này sẽ hết hạn sau <strong>10 phút</strong>. Tuyệt đối không chia sẻ mã này với bất kỳ ai.</p>
                        </div>
                        <p>Nếu bạn không yêu cầu đổi mật khẩu, vui lòng bỏ qua email này hoặc liên hệ hỗ trợ.</p>
                    </div>
                    <div class="footer">
                        <p>© 2026 Cinemate Team. Email này được gửi tự động, vui lòng không trả lời.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otp);
            
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Password change HTML OTP email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}", toEmail, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
