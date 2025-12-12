package com.nada.nada.data.services;

import com.nada.nada.data.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> buscarTodos();
    Optional<Post> buscarPorId(Long id);
    Post crearPost(Post post);
    void eliminarPost(Long id);
    List<Post> buscarPostsPorUsuario(Long usuarioId);
}
