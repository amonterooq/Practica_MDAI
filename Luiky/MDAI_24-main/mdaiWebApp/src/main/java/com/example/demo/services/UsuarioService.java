package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import com.example.demo.domain.Usuario;


public interface UsuarioService {
	
	public List <Usuario> findAllUsers();

	public void crearUsuario(Usuario usuario);
	public void actualizarUsuario(Usuario usuario);

	public Optional <Usuario> findUsuarioById(Long id);

	public void deleteUsuarioById(Long id);

}
