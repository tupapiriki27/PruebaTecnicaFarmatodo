package co.com.testapp.testapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio para envío de notificaciones por correo electrónico.
 * 
 * Proporciona funcionalidad para enviar notificaciones de pagos aprobados y fallidos
 * mediante HTML formateado usando la configuración de Gmail SMTP.
 * 
 * @author Test App
 * @version 1.0
 * @since 1.0
 */
@Service
@Slf4j
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${email.notification.enabled:true}")
  private boolean emailNotificationEnabled;

  @Value("${email.from.address:noreply@testapp.com}")
  private String fromAddress;

  @Value("${email.from.name:Test App}")
  private String fromName;

  /**
   * Constructor que inyecta el servidor de correo.
   * 
   * @param mailSender Implementación de JavaMailSender para enviar correos
   */
  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /**
   * Envía una notificación de fallo de pago al cliente.
   * 
   * Genera un email HTML con los detalles del fallo y lo envía
   * al cliente si las notificaciones están habilitadas.
   * 
   * @param customerEmail Email del cliente destinatario
   * @param customerName Nombre del cliente
   * @param orderId ID del pedido que falló
   * @param failureReason Razón del fallo del pago
   */
  public void sendPaymentFailureNotification(String customerEmail, String customerName, String orderId, String failureReason) {
    if (!emailNotificationEnabled) {
      log.debug("Email notifications are disabled");
      return;
    }

    try {
      log.info("Sending payment failure notification to: {} for order: {}", customerEmail, orderId);

      String subject = "Payment Failed - Order " + orderId;
      String body = buildPaymentFailureEmailBody(customerName, orderId, failureReason);

      sendHtmlEmail(customerEmail, subject, body);

      log.info("Payment failure notification sent successfully to: {}", customerEmail);
    } catch (MessagingException e) {
      log.error("Failed to send payment failure notification to: {}", customerEmail, e);
    } catch (Exception e) {
      log.error("Unexpected error while sending payment failure notification to: {}", customerEmail, e);
    }
  }

  /**
   * Envía una notificación de pago aprobado al cliente.
   * 
   * Genera un email HTML con los detalles del pago y lo envía
   * al cliente si las notificaciones están habilitadas.
   * 
   * @param customerEmail Email del cliente destinatario
   * @param customerName Nombre del cliente
   * @param orderId ID del pedido aprobado
   * @param amount Monto del pago aprobado
   */
  public void sendPaymentApprovedNotification(String customerEmail, String customerName, String orderId, String amount) {
    if (!emailNotificationEnabled) {
      log.debug("Email notifications are disabled");
      return;
    }

    try {
      log.info("Sending payment approved notification to: {} for order: {}", customerEmail, orderId);

      String subject = "Payment Approved - Order " + orderId;
      String body = buildPaymentApprovedEmailBody(customerName, orderId, amount);

      sendHtmlEmail(customerEmail, subject, body);

      log.info("Payment approved notification sent successfully to: {}", customerEmail);
    } catch (MessagingException e) {
      log.error("Failed to send payment approved notification to: {}", customerEmail, e);
    } catch (Exception e) {
      log.error("Unexpected error while sending payment approved notification to: {}", customerEmail, e);
    }
  }

  /**
   * Envía un email HTML formateado.
   * 
   * @param to Email del destinatario
   * @param subject Asunto del email
   * @param htmlContent Contenido HTML del email
   * @throws MessagingException Si hay error al enviar el email
   */
  private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setFrom(fromAddress, fromName);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true); // true = es HTML

      mailSender.send(mimeMessage);
    } catch (java.io.UnsupportedEncodingException e) {
      throw new MessagingException("Unsupported encoding when sending email", e);
    }
  }

  /**
   * Construye el cuerpo HTML de un email de fallo de pago.
   * 
   * @param customerName Nombre del cliente
   * @param orderId ID del pedido
   * @param failureReason Razón del fallo
   * @return Contenido HTML del email
   */
  private String buildPaymentFailureEmailBody(String customerName, String orderId, String failureReason) {
    return String.format(
        "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <style>\n" +
            "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n" +
            "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
            "    .header { background-color: #f8d7da; padding: 20px; border-radius: 5px; text-align: center; margin-bottom: 20px; }\n" +
            "    .header h2 { color: #721c24; margin: 0; }\n" +
            "    .content { background-color: #f5f5f5; padding: 20px; border-radius: 5px; margin-bottom: 20px; }\n" +
            "    .footer { text-align: center; color: #666; font-size: 12px; }\n" +
            "    .highlight { color: #d32f2f; font-weight: bold; }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div class=\"container\">\n" +
            "    <div class=\"header\">\n" +
            "      <h2>⚠️ Pago Rechazado</h2>\n" +
            "    </div>\n" +
            "    <div class=\"content\">\n" +
            "      <p>Estimado/a <strong>%s</strong>,</p>\n" +
            "      <p>Lamentamos informarte que el pago de tu pedido ha sido rechazado.</p>\n" +
            "      <p><strong>ID del Pedido:</strong> %s</p>\n" +
            "      <p><strong>Razón del Rechazo:</strong> <span class=\"highlight\">%s</span></p>\n" +
            "      <p>Hemos intentado procesar tu pago múltiples veces, pero desafortunadamente, todos los intentos han fallado.</p>\n" +
            "      <p><strong>¿Qué hacer ahora?</strong></p>\n" +
            "      <ul>\n" +
            "        <li>Por favor, verifica tu método de pago e intenta nuevamente</li>\n" +
            "        <li>Asegúrate de que tu tarjeta tenga fondos suficientes</li>\n" +
            "        <li>Contacta a tu banco si sospechas de fraude</li>\n" +
            "        <li>Comunícate con nuestro equipo de soporte para obtener ayuda</li>\n" +
            "      </ul>\n" +
            "      <p>Si tienes alguna pregunta, contáctanos en support@testapp.com</p>\n" +
            "    </div>\n" +
            "    <div class=\"footer\">\n" +
            "      <p>&copy; 2025 Test App. Todos los derechos reservados.</p>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>",
        customerName, orderId, failureReason
    );
  }

  /**
   * Construye el cuerpo HTML de un email de pago aprobado.
   * 
   * @param customerName Nombre del cliente
   * @param orderId ID del pedido
   * @param amount Monto del pago
   * @return Contenido HTML del email
   */
  private String buildPaymentApprovedEmailBody(String customerName, String orderId, String amount) {
    return String.format(
        "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <style>\n" +
            "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n" +
            "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
            "    .header { background-color: #d4edda; padding: 20px; border-radius: 5px; text-align: center; margin-bottom: 20px; }\n" +
            "    .header h2 { color: #155724; margin: 0; }\n" +
            "    .content { background-color: #f5f5f5; padding: 20px; border-radius: 5px; margin-bottom: 20px; }\n" +
            "    .order-details { background-color: #fff; padding: 15px; border-left: 4px solid #28a745; margin: 20px 0; }\n" +
            "    .footer { text-align: center; color: #666; font-size: 12px; }\n" +
            "    .highlight { color: #28a745; font-weight: bold; }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div class=\"container\">\n" +
            "    <div class=\"header\">\n" +
            "      <h2>✓ Pago Aprobado</h2>\n" +
            "    </div>\n" +
            "    <div class=\"content\">\n" +
            "      <p>Estimado/a <strong>%s</strong>,</p>\n" +
            "      <p>¡Gracias! Tu pago ha sido procesado exitosamente.</p>\n" +
            "      <div class=\"order-details\">\n" +
            "        <p><strong>ID del Pedido:</strong> %s</p>\n" +
            "        <p><strong>Monto Cobrado:</strong> <span class=\"highlight\">$%s</span></p>\n" +
            "      </div>\n" +
            "      <p><strong>¿Qué sucede ahora?</strong></p>\n" +
            "      <ul>\n" +
            "        <li>Tu pedido está siendo preparado para el envío</li>\n" +
            "        <li>Recibirás un número de seguimiento por correo tan pronto como sea enviado</li>\n" +
            "        <li>El tiempo típico de entrega es de 3 a 5 días hábiles</li>\n" +
            "      </ul>\n" +
            "      <p>Si tienes alguna pregunta sobre tu pedido, no dudes en contactarnos en support@testapp.com</p>\n" +
            "    </div>\n" +
            "    <div class=\"footer\">\n" +
            "      <p>&copy; 2025 Test App. Todos los derechos reservados.</p>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>",
        customerName, orderId, amount
    );
  }

}
