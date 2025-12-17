package com.nada.nada.data.services;

import com.nada.nada.data.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para la gestión de usuarios.
 * Define las operaciones de negocio disponibles para usuarios.
 */
public interface UsuarioService {

    /**
     * Obtiene todos los usuarios del sistema.
     *
     * @return lista de todos los usuarios
     */
    List<Usuario> buscarTodos();

    /**
     * Crea un nuevo usuario en el sistema.
     * Valida los datos y crea la carpeta de imágenes asociada.
     *
     * @param usuario datos del usuario a crear
     * @return el usuario creado con su ID asignado
     * @throws IllegalArgumentException si los datos son inválidos o hay duplicados
     */
    Usuario crearUsuario(Usuario usuario);

    /**
     * Valida las credenciales de login.
     *
     * @param username nombre de usuario
     * @param password contraseña
     * @return Optional con el usuario si las credenciales son válidas, vacío si no
     */
    Optional<Usuario> validarLogin(String username, String password);

    /**
     * Cambia la contraseña de un usuario.
     *
     * @param usuarioId ID del usuario
     * @param oldPassword contraseña actual
     * @param newPassword nueva contraseña
     * @throws IllegalArgumentException si la contraseña actual es incorrecta
     */
    void cambiarPassword(Long usuarioId, String oldPassword, String newPassword);

    /**
     * Elimina un usuario y sus datos asociados (prendas, conjuntos, imágenes).
     *
     * @param usuarioId ID del usuario a eliminar
     */
    void eliminarUsuario(Long usuarioId);

    /**
     * Añade un like de un usuario a un post.
     *
     * @param usuarioId ID del usuario que da like
     * @param postId ID del post
     * @return true si se añadió el like, false si ya existía
     */
    boolean darLikeAPost(Long usuarioId, Long postId);

    /**
     * Quita un like de un usuario a un post.
     *
     * @param usuarioId ID del usuario
     * @param postId ID del post
     * @return true si se quitó el like, false si no existía
     */
    boolean quitarLikeDePost(Long usuarioId, Long postId);
}
