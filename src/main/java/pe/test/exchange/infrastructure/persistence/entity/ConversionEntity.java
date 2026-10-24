package pe.test.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversiones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal montoOriginal;

    @Column(nullable = false, length = 3)
    private String monedaOrigen;

    @Column(nullable = false, length = 3)
    private String monedaDestino;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal tipoCambio;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montoConvertido;

    @Column(nullable = false)
    private LocalDateTime fechaOperacion;
}
