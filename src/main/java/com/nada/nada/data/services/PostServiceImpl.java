package com.nada.nada.data.services;

import com.nada.nada.data.model.Post;
import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> buscarTodos() {
        return postRepository.findAllWithLikes();
    }

    @Override
    public Optional<Post> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del post no puede ser nulo");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id del post debe ser un número positivo");
        }

        return postRepository.findById(id);
    }

    @Override
    @Transactional
    public Post crearPost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("El post no puede ser nulo");
        }

        // Aquí se podrían añadir validaciones específicas según los campos de Post
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void eliminarPost(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del post debe ser un número positivo y no nulo");
        }

        if (!postRepository.existsById(id)) {
            // Igual que en eliminarUsuario: no hace nada si el post no existe
            return;
        }

        postRepository.deleteById(id);
    }

    @Override
    public List<Post> buscarPostsPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El id del usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("El id del usuario debe ser un número positivo");
        }

        List<Post> resultado = new ArrayList<>();
        postRepository.findAll().forEach(post -> {
            if (post.getUsuario() != null
                    && post.getUsuario().getId() != null
                    && post.getUsuario().getId().equals(usuarioId)) {
                resultado.add(post);
            }
        });

        return resultado;
    }

    @Override
    public int contarLikes(Long postId) {
        if (postId == null) {
            return 0;
        }

        return postRepository.findByIdWithLikes(postId)
                .map(post -> post.getUsuariosQueDieronLike().size())
                .orElse(0);
    }

    @Override
    public boolean usuarioHaDadoLike(Long postId, Long usuarioId) {
        if (postId == null || usuarioId == null) {
            return false;
        }

        return postRepository.findByIdWithLikes(postId)
                .map(post -> post.getUsuariosQueDieronLike().stream()
                        .map(Usuario::getId)
                        .anyMatch(id -> id != null && id.equals(usuarioId)))
                .orElse(false);
    }
}
