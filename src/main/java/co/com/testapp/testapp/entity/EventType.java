package co.com.testapp.testapp.entity;

/**
 * Enumeración de tipos de eventos que pueden ocurrir en el sistema.
 * 
 * Utilizado para auditoría y registro de transacciones. Cada tipo de evento
 * representa una acción específica del sistema y proporciona una descripción
 * legible para fines de auditoría y análisis.
 * 
 * @author Test App
 * @version 1.0
 */
public enum EventType {
  // Eventos de Cliente
  /** Cliente registrado en el sistema */
  CUSTOMER_REGISTERED("Cliente registrado"),
  
  /** Datos de cliente actualizados */
  CUSTOMER_UPDATED("Cliente actualizado"),
  
  /** Cliente eliminado del sistema */
  CUSTOMER_DELETED("Cliente eliminado"),

  // Eventos de Tokenización
  /** Proceso de tokenización iniciado */
  TOKENIZATION_INITIATED("Tokenización iniciada"),
  
  /** Tokenización completada exitosamente */
  TOKENIZATION_COMPLETED("Tokenización completada"),
  
  /** Tokenización falló */
  TOKENIZATION_FAILED("Tokenización fallida"),

  // Eventos de Producto
  /** Nuevo producto creado en el sistema */
  PRODUCT_CREATED("Producto creado"),
  
  /** Información de producto actualizada */
  PRODUCT_UPDATED("Producto actualizado"),
  
  /** Producto eliminado del sistema */
  PRODUCT_DELETED("Producto eliminado"),

  // Eventos de Carrito
  /** Carrito de compras creado para un cliente */
  CART_CREATED("Carrito creado"),
  
  /** Artículo agregado al carrito */
  ITEM_ADDED_TO_CART("Item agregado al carrito"),
  
  /** Artículo removido del carrito */
  ITEM_REMOVED_FROM_CART("Item removido del carrito"),
  
  /** Carrito vaciado */
  CART_CLEARED("Carrito vaciado"),

  // Eventos de Pago
  /** Proceso de pago iniciado */
  PAYMENT_INITIATED("Pago iniciado"),
  
  /** Intento de procesamiento de pago */
  PAYMENT_ATTEMPTED("Intento de pago"),
  
  /** Pago aprobado exitosamente */
  PAYMENT_APPROVED("Pago aprobado"),
  
  /** Pago rechazado */
  PAYMENT_REJECTED("Pago rechazado"),
  
  /** Pago completado */
  PAYMENT_COMPLETED("Pago completado"),

  // Eventos de Email
  /** Email de notificación enviado */
  EMAIL_SENT("Email enviado"),
  
  /** Envío de email falló */
  EMAIL_FAILED("Email fallido"),

  // Eventos de Pedido
  /** Nuevo pedido creado en el sistema */
  ORDER_CREATED("Pedido creado"),
  
  /** Estado del pedido cambió */
  ORDER_STATUS_CHANGED("Estado del pedido cambió"),
  
  /** Pedido cancelado */
  ORDER_CANCELLED("Pedido cancelado");

  private final String description;

  /**
   * Constructor para inicializar un tipo de evento con su descripción.
   * 
   * @param description Descripción legible del tipo de evento
   */
  EventType(String description) {
    this.description = description;
  }

  /**
   * Obtiene la descripción del tipo de evento.
   * 
   * @return Descripción en español del evento
   */
  public String getDescription() {
    return description;
  }
}

