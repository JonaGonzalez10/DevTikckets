package com.devtickets.devtickets_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "funciones_evento")
@Getter
@Setter
@NoArgsConstructor
public class FuncionEvento {

    public static final String ESTADO_PROGRAMADO = "PROGRAMADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evento")
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto")
    private Recinto recinto;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "inicio_venta")
    private LocalDateTime inicioVenta;

    @Column(name = "fin_venta")
    private LocalDateTime finVenta;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    public boolean ventaAbierta(LocalDateTime ahora) {
        return ESTADO_PROGRAMADO.equals(estado)
                && fechaInicio.isAfter(ahora)
                && (inicioVenta == null || !ahora.isBefore(inicioVenta))
                && (finVenta == null || !ahora.isAfter(finVenta));
    }
}
