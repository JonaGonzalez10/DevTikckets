package com.devtickets.devtickets_backend.repository;

import com.devtickets.devtickets_backend.model.Pago;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findFirstByOrdenIdOrderByIdDesc(Long idOrden);
}