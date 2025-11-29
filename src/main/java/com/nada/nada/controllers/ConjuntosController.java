package com.nada.nada.controllers;

import com.nada.nada.data.model.*;
import com.nada.nada.data.services.ConjuntoService;
import com.nada.nada.data.services.PrendaService;
import com.nada.nada.data.services.UsuarioService;
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

    @Autowired
    private ConjuntoService conjuntoService;

    @Autowired
    private PrendaService prendaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String verConjuntos(Model model, HttpSession session) {
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

        List<Conjunto> conjuntos = conjuntoService.buscarConjuntosPorUsuarioId(usuarioId);

        model.addAttribute("conjuntos", conjuntos);
        model.addAttribute("prendasSuperiores", prendasSuperiores);
        model.addAttribute("prendasInferiores", prendasInferiores);
        model.addAttribute("prendasCalzados", prendasCalzados);

        // Flags para mostrar mensajes si falta algún tipo de prenda
        model.addAttribute("faltaSuperior", prendasSuperiores.isEmpty());
        model.addAttribute("faltaInferior", prendasInferiores.isEmpty());
        model.addAttribute("faltaCalzado", prendasCalzados.isEmpty());

        return "conjuntos";
    }

    @PostMapping("/crear")
    public String crearConjunto(@RequestParam("nombre") String nombre,
                                @RequestParam(value = "descripcion", required = false) String descripcion,
                                @RequestParam("prendaSuperiorId") Long prendaSuperiorId,
                                @RequestParam("prendaInferiorId") Long prendaInferiorId,
                                @RequestParam("prendaCalzadoId") Long prendaCalzadoId,
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

        Prenda prendaSuperiorPrenda = prendaService.buscarPrendaPorId(prendaSuperiorId).orElse(null);
        Prenda prendaInferiorPrenda = prendaService.buscarPrendaPorId(prendaInferiorId).orElse(null);
        Prenda prendaCalzadoPrenda = prendaService.buscarPrendaPorId(prendaCalzadoId).orElse(null);

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

        Conjunto nuevoConjunto = new Conjunto(nombre, usuario, descripcion, prendaSuperior, prendaInferior, prendaCalzado);
        conjuntoService.guardarConjunto(nuevoConjunto);

        redirectAttributes.addFlashAttribute("success", "Conjunto creado correctamente.");
        return "redirect:/conjuntos/";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarConjunto(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
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
}
