package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.domain.Direccion;
import com.example.demo.domain.Usuario;

public interface DireccionRepository extends CrudRepository<Direccion, Long> {
	
	List <Direccion> findByUsuarioId(Long id);
	
	List<Direccion> findByUsuario(Usuario u);
		


}
