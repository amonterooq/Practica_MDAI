package com.nada.nada.data.repository;

import com.nada.nada.data.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Usuario findByUsername(String username);

    // Añadidos para soporte en la capa servicio
    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);
}
