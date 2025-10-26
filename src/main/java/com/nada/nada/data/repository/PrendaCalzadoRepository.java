package com.nada.nada.data.repository;

import com.nada.nada.data.model.PrendaCalzado;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrendaCalzadoRepository extends CrudRepository<PrendaCalzado, Long> {
}
