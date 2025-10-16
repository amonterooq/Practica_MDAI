package com.nada.nada.data.repository;

import com.nada.nada.data.model.Prenda;
import org.springframework.data.repository.CrudRepository;

public interface PrendaRepository extends CrudRepository<Prenda, Long> {
    public Prenda findByNombre(String nombre);
}
