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

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UsuarioService usuarioService;

    @Autowired
    public PostController(PostService postService, UsuarioService usuarioService) {
        this.postService = postService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String verPosts(Model model, HttpSession session) {
        List<Post> posts = postService.buscarTodos();

        List<Conjunto> conjuntosPublicados = posts.stream()
                .map(Post::getConjunto)
                .filter(c -> c != null)
                .collect(Collectors.toList());

        model.addAttribute("posts", posts);
        model.addAttribute("conjuntosPublicados", conjuntosPublicados);

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuarioLogueado", usuarioLogueado);


        return "posts";
    }

    @PostMapping("/{postId}/like")
    public String darLike(@PathVariable Long postId, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado != null) {
            usuarioService.darLikeAPost(usuarioLogueado.getId(), postId);
        }
        return "redirect:/posts/";
    }

    @PostMapping("/{postId}/unlike")
    public String quitarLike(@PathVariable Long postId, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado != null) {
            usuarioService.quitarLikeDePost(usuarioLogueado.getId(), postId);
        }
        return "redirect:/posts/";
    }

    /**
     * Método para gestionar likes mediante AJAX (sin recargar la página).
     * Explicación:
     * Se utiliza @ResponseBody y ResponseEntity para devolver datos en formato JSON en lugar de una vista HTML.
     * Esto permite que el cliente (navegador) procese la respuesta con JavaScript (fetch API) y actualice
     * solo las partes necesarias (icono y contador) sin necesidad de recargar toda la página, que queda menos cutre.
     */
    @PostMapping("/{id}/toggle-like-api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleLikeApi(@PathVariable("id") Long id, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        Map<String, Object> response = new HashMap<>();

        if (usuarioLogueado == null) {
            response.put("success", false);
            return ResponseEntity.status(401).body(response);
        }

        boolean yaDioLike = postService.usuarioHaDadoLike(id, usuarioLogueado.getId());
        boolean esLike;

        if (yaDioLike) {
            usuarioService.quitarLikeDePost(usuarioLogueado.getId(), id);
            esLike = false;
        } else {
            usuarioService.darLikeAPost(usuarioLogueado.getId(), id);
            esLike = true;
        }

        int likesCount = postService.contarLikes(id);

        response.put("success", true);
        response.put("liked", esLike);
        response.put("likesCount", likesCount);

        return ResponseEntity.ok(response);
    }
}