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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asientos_funcion")
@Getter
@Setter
@NoArgsConstructor
public class AsientoFuncion {

    public static final String ESTADO_DISPONIBLE = "DISPONIBLE";
    public static final String ESTADO_VENDIDO = "VENDIDO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asiento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_funcion")
    private FuncionEvento funcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_entrada")
    private TipoEntrada tipoEntrada;

    @Column(name = "seccion", nullable = false, length = 50)
    private String seccion;

    @Column(name = "fila", nullable = false, length = 10)
    private String fila;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
