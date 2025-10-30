package com.example.demo;

/** 
 * 
 * EJEMPLO BASICO CON UN USARIO Y DOS DIRECCIONES BIDIRECCIONAL PARA EXPLICAR CARGA (fetch)
 * DE DIRECCIONES LAZY
 * 
 * **/

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.domain.Direccion;
import com.example.demo.domain.Usuario;
import com.example.demo.repositories.DireccionRepository;
import com.example.demo.repositories.UsuarioRepository;

@SpringBootTest
class BidirectionalTest {
	
	@Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DireccionRepository direccionRepository;

	@Test
	void test() {
		// Crear el primer usuario
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
        
        //Direcciones guardas correctamente
        System.out.println("\t "+ direccion1Saved.toString());
        System.out.println("\t "+ direccion2Saved.toString());
              
        // Asignar direcciones al usuario
        usuarioSaved.getDirecciones().add(direccion1);
        usuarioSaved.getDirecciones().add(direccion2);


        
       //usuarioSaved memoria
        System.out.println("\t usuarioSaved en Memoria: "+ usuarioSaved.toString());
        
        //Instancia devuelta al persistir por hibernate. Null las direcciones.        
        usuarioSaved=usuarioRepository.save(usuarioSaved);
        System.out.println("\n\t usuarioSaved tras persistir: "+ usuarioSaved.toString());
        
        
        //usuarioSaved en H2. Null direcciones
        usuarioSaved=usuarioRepository.findById(1L).get(); //este .get bruto por el optional NO IMITAR
        
        
        //Consultar en direcciones con ese usuario y devolver sus direcciones (como en una BD es un where). Asignarlas al usuario.
        usuarioSaved.setDirecciones(direccionRepository.findByUsuarioId(1L));
        //ahora direcciones no está a Null y podemos acceder.
        System.out.println("\t Consultamos sus direcciones y se las asignamos: usuarioSaved.getDirecciones().size(): " + +usuarioSaved.getDirecciones().size());
                 
 
        //Conseguir el usuario con sus direcciones desde usuarioRepository. 
        //Con un método @Query que nos carga las direcciones. 
        Usuario usuarioGet = usuarioRepository.findUsuarioConDirecciones(1L);
        System.out.println("\t "+ usuarioGet.toString() + " usuarioGet.getDirecciones().size(): " + usuarioGet.getDirecciones().size());

        
        
	}


}
