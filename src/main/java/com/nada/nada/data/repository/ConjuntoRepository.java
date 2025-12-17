package com.nada.nada.data.repository;

import com.nada.nada.data.model.Conjunto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repositorio para operaciones CRUD de la entidad Conjunto.
 * Proporciona consultas para búsqueda de conjuntos por usuario.
 */
public interface ConjuntoRepository extends CrudRepository<Conjunto, Long> {

    /**
     * Busca un conjunto por su nombre exacto.
     *
     * @param nombre nombre del conjunto
     * @return el conjunto si existe, null si no
     */
    Conjunto findByNombre(String nombre);

    /**
     * Cuenta el número de conjuntos de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return número de conjuntos
     */
    long countByUsuario_Id(Long usuarioId);

    /**
     * Obtiene todos los conjuntos de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de conjuntos del usuario
     */
    List<Conjunto> findByUsuario_Id(Long usuarioId);
}