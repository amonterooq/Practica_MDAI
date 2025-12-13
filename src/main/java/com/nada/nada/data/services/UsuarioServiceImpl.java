package com.nada.nada.data.services;

import com.nada.nada.data.model.Post;
import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.PostRepository;
import com.nada.nada.data.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PostRepository postRepository;

    // Ruta del contenedor (Compartida mediante volumen)
    private final Path IMAGES_BASE_DIR = Paths.get("/app/static/images");

    @Autowired
    public UsuarioServiceImpl (UsuarioRepository usuarioRepository, PostRepository postRepository) {
        this.usuarioRepository = usuarioRepository;
        this.postRepository = postRepository;
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

        // Guardar primero para obtener el ID
        Usuario guardado = usuarioRepository.save(usuario);

        // Crear carpeta de imágenes para este usuario (no lanzar excepción si falla)
        crearCarpetaImagenesUsuario(guardado.getId());

        return guardado;
    }

    private void crearCarpetaImagenesUsuario(Long usuarioId) {
        if (usuarioId == null) return;
        try {
            if (!Files.exists(IMAGES_BASE_DIR)) {
                Files.createDirectories(IMAGES_BASE_DIR);
            }
            Path userDir = IMAGES_BASE_DIR.resolve(String.valueOf(usuarioId));
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }
        } catch (IOException e) {
            System.err.println("No se pudo crear carpeta de imágenes para usuario " + usuarioId + ": " + e.getMessage());
        }
    }

    @Override
    public Optional<Usuario> validarLogin(String username, String password) {
        Optional<Usuario> u = usuarioRepository.findByUsername(username);

        // Comprueba si el usuario existe y si la contraseña coincide
        if (u.isPresent() && u.get().getPassword().equals(password)) {
            return u;
        }

        return Optional.empty();
    }

    @Override
    public void cambiarPassword(Long usuarioId, String oldPassword, String newPassword) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // Comprobación de contraseña sin encriptar
        if (!oldPassword.equals(usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }

        usuario.setPassword(newPassword);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            return; // Simplemente no hace nada si el usuario no existe
        }

        // Borrar primero la carpeta de imágenes asociada al usuario
        borrarCarpetaImagenesUsuario(usuarioId);

        // Después eliminar el usuario en BD
        usuarioRepository.deleteById(usuarioId);
    }

    private void borrarCarpetaImagenesUsuario(Long usuarioId) {
        Path userDir = IMAGES_BASE_DIR.resolve(String.valueOf(usuarioId));
        if (!Files.exists(userDir)) {
            return;
        }
        try {
            Files.walk(userDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            System.err.println("No se pudo borrar " + path + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error al recorrer carpeta de imágenes de usuario " + usuarioId + ": " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean darLikeAPost(Long usuarioId, Long postId) {
        if (usuarioId == null || postId == null) {
            throw new IllegalArgumentException("El id de usuario y el id de post no pueden ser nulos");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Usar findByIdWithLikes para asegurar que la colección de likes esté inicializada
        Post post = postRepository.findByIdWithLikes(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post no encontrado"));

        boolean cambiado = usuario.likePost(post);

        if (cambiado) {
            usuarioRepository.save(usuario);
        }

        return cambiado;
    }

    @Override
    @Transactional
    public boolean quitarLikeDePost(Long usuarioId, Long postId) {
        if (usuarioId == null || postId == null) {
            throw new IllegalArgumentException("El id de usuario y el id de post no pueden ser nulos");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Usar findByIdWithLikes para asegurar que la colección de likes esté inicializada
        Post post = postRepository.findByIdWithLikes(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post no encontrado"));

        boolean cambiado = usuario.unlikePost(post);

        if (cambiado) {
            usuarioRepository.save(usuario);
        }

        return cambiado;
    }
}
