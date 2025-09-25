package com.furkanerd.hr_management_system.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    private String testEmail;
    private String testSubject;
    private String testContent;

    @BeforeEach
    void setUp() {
        testEmail = "test@company.com";
        testSubject = "Test Subject";
        testContent = "Test Content";
    }

    @Test
    void sendMail_WithValidParameters_ShouldSendMailSuccessfully() {
        // Given
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> mailService.sendMail(testEmail, testSubject, testContent));

        // Then & Verify
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendBulkAnnouncementMail_WithValidParameters_ShouldSendBulkMailSuccessfully() {
        // Given
        List<String> emails = Arrays.asList("user1@company.com", "user2@company.com", "user3@company.com");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> mailService.sendBulkAnnouncementMail(emails, testSubject, testContent));

        // Then & Verify
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendBulkAnnouncementMail_WithMailSenderException_ShouldHandleExceptionGracefully() {
        // Given
        List<String> emails = Arrays.asList("user1@company.com", "user2@company.com");
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> mailService.sendBulkAnnouncementMail(emails, testSubject, testContent));

        // Then & Verify
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}

