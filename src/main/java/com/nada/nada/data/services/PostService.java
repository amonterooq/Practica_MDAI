package com.nada.nada.data.services;

import com.nada.nada.data.model.Post;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para la gestión de publicaciones (posts).
 * Define las operaciones CRUD y de interacción social para posts.
 */
public interface PostService {

    /**
     * Obtiene todos los posts con sus likes cargados.
     *
     * @return lista de todos los posts
     */
    List<Post> buscarTodos();

    /**
     * Busca un post por su ID.
     *
     * @param id ID del post
     * @return Optional con el post si existe
     */
    Optional<Post> buscarPorId(Long id);

    /**
     * Crea un nuevo post.
     *
     * @param post post a crear
     * @return el post creado
     */
    Post crearPost(Post post);

    /**
     * Elimina un post y sus likes asociados.
     *
     * @param id ID del post a eliminar
     */
    void eliminarPost(Long id);

    /**
     * Obtiene todos los posts de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de posts del usuario
     */
    List<Post> buscarPostsPorUsuario(Long usuarioId);

    /**
     * Cuenta el número de likes de un post.
     *
     * @param postId ID del post
     * @return número de likes
     */
    int contarLikes(Long postId);

    /**
     * Verifica si un usuario ha dado like a un post.
     *
     * @param postId ID del post
     * @param usuarioId ID del usuario
     * @return true si el usuario ha dado like
     */
    boolean usuarioHaDadoLike(Long postId, Long usuarioId);

    /**
     * Elimina todos los likes de un post.
     * Necesario antes de eliminar un post por orphanRemoval.
     *
     * @param postId ID del post
     */
    void eliminarLikesDelPost(Long postId);
}