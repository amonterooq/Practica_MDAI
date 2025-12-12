package com.nada.nada.controllers;

import com.nada.nada.data.model.Conjunto;
import com.nada.nada.data.model.Post;
import com.nada.nada.data.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String verPosts(Model model) {
        List<Post> posts;
        try {
            posts = postService.buscarTodos();
        } catch (RuntimeException e) {
            // El servicio actualmente lanza una RuntimeException si no hay posts; aquí la convertimos en lista vacía
            posts = List.of();
        }

        // Extraer los conjuntos asociados para la vista si se quiere trabajar directamente con ellos
        List<Conjunto> conjuntosPublicados = posts.stream()
                .map(Post::getConjunto)
                .filter(c -> c != null)
                .collect(Collectors.toList());

        model.addAttribute("posts", posts);
        model.addAttribute("conjuntosPublicados", conjuntosPublicados);
        return "posts";
    }
}