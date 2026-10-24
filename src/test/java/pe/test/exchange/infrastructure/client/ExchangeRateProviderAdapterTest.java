package pe.test.exchange.infrastructure.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pe.test.exchange.domain.exception.CurrencyNotFoundException;
import pe.test.exchange.infrastructure.external.ExchangeRateFeignClient;
import pe.test.exchange.infrastructure.external.ExchangeRateProviderAdapter;
import pe.test.exchange.infrastructure.external.dto.ExchangeRateApiResponse;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateProviderAdapterTest {

    @Mock
    private ExchangeRateFeignClient feignClient;

    @InjectMocks
    private ExchangeRateProviderAdapter adapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "apiKey", "test-api-key");
    }

    @Test
    @DisplayName("Debe retornar el tipo de cambio cuando la moneda destino existe")
    void shouldReturnExchangeRateWhenCurrencyExists() {
        ExchangeRateApiResponse mockResponse = new ExchangeRateApiResponse(
                "success",
                "USD",
                Map.of("PEN", new BigDecimal("3.70"))
        );

        when(feignClient.getLatestRates("test-api-key", "USD")).thenReturn(mockResponse);

        BigDecimal rate = adapter.getExchangeRate("USD", "PEN");

        assertEquals(new BigDecimal("3.70"), rate);
    }

    @Test
    @DisplayName("Debe lanzar CurrencyNotFoundException cuando la moneda destino no existe")
    void shouldThrowExceptionWhenTargetCurrencyNotFound() {
        ExchangeRateApiResponse mockResponse = new ExchangeRateApiResponse(
                "success",
                "USD",
                Map.of("EUR", new BigDecimal("0.92"))
        );

        when(feignClient.getLatestRates("test-api-key", "USD")).thenReturn(mockResponse);

        assertThrows(CurrencyNotFoundException.class, () ->
                adapter.getExchangeRate("USD", "PEN")
        );
    }
}
