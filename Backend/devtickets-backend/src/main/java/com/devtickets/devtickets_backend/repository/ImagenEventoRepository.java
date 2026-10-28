package com.devtickets.devtickets_backend.repository;

import com.devtickets.devtickets_backend.model.ImagenEvento;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenEventoRepository extends JpaRepository<ImagenEvento, Long> {

    List<ImagenEvento> findByEventoIdInAndEsPrincipalTrue(Collection<Long> idsEvento);
}