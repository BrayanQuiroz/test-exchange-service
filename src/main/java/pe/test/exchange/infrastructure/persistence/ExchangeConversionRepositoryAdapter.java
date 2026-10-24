package pe.test.exchange.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.test.exchange.domain.model.ExchangeConversion;
import pe.test.exchange.domain.repository.ExchangeConversionRepository;
import pe.test.exchange.infrastructure.persistence.entity.ConversionEntity;
import pe.test.exchange.infrastructure.persistence.repository.SpringDataConversionRepository;

@Component
@RequiredArgsConstructor
public class ExchangeConversionRepositoryAdapter implements ExchangeConversionRepository {

    private final SpringDataConversionRepository jpaRepository;


    @Override
    public ExchangeConversion save(ExchangeConversion domain) {
        ConversionEntity entity = ConversionEntity.builder()
                .id(domain.getId())
                .montoOriginal(domain.getMonto())
                .monedaOrigen(domain.getMonedaOrigen())
                .monedaDestino(domain.getMonedaDestino())
                .tipoCambio(domain.getTipoCambio())
                .montoConvertido(domain.getMontoConvertido())
                .fechaOperacion(domain.getFechaOperacion())
                .build();

        ConversionEntity saved = jpaRepository.save(entity);

        return ExchangeConversion.reconstruct(
                saved.getId(),
                saved.getMontoOriginal(),
                saved.getMonedaOrigen(),
                saved.getMonedaDestino(),
                saved.getTipoCambio(),
                saved.getMontoConvertido(),
                saved.getFechaOperacion()
        );
    }
}
