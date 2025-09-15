package com.furkanerd.hr_management_system.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendBulkAnnouncementMail(List<String> toEmails, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("no-reply@company.com");
            message.setBcc(toEmails.toArray(new String[0]));
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Bulk announcement mail failed: {}", e.getMessage());
        }
    }
}
