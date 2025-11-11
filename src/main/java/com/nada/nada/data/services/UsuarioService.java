package com.nada.nada.data.services;

import com.nada.nada.data.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    public List<Usuario> buscarTodosUsuarios();

    public void crearUsuario(Usuario usuario);
    public void actualizarUsuario(Usuario usuario);

    public Optional<Usuario> findUsuarioById(Long id);

    public void deleteUsuarioById(Long id);
}
