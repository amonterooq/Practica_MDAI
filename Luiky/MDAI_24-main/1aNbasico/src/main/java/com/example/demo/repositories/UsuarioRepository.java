package com.example.demo.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
	
	
	// Consulta personalizada con JOIN FETCH para cargar usuario y sus direcciones //Gemini:
	// JOIN FETCH le dice a Hibernate que debe cargar inmediatamente la relación especificada 
	// (en este caso, u.direcciones) junto con el Usuario en una sola consulta SQL. 
	// Esto garantiza que, cuando accedas a u.direcciones, las direcciones ya estén cargadas en memoria.
    @Query("SELECT u FROM Usuario u JOIN FETCH u.direcciones WHERE u.id = :id")
    Usuario findUsuarioConDirecciones(@Param("id") Long id);
}
