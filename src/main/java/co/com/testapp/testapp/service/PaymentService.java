package co.com.testapp.testapp.service;

import co.com.testapp.testapp.dto.*;
import co.com.testapp.testapp.entity.*;
import co.com.testapp.testapp.exception.CustomerNotFoundException;
import co.com.testapp.testapp.exception.OrderNotFoundException;
import co.com.testapp.testapp.exception.PaymentFailedException;
import co.com.testapp.testapp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(noRollbackFor = PaymentFailedException.class)
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final AuditService auditService;
    private final Random random = new Random();

    @Value("${payment.approval.probability:0.7}")
    private double approvalProbability;

    @Value("${payment.max.retry.attempts:3}")
    private int maxRetryAttempts;

    @Value("${payment.retry.delay.millis:1000}")
    private long retryDelayMillis;

    public PaymentService(OrderRepository orderRepository,
                          PaymentRepository paymentRepository,
                          CustomerRepository customerRepository,
                          EmailService emailService,
                          AuditService auditService,
                          ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.auditService = auditService;
        this.productRepository = productRepository;
    }

    public CheckoutResponse processCheckout(CheckoutRequest request) {
        log.info("Processing checkout for customer: {}", request.getCustomerId());

        // Validar cliente
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Customer with ID %d not found", request.getCustomerId())
                ));

        // Obtener carrito activo
        Order cart = orderRepository.findActiveCartByCustomerId(request.getCustomerId())
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("No active cart found for customer %d", request.getCustomerId())
                ));

        // Validar que el carrito tiene items
        if (cart.getItems().isEmpty()) {
            throw new OrderNotFoundException("Cart is empty. Cannot proceed with checkout.");
        }

        // Actualizar información de entrega
        cart.setShippingAddress(request.getShippingAddress());
        cart.setShippingCity(request.getShippingCity());
        cart.setShippingState(request.getShippingState());
        cart.setShippingZipCode(request.getShippingZipCode());
        cart.setShippingCountry(request.getShippingCountry());
        cart.setStatus(OrderStatus.PENDING);
        cart.setUpdatedAt(LocalDateTime.now());

        Order pendingOrder = orderRepository.save(cart);

        // Log de auditoría: Pedido creado
        auditService.logSuccessEvent(
                "ORDER_CREATED",
                "ORDER",
                pendingOrder.getId().toString(),
                customer.getId().toString(),
                "Pedido creado para checkout",
                null
        );

        // Crear registro de pago
        Payment payment = Payment.builder()
                .order(pendingOrder)
                .tokenizedCard(request.getTokenizedCard())
                .amount(pendingOrder.getTotalAmount())
                .status(PaymentStatus.PROCESSING)
                .attemptCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Log de auditoría: Pago iniciado
        auditService.logSuccessEvent(
                "PAYMENT_INITIATED",
                "PAYMENT",
                savedPayment.getId().toString(),
                customer.getId().toString(),
                "Procesamiento de pago iniciado",
                null
        );

        // Procesar pago con reintentos
        processPaymentWithRetries(customer, pendingOrder, savedPayment);

        // Recargar datos actualizados
        Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).orElse(savedPayment);
        Order updatedOrder = orderRepository.findById(pendingOrder.getId()).orElse(pendingOrder);

        return buildCheckoutResponse(updatedOrder, customer, updatedPayment);
    }

    private void processPaymentWithRetries(Customer customer, Order order, Payment payment) {
        log.info("Starting payment processing with retries for order: {}", order.getId());

        int attempt = 0;
        boolean paymentApproved = false;

        while (attempt < maxRetryAttempts && !paymentApproved) {
            attempt++;
            payment.setAttemptCount(attempt);

            log.info("Payment attempt {}/{} for order: {}", attempt, maxRetryAttempts, order.getId());

            // Simular procesamiento de pago
            if (random.nextDouble() < approvalProbability) {
                log.info("Payment APPROVED on attempt {} for order: {}", attempt, order.getId());
                payment.setStatus(PaymentStatus.APPROVED);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(payment);

                // Log de auditoría: Pago aprobado
                auditService.logSuccessEvent(
                        "PAYMENT_APPROVED",
                        "PAYMENT",
                        payment.getId().toString(),
                        customer.getId().toString(),
                        "Pago aprobado exitosamente en intento " + attempt,
                        null
                );

                // Actualizar estado del pedido
                order.setStatus(OrderStatus.CONFIRMED);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);

                // Log de auditoría: Pedido confirmado
                auditService.logSuccessEvent(
                        "ORDER_STATUS_CHANGED",
                        "ORDER",
                        order.getId().toString(),
                        customer.getId().toString(),
                        "Pedido confirmado después de pago aprobado",
                        null
                );

                decreaseProductStock(order);

                // Notificar al cliente
                emailService.sendPaymentApprovedNotification(
                        customer.getEmail(),
                        customer.getFirstName() + " " + customer.getLastName(),
                        order.getId().toString(),
                        payment.getAmount().toString()
                );

                paymentApproved = true;
            } else {
                String failureReason = String.format("Payment declined by gateway (attempt %d/%d)", attempt, maxRetryAttempts);
                log.warn("Payment REJECTED on attempt {}/{} for order: {}", attempt, maxRetryAttempts, order.getId());
                payment.setFailureReason(failureReason);

                // Log de auditoría: Intento de pago rechazado
                auditService.logRetryEvent(
                        "PAYMENT_ATTEMPTED",
                        "PAYMENT",
                        payment.getId().toString(),
                        customer.getId().toString(),
                        "Intento de pago rechazado " + attempt + "/" + maxRetryAttempts,
                        null
                );

                if (attempt < maxRetryAttempts) {
                    // Esperar antes de reintentar
                    try {
                        log.debug("Waiting {} ms before retry...", retryDelayMillis);
                        Thread.sleep(retryDelayMillis);
                    } catch (InterruptedException e) {
                        log.error("Sleep interrupted during payment retry", e);
                        Thread.currentThread().interrupt();
                    }
                } else {
                    // Todos los intentos fallaron
                    log.error("All payment attempts failed for order: {}", order.getId());
                    payment.setStatus(PaymentStatus.FAILED_FINAL);
                    payment.setUpdatedAt(LocalDateTime.now());
                    paymentRepository.save(payment);

                    // Actualizar estado del pedido
                    order.setStatus(OrderStatus.CANCELLED);
                    order.setUpdatedAt(LocalDateTime.now());
                    orderRepository.save(order);

                    // Log de auditoría: Pago falló definitivamente
                    try {
                        log.info("Attempting to log payment failure to audit...");
                        auditService.logFailureEvent(
                                "PAYMENT_REJECTED",
                                "PAYMENT",
                                payment.getId().toString(),
                                customer.getId().toString(),
                                "Pago rechazado después de " + maxRetryAttempts + " intentos",
                                failureReason,
                                null
                        );
                        log.info("Payment failure logged to audit successfully");
                    } catch (Exception e) {
                        log.error("Failed to log payment failure to audit", e);
                    }

                    // Log de auditoría: Pedido cancelado
                    try {
                        log.info("Attempting to log order cancellation to audit...");
                        auditService.logSuccessEvent(
                                "ORDER_CANCELLED",
                                "ORDER",
                                order.getId().toString(),
                                customer.getId().toString(),
                                "Pedido cancelado por fallo de pago",
                                null
                        );
                        log.info("Order cancellation logged to audit successfully");
                    } catch (Exception e) {
                        log.error("Failed to log order cancellation to audit", e);
                    }

                    // Notificar al cliente sobre fallo final
                    emailService.sendPaymentFailureNotification(
                            customer.getEmail(),
                            customer.getFirstName() + " " + customer.getLastName(),
                            order.getId().toString(),
                            failureReason
                    );

                    throw new PaymentFailedException(
                            "Payment processing failed after " + maxRetryAttempts + " attempts. Please contact support."
                    );
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public CheckoutResponse getCheckoutStatus(Long customerId, Long orderId) {
        log.info("Fetching checkout status for customer: {}, order: {}", customerId, orderId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Customer with ID %d not found", customerId)
                ));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Order with ID %d not found", orderId)
                ));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentFailedException(
                        String.format("Payment not found for order %d", orderId)
                ));

        return buildCheckoutResponse(order, customer, payment);
    }

    private CheckoutResponse buildCheckoutResponse(Order order, Customer customer, Payment payment) {
        return CheckoutResponse.builder()
                .orderId(order.getId())
                .customerId(customer.getId())
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .items(order.getItems().stream()
                        .map(item -> mapItemToResponse(item))
                        .collect(Collectors.toList()))
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getStatus().name())
                .payment(mapPaymentToResponse(payment))
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState())
                .shippingZipCode(order.getShippingZipCode())
                .shippingCountry(order.getShippingCountry())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    private PaymentResponse mapPaymentToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .attemptCount(payment.getAttemptCount())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }



    private void decreaseProductStock(Order order) {
        try {
            log.info("Decreasing product stock for order: {}", order.getId());
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                int newStock = product.getStock() - item.getQuantity();
                if (newStock < 0) {
                    log.error("Insufficient stock for product: {} (required: {}, available: {})",
                            product.getId(), item.getQuantity(), product.getStock());
                } else {
                    product.setStock(newStock);
                    product.setUpdatedAt(LocalDateTime.now());
                    productRepository.save(product);
                    log.info("Product stock updated for product: {}, new stock: {}", product.getId(), newStock);
                }
            }
        } catch (Exception e) {
            log.error("Error decreasing product stock for order: {}", order.getId(), e);
        }
    }

}