package com.devtickets.devtickets_backend.repository;

import com.devtickets.devtickets_backend.model.OrdenCompra;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    List<OrdenCompra> findByUsuarioIdOrderByCreadoEnDesc(Long idUsuario);

    @Modifying
    @Query("""
            update OrdenCompra o set o.estado = 'EXPIRADA'
            where o.estado = 'PENDIENTE' and o.expiraEn <= :ahora
            """)
    int expirarPendientes(@Param("ahora") LocalDateTime ahora);
}