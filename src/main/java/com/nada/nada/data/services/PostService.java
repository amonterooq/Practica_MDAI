package com.nada.nada.data.services;

import com.nada.nada.data.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    public List<Post> buscarTodos();
    public Optional<Post> buscarPorId(Long id);
    public Post crearPost(Post post);
    public void eliminarPost(Long id);
    public List<Post> buscarPostsPorUsuario(Long usuarioId);
    public int contarLikes(Long postId);
    public boolean usuarioHaDadoLike(Long postId, Long usuarioId);
}
