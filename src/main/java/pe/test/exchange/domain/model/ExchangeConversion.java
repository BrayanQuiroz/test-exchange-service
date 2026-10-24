package pe.test.exchange.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class ExchangeConversion {

    private Long id;
    private final BigDecimal monto;
    private final String monedaOrigen;
    private final String monedaDestino;
    private final BigDecimal tipoCambio;
    private final BigDecimal montoConvertido;
    private final LocalDateTime fechaOperacion;

    private ExchangeConversion(Long id, BigDecimal monto, String monedaOrigen, String monedaDestino,
                               BigDecimal tipoCambio, BigDecimal montoConvertido, LocalDateTime fechaOperacion) {
        this.id = id;
        this.monto = monto;
        this.monedaOrigen = monedaOrigen.toUpperCase();
        this.monedaDestino = monedaDestino.toUpperCase();
        this.tipoCambio = tipoCambio;
        this.montoConvertido = montoConvertido;
        this.fechaOperacion = fechaOperacion;
    }

    // Factory method de dominio: ejecuta el cálculo de negocio
    public static ExchangeConversion createNew(BigDecimal monto, String monedaOrigen, String monedaDestino, BigDecimal tipoCambio) {
        BigDecimal convertido = monto.multiply(tipoCambio).setScale(2, RoundingMode.HALF_UP);
        return new ExchangeConversion(null, monto, monedaOrigen, monedaDestino, tipoCambio, convertido, LocalDateTime.now());
    }

    // Reconstrucción desde base de datos
    public static ExchangeConversion reconstruct(Long id, BigDecimal monto, String monedaOrigen, String monedaDestino,
                                                 BigDecimal tipoCambio, BigDecimal montoConvertido, LocalDateTime fechaOperacion) {
        return new ExchangeConversion(id, monto, monedaOrigen, monedaDestino, tipoCambio, montoConvertido, fechaOperacion);
    }

    public Long getId() { return id; }
    public BigDecimal getMonto() { return monto; }
    public String getMonedaOrigen() { return monedaOrigen; }
    public String getMonedaDestino() { return monedaDestino; }
    public BigDecimal getTipoCambio() { return tipoCambio; }
    public BigDecimal getMontoConvertido() { return montoConvertido; }
    public LocalDateTime getFechaOperacion() { return fechaOperacion; }
}
