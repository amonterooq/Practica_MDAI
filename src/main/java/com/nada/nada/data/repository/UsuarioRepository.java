package com.nada.nada.data.repository;

import com.nada.nada.data.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    public Usuario findByUsername(String username);
}
