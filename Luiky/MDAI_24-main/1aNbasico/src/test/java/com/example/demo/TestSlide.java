package com.example.demo;

/** 
 * 
 * TEST DE LA DIAPOSITIVA 15 TEMA 2.
 * 
 * **/
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.domain.Direccion;
import com.example.demo.domain.Usuario;
import com.example.demo.repositories.DireccionRepository;
import com.example.demo.repositories.UsuarioRepository;

@SpringBootTest
class TestSlide {
	
	@Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DireccionRepository direccionRepository;

	@Test
	void test() {
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
        
        // --------------  Verificar que los usuarios se persistieron correctamente
        mostrarUsuarios();
	}
	
	//fijamos los ids NO IMITAR
	void mostrarUsuarios() {
		System.out.println("Usuario 1 con sus direcciones:");
        //recuperamos el usuario
        Usuario usuarioGet1 = usuarioRepository.findById(1L).get();
        System.out.println("\t " + usuarioGet1.toString());
        
        //llamamos al metodo creado con el patron de nombres del repository direccionRepository        
        for (Direccion direccion : direccionRepository.findByUsuarioId(usuarioGet1.getId())) {
            System.out.println("\t " + direccion.getCalle() + ", " + direccion.getCiudad());
        }       
        
        System.out.println("\nUsuario 2 con su direccion:");
        
      //llamamos al metodo personalizado del repository usuarioRepository
        Usuario usuarioGet2 = usuarioRepository.findUsuarioConDirecciones(2L);
        System.out.println("\t " + usuarioGet2.toString());        

        for (Direccion direccion : usuarioGet2.getDirecciones()) {
            System.out.println("\t " + direccion.getCalle() + ", " + direccion.getCiudad());
        }

	}

}
