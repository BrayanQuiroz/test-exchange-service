package pe.test.exchange.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.test.exchange.application.dto.ConversionRequest;
import pe.test.exchange.application.dto.ConversionResponse;
import pe.test.exchange.application.mapper.ConversionDtoMapper;
import pe.test.exchange.domain.model.ExchangeConversion;
import pe.test.exchange.domain.repository.ExchangeConversionRepository;
import pe.test.exchange.domain.service.ExchangeRateProvider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionApplicationServiceTest {

    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    @Mock
    private ExchangeConversionRepository conversionRepository;

    private ConversionApplicationService applicationService;

    @BeforeEach
    void setUp() {
        ConversionDtoMapper mapper = new ConversionDtoMapper();
        applicationService = new ConversionApplicationService(exchangeRateProvider, conversionRepository, mapper);
    }

    @Test
    @DisplayName("Debe obtener tasa, calcular conversión, guardar en BD y retornar DTO")
    void shouldConvertCurrencySuccessfully() {
        // Arrange
        ConversionRequest request = new ConversionRequest(
                new BigDecimal("100"), "USD", "PEN"
        );
        BigDecimal mockRate = new BigDecimal("3.70");

        when(exchangeRateProvider.getExchangeRate("USD", "PEN")).thenReturn(mockRate);
        when(conversionRepository.save(any(ExchangeConversion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ConversionResponse response = applicationService.convertCurrency(request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("100"), response.monto());
        assertEquals(new BigDecimal("370.00"), response.montoConvertido());
        assertEquals("USD", response.monedaOrigen());
        assertEquals("PEN", response.monedaDestino());
        assertEquals(new BigDecimal("3.70"), response.tipoCambio());

        verify(exchangeRateProvider, times(1)).getExchangeRate("USD", "PEN");
        verify(conversionRepository, times(1)).save(any(ExchangeConversion.class));
    }
}