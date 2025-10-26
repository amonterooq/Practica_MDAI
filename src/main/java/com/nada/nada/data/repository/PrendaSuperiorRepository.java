package com.nada.nada.data.repository;

import com.nada.nada.data.model.PrendaSuperior;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrendaSuperiorRepository extends CrudRepository<PrendaSuperior, Long> {
}
