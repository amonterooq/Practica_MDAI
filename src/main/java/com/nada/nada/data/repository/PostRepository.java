package com.nada.nada.data.repository;

import com.nada.nada.data.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de la entidad Post.
 * Incluye consultas optimizadas con JOIN FETCH para cargar likes.
 */
public interface PostRepository extends CrudRepository<Post, Long> {

    /**
     * Busca un post por su ID (método legacy).
     *
     * @param id ID del post
     * @return el post si existe, null si no
     */
    Post findById(long id);

    /**
     * Obtiene todos los posts con sus likes precargados.
     * Usa LEFT JOIN FETCH para evitar el problema N+1.
     *
     * @return lista de todos los posts con likes cargados
     */
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.usuariosQueDieronLike")
    List<Post> findAllWithLikes();

    /**
     * Busca un post por ID con sus likes precargados.
     * Usa LEFT JOIN FETCH para cargar los usuarios que dieron like.
     *
     * @param id ID del post
     * @return Optional con el post y sus likes si existe
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.usuariosQueDieronLike WHERE p.id = :id")
    Optional<Post> findByIdWithLikes(@Param("id") Long id);
}