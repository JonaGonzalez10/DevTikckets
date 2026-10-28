package com.devtickets.devtickets_backend.repository;

import com.devtickets.devtickets_backend.model.TipoEntrada;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TipoEntradaRepository extends JpaRepository<TipoEntrada, Long> {

    List<TipoEntrada> findByFuncionIdInOrderByPrecioDesc(Collection<Long> idsFuncion);

    /** Incremento atomico: devuelve 0 si ya no hay cupo suficiente. */
    @Modifying
    @Query("""
            update TipoEntrada t set t.cantidadVendida = t.cantidadVendida + :cantidad
            where t.id = :id and t.cantidadVendida + :cantidad <= t.cantidadTotal
            """)
    int incrementarVendidas(@Param("id") Long id, @Param("cantidad") int cantidad);
}