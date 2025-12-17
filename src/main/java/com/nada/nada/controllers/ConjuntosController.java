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

/**
 * Controlador encargado de gestionar las operaciones CRUD de conjuntos de ropa.
 * Un conjunto es una combinación de prenda superior, inferior y calzado.
 */
@Controller
@RequestMapping("/conjuntos")
public class ConjuntosController {

    private final ConjuntoService conjuntoService;
    private final PrendaService prendaService;
    private final PostService postService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param conjuntoService servicio para gestionar conjuntos
     * @param prendaService servicio para gestionar prendas
     * @param postService servicio para gestionar publicaciones
     */
    @Autowired
    public ConjuntosController(ConjuntoService conjuntoService, PrendaService prendaService, PostService postService) {
        this.conjuntoService = conjuntoService;
        this.prendaService = prendaService;
        this.postService = postService;
    }

    /**
     * Muestra la página principal de conjuntos del usuario.
     * Carga todas las prendas del usuario separadas por tipo para poder crear nuevos conjuntos.
     *
     * @param model modelo para pasar datos a la vista
     * @param session sesión HTTP con el usuario logueado
     * @return vista de conjuntos o redirección al login si no hay sesión
     */
    @GetMapping("/")
    public String verConjuntos(Model model, HttpSession session) {
        // Verificar que el usuario está logueado
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Long usuarioId = usuarioLogueado.getId();

        // Cargar todas las prendas del usuario y separarlas por tipo
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

        // Cargar los conjuntos existentes del usuario
        List<Conjunto> conjuntos = conjuntoService.buscarConjuntosPorUsuarioId(usuarioId);

        // Pasar datos al modelo para la vista
        model.addAttribute("conjuntos", conjuntos);
        model.addAttribute("prendasSuperiores", prendasSuperiores);
        model.addAttribute("prendasInferiores", prendasInferiores);
        model.addAttribute("prendasCalzados", prendasCalzados);

        // Flags para mostrar mensajes si falta algún tipo de prenda
        model.addAttribute("faltaSuperior", prendasSuperiores.isEmpty());
        model.addAttribute("faltaInferior", prendasInferiores.isEmpty());
        model.addAttribute("faltaCalzado", prendasCalzados.isEmpty());

        // Nombre de usuario para el saludo del chat IA
        model.addAttribute("username", usuarioLogueado.getUsername());

        return "conjuntos";
    }

    /**
     * Crea un nuevo conjunto con las prendas seleccionadas.
     *
     * @param nombre nombre del conjunto
     * @param descripcion descripción opcional del conjunto
     * @param prendaSuperiorId ID de la prenda superior seleccionada
     * @param prendaInferiorId ID de la prenda inferior seleccionada
     * @param prendaCalzadoId ID del calzado seleccionado
     * @param session sesión HTTP con el usuario logueado
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de conjuntos
     */
    @PostMapping("/crear")
    public String crearConjunto(
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "prendaSuperiorId", required = false) Long prendaSuperiorId,
            @RequestParam(value = "prendaInferiorId", required = false) Long prendaInferiorId,
            @RequestParam(value = "prendaCalzadoId", required = false) Long prendaCalzadoId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Verificar sesión
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        // Validar que se seleccionó una prenda de cada tipo
        if (prendaSuperiorId == null || prendaInferiorId == null || prendaCalzadoId == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Debes seleccionar una prenda superior, una inferior y un calzado para crear un conjunto.");
            return "redirect:/conjuntos/";
        }

        // Recuperar las prendas por ID
        Prenda prendaSuperiorPrenda = prendaService.buscarPrendaPorId(prendaSuperiorId).orElse(null);
        Prenda prendaInferiorPrenda = prendaService.buscarPrendaPorId(prendaInferiorId).orElse(null);
        Prenda prendaCalzadoPrenda = prendaService.buscarPrendaPorId(prendaCalzadoId).orElse(null);

        // Verificar que las prendas son del tipo correcto
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

        // Hacer cast a los tipos específicos
        PrendaSuperior prendaSuperior = (PrendaSuperior) prendaSuperiorPrenda;
        PrendaInferior prendaInferior = (PrendaInferior) prendaInferiorPrenda;
        PrendaCalzado prendaCalzado = (PrendaCalzado) prendaCalzadoPrenda;

        // Crear y guardar el conjunto
        try {
            Conjunto nuevoConjunto = new Conjunto(nombre, usuario, descripcion, prendaSuperior, prendaInferior, prendaCalzado);
            conjuntoService.guardarConjunto(nuevoConjunto);
            redirectAttributes.addFlashAttribute("success", "Conjunto creado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el conjunto: " + e.getMessage());
        }

        return "redirect:/conjuntos/";
    }

    /**
     * Actualiza un conjunto existente.
     *
     * @param id ID del conjunto a actualizar
     * @param nombre nuevo nombre del conjunto
     * @param descripcion nueva descripción
     * @param prendaSuperiorId nuevo ID de prenda superior
     * @param prendaInferiorId nuevo ID de prenda inferior
     * @param prendaCalzadoId nuevo ID de calzado
     * @param session sesión HTTP
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de conjuntos
     */
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

        // Verificar sesión
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

        // Validar tipos de prenda
        if (!(prendaSuperiorPrenda instanceof PrendaSuperior) ||
                !(prendaInferiorPrenda instanceof PrendaInferior) ||
                !(prendaCalzadoPrenda instanceof PrendaCalzado)) {
            redirectAttributes.addFlashAttribute("error", "Los tipos de prenda seleccionados son incorrectos.");
            return "redirect:/conjuntos/";
        }

        // Verificar pertenencia de las prendas
        if (!usuarioLogueado.getId().equals(prendaSuperiorPrenda.getUsuario().getId()) ||
                !usuarioLogueado.getId().equals(prendaInferiorPrenda.getUsuario().getId()) ||
                !usuarioLogueado.getId().equals(prendaCalzadoPrenda.getUsuario().getId())) {
            redirectAttributes.addFlashAttribute("error", "Una o más prendas seleccionadas no te pertenecen.");
            return "redirect:/conjuntos/";
        }

        // Actualizar los datos del conjunto
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

    /**
     * Elimina un conjunto del usuario.
     *
     * @param id ID del conjunto a eliminar
     * @param session sesión HTTP
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de conjuntos
     */
    @PostMapping("/eliminar")
    public String eliminarConjunto(@RequestParam("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Verificar sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        // Verificar permisos y eliminar
        Conjunto conjunto = conjuntoService.buscarConjuntoPorId(id).orElse(null);
        if (conjunto != null && conjunto.getUsuario().getId().equals(usuarioLogueado.getId())) {
            conjuntoService.borrarConjunto(id);
            redirectAttributes.addFlashAttribute("success", "Conjunto eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar este conjunto.");
        }

        return "redirect:/conjuntos/";
    }

    /**
     * Alterna el estado de publicación de un conjunto.
     * Si el conjunto no está publicado, crea un post.
     * Si ya está publicado, elimina el post asociado.
     *
     * @param id ID del conjunto
     * @param session sesión HTTP
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de conjuntos
     */
    @PostMapping("/{id}/toggle-publicacion")
    public String togglePublicacion(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Verificar sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        // Buscar el conjunto
        Conjunto conjunto = conjuntoService.buscarConjuntoPorId(id).orElse(null);
        if (conjunto == null) {
            redirectAttributes.addFlashAttribute("error", "Conjunto no encontrado.");
            return "redirect:/conjuntos/";
        }

        // Verificar permisos
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
                // Despublicar: eliminar likes y desasociar el post
                Post post = conjunto.getPost();
                Long postId = (post != null ? post.getId() : null);

                // Eliminar los likes ANTES de desvincular el post
                // porque orphanRemoval borrará el post automáticamente al hacer setPost(null)
                if (postId != null) {
                    postService.eliminarLikesDelPost(postId);
                }

                // Desvincular el post del conjunto
                if (post != null) {
                    post.setConjunto(null);
                }
                conjunto.setPost(null);
                conjuntoService.guardarConjunto(conjunto);

                redirectAttributes.addFlashAttribute("success", "Publicación del conjunto eliminada.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la publicación: " + e.getMessage());
        }

        return "redirect:/conjuntos/";
    }
}
