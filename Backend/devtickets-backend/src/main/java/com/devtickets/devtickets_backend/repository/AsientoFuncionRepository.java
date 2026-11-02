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
            select a from AsientoFuncion a join fetch a.tipoEntrada left join fetch a.reserva
            where a.funcion.id = :idFuncion
            order by a.seccion, a.fila, a.numero
            """)
    List<AsientoFuncion> mapaDeFuncion(@Param("idFuncion") Long idFuncion);

    @Query("select a from AsientoFuncion a join fetch a.tipoEntrada where a.id in :ids and a.funcion.id = :idFuncion")
    List<AsientoFuncion> findParaCompra(@Param("ids") Collection<Long> ids, @Param("idFuncion") Long idFuncion);

    @Query("""
            select a from AsientoFuncion a join fetch a.tipoEntrada
            where a.reserva.id = :idOrden
            order by a.seccion, a.fila, a.numero
            """)
    List<AsientoFuncion> findByReservaId(@Param("idOrden") Long idOrden);

    @Modifying
    @Query(value = """
            UPDATE asientos_funcion
            SET estado = 'RESERVADO', id_orden_reserva = :idOrden
            WHERE id_asiento IN (:ids) AND id_funcion = :idFuncion
              AND (estado = 'DISPONIBLE'
                OR (estado = 'RESERVADO' AND id_orden_reserva IN (
                  SELECT id_orden FROM ordenes_compra WHERE estado = 'EXPIRADA'
                )))
            """, nativeQuery = true)
    int reservarDisponibles(
            @Param("ids") Collection<Long> ids,
            @Param("idFuncion") Long idFuncion,
            @Param("idOrden") Long idOrden);

    @Modifying
    @Query(value = """
            UPDATE asientos_funcion
            SET estado = 'DISPONIBLE', id_orden_reserva = NULL
            WHERE estado = 'RESERVADO' AND id_orden_reserva IN (
              SELECT id_orden FROM ordenes_compra WHERE estado = 'EXPIRADA'
            )
            """, nativeQuery = true)
    int liberarReservasExpiradas();

    /** Cambia a vendidos solamente los asientos que siguen reservados para esta orden. */
    @Modifying
    @Query(value = """
            UPDATE asientos_funcion
            SET estado = 'VENDIDO', id_orden_reserva = NULL
            WHERE id_orden_reserva = :idOrden AND estado = 'RESERVADO'
            """, nativeQuery = true)
    int marcarVendidos(@Param("idOrden") Long idOrden);
}