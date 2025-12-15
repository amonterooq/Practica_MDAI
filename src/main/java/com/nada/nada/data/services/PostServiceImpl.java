package com.nada.nada.data.services;

import com.nada.nada.data.model.Post;
import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.PostRepository;
import com.nada.nada.data.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UsuarioRepository usuarioRepository) {
        this.postRepository = postRepository;
        this.usuarioRepository = usuarioRepository;
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

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void eliminarPost(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del post debe ser un número positivo y no nulo");
        }

        if (!postRepository.existsById(id)) {
            return;
        }

        // Primero eliminar los likes
        eliminarLikesDelPost(id);

        // Ahora borrar el post
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
                        .anyMatch(uid -> uid != null && uid.equals(usuarioId)))
                .orElse(false);
    }

    @Override
    @Transactional
    public void eliminarLikesDelPost(Long postId) {
        if (postId == null || postId <= 0) {
            return;
        }

        // Cargar el post con sus likes
        Optional<Post> postOpt = postRepository.findByIdWithLikes(postId);
        if (postOpt.isEmpty()) {
            return;
        }

        Post post = postOpt.get();

        // Recorrer los usuarios que dieron like y eliminar el post de sus listas
        // Copiamos la lista para evitar ConcurrentModificationException
        List<Usuario> usuarios = new ArrayList<>(post.getUsuariosQueDieronLike());
        for (Usuario usuario : usuarios) {
            usuario.getPostsLikeados().remove(post);
            usuarioRepository.save(usuario);
        }
        post.getUsuariosQueDieronLike().clear();
    }
}
