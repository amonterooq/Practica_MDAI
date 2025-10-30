package com.example.demo.services;

import java.util.List;

import com.example.demo.domain.Usuario;


public interface UsuarioService {
	
	public List <Usuario> findAllUsers();

	public void crearUsuario(Usuario usuario);

}
