package com.nada.nada.data.repository;

import com.nada.nada.data.model.Prenda;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio para operaciones CRUD de la entidad Prenda.
 * Proporciona consultas para búsqueda y filtrado de prendas.
 */
public interface PrendaRepository extends CrudRepository<Prenda, Long> {

    /**
     * Busca una prenda por su nombre exacto.
     *
     * @param nombre nombre de la prenda
     * @return la prenda si existe, null si no
     */
    public Prenda findByNombre(String nombre);

    /**
     * Busca una prenda por su ID (método legacy).
     *
     * @param id ID de la prenda
     * @return la prenda si existe, null si no
     */
    public Prenda findById(long id);

    /**
     * Cuenta el número de prendas de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return número de prendas
     */
    public long countByUsuario_Id(long usuarioId);

    /**
     * Busca prendas de un usuario que contengan el color especificado.
     */
    public List<Prenda> findAllByUsuario_IdAndColorContainingIgnoreCase(Long usuarioId, String color);

    /**
     * Busca prendas de un usuario que contengan la marca especificada.
     */
    public List<Prenda> findAllByUsuario_IdAndMarcaContainingIgnoreCase(Long usuarioId, String marca);

    /**
     * Busca prendas de un usuario con la talla exacta especificada.
     */
    public List<Prenda> findAllByUsuario_IdAndTallaIgnoreCase(Long usuarioId, String talla);
    
    /**
     * Obtiene todas las marcas únicas de las prendas de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de marcas únicas ordenadas alfabéticamente
     */
    @Query("SELECT DISTINCT p.marca FROM Prenda p WHERE p.usuario.id = :usuarioId ORDER BY p.marca")
    List<String> findDistinctMarcasByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    /**
     * Obtiene todos los colores únicos de las prendas de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de colores únicos ordenados alfabéticamente
     */
    @Query("SELECT DISTINCT p.color FROM Prenda p WHERE p.usuario.id = :usuarioId ORDER BY p.color")
    List<String> findDistinctColoresByUsuarioId(@Param("usuarioId") Long usuarioId);
}
