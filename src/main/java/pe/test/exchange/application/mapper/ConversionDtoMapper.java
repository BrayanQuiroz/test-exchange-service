package pe.test.exchange.application.mapper;

import org.springframework.stereotype.Component;
import pe.test.exchange.application.dto.ConversionResponse;
import pe.test.exchange.domain.model.ExchangeConversion;

@Component
public class ConversionDtoMapper {

    public ConversionResponse toResponse(ExchangeConversion domain) {
        return new ConversionResponse(
                domain.getMonto(),
                domain.getMontoConvertido(),
                domain.getMonedaOrigen(),
                domain.getMonedaDestino(),
                domain.getTipoCambio()
        );
    }
}
