# Servicio de Tipo de Cambio (test-exchange-service)

Microservicio desarrollado con **Java 17+** y **Spring Boot** que consume tasas de cambio en tiempo real desde **ExchangeRate-API** mediante **Spring Cloud OpenFeign**, ejecuta el cálculo de conversión monetaria y registra la auditoría de cada operación en una base de datos en memoria **H2**.

## Arquitectura y Diseño (DDD)

El proyecto implementa **Domain-Driven Design (DDD)** y **Arquitectura Hexagonal (Puertos y Adaptadores)** organizado bajo el paquete base `pe.test.exchange`:

```text
pe.test.exchange
├── domain
│   ├── model          (Entidad de dominio ExchangeConversion con lógica de negocio y redondeo)
│   ├── repository     (Puerto de salida: ExchangeConversionRepository)
│   ├── service        (Puerto de salida: ExchangeRateProvider)
│   └── exception      (Excepciones de negocio: CurrencyNotFoundException)
├── application
│   ├── controller     (Exposición REST: ConversionController)
│   ├── service        (Caso de uso: ConversionApplicationService)
│   ├── dto            (Records ConversionRequest y ConversionResponse con Jakarta Validation)
│   └── mapper         (Transformación Dominio <-> DTO)
└── infrastructure
    ├── external       (Adaptador OpenFeign: ExchangeRateFeignClient y ExchangeRateProviderAdapter)
    ├── persistence    (Adaptador JPA/H2: ConversionEntity y ExchangeConversionRepositoryAdapter)
    └── config         (Configuración de Feign y manejo global de errores con ProblemDetail RFC 7807)
```

## Stack Tecnológico

* **Lenguaje:** Java 17+
* **Framework:** Spring Boot (Web, Data JPA, Validation)
* **Cliente HTTP:** Spring Cloud OpenFeign
* **Base de datos:** H2 Database (En memoria)
* **Manejo de Errores:** Estándar RFC 7807 (`ProblemDetail`)
* **Testing:** JUnit 5 y Mockito

## Instalación y Ejecución

1. Clonar el repositorio y ubicarse en la carpeta raíz:
   ```bash
   git clone <url-de-tu-repositorio>
   cd test-exchange-service
   ```

2. Compilar y levantar el servicio:
   ```bash
   mvn spring-boot:run
   ```
   La aplicación iniciará en `http://localhost:8080`.

---

## Ejecución de Pruebas Unitarias (JUnit 5 + Mockito)

El proyecto cuenta con pruebas unitarias aisladas por capa para garantizar la calidad del código sin depender de servicios externos ni levantar el contexto completo de base de datos:

* **`ExchangeConversionTest` (Capa Domain):** Valida el cálculo exacto del monto convertido, la normalización de códigos ISO a mayúsculas y el redondeo a 2 decimales (`RoundingMode.HALF_UP`).
* **`ConversionApplicationServiceTest` (Capa Application):** Utiliza `@Mock` de Mockito para simular el puerto de Feign y el repositorio, verificando la orquestación del caso de uso.
* **`ExchangeRateProviderAdapterTest` (Capa Infrastructure):** Simula las respuestas de `ExchangeRateFeignClient` y comprueba que se lance `CurrencyNotFoundException` cuando se solicita una divisa inexistente.

### Comandos para ejecutar los tests:

1. **Ejecutar toda la suite de pruebas:**
   ```bash
   mvn clean test
   ```

2. **Ejecutar una clase de prueba específica:**
   ```bash
   mvn test -Dtest=ExchangeConversionTest
   mvn test -Dtest=ConversionApplicationServiceTest
   mvn test -Dtest=ExchangeRateProviderAdapterTest
   ```

3. **Desde el IDE (IntelliJ IDEA):**
   Haz clic derecho sobre la carpeta `src/test/java` y selecciona **`Run 'All Tests'`**.

---

## Cómo Probar la API con la Colección de Postman (`.json`)

En el proyecto se incluye el archivo **`exchange-service.postman_collection.json`** con las peticiones listas para probar tanto el flujo exitoso como el manejo de errores.

### Pasos para importar y usar en Postman:
1. Abre **Postman** y haz clic en el botón **Import** (esquina superior izquierda).
2. Arrastra el archivo **`exchange-service.postman_collection.json`** (o selecciónalo desde tu explorador de archivos).
3. En el panel izquierdo verás la colección **"Test Exchange Service API"** (la variable `{{baseUrl}}` ya viene preconfigurada apuntando a `http://localhost:8080`).
4. Con la aplicación corriendo, ejecuta cualquiera de las 3 peticiones incluidas:

#### Petición 1: `1. Convertir Moneda - Exitoso (USD a PEN)` (`200 OK`)
* **Endpoint:** `POST http://localhost:8080/api/v1/exchange/convert`
* **Body enviado:**
  ```json
  {
    "monto": 100,
    "monedaOrigen": "USD",
    "monedaDestino": "PEN"
  }
  ```
* **Respuesta esperada:**
  ```json
  {
    "monto": 100,
    "montoConvertido": 370.00,
    "monedaOrigen": "USD",
    "monedaDestino": "PEN",
    "tipoCambio": 3.70
  }
  ```

#### Petición 2: `2. Error 400 - Validación (Monto negativo y código inválido)` (`400 Bad Request`)
Prueba las validaciones de entrada (`@NotNull`, `@Positive`, `@NotBlank`, `@Size`) y la respuesta estandarizada con `ProblemDetail`:
```json
{
  "type": "about:blank",
  "title": "Error de validación",
  "status": 400,
  "detail": "La validación falló en uno o más campos",
  "instance": "/api/v1/exchange/convert",
  "error": "Validation Error",
  "path": "/api/v1/exchange/convert",
  "errors": {
    "monto": "El monto debe ser mayor a cero",
    "monedaOrigen": "La moneda origen debe tener 3 caracteres ISO (ej. USD)",
    "monedaDestino": "La moneda destino es obligatoria"
  }
}
```

#### Petición 3: `3. Error 400 - Moneda Destino No Soportada` (`400 Bad Request`)
Prueba la excepción de negocio (`CurrencyNotFoundException`) cuando se envía una moneda inexistente (`XYZ`):
```json
{
  "type": "about:blank",
  "title": "Moneda no soportada",
  "status": 400,
  "detail": "Moneda destino no soportada: XYZ",
  "instance": "/api/v1/exchange/convert",
  "error": "Currency Not Found",
  "path": "/api/v1/exchange/convert"
}
```

---

## Verificación de Persistencia en H2

Después de ejecutar la **Petición 1** en Postman, puedes comprobar que la conversión se guardó en la base de datos en memoria:

1. Abre en el navegador: `http://localhost:8080/h2-console`
2. Ingresa las credenciales configuradas en `application.yml`:
    * **Driver Class:** `org.h2.Driver`
    * **JDBC URL:** `jdbc:h2:mem:testdb`
    * **User Name:** `sa`
    * **Password:** `password`
3. Puede usar ejemplo.json en el postman