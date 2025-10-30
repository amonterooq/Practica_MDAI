package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.demo.domain.Usuario;
import com.example.demo.repositories.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private final UsuarioRepository usuarioRepository;	
	
	@Autowired //no necesario, para recordar que nos inyecta y crea el IoC de Spring
	public UsuarioServiceImpl (UsuarioRepository usuarioRepository) {
		System.out.println("\t UsuarioServiceImpl constructor ");		
		this.usuarioRepository=usuarioRepository;

	}

	//En la capa servicios es donde se implementa la LOGICA de negocio
	@Override
	public List<Usuario> findAllUsers() {
			
		// TODO Asi no es recomendable: Comprobar si hay usuarios, si esta vacio ... 
		return this.usuarioRepository.findAllWithDirecciones();
	}

	@Override
	public void crearUsuario(Usuario usuario) {
		// TODO Logica...
		usuarioRepository.save(usuario);
	}
	
	
}
