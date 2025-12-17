package com.nada.nada.controllers;

import com.nada.nada.data.model.Conjunto;
import com.nada.nada.data.model.Post;
import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.services.PostService;
import com.nada.nada.data.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador encargado de gestionar las publicaciones (posts) de conjuntos.
 * Permite ver el feed de posts, dar/quitar likes y gestionar interacciones sociales.
 */
@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param postService servicio para gestionar posts
     * @param usuarioService servicio para gestionar usuarios y sus likes
     */
    @Autowired
    public PostController(PostService postService, UsuarioService usuarioService) {
        this.postService = postService;
        this.usuarioService = usuarioService;
    }

    /**
     * Muestra el feed de publicaciones.
     * Carga todos los posts con sus conjuntos asociados.
     *
     * @param model modelo para pasar datos a la vista
     * @param session sesión HTTP con el usuario logueado (opcional)
     * @return vista del feed de posts
     */
    @GetMapping("/")
    public String verPosts(Model model, HttpSession session) {
        // Obtener todos los posts
        List<Post> posts = postService.buscarTodos();

        // Extraer los conjuntos publicados para mostrar sus detalles
        List<Conjunto> conjuntosPublicados = posts.stream()
                .map(Post::getConjunto)
                .filter(c -> c != null)
                .collect(Collectors.toList());

        // Pasar datos al modelo
        model.addAttribute("posts", posts);
        model.addAttribute("conjuntosPublicados", conjuntosPublicados);

        // Añadir usuario logueado si existe (para mostrar botones de like)
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuarioLogueado", usuarioLogueado);


        return "posts";
    }

    /**
     * Añade un like a un post (versión con recarga de página).
     *
     * @param postId ID del post
     * @param session sesión HTTP con el usuario logueado
     * @return redirección al feed de posts
     */
    @PostMapping("/{postId}/like")
    public String darLike(@PathVariable Long postId, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado != null) {
            usuarioService.darLikeAPost(usuarioLogueado.getId(), postId);
        }

        return "redirect:/posts/";
    }

    /**
     * Quita un like de un post (versión con recarga de página).
     *
     * @param postId ID del post
     * @param session sesión HTTP con el usuario logueado
     * @return redirección al feed de posts
     */
    @PostMapping("/{postId}/unlike")
    public String quitarLike(@PathVariable Long postId, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado != null) {
            usuarioService.quitarLikeDePost(usuarioLogueado.getId(), postId);
        }

        return "redirect:/posts/";
    }

    /**
     * Alterna el estado de like de un post mediante AJAX.
     * Devuelve datos JSON para que el cliente actualice la UI sin recargar.
     *
     * Este endpoint usa @ResponseBody para devolver JSON en lugar de una vista HTML,
     * permitiendo que JavaScript (fetch API) actualice solo el icono y contador
     * sin necesidad de recargar toda la página.
     *
     * @param id ID del post
     * @param session sesión HTTP con el usuario logueado
     * @return ResponseEntity con el estado del like y contador actualizado
     */
    @PostMapping("/{id}/toggle-like-api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleLikeApi(@PathVariable("id") Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Verificar que el usuario está logueado
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            response.put("success", false);
            return ResponseEntity.status(401).body(response);
        }

        // Determinar si el usuario ya dio like
        boolean yaDioLike = postService.usuarioHaDadoLike(id, usuarioLogueado.getId());
        boolean esLike;

        // Alternar el estado del like
        if (yaDioLike) {
            usuarioService.quitarLikeDePost(usuarioLogueado.getId(), id);
            esLike = false;
        } else {
            usuarioService.darLikeAPost(usuarioLogueado.getId(), id);
            esLike = true;
        }

        // Obtener el contador actualizado de likes
        int likesCount = postService.contarLikes(id);

        // Construir respuesta JSON
        response.put("success", true);
        response.put("liked", esLike);
        response.put("likesCount", likesCount);

        return ResponseEntity.ok(response);
    }
}