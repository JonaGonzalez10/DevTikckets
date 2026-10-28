package com.devtickets.devtickets_backend.repository;

import com.devtickets.devtickets_backend.model.OrdenCompra;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    List<OrdenCompra> findByUsuarioIdOrderByCreadoEnDesc(Long idUsuario);
}