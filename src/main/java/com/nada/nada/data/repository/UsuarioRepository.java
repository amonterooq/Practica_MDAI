package com.nada.nada.data.repository;

import com.nada.nada.data.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de la entidad Usuario.
 * Extiende CrudRepository proporcionando métodos básicos de persistencia
 * y añade consultas personalizadas para búsqueda y validación.
 */
@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario a buscar
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email email a verificar
     * @return true si ya existe un usuario con ese email
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por su email.
     *
     * @param email email a buscar
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado.
     *
     * @param username nombre de usuario a verificar
     * @return true si ya existe un usuario con ese nombre
     */
    boolean existsByUsername(String username);
}
