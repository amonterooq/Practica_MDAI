package com.nada.nada.data.services;

import com.nada.nada.data.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    public List<Usuario> buscarTodos();
    public Usuario crearUsuario(Usuario usuario);
    public Optional<Usuario> encontrarPorId(Long id);
    public Optional<Usuario> validarLogin(String username, String password);
    public void eliminarPorId(Long id);
}
