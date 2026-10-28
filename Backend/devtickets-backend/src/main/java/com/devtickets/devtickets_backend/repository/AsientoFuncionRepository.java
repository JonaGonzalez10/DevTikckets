package com.devtickets.devtickets_backend.repository;

import com.devtickets.devtickets_backend.model.AsientoFuncion;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AsientoFuncionRepository extends JpaRepository<AsientoFuncion, Long> {

    @Query("""
            select a from AsientoFuncion a join fetch a.tipoEntrada
            where a.funcion.id = :idFuncion
            order by a.seccion, a.fila, a.numero
            """)
    List<AsientoFuncion> mapaDeFuncion(@Param("idFuncion") Long idFuncion);

    @Query("select a from AsientoFuncion a join fetch a.tipoEntrada where a.id in :ids and a.funcion.id = :idFuncion")
    List<AsientoFuncion> findParaCompra(@Param("ids") Collection<Long> ids, @Param("idFuncion") Long idFuncion);

    /** Marca como vendidos solo los que siguen disponibles; evita doble venta bajo concurrencia. */
    @Modifying
    @Query("update AsientoFuncion a set a.estado = 'VENDIDO' where a.id in :ids and a.estado = 'DISPONIBLE'")
    int marcarVendidos(@Param("ids") Collection<Long> ids);
}