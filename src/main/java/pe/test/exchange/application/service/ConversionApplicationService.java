package pe.test.exchange.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.test.exchange.application.dto.ConversionRequest;
import pe.test.exchange.application.dto.ConversionResponse;
import pe.test.exchange.application.mapper.ConversionDtoMapper;
import pe.test.exchange.domain.model.ExchangeConversion;
import pe.test.exchange.domain.repository.ExchangeConversionRepository;
import pe.test.exchange.domain.service.ExchangeRateProvider;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ConversionApplicationService {

    private final ExchangeRateProvider exchangeRateProvider;
    private final ExchangeConversionRepository conversionRepository;
    private final ConversionDtoMapper mapper;

    @Transactional
    public ConversionResponse convertCurrency(ConversionRequest request) {
        BigDecimal rate = exchangeRateProvider.getExchangeRate(
                request.monedaOrigen(),
                request.monedaDestino()
        );
        ExchangeConversion conversion = ExchangeConversion.createNew(
                request.monto(),
                request.monedaOrigen(),
                request.monedaDestino(),
                rate
        );
        ExchangeConversion savedConversion = conversionRepository.save(conversion);
        return mapper.toResponse(savedConversion);
    }
}
