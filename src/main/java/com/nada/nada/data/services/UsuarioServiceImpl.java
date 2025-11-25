package com.nada.nada.data.services;

import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<Usuario> buscarTodos() {
        List<Usuario> usuarios = (List<Usuario>) this.usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            throw new RuntimeException("No hay usuarios en la base de datos");
        }

        return usuarios;
    }

    @Override
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {

        // Usuario no puede ser nulo
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        // El nombre de usuario no puede estar vacio
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        }
        // Comprobar duplicado
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario");
        }

        // Nombre de usuario tiene que tener longitud apropiada
        if (usuario.getUsername().length() < 2 || usuario.getUsername().length() > 15) {
            throw new IllegalArgumentException("El nombre del usuario debe tener entre 2 y 15 caracteres");
        }

        // Validar mail
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del usuario no puede estar vacío");
        }
        if (!usuario.getEmail().contains("@") || !usuario.getEmail().contains(".")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        // Comprobar duplicado
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        // Inicializar colecciones si vienen nulas
        if (usuario.getPrendas() == null) usuario.setPrendas(new ArrayList<>());
        if (usuario.getConjuntos() == null) usuario.setConjuntos(new ArrayList<>());

        return usuarioRepository.save(usuario);
    }


    @Override
    public Optional<Usuario> encontrarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido: " + id);
        }
        return usuarioRepository.findById(id);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido: " + id);
        }
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario con ID " + id + " no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
