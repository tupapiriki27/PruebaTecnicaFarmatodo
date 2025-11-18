package co.com.testapp.testapp.entity;

/**
 * Enumeración de estados posibles de un pedido en el sistema.
 * 
 * Representa el ciclo de vida completo de una orden, desde su creación
 * en el carrito hasta su entrega o cancelación. Proporciona métodos para
 * verificar el estado del pedido en diferentes puntos del proceso.
 * 
 * @author Test App
 * @version 1.0
 */
public enum OrderStatus {
  /** Pedido en carrito sin finalizar */
  CART("Carrito", "El pedido está en carrito sin finalizar"),
  
  /** Pedido pendiente de procesamiento del pago */
  PENDING("Pendiente", "El pedido está pendiente de procesamiento del pago"),
  
  /** Pedido confirmado y pagado exitosamente */
  CONFIRMED("Confirmado", "El pedido fue confirmado y pagado exitosamente"),
  
  /** Pedido siendo procesado para envío */
  PROCESSING("Procesando", "El pedido está siendo procesado para envío"),
  
  /** Pedido enviado al cliente */
  SHIPPED("Enviado", "El pedido ha sido enviado al cliente"),
  
  /** Pedido entregado exitosamente */
  DELIVERED("Entregado", "El pedido fue entregado exitosamente"),
  
  /** Pedido cancelado */
  CANCELLED("Cancelado", "El pedido fue cancelado");

  private final String displayName;
  private final String description;

  /**
   * Constructor para inicializar un estado de pedido.
   * 
   * @param displayName Nombre del estado para mostrar
   * @param description Descripción detallada del estado
   */
  OrderStatus(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }

  /**
   * Obtiene el nombre mostrable del estado del pedido.
   * 
   * @return Nombre del estado en español
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Obtiene la descripción detallada del estado del pedido.
   * 
   * @return Descripción del estado
   */
  public String getDescription() {
    return description;
  }

  /**
   * Verifica si el estado del pedido es final (no puede cambiar a otro estado).
   * 
   * Un estado es final si el pedido ha sido entregado o cancelado.
   * 
   * @return true si el estado es final, false en caso contrario
   */
  public boolean isFinal() {
    return this == DELIVERED || this == CANCELLED;
  }

  /**
   * Verifica si el pedido ha sido completado.
   * 
   * Un pedido se considera completado cuando ha sido entregado o cancelado.
   * 
   * @return true si el pedido está completado, false en caso contrario
   */
  public boolean isCompleted() {
    return this == DELIVERED || this == CANCELLED;
  }
}

