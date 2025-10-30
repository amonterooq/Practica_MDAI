package com.example.demo.commandLineRunner;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.domain.Direccion;
import com.example.demo.domain.Usuario;
import com.example.demo.repositories.DireccionRepository;
import com.example.demo.repositories.UsuarioRepository;


@Component
public class MyCommandLineRunner implements CommandLineRunner {
	
	private final UsuarioRepository usuarioRepository;
	private final DireccionRepository direccionRepository;
	

	public MyCommandLineRunner(UsuarioRepository usuarioRepository, DireccionRepository direccionRepository) {
		// TODO Auto-generated constructor stub
		//System.out.println("\t MyCommandLineRunner Builder ");
		this.usuarioRepository=usuarioRepository;
		this.direccionRepository=direccionRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("\t MyCommandLineRunner is running! ");
		poblarBD();
	}
	
	void poblarBD() {

		// Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setNombre("Luiky");
        
        //guardarlo primero suele ser bueno
        Usuario usuarioSaved= usuarioRepository.save(usuario);

        // Crear las direcciones
        Direccion direccion1 = new Direccion();
        direccion1.setCalle("Calle 1");
        direccion1.setCiudad("Ciudad 1");
        direccion1.setUsuario(usuarioSaved); // Relación bidireccional

        Direccion direccion2 = new Direccion();
        direccion2.setCalle("Calle 2");
        direccion2.setCiudad("Ciudad 2");
        direccion2.setUsuario(usuarioSaved); // Relación bidireccional

        // Guardar las direcciones primero, sino id a null. Guardar el usuario no me propagara el guardado de direcciones.
        Direccion direccion1Saved =direccionRepository.save(direccion1);
        Direccion direccion2Saved =direccionRepository.save(direccion2);
        
        // Asignar direcciones al usuario
        usuarioSaved.getDirecciones().add(direccion1Saved);
        usuarioSaved.getDirecciones().add(direccion2Saved);   
        
        //persistir el usuario
        usuarioSaved= usuarioRepository.save(usuarioSaved);
        
        // ---- crear el segundo usuario.
        
     // Crear segundo usuario con una dirección
        Usuario usuario2 = new Usuario();
        usuario2.setNombre("Pedro");
        
        //guardarlo primero suele ser bueno
        Usuario usuario2Saved= usuarioRepository.save(usuario2);

        Direccion direccion3 = new Direccion();
        direccion3.setCalle("Calle 3");
        direccion3.setCiudad("Ciudad 3");
        direccion3.setUsuario(usuario2Saved); // Relación bidireccional
        

        
        // Persistir la dirección
     // Guardar las direcciones primero, sino id a null. Guardar el usuario no me propagara el guardado de direcciones.
        Direccion direccion3Saved =direccionRepository.save(direccion3);
        
        // Asignar la dirección al usuario y persistir el usuario
        usuario2Saved.getDirecciones().add(direccion3Saved);
        usuario2Saved= usuarioRepository.save(usuario2Saved);
		
	}


}
