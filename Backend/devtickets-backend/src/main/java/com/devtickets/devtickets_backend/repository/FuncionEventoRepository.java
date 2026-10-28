package com.devtickets.devtickets_backend.repository;

import com.devtickets.devtickets_backend.model.FuncionEvento;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FuncionEventoRepository extends JpaRepository<FuncionEvento, Long> {

    @Query("""
            select f from FuncionEvento f join fetch f.recinto
            where f.evento.id in :idsEvento and f.estado = 'PROGRAMADO' and f.fechaInicio > :ahora
            order by f.fechaInicio
            """)
    List<FuncionEvento> proximasPorEventos(
            @Param("idsEvento") Collection<Long> idsEvento, @Param("ahora") LocalDateTime ahora);

    @Query("select f from FuncionEvento f join fetch f.evento join fetch f.recinto where f.id = :id")
    Optional<FuncionEvento> findDetalleById(@Param("id") Long id);
}