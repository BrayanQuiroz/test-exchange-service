package pe.test.exchange.infrastructure.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.test.exchange.domain.exception.CurrencyNotFoundException;
import pe.test.exchange.domain.service.ExchangeRateProvider;
import pe.test.exchange.infrastructure.external.dto.ExchangeRateApiResponse;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExchangeRateProviderAdapter implements ExchangeRateProvider {

    private final ExchangeRateFeignClient feignClient;

    @Value("${exchange-rate.api.key}")
    private String apiKey;

    @Override
    public BigDecimal getExchangeRate(String monedaOrigen, String monedaDestino) {
        String origen = monedaOrigen.toUpperCase();
        String destino = monedaDestino.toUpperCase();

        ExchangeRateApiResponse response = feignClient.getLatestRates(apiKey, origen);

        if (response == null || response.conversionRates() == null) {
            throw new CurrencyNotFoundException("No se encontraron tasas de cambio para la moneda origen: " + origen);
        }

        Map<String, BigDecimal> rates = response.conversionRates();
        BigDecimal rate = rates.get(destino);

        if (rate == null) {
            throw new CurrencyNotFoundException("Moneda destino no soportada: " + destino);
        }

        return rate;
    }
}
