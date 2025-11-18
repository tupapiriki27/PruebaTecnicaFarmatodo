package co.com.testapp.testapp.entity;

/**
 * Enumeración de estados posibles de un evento en el sistema.
 * 
 * Utilizado para rastrear el resultado de cada operación realizada
 * en el sistema. Proporciona información sobre si una operación fue
 * completada exitosamente, falló, está pendiente o se está reintentando.
 * 
 * @author Test App
 * @version 1.0
 */
public enum EventStatus {
  /** Evento completado exitosamente */
  SUCCESS("Éxito", "El evento se completó exitosamente"),
  
  /** Evento falló durante la ejecución */
  FAILURE("Fallo", "El evento falló durante la ejecución"),
  
  /** Evento pendiente de procesamiento */
  PENDING("Pendiente", "El evento está pendiente de procesamiento"),
  
  /** Se está reintentando el evento después de un fallo */
  RETRY("Reintento", "Se está reintentando el evento después de un fallo");

  private final String name;
  private final String description;

  /**
   * Constructor para inicializar un estado de evento.
   * 
   * @param name Nombre del estado para mostrar
   * @param description Descripción detallada del estado
   */
  EventStatus(String name, String description) {
    this.name = name;
    this.description = description;
  }

  /**
   * Obtiene el nombre mostrable del estado del evento.
   * 
   * @return Nombre del estado en español
   */
  public String getDisplayName() {
    return name;
  }

  /**
   * Obtiene la descripción detallada del estado del evento.
   * 
   * @return Descripción del estado
   */
  public String getDescription() {
    return description;
  }
}

