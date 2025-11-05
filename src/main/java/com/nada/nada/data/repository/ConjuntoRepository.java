package com.nada.nada.data.repository;

import com.nada.nada.data.model.Conjunto;
import org.springframework.data.repository.CrudRepository;

public interface ConjuntoRepository extends CrudRepository<Conjunto, Long> {
    public Conjunto findByNombre(String nombre);
    public long countByUsuario_Id(Long usuarioId);
}
