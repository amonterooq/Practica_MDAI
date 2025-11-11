package com.nada.nada.data.services;

import com.nada.nada.data.model.Conjunto;
import com.nada.nada.data.model.Prenda;
import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.ConjuntoRepository;
import com.nada.nada.data.repository.PrendaRepository;
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
    private final PrendaRepository prendaRepository;
    private final ConjuntoRepository conjuntoRepository;


    @Autowired //no necesario, para recordar que nos inyecta y crea el IoC de Spring
    public UsuarioServiceImpl (UsuarioRepository usuarioRepository, PrendaRepository prendaRepository, ConjuntoRepository conjuntoRepository) {
        System.out.println("\t UsuarioServiceImpl constructor ");
        this.usuarioRepository=usuarioRepository;
        this.prendaRepository=prendaRepository;
        this.conjuntoRepository=conjuntoRepository;
    }

    //En la capa servicios es donde se implementa la LOGICA de negocio
    @Override
    public List<Usuario> buscarTodosUsuarios() {
        List<Usuario> usuarios = (List<Usuario>) this.usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            return null;
        }
        return usuarios;
    }

    @Override
    @Transactional
    public void crearUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        // Comprobar duplicado de email
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        // Inicializar colecciones si vienen nulas
        if (usuario.getPrendas() == null) usuario.setPrendas(new ArrayList<>());
        if (usuario.getConjuntos() == null) usuario.setConjuntos(new ArrayList<>());

        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void actualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException("El usuario y su ID no pueden ser nulos");
        }

        Usuario usuarioExistente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + usuario.getId() + " no encontrado"));

        // Actualizar solo campos permitidos
        if (usuario.getUsername() != null && !usuario.getUsername().trim().isEmpty()) {
            usuarioExistente.setUsername(usuario.getUsername());
        }
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(usuario.getPassword());
        }
        if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
            Optional<Usuario> porEmail = usuarioRepository.findByEmail(usuario.getEmail());
            if (porEmail.isPresent() && !porEmail.get().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Ya existe otro usuario con ese email");
            }
            usuarioExistente.setEmail(usuario.getEmail());
        }

        usuarioRepository.save(usuarioExistente);

    }

    @Override
    public Optional<Usuario> findUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    @Override
    @Transactional
    public void deleteUsuarioById(Long usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("El id no puede ser nulo");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + usuarioId + " no encontrado"));

        // Eliminar conjuntos que requieren usuario no nulo (joinColumn nullable = false)
        List<Conjunto> conjuntos = conjuntoRepository.findByUsuario_Id(usuarioId);
        if (conjuntos != null && !conjuntos.isEmpty()) {
            for (Conjunto c : conjuntos) {
                conjuntoRepository.delete(c);
            }
        }

        // Desvincular prendas: poner usuario = null para evitar violación FK
        if (usuario.getPrendas() != null && !usuario.getPrendas().isEmpty()) {
            for (Prenda p : new ArrayList<>(usuario.getPrendas())) {
                p.setUsuario(null);
                prendaRepository.save(p);
            }
        }

        usuarioRepository.deleteById(usuarioId);
    }
}
