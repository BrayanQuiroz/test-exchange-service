package pe.test.exchange.application.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ConversionRequest(
        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @NotBlank(message = "La moneda origen es obligatoria")
        @Size(min = 3, max = 3, message = "La moneda origen debe tener 3 caracteres ISO (ej. USD)")
        String monedaOrigen,

        @NotBlank(message = "La moneda destino es obligatoria")
        @Size(min = 3, max = 3, message = "La moneda destino debe tener 3 caracteres ISO (ej. PEN)")
        String monedaDestino
) {}
