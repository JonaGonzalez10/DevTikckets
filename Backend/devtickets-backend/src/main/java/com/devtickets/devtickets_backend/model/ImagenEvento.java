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
@Table(name = "imagenes_evento")
@Getter
@Setter
@NoArgsConstructor
public class ImagenEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evento")
    private Evento evento;

    @Column(name = "url_imagen", nullable = false, length = 2048)
    private String urlImagen;

    @Column(name = "texto_alternativo")
    private String textoAlternativo;

    @Column(name = "orden_presentacion", nullable = false)
    private Integer ordenPresentacion;

    @Column(name = "es_principal", nullable = false)
    private boolean esPrincipal;
}
