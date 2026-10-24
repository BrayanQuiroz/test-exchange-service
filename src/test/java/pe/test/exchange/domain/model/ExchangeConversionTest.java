package pe.test.exchange.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

class ExchangeConversionTest {

    @Test
    @DisplayName("Debe calcular correctamente el monto convertido y normalizar monedas a mayúsculas")
    void shouldCreateNewConversionAndCalculateAmount() {
        BigDecimal monto = new BigDecimal("100");
        BigDecimal tipoCambio = new BigDecimal("3.70");

        ExchangeConversion conversion = ExchangeConversion.createNew(monto, "usd", "pen", tipoCambio);

        assertNotNull(conversion);
        assertEquals(new BigDecimal("100"), conversion.getMonto());
        assertEquals("USD", conversion.getMonedaOrigen());
        assertEquals("PEN", conversion.getMonedaDestino());
        assertEquals(new BigDecimal("3.70"), conversion.getTipoCambio());
        assertEquals(new BigDecimal("370.00"), conversion.getMontoConvertido());
        assertNotNull(conversion.getFechaOperacion());
    }

    @Test
    @DisplayName("Debe redondear el monto convertido a 2 decimales usando HALF_UP")
    void shouldRoundConvertedAmountToTwoDecimals() {
        BigDecimal monto = new BigDecimal("100.50");
        BigDecimal tipoCambio = new BigDecimal("3.756"); // 100.50 * 3.756 = 377.478 -> 377.48

        ExchangeConversion conversion = ExchangeConversion.createNew(monto, "USD", "PEN", tipoCambio);

        assertEquals(new BigDecimal("377.48"), conversion.getMontoConvertido());
    }
}
