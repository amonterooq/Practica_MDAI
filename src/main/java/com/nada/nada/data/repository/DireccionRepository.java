package com.nada.nada.data.repository;

import com.nada.nada.data.model.Direccion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionRepository extends CrudRepository<Direccion, Long> {
    public Direccion findByName(String name);
}
