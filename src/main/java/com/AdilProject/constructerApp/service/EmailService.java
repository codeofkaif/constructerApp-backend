package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.entity.ConsultationLead;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${admin.notification.email:kkaif2687@gmail.com}")
    private String adminNotificationEmail;

    @Value("${spring.mail.username:kkaif2687@gmail.com}")
    private String fromEmail;

    @Async
    public void sendLeadNotification(ConsultationLead lead) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminNotificationEmail);
            helper.setSubject("🏗️ New Consultation Lead: " + lead.getName());

            String htmlBody = """
                <div style="font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #111827; color: #ffffff; padding: 28px; border-radius: 12px; max-width: 600px; margin: 0 auto; border: 1px solid #374151;">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <h2 style="color: #D4AF37; margin: 0; font-size: 22px;">🏗️ Adil Constructions</h2>
                        <p style="color: #9CA3AF; font-size: 13px; margin-top: 4px;">New Website Consultation Lead Received</p>
                    </div>

                    <div style="background-color: #1F2937; padding: 20px; border-radius: 8px; border: 1px solid #374151;">
                        <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                            <tr>
                                <td style="padding: 10px 0; color: #D4AF37; font-weight: 600; width: 100px;">Client Name:</td>
                                <td style="padding: 10px 0; color: #F9FAFB; font-weight: bold;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px 0; color: #D4AF37; font-weight: 600;">Phone:</td>
                                <td style="padding: 10px 0; color: #60A5FA; font-weight: bold;">
                                    <a href="tel:%s" style="color: #60A5FA; text-decoration: none;">%s</a>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 10px 0; color: #D4AF37; font-weight: 600; vertical-align: top;">Message:</td>
                                <td style="padding: 10px 0; color: #E5E7EB; line-height: 1.5;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px 0; color: #D4AF37; font-weight: 600;">Status:</td>
                                <td style="padding: 10px 0; color: #34D399; font-weight: bold;">NEW</td>
                            </tr>
                        </table>
                    </div>

                    <div style="margin-top: 24px; text-align: center;">
                        <p style="font-size: 12px; color: #6B7280; margin: 0;">This lead has also been saved to your Admin Dashboard.</p>
                    </div>
                </div>
            """.formatted(
                lead.getName(),
                lead.getPhone(),
                lead.getPhone(),
                lead.getMessage() != null && !lead.getMessage().isBlank() ? lead.getMessage() : "No message provided."
            );

            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("✅ Lead notification email sent to Admin: {}", adminNotificationEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send lead notification email: {}", e.getMessage());
        }
    }
}
