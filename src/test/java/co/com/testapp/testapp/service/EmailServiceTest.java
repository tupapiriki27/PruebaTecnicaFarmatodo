package co.com.testapp.testapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @Mock
  private JavaMailSender javaMailSender;

  private EmailService emailService;

  @BeforeEach
  void setUp() {
    emailService = new EmailService(javaMailSender);
    ReflectionTestUtils.setField(emailService, "emailNotificationEnabled", true);
    ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@testapp.com");
    ReflectionTestUtils.setField(emailService, "fromName", "Test App");
  }

  @Test
  void sendPaymentApprovedNotification_ShouldSendEmail() {
    // Setup
    String customerEmail = "john@example.com";
    String customerName = "John Doe";
    String orderId = "123";
    String amount = "2599.98";

    MimeMessage mimeMessage = org.mockito.Mockito.mock(MimeMessage.class);
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    // Execute
    emailService.sendPaymentApprovedNotification(customerEmail, customerName, orderId, amount);

    // Verify
    verify(javaMailSender, times(1)).createMimeMessage();
    verify(javaMailSender, times(1)).send(any(MimeMessage.class));
  }

  @Test
  void sendPaymentFailureNotification_ShouldSendEmail() {
    // Setup
    String customerEmail = "john@example.com";
    String customerName = "John Doe";
    String orderId = "123";
    String failureReason = "Card declined by gateway";

    MimeMessage mimeMessage = org.mockito.Mockito.mock(MimeMessage.class);
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    // Execute
    emailService.sendPaymentFailureNotification(customerEmail, customerName, orderId, failureReason);

    // Verify
    verify(javaMailSender, times(1)).createMimeMessage();
    verify(javaMailSender, times(1)).send(any(MimeMessage.class));
  }

  @Test
  void sendPaymentApprovedNotification_WhenDisabled_ShouldNotSendEmail() {
    // Setup
    ReflectionTestUtils.setField(emailService, "emailNotificationEnabled", false);

    String customerEmail = "john@example.com";
    String customerName = "John Doe";
    String orderId = "123";
    String amount = "2599.98";

    // Execute
    emailService.sendPaymentApprovedNotification(customerEmail, customerName, orderId, amount);

    // Verify - no email should be sent
    verify(javaMailSender, times(0)).send(any(MimeMessage.class));
  }

}

