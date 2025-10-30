package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Direccion;
import com.example.demo.domain.Usuario;
import com.example.demo.repositories.DireccionRepository;
import com.example.demo.repositories.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	
	@Autowired //no necesario, para recordar que nos inyecta y crea el IoC de Spring
	public UsuarioServiceImpl (UsuarioRepository usuarioRepository, DireccionRepository direccionRepository) {
		System.out.println("\t UsuarioServiceImpl constructor ");

	}
	
	
}
