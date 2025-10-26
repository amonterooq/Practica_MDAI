package com.nada.nada.data.repository;

import com.nada.nada.data.model.PrendaInferior;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrendaInferiorRepository extends CrudRepository<PrendaInferior, Long> {
}
