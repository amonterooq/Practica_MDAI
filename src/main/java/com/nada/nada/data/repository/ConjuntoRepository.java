package com.nada.nada.data.repository;

import com.nada.nada.data.model.Conjunto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConjuntoRepository extends CrudRepository<Conjunto, Long> {
    public Conjunto findByNombre(String nombre);
    public long countByUsuario_Id(Long usuarioId);

    // Añadido para permitir obtener los conjuntos de un usuario desde el servicio
    List<Conjunto> findByUsuario_Id(Long usuarioId);
}