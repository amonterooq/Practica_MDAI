package com.nada.nada.controllers;

import com.nada.nada.data.model.*;
import com.nada.nada.data.services.ConjuntoService;
import com.nada.nada.data.services.PrendaService;
import com.nada.nada.data.services.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/conjuntos")
public class ConjuntosController {


    private ConjuntoService conjuntoService;
    private PrendaService prendaService;
    private PostService postService;

    @Autowired
    public ConjuntosController(ConjuntoService conjuntoService, PrendaService prendaService, PostService postService) {
        this.conjuntoService = conjuntoService;
        this.prendaService = prendaService;
        this.postService = postService;
    }

    @GetMapping("/")
    public String verConjuntos(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Long usuarioId = usuarioLogueado.getId();

        // Cargar todas las prendas del usuario y separarlas por tipo para pasar a la vista
        List<Prenda> prendasUsuario = prendaService.buscarPrendasPorUsuarioId(usuarioId);
        List<PrendaSuperior> prendasSuperiores = prendasUsuario.stream()
                .filter(p -> p instanceof PrendaSuperior)
                .map(p -> (PrendaSuperior) p)
                .collect(Collectors.toList());
        List<PrendaInferior> prendasInferiores = prendasUsuario.stream()
                .filter(p -> p instanceof PrendaInferior)
                .map(p -> (PrendaInferior) p)
                .collect(Collectors.toList());
        List<PrendaCalzado> prendasCalzados = prendasUsuario.stream()
                .filter(p -> p instanceof PrendaCalzado)
                .map(p -> (PrendaCalzado) p)
                .collect(Collectors.toList());

        List<Conjunto> conjuntos = conjuntoService.buscarConjuntosPorUsuarioId(usuarioId);

        // Pasar datos al modelo para que la plantilla `conjuntos.html` los muestre
        model.addAttribute("conjuntos", conjuntos);
        model.addAttribute("prendasSuperiores", prendasSuperiores);
        model.addAttribute("prendasInferiores", prendasInferiores);
        model.addAttribute("prendasCalzados", prendasCalzados);

        // Flags que indican si falta algún tipo de prenda (para mostrar mensajes en la UI)
        model.addAttribute("faltaSuperior", prendasSuperiores.isEmpty());
        model.addAttribute("faltaInferior", prendasInferiores.isEmpty());
        model.addAttribute("faltaCalzado", prendasCalzados.isEmpty());

        return "conjuntos";
    }

    @PostMapping("/crear")
    public String crearConjunto(
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "prendaSuperiorId", required = false) Long prendaSuperiorId,
            @RequestParam(value = "prendaInferiorId", required = false) Long prendaInferiorId,
            @RequestParam(value = "prendaCalzadoId", required = false) Long prendaCalzadoId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        // Validar que se ha seleccionado una prenda de cada tipo
        if (prendaSuperiorId == null || prendaInferiorId == null || prendaCalzadoId == null) {
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar una prenda superior, una inferior y un calzado para crear un conjunto.");
            return "redirect:/conjuntos/";
        }

        // Recuperar las prendas por id
        Prenda prendaSuperiorPrenda = prendaService.buscarPrendaPorId(prendaSuperiorId).orElse(null);
        Prenda prendaInferiorPrenda = prendaService.buscarPrendaPorId(prendaInferiorId).orElse(null);
        Prenda prendaCalzadoPrenda = prendaService.buscarPrendaPorId(prendaCalzadoId).orElse(null);

        // Comprobar que las prendas son del tipo esperado
        if (!(prendaSuperiorPrenda instanceof PrendaSuperior) ||
                !(prendaInferiorPrenda instanceof PrendaInferior) ||
                !(prendaCalzadoPrenda instanceof PrendaCalzado)) {
            redirectAttributes.addFlashAttribute("error", "Los tipos de prenda seleccionados son incorrectos.");
            return "redirect:/conjuntos/";
        }

        // Verificar que las prendas pertenecen al usuario logueado
        if (!usuario.getId().equals(prendaSuperiorPrenda.getUsuario().getId()) ||
                !usuario.getId().equals(prendaInferiorPrenda.getUsuario().getId()) ||
                !usuario.getId().equals(prendaCalzadoPrenda.getUsuario().getId())) {
            redirectAttributes.addFlashAttribute("error", "Una o más prendas seleccionadas no te pertenecen.");
            return "redirect:/conjuntos/";
        }

        PrendaSuperior prendaSuperior = (PrendaSuperior) prendaSuperiorPrenda;
        PrendaInferior prendaInferior = (PrendaInferior) prendaInferiorPrenda;
        PrendaCalzado prendaCalzado = (PrendaCalzado) prendaCalzadoPrenda;

        try {
            conjuntoService.guardarConjunto(new Conjunto(nombre, usuario, descripcion, prendaSuperior, prendaInferior, prendaCalzado));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el conjunto: " + e.getMessage());
            return "redirect:/conjuntos/";
        }

        redirectAttributes.addFlashAttribute("success", "Conjunto creado correctamente.");
        return "redirect:/conjuntos/";
    }

    @PostMapping("/actualizar")
    public String actualizarConjunto(
            @RequestParam("id") Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam("prendaSuperiorId") Long prendaSuperiorId,
            @RequestParam("prendaInferiorId") Long prendaInferiorId,
            @RequestParam("prendaCalzadoId") Long prendaCalzadoId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        // Buscar el conjunto existente
        Conjunto conjunto = conjuntoService.buscarConjuntoPorId(id).orElse(null);
        if (conjunto == null) {
            redirectAttributes.addFlashAttribute("error", "Conjunto no encontrado.");
            return "redirect:/conjuntos/";
        }

        // Verificar que el conjunto pertenece al usuario
        if (!conjunto.getUsuario().getId().equals(usuarioLogueado.getId())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar este conjunto.");
            return "redirect:/conjuntos/";
        }

        // Recuperar las prendas seleccionadas
        Prenda prendaSuperiorPrenda = prendaService.buscarPrendaPorId(prendaSuperiorId).orElse(null);
        Prenda prendaInferiorPrenda = prendaService.buscarPrendaPorId(prendaInferiorId).orElse(null);
        Prenda prendaCalzadoPrenda = prendaService.buscarPrendaPorId(prendaCalzadoId).orElse(null);

        // Validar tipos
        if (!(prendaSuperiorPrenda instanceof PrendaSuperior) ||
                !(prendaInferiorPrenda instanceof PrendaInferior) ||
                !(prendaCalzadoPrenda instanceof PrendaCalzado)) {
            redirectAttributes.addFlashAttribute("error", "Los tipos de prenda seleccionados son incorrectos.");
            return "redirect:/conjuntos/";
        }

        // Verificar que las prendas pertenecen al usuario
        if (!usuarioLogueado.getId().equals(prendaSuperiorPrenda.getUsuario().getId()) ||
                !usuarioLogueado.getId().equals(prendaInferiorPrenda.getUsuario().getId()) ||
                !usuarioLogueado.getId().equals(prendaCalzadoPrenda.getUsuario().getId())) {
            redirectAttributes.addFlashAttribute("error", "Una o más prendas seleccionadas no te pertenecen.");
            return "redirect:/conjuntos/";
        }

        // Actualizar el conjunto
        conjunto.setNombre(nombre);
        conjunto.setDescripcion(descripcion);
        conjunto.setPrendaSuperior((PrendaSuperior) prendaSuperiorPrenda);
        conjunto.setPrendaInferior((PrendaInferior) prendaInferiorPrenda);
        conjunto.setPrendaCalzado((PrendaCalzado) prendaCalzadoPrenda);

        try {
            conjuntoService.guardarConjunto(conjunto);
            redirectAttributes.addFlashAttribute("success", "Conjunto actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el conjunto: " + e.getMessage());
        }

        return "redirect:/conjuntos/";
    }

    @PostMapping("/eliminar")
    public String eliminarConjunto(@RequestParam("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Conjunto conjunto = conjuntoService.buscarConjuntoPorId(id).orElse(null);
        if (conjunto != null && conjunto.getUsuario().getId().equals(usuarioLogueado.getId())) {
            conjuntoService.borrarConjunto(id);
            redirectAttributes.addFlashAttribute("success", "Conjunto eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar este conjunto.");
        }
        return "redirect:/conjuntos/";
    }

    @PostMapping("/{id}/toggle-publicacion")
    public String togglePublicacion(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Conjunto conjunto = conjuntoService.buscarConjuntoPorId(id).orElse(null);
        if (conjunto == null) {
            redirectAttributes.addFlashAttribute("error", "Conjunto no encontrado.");
            return "redirect:/conjuntos/";
        }

        if (!conjunto.getUsuario().getId().equals(usuarioLogueado.getId())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar la publicación de este conjunto.");
            return "redirect:/conjuntos/";
        }

        try {
            if (conjunto.getPost() == null) {
                // Publicar: crear post y asociarlo bidireccionalmente
                Post post = new Post(usuarioLogueado, conjunto);
                post.setConjunto(conjunto);
                conjunto.setPost(post);
                postService.crearPost(post);
                conjuntoService.guardarConjunto(conjunto);
                redirectAttributes.addFlashAttribute("success", "Conjunto publicado correctamente.");
            } else {
                // Despublicar: eliminar post y desasociarlo en ambos lados
                Post post = conjunto.getPost();
                Long postId = (post != null ? post.getId() : null);

                if (post != null) {
                    post.setConjunto(null);
                }
                conjunto.setPost(null);
                conjuntoService.guardarConjunto(conjunto);

                if (postId != null) {
                    postService.eliminarPost(postId);
                }
                redirectAttributes.addFlashAttribute("success", "Publicación del conjunto eliminada.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la publicación: " + e.getMessage());
        }

        return "redirect:/conjuntos/";
    }
}
