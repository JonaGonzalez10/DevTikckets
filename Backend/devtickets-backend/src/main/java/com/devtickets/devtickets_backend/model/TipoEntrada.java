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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipos_entrada")
@Getter
@Setter
@NoArgsConstructor
public class TipoEntrada {

    public static final String ESTADO_ACTIVO = "ACTIVO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_entrada")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_funcion")
    private FuncionEvento funcion;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    @Column(name = "cantidad_total", nullable = false)
    private Integer cantidadTotal;

    @Column(name = "cantidad_vendida", nullable = false)
    private Integer cantidadVendida;

    @Column(name = "inicio_venta")
    private LocalDateTime inicioVenta;

    @Column(name = "fin_venta")
    private LocalDateTime finVenta;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    public int disponibles() {
        return Math.max(0, cantidadTotal - cantidadVendida);
    }

    public boolean ventaAbierta(LocalDateTime ahora) {
        return ESTADO_ACTIVO.equals(estado)
                && (inicioVenta == null || !ahora.isBefore(inicioVenta))
                && (finVenta == null || !ahora.isAfter(finVenta));
    }
}
