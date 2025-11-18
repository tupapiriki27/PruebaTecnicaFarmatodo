# Sistema de Tokenización, Gestión de Clientes, Carrito y Pagos

Sistema desarrollado con **Java 21** y **Spring Boot 3.5.7** que simula un proceso completo de e-commerce: tokenización de tarjetas de crédito, gestión de clientes, carrito de compras, procesamiento de pagos con reintentos automáticos y notificaciones por email. Incluye autenticación por API Key, control de errores y validaciones robustas.

## 📋 Tabla de Contenidos

- [Características Principales](#características-principales)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
  - [Modo Desarrollo (Maven)](#modo-desarrollo-maven)
  - [Modo Producción (Maven)](#modo-producción-maven)
  - [Modo Docker](#modo-docker-🐳)
- [Descripción del Sistema y Componentes](#🏗️-descripción-del-sistema-y-componentes)
  - [Arquitectura General](#arquitectura-general)
  - [Componentes Principales](#componentes-principales)
- [Pruebas y Testing](#🧪-pruebas-y-testing)
- [Usar Colecciones API](#🔌-usar-colecciones-api-postman-insomnia-bruno)
  - [Postman](#📮-postman)
  - [Insomnia](#🐛-insomnia)
  - [Bruno](#🎭-bruno)
- [Desplegar en GCP](#🚀-desplegar-en-gcp-desde-cero)
- [Endpoints de la API](#endpoints-de-la-api)
  - [Ping](#ping)
  - [Tokenización](#tokenización)
  - [Gestión de Clientes](#gestión-de-clientes)
  - [Productos](#productos)
  - [Carrito de Compras](#carrito-de-compras)
  - [Pagos y Checkout](#pagos-y-checkout)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Tests](#tests)
- [Base de Datos](#base-de-datos)
- [Seguridad](#seguridad)
- [Manejo de Errores](#manejo-de-errores)

## 🎯 Características Principales

### Tokenización de Tarjetas de Crédito
- ✅ Generación de tokens únicos y seguros para tarjetas de crédito
- ✅ Detección automática de marca de tarjeta (VISA, MASTERCARD, AMEX)
- ✅ Validación de fecha de expiración
- ✅ Probabilidad de rechazo configurable (para simulación de rechazos)
- ✅ Autenticación mediante API Key
- ✅ Almacenamiento seguro en base de datos

### Gestión de Clientes
- ✅ Registro de clientes con datos completos
- ✅ Validación de unicidad de email y número de teléfono
- ✅ Validaciones exhaustivas de formato y contenido
- ✅ Normalización automática de emails a minúsculas
- ✅ Autenticación mediante API Key

### Gestión de Productos
- ✅ Crear y actualizar productos
- ✅ Listar productos activos
- ✅ Validaciones de stock y precios
- ✅ Categorización de productos
- ✅ Gestión de SKU

### Carrito de Compras
- ✅ Agregar productos al carrito
- ✅ Validación automática de stock disponible
- ✅ Actualización de cantidades si el producto ya existe en el carrito
- ✅ Cálculo automático de subtotales y total
- ✅ Un carrito activo por cliente
- ✅ Autenticación mediante API Key

### Pagos y Checkout
- ✅ Registrar pedidos con detalles del cliente y dirección de entrega
- ✅ Procesamiento de pagos con tarjeta tokenizada
- ✅ Aprobación/rechazo de pagos con probabilidad configurable
- ✅ Reintentos automáticos (N intentos configurables)
- ✅ Notificación por email cuando se agotan los reintentos
- ✅ Estados de pago: PENDING, PROCESSING, APPROVED, REJECTED, FAILED_FINAL
- ✅ **Integración con Gmail para envío de notificaciones**
- ✅ Emails HTML formateados con estilos personalizados
- ✅ Todos los parámetros configurables en `application.properties`

### Auditoría y Registro de Eventos
- ✅ UUID único para cada transacción (`java.util.UUID`)
- ✅ Registro de todos los eventos del sistema
- ✅ Tipos de eventos: clientes, tokenización, productos, carrito, pagos, emails, pedidos
- ✅ Estados de eventos: SUCCESS, FAILURE, PENDING, RETRY
- ✅ Querys por: ID, Entity ID, Event Type, Entity Type, User ID, Status, Date Range
- ✅ Endpoints REST para consultar logs
- ✅ Datos JSON serializados en cada evento
- ✅ Rastreabilidad completa de transacciones

### Ping API
- ✅ Endpoint de health check sin autenticación
- ✅ Retorna `pong` con código HTTP 200

## 🛠️ Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.5.7**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **Lombok** - Reducción de código boilerplate
- **H2 Database** - Base de datos en memoria (desarrollo)
- **Maven** - Gestión de dependencias
- **JUnit 5 + Mockito** - Testing

## 📦 Requisitos Previos

### Para Ejecutar Localmente (Maven)
- Java 21 o superior
- Maven 3.6+
- IDE compatible (IntelliJ IDEA, Eclipse, VS Code)

### Para Ejecutar con Docker
- Docker ([Descargar](https://www.docker.com/products/docker-desktop))
- Docker Compose (incluido en Docker Desktop)

## 🚀 Instalación

1. Clonar el repositorio o descargar el código:

```bash
cd test-app
```

2. Compilar el proyecto:

```bash
mvn clean install
```

## ⚙️ Configuración

El archivo `src/main/resources/application.properties` contiene la configuración principal:

```properties
# Base de Datos H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Configuración de Tokenización
tokenization.api.key=tk_live_secure_tokenization_key_2024
tokenization.rejection.probability=0.1

# Configuración de Clientes
customer.api.key=cs_live_secure_customer_key_2024

# Configuración de Productos
products.api.key=pd_live_secure_products_key_2024

# Configuración de Pedidos/Carrito
orders.api.key=or_live_secure_orders_key_2024

# Configuración de Pagos
payments.api.key=py_live_secure_payments_key_2024
payment.approval.probability=0.7
payment.max.retry.attempts=3
payment.retry.delay.millis=1000

# Configuración de Email
email.notification.enabled=true
email.from.address=noreply@testapp.com

# Puerto del servidor
server.port=8080
```

### Configuración de Gmail para Notificaciones por Email

Para habilitar el envío de correos de notificación mediante Gmail, sigue estos pasos:

#### 1. Habilitar 2-Step Verification en tu cuenta Google

- Ve a [Configuración de Google Account](https://myaccount.google.com/security)
- Activa "2-Step Verification"

#### 2. Generar App Password

- Ve a [Configuración de Contraseñas de Aplicación](https://myaccount.google.com/apppasswords)
- Selecciona "Mail" y "Windows Computer" (o tu sistema operativo)
- Google te generará una contraseña de 16 caracteres

#### 3. Configurar las Variables de Entorno

Antes de ejecutar la aplicación, establece estas variables de entorno:

```bash
export GMAIL_USERNAME="tu-email@gmail.com"
export GMAIL_PASSWORD="tu-app-password-de-16-caracteres"
```

#### 4. Propiedades en application.properties

```properties
# Email Configuration (Gmail)
email.notification.enabled=true
email.from.address=tu-email@gmail.com
email.from.name=Test App

# Gmail SMTP Configuration (estos valores son automáticos)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${GMAIL_USERNAME:tu-email@gmail.com}
spring.mail.password=${GMAIL_PASSWORD:tu-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

#### 5. Prueba de Envío

Cuando un pago se rechaza después de todos los reintentos o se aprueba, recibirás un email HTML formateado automáticamente.

### Configuración de Probabilidad de Rechazo

El sistema permite simular rechazos de tokenización mediante la propiedad:

```properties
tokenization.rejection.probability=0.1  # 10% de probabilidad de rechazo
```

Valores válidos: `0.0` (sin rechazos) a `1.0` (100% de rechazos)

## ▶️ Ejecución

### Modo Desarrollo (Maven)

```bash
mvn spring-boot:run
```

### Modo Producción (Maven)

```bash
mvn clean package
java -jar target/test-app-0.0.1-SNAPSHOT.jar
```

### Modo Docker 🐳

#### Ejecutar con Docker Compose

```bash
# Construir e iniciar
docker-compose up --build

# O en background
docker-compose up -d --build

# Ver logs
docker-compose logs -f

# Detener
docker-compose down
```

#### Ejecutar con Docker

```bash
# Construir imagen
docker build -t test-app:latest .

# Ejecutar contenedor
docker run -p 8080:8080 test-app:latest
```

#### Usar Script Helper (opcional)

```bash
# Hacer el script ejecutable
chmod +x docker-helper.sh

# Ver opciones disponibles
./docker-helper.sh help

# Iniciar aplicación
./docker-helper.sh up

# Ver logs
./docker-helper.sh logs

# Detener
./docker-helper.sh down
```

La aplicación estará disponible en: `http://localhost:8080`

**Para más detalles sobre Docker, revisa [DOCKER_GUIDE.md](./DOCKER_GUIDE.md)**

## 📡 Endpoints de la API

### Ping

**Verificar disponibilidad del servicio**

```
GET /ping
```

**Respuesta:**
```
pong
```

**Código HTTP:** `200 OK`

**Nota:** Este endpoint NO requiere autenticación.

---

### Tokenización

#### Crear Token

**Tokenizar una tarjeta de crédito**

```
POST /api/v1/tokenization/tokens
```

**Headers:**
```
X-API-Key: tk_live_secure_tokenization_key_2024
Content-Type: application/json
```

**Body:**
```json
{
  "cardNumber": "4111111111111111",
  "cvv": "123",
  "expirationDate": "12/25",
  "cardholderName": "John Doe"
}
```

**Validaciones:**
- `cardNumber`: 13-19 dígitos numéricos
- `cvv`: 3-4 dígitos numéricos
- `expirationDate`: Formato MM/YY, no expirada
- `cardholderName`: 3-100 caracteres

**Respuesta Exitosa (201 Created):**
```json
{
  "token": "tok_8037b35af29df3d4dae563085f47bfe9",
  "lastFourDigits": "1111",
  "cardBrand": "VISA",
  "expirationDate": "12/25",
  "createdAt": "2024-11-16T18:15:23",
  "active": true
}
```

**Errores Posibles:**
- `400 Bad Request`: Datos inválidos
- `403 Forbidden`: API Key inválida o faltante
- `422 Unprocessable Entity`: Tokenización rechazada por probabilidad configurada

---

### Gestión de Clientes

#### Registrar Cliente

**Registrar un nuevo cliente**

```
POST /api/v1/customers
```

**Headers:**
```
X-API-Key: cs_live_secure_customer_key_2024
Content-Type: application/json
```

**Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "address": "123 Main Street",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}
```

**Campos Obligatorios:**
- `firstName`: 2-100 caracteres
- `lastName`: 2-100 caracteres
- `email`: Email válido, máximo 150 caracteres, único
- `phoneNumber`: 10-20 dígitos, puede incluir `+`, único
- `address`: 5-255 caracteres

**Campos Opcionales:**
- `city`: Máximo 100 caracteres
- `state`: Máximo 100 caracteres
- `zipCode`: 5-20 dígitos
- `country`: Máximo 100 caracteres

**Respuesta Exitosa (201 Created):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "address": "123 Main Street",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA",
  "createdAt": "2024-11-16T18:15:23",
  "updatedAt": "2024-11-16T18:15:23",
  "active": true
}
```

**Errores Posibles:**
- `400 Bad Request`: Datos inválidos o formato incorrecto
- `403 Forbidden`: API Key inválida o faltante
- `409 Conflict`: Email o número de teléfono ya registrado

---

### Productos

#### Crear Producto

**Crear un nuevo producto**

```
POST /api/v1/products
```

**Headers:**
```
X-API-Key: pd_live_secure_products_key_2024
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Laptop Dell XPS 15",
  "description": "High-performance laptop with 16GB RAM",
  "price": 1299.99,
  "stock": 50,
  "category": "Electronics",
  "sku": "DELL-XPS15-001"
}
```

**Validaciones:**
- `name`: 3-200 caracteres (requerido)
- `description`: Máximo 1000 caracteres
- `price`: Mayor a 0 (requerido)
- `stock`: Mayor o igual a 0 (requerido)
- `category`: Máximo 100 caracteres
- `sku`: Máximo 50 caracteres

**Respuesta Exitosa (201 Created):**
```json
{
  "id": 1,
  "name": "Laptop Dell XPS 15",
  "description": "High-performance laptop with 16GB RAM",
  "price": 1299.99,
  "stock": 50,
  "category": "Electronics",
  "sku": "DELL-XPS15-001",
  "active": true,
  "createdAt": "2024-11-16T18:15:23",
  "updatedAt": "2024-11-16T18:15:23"
}
```

#### Listar Productos

**Obtener todos los productos activos**

```
GET /api/v1/products
```

**Headers:**
```
X-API-Key: pd_live_secure_products_key_2024
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptop Dell XPS 15",
    "description": "High-performance laptop with 16GB RAM",
    "price": 1299.99,
    "stock": 50,
    "category": "Electronics",
    "sku": "DELL-XPS15-001",
    "active": true,
    "createdAt": "2024-11-16T18:15:23",
    "updatedAt": "2024-11-16T18:15:23"
  }
]
```

#### Obtener Producto por ID

**Obtener detalles de un producto específico**

```
GET /api/v1/products/{id}
```

**Headers:**
```
X-API-Key: pd_live_secure_products_key_2024
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": 1,
  "name": "Laptop Dell XPS 15",
  "description": "High-performance laptop with 16GB RAM",
  "price": 1299.99,
  "stock": 50,
  "category": "Electronics",
  "sku": "DELL-XPS15-001",
  "active": true,
  "createdAt": "2024-11-16T18:15:23",
  "updatedAt": "2024-11-16T18:15:23"
}
```

**Errores Posibles:**
- `400 Bad Request`: Datos inválidos
- `403 Forbidden`: API Key inválida o faltante
- `404 Not Found`: Producto no encontrado

---

### Carrito de Compras

#### Agregar al Carrito

**Agregar un producto al carrito del cliente**

```
POST /api/v1/orders/cart/{customerId}
```

**Headers:**
```
X-API-Key: or_live_secure_orders_key_2024
Content-Type: application/json
```

**Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Validaciones:**
- `productId`: ID válido de producto existente (requerido)
- `quantity`: Mínimo 1 (requerido)
- Validación automática de stock disponible

**Respuesta Exitosa (201 Created):**
```json
{
  "id": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop Dell XPS 15",
      "quantity": 2,
      "unitPrice": 1299.99,
      "subtotal": 2599.98
    }
  ],
  "totalAmount": 2599.98,
  "status": "CART",
  "createdAt": "2024-11-16T18:15:23",
  "updatedAt": "2024-11-16T18:15:23"
}
```

**Notas:**
- Si el producto ya existe en el carrito, se actualiza la cantidad
- Se crea automáticamente un carrito si el cliente no tiene uno activo
- El stock se valida antes de agregar al carrito

#### Obtener Carrito

**Obtener el carrito activo de un cliente**

```
GET /api/v1/orders/cart/{customerId}
```

**Headers:**
```
X-API-Key: or_live_secure_orders_key_2024
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop Dell XPS 15",
      "quantity": 2,
      "unitPrice": 1299.99,
      "subtotal": 2599.98
    }
  ],
  "totalAmount": 2599.98,
  "status": "CART",
  "createdAt": "2024-11-16T18:15:23",
  "updatedAt": "2024-11-16T18:15:23"
}
```

**Errores Posibles:**
- `400 Bad Request`: Stock insuficiente
- `403 Forbidden`: API Key inválida o faltante
- `404 Not Found`: Cliente o producto no encontrado

---

## 📝 Ejemplos de Uso

### Ejemplo con cURL - Tokenización

```bash
curl -X POST http://localhost:8080/api/v1/tokenization/tokens \
  -H "X-API-Key: tk_live_secure_tokenization_key_2024" \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "4111111111111111",
    "cvv": "123",
    "expirationDate": "12/25",
    "cardholderName": "John Doe"
  }'
```

### Ejemplo con cURL - Registro de Cliente

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "X-API-Key: cs_live_secure_customer_key_2024" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "phoneNumber": "+1987654321",
    "address": "456 Oak Avenue",
    "city": "Los Angeles",
    "state": "CA",
    "zipCode": "90001",
    "country": "USA"
  }'
```

### Ejemplo con cURL - Ping

```bash
curl http://localhost:8080/ping
```

### Ejemplo con cURL - Crear Producto

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "X-API-Key: pd_live_secure_products_key_2024" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS 15",
    "description": "High-performance laptop with 16GB RAM",
    "price": 1299.99,
    "stock": 50,
    "category": "Electronics",
    "sku": "DELL-XPS15-001"
  }'
```

### Ejemplo con cURL - Agregar al Carrito

```bash
curl -X POST http://localhost:8080/api/v1/orders/cart/1 \
  -H "X-API-Key: or_live_secure_orders_key_2024" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

### Ejemplo con cURL - Obtener Carrito

```bash
curl http://localhost:8080/api/v1/orders/cart/1 \
  -H "X-API-Key: or_live_secure_orders_key_2024"
```

### Pagos y Checkout

#### Procesar Checkout

**Procesar el pago y crear el pedido**

```
POST /api/v1/payments/checkout
```

**Headers:**
```
X-API-Key: py_live_secure_payments_key_2024
Content-Type: application/json
```

**Body:**
```json
{
  "customerId": 1,
  "tokenizedCard": "tok_8037b35af29df3d4dae563085f47bfe9",
  "shippingAddress": "123 Main Street",
  "shippingCity": "New York",
  "shippingState": "NY",
  "shippingZipCode": "10001",
  "shippingCountry": "USA"
}
```

**Validaciones:**
- `customerId`: ID válido del cliente (requerido)
- `tokenizedCard`: Token de tarjeta previamente tokenizada (requerido)
- `shippingAddress`: Dirección completa (requerida)
- `shippingCity`: Ciudad (requerida)
- `shippingState`: Estado/Provincia (requerido)
- `shippingZipCode`: Código postal (requerido)
- `shippingCountry`: País (requerido)

**Respuesta Exitosa (201 Created):**
```json
{
  "orderId": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop Dell XPS 15",
      "quantity": 2,
      "unitPrice": 1299.99,
      "subtotal": 2599.98
    }
  ],
  "totalAmount": 2599.98,
  "orderStatus": "CONFIRMED",
  "payment": {
    "id": 1,
    "orderId": 1,
    "amount": 2599.98,
    "status": "APPROVED",
    "attemptCount": 1,
    "failureReason": null,
    "createdAt": "2024-11-16T18:15:23",
    "updatedAt": "2024-11-16T18:15:23"
  },
  "shippingAddress": "123 Main Street",
  "shippingCity": "New York",
  "shippingState": "NY",
  "shippingZipCode": "10001",
  "shippingCountry": "USA",
  "createdAt": "2024-11-16T18:15:23",
  "updatedAt": "2024-11-16T18:15:23"
}
```

**Notas:**
- El sistema reintenta automáticamente según `payment.max.retry.attempts` (default: 3)
- Si el pago se aprueba, el pedido pasa a estado CONFIRMED
- Si todos los reintentos fallan, el pedido se cancela y se envía email de notificación
- La probabilidad de aprobación es configurable en `payment.approval.probability`

#### Obtener Estado del Checkout

**Obtener el estado del pago y pedido**

```
GET /api/v1/payments/checkout/{customerId}/{orderId}
```

**Headers:**
```
X-API-Key: py_live_secure_payments_key_2024
```

**Respuesta Exitosa (200 OK):**
```json
{
  "orderId": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "items": [...],
  "totalAmount": 2599.98,
  "orderStatus": "CONFIRMED",
  "payment": {
    "id": 1,
    "orderId": 1,
    "amount": 2599.98,
    "status": "APPROVED",
    "attemptCount": 1,
    "failureReason": null,
    "createdAt": "2024-11-16T18:15:23",
    "updatedAt": "2024-11-16T18:15:23"
  },
  ...
}
```

**Errores Posibles:**
- `402 Payment Required`: Pago rechazado o falló después de todos los reintentos
- `403 Forbidden`: API Key inválida o faltante
- `404 Not Found`: Cliente, pedido o pago no encontrado

### Ejemplo con cURL - Procesar Checkout

```bash
curl -X POST http://localhost:8080/api/v1/payments/checkout \
  -H "X-API-Key: py_live_secure_payments_key_2024" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "tokenizedCard": "tok_8037b35af29df3d4dae563085f47bfe9",
    "shippingAddress": "123 Main Street",
    "shippingCity": "New York",
    "shippingState": "NY",
    "shippingZipCode": "10001",
    "shippingCountry": "USA"
  }'
```

### Ejemplo con cURL - Obtener Estado del Checkout

```bash
curl http://localhost:8080/api/v1/payments/checkout/1/1 \
  -H "X-API-Key: py_live_secure_payments_key_2024"
```

### Auditoría y Registro de Eventos

#### Obtener Log de Auditoría por ID

**Obtener un log específico usando su UUID**

```
GET /api/v1/audit/{id}
```

**Headers:**
```
Sin autenticación requerida (auditoría interna)
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "eventType": "PAYMENT_APPROVED",
  "entityType": "PAYMENT",
  "entityId": "1",
  "userId": "1",
  "description": "Payment approved successfully",
  "details": "{\"amount\":\"2599.98\",\"orderId\":\"1\"}",
  "status": "SUCCESS",
  "errorMessage": null,
  "createdAt": "2024-11-16T18:15:23",
  "sourceIp": null
}
```

#### Obtener Logs por Entidad

**Obtener todos los eventos asociados a una entidad específica**

```
GET /api/v1/audit/entity/{entityId}
```

**Ejemplo:**
```bash
curl http://localhost:8080/api/v1/audit/entity/payment-123
```

**Respuesta:** Array de AuditLogResponse

#### Obtener Logs por Tipo de Evento

**Filtrar eventos por tipo**

```
GET /api/v1/audit/event-type/{eventType}?page=0&size=20
```

**Event Types disponibles:**
- `CUSTOMER_REGISTERED`, `CUSTOMER_UPDATED`, `CUSTOMER_DELETED`
- `TOKENIZATION_INITIATED`, `TOKENIZATION_COMPLETED`, `TOKENIZATION_FAILED`
- `PRODUCT_CREATED`, `PRODUCT_UPDATED`, `PRODUCT_DELETED`
- `CART_CREATED`, `ITEM_ADDED_TO_CART`, `ITEM_REMOVED_FROM_CART`, `CART_CLEARED`
- `PAYMENT_INITIATED`, `PAYMENT_ATTEMPTED`, `PAYMENT_APPROVED`, `PAYMENT_REJECTED`, `PAYMENT_COMPLETED`
- `EMAIL_SENT`, `EMAIL_FAILED`
- `ORDER_CREATED`, `ORDER_STATUS_CHANGED`, `ORDER_CANCELLED`

**Ejemplo:**
```bash
curl "http://localhost:8080/api/v1/audit/event-type/PAYMENT_APPROVED?page=0&size=20"
```

#### Obtener Logs por Rango de Fechas

**Buscar eventos dentro de un rango de fechas**

```
GET /api/v1/audit/date-range?startDate=2024-11-16T00:00:00&endDate=2024-11-16T23:59:59&page=0&size=20
```

**Ejemplo:**
```bash
curl "http://localhost:8080/api/v1/audit/date-range?startDate=2024-11-16T00:00:00&endDate=2024-11-16T23:59:59"
```

#### Obtener Logs por Usuario

**Obtener eventos realizados por un usuario específico**

```
GET /api/v1/audit/user/{userId}?page=0&size=20
```

#### Obtener Logs por Estado

**Filtrar eventos por estado (SUCCESS, FAILURE, PENDING, RETRY)**

```
GET /api/v1/audit/status/{status}?page=0&size=20
```

**Ejemplo:**
```bash
curl "http://localhost:8080/api/v1/audit/status/FAILURE?page=0&size=20"
```

### Características de Auditoría

- **UUID Único**: Cada transacción tiene un identificador único UUID para trazabilidad completa
- **Serialización JSON**: Todos los detalles complejos se guardan como JSON para análisis
- **Timestamps Precisos**: Cada evento registra su fecha/hora exacta
- **Estados Variados**: SUCCESS (éxito), FAILURE (fallo), PENDING (pendiente), RETRY (reintento)
- **Rastreabilidad de Usuario**: Información opcional del usuario que realizó la acción
- **Filtrado Flexible**: Múltiples formas de consultar logs para análisis
- **Sin Autenticación**: Los endpoints de auditoría son accesibles sin API Key (auditoría interna)

## 🧪 Tests

El proyecto incluye tests exhaustivos:

### Ejecutar Tests

```bash
mvn test
```

### Cobertura de Tests

- ✅ **PingControllerTest**: Verificación del endpoint de health check
- ✅ **TokenizationServiceTest**: Lógica de tokenización, validaciones y rechazos
- ✅ **TokenizationControllerTest**: API de tokenización, autenticación y validaciones
- ✅ **CustomerServiceTest**: Lógica de registro y validaciones de unicidad
- ✅ **CustomerControllerTest**: API de clientes, autenticación y validaciones
- ✅ **ProductServiceTest**: Lógica de productos, creación y actualización
- ✅ **OrderServiceTest**: Lógica de carrito, validación de stock y agregado de items
- ✅ **PaymentServiceTest**: Lógica de pagos, reintentos y notificaciones
- ✅ **EmailServiceTest**: Envío de emails, integración con Gmail
- ✅ **AuditServiceTest**: Registro de eventos, logging y auditoría

**Total: 36 tests**

### Reporte de Tests

```
Tests run: 36, Failures: 0, Errors: 0, Skipped: 0
```

## 🗄️ Base de Datos

### Consola H2

Acceder a la consola de base de datos H2:

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
User: sa
Password: (dejar vacío)
```

### Tablas

#### card_tokens
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT | ID autoincremental |
| token | VARCHAR(64) | Token único |
| last_four_digits | VARCHAR(4) | Últimos 4 dígitos |
| card_brand | VARCHAR(50) | Marca de tarjeta |
| expiration_date | VARCHAR(255) | Fecha de expiración |
| cardholder_name | VARCHAR(100) | Nombre del titular |
| created_at | TIMESTAMP | Fecha de creación |
| active | BOOLEAN | Estado activo |

#### customers
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT | ID autoincremental |
| first_name | VARCHAR(100) | Nombre |
| last_name | VARCHAR(100) | Apellido |
| email | VARCHAR(150) | Email (único) |
| phone_number | VARCHAR(20) | Teléfono (único) |
| address | VARCHAR(255) | Dirección |
| city | VARCHAR(100) | Ciudad |
| state | VARCHAR(100) | Estado/Provincia |
| zip_code | VARCHAR(20) | Código postal |
| country | VARCHAR(100) | País |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |
| active | BOOLEAN | Estado activo |

#### products
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT | ID autoincremental |
| name | VARCHAR(200) | Nombre del producto |
| description | VARCHAR(1000) | Descripción |
| price | DECIMAL(10,2) | Precio unitario |
| stock | INTEGER | Stock disponible |
| category | VARCHAR(100) | Categoría |
| sku | VARCHAR(50) | SKU único |
| active | BOOLEAN | Estado activo |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

#### orders
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT | ID autoincremental |
| customer_id | BIGINT | ID del cliente (FK) |
| total_amount | DECIMAL(10,2) | Monto total |
| status | ENUM | Estado (CART, PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED) |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

#### order_items
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT | ID autoincremental |
| order_id | BIGINT | ID del pedido (FK) |
| product_id | BIGINT | ID del producto (FK) |
| quantity | INTEGER | Cantidad |
| unit_price | DECIMAL(10,2) | Precio unitario |
| subtotal | DECIMAL(10,2) | Subtotal |

#### payments
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT | ID autoincremental |
| order_id | BIGINT | ID del pedido (FK, único) |
| tokenized_card | VARCHAR(255) | Token de tarjeta |
| amount | DECIMAL(10,2) | Monto del pago |
| status | ENUM | Estado (PENDING, PROCESSING, APPROVED, REJECTED, FAILED_FINAL) |
| attempt_count | INTEGER | Número de intentos realizados |
| failure_reason | VARCHAR(500) | Razón del fallo si aplica |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

## 🔒 Seguridad

### Autenticación

El sistema utiliza autenticación basada en API Keys:

- **Tokenización**: `X-API-Key: tk_live_secure_tokenization_key_2024`
- **Clientes**: `X-API-Key: cs_live_secure_customer_key_2024`
- **Productos**: `X-API-Key: pd_live_secure_products_key_2024`
- **Carrito/Pedidos**: `X-API-Key: or_live_secure_orders_key_2024`
- **Pagos/Checkout**: `X-API-Key: py_live_secure_payments_key_2024`
- **Ping**: Sin autenticación

### Configuración de Spring Security

- CSRF deshabilitado (API REST)
- Sesiones stateless
- Filtro personalizado de API Key
- H2 Console accesible sin autenticación (solo desarrollo)

### Recomendaciones de Producción

⚠️ **Importante**: Antes de desplegar en producción:

1. Cambiar las API Keys en `application.properties`
2. Utilizar variables de entorno para las claves
3. Migrar de H2 a una base de datos de producción (PostgreSQL, MySQL)
4. Habilitar HTTPS
5. Implementar rate limiting
6. Agregar logging y monitoreo

## ❌ Manejo de Errores

### Estructura de Respuesta de Error

```json
{
  "timestamp": "2024-11-16T18:15:23",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid request parameters",
  "path": "/api/v1/tokenization/tokens",
  "details": [
    "cardNumber: Card number must be between 13 and 19 digits",
    "cvv: CVV must be 3 or 4 digits"
  ]
}
```

### Códigos de Estado HTTP

| Código | Significado | Descripción |
|--------|-------------|-------------|
| 200 | OK | Solicitud exitosa (ping) |
| 201 | Created | Recurso creado exitosamente |
| 400 | Bad Request | Datos inválidos o malformados |
| 403 | Forbidden | API Key inválida o faltante |
| 409 | Conflict | Email o teléfono duplicado |
| 422 | Unprocessable Entity | Tokenización rechazada |
| 500 | Internal Server Error | Error inesperado del servidor |

## 🏗️ Arquitectura del Proyecto

```
src/
├── main/
│   ├── java/co/com/testapp/testapp/
│   │   ├── controller/
│   │   │   ├── PingController.java
│   │   │   ├── TokenizationController.java
│   │   │   └── CustomerController.java
│   │   ├── dto/
│   │   │   ├── TokenizationRequest.java
│   │   │   ├── TokenizationResponse.java
│   │   │   ├── CustomerRegistrationRequest.java
│   │   │   ├── CustomerResponse.java
│   │   │   └── ErrorResponse.java
│   │   ├── entity/
│   │   │   ├── CardToken.java
│   │   │   └── Customer.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── TokenizationRejectedException.java
│   │   │   ├── InvalidCardDataException.java
│   │   │   ├── DuplicateCustomerException.java
│   │   │   └── CustomerNotFoundException.java
│   │   ├── repository/
│   │   │   ├── CardTokenRepository.java
│   │   │   └── CustomerRepository.java
│   │   ├── security/
│   │   │   ├── ApiKeyAuthenticationFilter.java
│   │   │   └── SecurityConfig.java
│   │   ├── service/
│   │   │   ├── TokenizationService.java
│   │   │   └── CustomerService.java
│   │   └── TestAppApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/co/com/testapp/testapp/
        ├── controller/
        ├── service/
        └── TestAppApplicationTests.java
```

## 🏗️ Descripción del Sistema y Componentes

### Arquitectura General

El proyecto sigue una **arquitectura en capas** con separación clara de responsabilidades:

```
┌─────────────────────────────────────────────────────┐
│           Capa de Presentación (REST API)           │
│  (/api/v1/tokenization, /api/v1/customers, etc.)   │
└────────────────┬────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────┐
│         Capa de Seguridad (API Key Auth)            │
│     (ApiKeyAuthenticationFilter, SecurityConfig)    │
└────────────────┬────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────┐
│         Capa de Lógica de Negocio (Services)        │
│  (TokenizationService, CustomerService, etc.)       │
└────────────────┬────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────┐
│      Capa de Persistencia (Repository / JPA)        │
│  (CardTokenRepository, CustomerRepository, etc.)    │
└────────────────┬────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────┐
│      Base de Datos (H2 en Memoria)                  │
│              (Development)                          │
└─────────────────────────────────────────────────────┘
```

### Componentes Principales

#### 1. **Tokenización de Tarjetas** 🔐
- **Controller**: `TokenizationController`
- **Service**: `TokenizationService`
- **Entity**: `CardToken`
- **Funcionalidad**: Genera tokens seguros para tarjetas de crédito con detección automática de marca (VISA, MASTERCARD, AMEX)

#### 2. **Gestión de Clientes** 👥
- **Controller**: `CustomerController`
- **Service**: `CustomerService`
- **Entity**: `Customer`
- **Funcionalidad**: Registro y validación de clientes con unicidad de email y teléfono

#### 3. **Gestión de Productos** 📦
- **Controller**: `ProductController`
- **Service**: `ProductService`
- **Entity**: `Product`
- **Funcionalidad**: CRUD de productos con gestión de stock y categorización

#### 4. **Carrito de Compras** 🛒
- **Controller**: `OrderController`
- **Service**: `OrderService`
- **Entity**: `Order`, `OrderItem`
- **Funcionalidad**: Agregar productos al carrito, validar stock, calcular totales

#### 5. **Procesamiento de Pagos** 💳
- **Controller**: `PaymentController`
- **Service**: `PaymentService`, `EmailService`
- **Entity**: `Payment`
- **Funcionalidad**: Procesa pagos con reintentos automáticos y notificaciones por email

#### 6. **Auditoría y Logging** 📊
- **Controller**: `AuditController`
- **Service**: `AuditService`
- **Entity**: `AuditLog`
- **Funcionalidad**: Registro completo de eventos del sistema con trazabilidad

#### 7. **Health Check** ✅
- **Controller**: `PingController`
- **Endpoint**: `GET /ping`
- **Funcionalidad**: Verifica disponibilidad de la aplicación (sin autenticación)

### Flujo de Datos Típico

```
1. Cliente REST → Endpoint (/api/v1/...)
        ↓
2. Spring Web → Dispatcher Servlet
        ↓
3. Security Filter → Validar API Key
        ↓
4. Controller → Procesar request
        ↓
5. Service → Lógica de negocio
        ↓
6. Repository → Acceso a datos
        ↓
7. Database → Persistencia
        ↓
8. Response → Cliente (JSON)
```

---

## 🧪 Pruebas y Testing

### Ejecutar Tests Localmente

#### Opción 1: Maven (Command Line)

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar un test específico
mvn test -Dtest=TokenizationServiceTest

# Ejecutar tests con cobertura
mvn test jacoco:report

# Ver reporte de cobertura
# Abre: target/site/jacoco/index.html
```

#### Opción 2: IDE (IntelliJ IDEA)

1. Click derecho en la carpeta `src/test/java`
2. Selecciona "Run 'All Tests'"
3. O en un archivo específico: Click derecho → "Run"

#### Opción 3: Docker

```bash
docker-compose exec test-app mvn test
```

### Cobertura de Tests

**Total: 36 tests** cubriendo:

- ✅ **PingControllerTest** (1 test)
- ✅ **TokenizationServiceTest** (4 tests)
- ✅ **TokenizationControllerTest** (4 tests)
- ✅ **CustomerServiceTest** (4 tests)
- ✅ **CustomerControllerTest** (4 tests)
- ✅ **ProductServiceTest** (4 tests)
- ✅ **OrderServiceTest** (4 tests)
- ✅ **PaymentServiceTest** (4 tests)
- ✅ **EmailServiceTest** (2 tests)
- ✅ **AuditServiceTest** (2 tests)

### Ver Resultados de Tests

```bash
# Después de ejecutar tests
cat target/surefire-reports/TEST-*.xml

# O desde el navegador:
# target/surefire-reports/index.html
```

---

## 🔌 Usar Colecciones API (Postman, Insomnia, Bruno)

### 📮 Postman

#### Opción 1: Importar Colección

1. **Abre Postman**
2. Click en **Import**
3. Selecciona: `postman_collection.json` (en raíz del proyecto)
4. Selecciona: `postman_environment.json`
5. ¡Listo! Ya tienes todos los endpoints

#### Opción 2: Crear Variables de Entorno

En Postman → Environments → New:

```json
{
  "base_url": "http://localhost:8080",
  "tokenization_key": "tk_live_secure_tokenization_key_2024",
  "customer_key": "cs_live_secure_customer_key_2024",
  "products_key": "pd_live_secure_products_key_2024",
  "orders_key": "or_live_secure_orders_key_2024",
  "payments_key": "py_live_secure_payments_key_2024"
}
```

#### Opciones 3: Usar Headers en cada request

```
X-API-Key: tk_live_secure_tokenization_key_2024
Content-Type: application/json
```

---

### 🐛 Insomnia

#### Importar Colección

1. **Abre Insomnia**
2. Menú → **Import/Export** → **Import Data**
3. Selecciona `postman_collection.json`
4. Insomnia convertirá automáticamente al formato

#### Crear Workspace

```bash
# O crea manualmente:
# 1. New Request → GET
# 2. URL: http://localhost:8080/ping
# 3. Click Send
```

---

### 🎭 Bruno

#### Usar Colección

Bruno es compatible con colecciones de Postman:

1. **Abre Bruno**
2. New Collection → Selecciona carpeta del proyecto
3. Click **Import** → Selecciona `postman_collection.json`
4. ¡Listo!

#### Ventaja de Bruno

- ✅ Open source
- ✅ Git-friendly (almacena en texto plano)
- ✅ Sin servidor en la nube

---

### 📋 Ejemplos de Requests

#### Health Check (Sin Autenticación)

```bash
curl -X GET http://localhost:8080/ping
```

#### Crear Cliente (Con API Key)

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "X-API-Key: cs_live_secure_customer_key_2024" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@example.com",
    "phoneNumber": "+34600000000",
    "address": "Calle Principal 123",
    "city": "Madrid",
    "state": "Madrid",
    "zipCode": "28001",
    "country": "España"
  }'
```

#### Crear Producto

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "X-API-Key: pd_live_secure_products_key_2024" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS",
    "description": "High performance laptop",
    "price": 1299.99,
    "stock": 50,
    "category": "Electronics",
    "sku": "DELL-XPS-001"
  }'
```

#### Tokenizar Tarjeta

```bash
curl -X POST http://localhost:8080/api/v1/tokenization/tokens \
  -H "X-API-Key: tk_live_secure_tokenization_key_2024" \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "4111111111111111",
    "cvv": "123",
    "expirationDate": "12/25",
    "cardholderName": "John Doe"
  }'
```

---

## 🚀 Desplegar en GCP desde Cero

### Requisitos Previos

- Cuenta de Google Cloud ([Crear](https://console.cloud.google.com))
- Project creado en GCP
- GCP CLI instalado ([Descargar](https://cloud.google.com/sdk/docs/install))
- Docker installed locally

### Paso 1: Autenticarse en GCP

```bash
# Iniciar sesión en GCP
gcloud auth login

# Configurar proyecto
gcloud config set project MY-PROJECT-ID

# Habilitar servicios necesarios
gcloud services enable compute.googleapis.com
gcloud services enable containerregistry.googleapis.com
gcloud services enable artifactregistry.googleapis.com
```

### Paso 2: Crear Artifact Registry (Docker Repository)

```bash
# Crear repositorio
gcloud artifacts repositories create test-app-repo \
  --repository-format=docker \
  --location=us-central1 \
  --description="Docker repository for test-app"

# Configurar Docker
gcloud auth configure-docker us-central1-docker.pkg.dev
```

### Paso 3: Construir y Subir Imagen a GCP

```bash
# Desde la carpeta del proyecto
cd /Users/rbeltran/Desktop/test-app

# Construir imagen
docker build -t test-app:latest .

# Etiquetar para GCP
docker tag test-app:latest \
  us-central1-docker.pkg.dev/MY-PROJECT-ID/test-app-repo/test-app:latest

# Subir a Artifact Registry
docker push us-central1-docker.pkg.dev/MY-PROJECT-ID/test-app-repo/test-app:latest
```

### Paso 4: Crear Instancia en Compute Engine

```bash
# Crear VM instance
gcloud compute instances create test-app-vm \
  --zone=us-central1-a \
  --machine-type=e2-medium \
  --image-family=debian-11 \
  --image-project=debian-cloud \
  --scopes=cloud-platform

# SSH a la instancia
gcloud compute ssh test-app-vm --zone=us-central1-a
```

### Paso 5: Instalar Docker en la Instancia GCP

```bash
# En la VM remota
sudo apt-get update
sudo apt-get install -y docker.io

# Iniciar Docker
sudo systemctl start docker
sudo systemctl enable docker

# Agregar usuario al grupo docker
sudo usermod -aG docker $USER
newgrp docker
```

### Paso 6: Descargar y Ejecutar Imagen en GCP

```bash
# En la VM remota
gcloud auth configure-docker us-central1-docker.pkg.dev

# Descargar imagen
docker pull us-central1-docker.pkg.dev/MY-PROJECT-ID/test-app-repo/test-app:latest

# Ejecutar contenedor
docker run -d \
  -p 8080:8080 \
  -e GMAIL_USERNAME="tu-email@gmail.com" \
  -e GMAIL_PASSWORD="tu-app-password" \
  --name test-app-container \
  us-central1-docker.pkg.dev/MY-PROJECT-ID/test-app-repo/test-app:latest

# Verificar que está corriendo
docker ps
```

### Paso 7: Abrir Firewall en GCP

```bash
# Desde tu máquina local
gcloud compute firewall-rules create allow-test-app \
  --allow=tcp:8080 \
  --source-ranges=0.0.0.0/0 \
  --target-tags=test-app

# Aplicar tag a la instancia
gcloud compute instances add-tags test-app-vm \
  --tags=test-app \
  --zone=us-central1-a
```

### Paso 8: Acceder a la Aplicación

```bash
# Obtener IP pública
gcloud compute instances describe test-app-vm \
  --zone=us-central1-a \
  --format='get(networkInterfaces[0].accessConfigs[0].natIP)'

# Acceder a la aplicación
# http://<EXTERNAL_IP>:8080/ping
# http://<EXTERNAL_IP>:8080/h2-console
```

### Paso 9: Usar Docker Compose en GCP (Opcional)

Si prefieres usar docker-compose:

```bash
# En la VM remota, clonar el repositorio
git clone <tu-repo> test-app
cd test-app

# Ejecutar con docker-compose
docker-compose up -d

# Ver logs
docker-compose logs -f
```

### Paso 10: Configurar Cloud SQL (Producción)

```bash
# Crear instancia CloudSQL
gcloud sql instances create test-app-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1

# Crear base de datos
gcloud sql databases create testdb --instance=test-app-db

# Obtener conexión
gcloud sql instances describe test-app-db

# Actualizar docker-compose.yml con Cloud SQL
# (Ver sección Opción 3 en docker-compose)
```

### Paso 11: Monitoreo (Cloud Monitoring)

```bash
# Ver logs de la aplicación
gcloud compute instances tail test-app-vm \
  --zone=us-central1-a

# O acceder a Cloud Logging
# https://console.cloud.google.com/logs
```

### Crear Script Automático para GCP

Crea un archivo `deploy-gcp.sh`:

```bash
#!/bin/bash
set -e

PROJECT_ID="MY-PROJECT-ID"
REGION="us-central1"
REPO_NAME="test-app-repo"
IMAGE_NAME="test-app"
VM_NAME="test-app-vm"
VM_ZONE="us-central1-a"

echo "🚀 Iniciando deploy en GCP..."

# Construir imagen
echo "📦 Construyendo imagen..."
docker build -t ${IMAGE_NAME}:latest .

# Etiquetar
docker tag ${IMAGE_NAME}:latest \
  ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest

# Subir
echo "📤 Subiendo a Artifact Registry..."
docker push ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest

# Desplegar
echo "🚀 Desplegando en Compute Engine..."
gcloud compute ssh ${VM_NAME} --zone=${VM_ZONE} --command="\
  docker pull ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest && \
  docker stop test-app-container || true && \
  docker rm test-app-container || true && \
  docker run -d \
    -p 8080:8080 \
    -e GMAIL_USERNAME='${GMAIL_USERNAME}' \
    -e GMAIL_PASSWORD='${GMAIL_PASSWORD}' \
    --name test-app-container \
    ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest
"

echo "✅ Deploy completado!"
```

Usa:
```bash
chmod +x deploy-gcp.sh
GMAIL_USERNAME="tu-email@gmail.com" GMAIL_PASSWORD="tu-pass" ./deploy-gcp.sh
```

---

## 📚 Documentación Adicional

Para más información sobre las tecnologías utilizadas:

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [Lombok](https://projectlombok.org/)

## 👨‍💻 Desarrollo

### Estándares de Código

- Java 21 features
- Lombok para reducir boilerplate
- Validaciones con Bean Validation
- Manejo centralizado de excepciones
- Logging con SLF4J
- Tests exhaustivos con JUnit 5

### Mejoras Futuras

- [ ] Agregar paginación a los endpoints
- [ ] Implementar endpoints GET para consultas
- [ ] Agregar documentación con Swagger/OpenAPI
- [ ] Implementar auditoría de acciones
- [ ] Agregar caché con Redis
- [ ] Implementar circuit breakers
- [ ] Agregar métricas con Micrometer
- [ ] Implementar procesamiento asíncrono

---

**Desarrollado con Java 21 y Spring Boot 3.5.7**

