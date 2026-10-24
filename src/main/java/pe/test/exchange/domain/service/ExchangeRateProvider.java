package pe.test.exchange.domain.service;

import java.math.BigDecimal;

public interface ExchangeRateProvider {
    BigDecimal getExchangeRate(String monedaOrigen, String monedaDestino);
}
