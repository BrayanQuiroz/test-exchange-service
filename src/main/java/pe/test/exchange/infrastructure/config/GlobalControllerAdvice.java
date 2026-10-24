package pe.test.exchange.infrastructure.config;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import pe.test.exchange.domain.exception.CurrencyNotFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {

    // Logger nativo de Spring Boot (no requiere Lombok ni dependencias extra)
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ProblemDetail handleCurrencyNotFoundException(CurrencyNotFoundException ex, WebRequest request) {
        log.warn("Moneda no encontrada: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Moneda no soportada");
        problemDetail.setProperty("error", "Currency Not Found");
        problemDetail.setProperty("path", extractPath(request));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Error de validación en la petición: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "La validación falló en uno o más campos");

        problemDetail.setTitle("Error de validación");
        problemDetail.setProperty("error", "Validation Error");
        problemDetail.setProperty("path", extractPath(request));
        problemDetail.setProperty("timestamp", Instant.now());

        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMap.put(error.getField(), error.getDefaultMessage())
        );
        problemDetail.setProperty("errors", errorMap);

        return problemDetail;
    }

    @ExceptionHandler(FeignException.class)
    public ProblemDetail handleFeignException(FeignException ex, WebRequest request) {
        log.error("Error al consumir ExchangeRate-API. Status: {}, Mensaje: {}", ex.status(), ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
                "Error en proveedor externo: " + ex.getMessage());
        problemDetail.setTitle("Error en servicio externo");
        problemDetail.setProperty("error", "External API Error");
        problemDetail.setProperty("path", extractPath(request));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, WebRequest request) {
        // Imprime todo el stacktrace en la consola de IntelliJ
        log.error("Error inesperado en el servidor: ", ex);

        // Envía el nombre de la excepción y el mensaje exacto en el JSON de Postman
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getClass().getName() + " -> " + ex.getMessage()
        );
        problemDetail.setTitle("Error interno del servidor");
        problemDetail.setProperty("error", "Internal Server Error");
        problemDetail.setProperty("rootCause", ex.getCause() != null ? ex.getCause().toString() : "Sin causa raíz adicional");
        problemDetail.setProperty("path", extractPath(request));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}