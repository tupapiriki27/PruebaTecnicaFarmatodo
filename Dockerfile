# Multi-stage build para optimizar la imagen
# Stage 1: Builder
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Descargar dependencias (para mejor cacheing de capas)
RUN mvn dependency:resolve

# Copiar código fuente
COPY src src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar el JAR desde el builder
COPY --from=builder /build/target/*.jar app.jar

# Crear usuario no-root para seguridad
RUN useradd -m -u 1001 appuser && \
    chown -R appuser:appuser /app

USER appuser

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD java -version

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

