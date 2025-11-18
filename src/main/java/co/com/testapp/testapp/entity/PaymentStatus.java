package co.com.testapp.testapp.entity;

/**
 * Enumeración de estados posibles de un pago en el sistema.
 * 
 * Representa el ciclo de vida completo de una transacción de pago, desde su
 * inicio hasta su aprobación o rechazo final. Proporciona métodos utilitarios
 * para consultar el resultado del pago de manera segura.
 * 
 * @author Test App
 * @version 1.0
 */
public enum PaymentStatus {
  /** Pago pendiente de procesamiento */
  PENDING("Pendiente", "El pago está pendiente de procesamiento"),
  
  /** Pago siendo procesado actualmente */
  PROCESSING("Procesando", "El pago está siendo procesado"),
  
  /** Pago aprobado exitosamente */
  APPROVED("Aprobado", "El pago fue aprobado exitosamente"),
  
  /** Pago rechazado en este intento */
  REJECTED("Rechazado", "El pago fue rechazado en este intento"),
  
  /** Pago falló después de todos los intentos */
  FAILED_FINAL("Fallo Final", "El pago falló después de todos los intentos");

  private final String displayName;
  private final String description;

  /**
   * Constructor para inicializar un estado de pago.
   * 
   * @param displayName Nombre del estado para mostrar
   * @param description Descripción detallada del estado
   */
  PaymentStatus(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }

  /**
   * Obtiene el nombre mostrable del estado del pago.
   * 
   * @return Nombre del estado en español
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Obtiene la descripción detallada del estado del pago.
   * 
   * @return Descripción del estado
   */
  public String getDescription() {
    return description;
  }

  /**
   * Verifica si el estado del pago es final (no puede cambiar a otro estado).
   * 
   * Un estado es final si el pago fue aprobado o falló definitivamente.
   * 
   * @return true si el estado es final, false en caso contrario
   */
  public boolean isFinal() {
    return this == APPROVED || this == FAILED_FINAL;
  }

  /**
   * Verifica si el pago fue exitoso.
   * 
   * Un pago es exitoso si fue aprobado.
   * 
   * @return true si el pago fue aprobado, false en caso contrario
   */
  public boolean isSuccessful() {
    return this == APPROVED;
  }

  /**
   * Verifica si el pago falló.
   * 
   * Un pago se considera fallido si fue rechazado o falló definitivamente.
   * 
   * @return true si el pago falló, false en caso contrario
   */
  public boolean isFailed() {
    return this == REJECTED || this == FAILED_FINAL;
  }
}

