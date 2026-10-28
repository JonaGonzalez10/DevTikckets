package com.devtickets.devtickets_backend.repository;

import com.devtickets.devtickets_backend.model.Evento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    @Query("""
            select e from Evento e left join fetch e.categoria c
            where e.estado = 'PUBLICADO'
              and (:categoriaId is null or c.id = :categoriaId)
              and lower(e.nombre) like lower(concat('%', :q, '%'))
            order by e.nombre
            """)
    List<Evento> buscarPublicados(@Param("categoriaId") Long categoriaId, @Param("q") String q);

    @Query("select e from Evento e left join fetch e.categoria where e.id = :id and e.estado = 'PUBLICADO'")
    Optional<Evento> findPublicadoById(@Param("id") Long id);
}