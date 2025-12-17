package com.nada.nada.data.services;

import com.nada.nada.data.model.Conjunto;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para la gestión de conjuntos de ropa.
 * Define las operaciones CRUD para conjuntos.
 */
public interface ConjuntoService {

    /**
     * Guarda un conjunto (nuevo o existente).
     *
     * @param conjunto conjunto a guardar
     * @return el conjunto guardado
     */
    Conjunto guardarConjunto(Conjunto conjunto);

    /**
     * Busca un conjunto por su ID.
     *
     * @param id ID del conjunto
     * @return Optional con el conjunto si existe
     */
    Optional<Conjunto> buscarConjuntoPorId(Long id);

    /**
     * Obtiene todos los conjuntos de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de conjuntos ordenados por fecha (más reciente primero)
     */
    List<Conjunto> buscarConjuntosPorUsuarioId(Long usuarioId);

    /**
     * Elimina un conjunto.
     *
     * @param id ID del conjunto a eliminar
     * @return true si se eliminó correctamente
     */
    boolean borrarConjunto(Long id);

    /**
     * Busca los conjuntos que contienen una prenda específica.
     * Útil para advertir al usuario antes de eliminar una prenda.
     *
     * @param prendaId ID de la prenda
     * @return lista de conjuntos que la contienen
     */
    List<Conjunto> buscarConjuntosConPrenda(Long prendaId);
}
