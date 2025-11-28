package com.nada.nada.controllers;

import com.nada.nada.data.model.*;
import com.nada.nada.data.services.ConjuntoService;
import com.nada.nada.data.services.PrendaService;
import com.nada.nada.data.services.UsuarioService;
import jakarta.servlet.http.HttpSession; // Importar HttpSession
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Importar RedirectAttributes

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
        List<Conjunto> conjuntos = conjuntoService.buscarConjuntosPorUsuarioId(usuarioLogueado.getId());
        model.addAttribute("conjuntos", conjuntos);
        return "conjuntos";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }
        List<Prenda> prendas = prendaService.buscarPrendasPorUsuarioId(usuarioLogueado.getId());
        model.addAttribute("prendasSuperiores", prendas.stream().filter(p -> p instanceof PrendaSuperior).collect(Collectors.toList()));
        model.addAttribute("prendasInferiores", prendas.stream().filter(p -> p instanceof PrendaInferior).collect(Collectors.toList()));
        model.addAttribute("prendasCalzados", prendas.stream().filter(p -> p instanceof PrendaCalzado).collect(Collectors.toList()));
        model.addAttribute("conjunto", new Conjunto());
        return "formularioConjunto";
    }

    @PostMapping("/crear")
    public String crearConjunto(@RequestParam String nombre,
                                @RequestParam String descripcion,
                                @RequestParam Long prendaSuperiorId,
                                @RequestParam Long prendaInferiorId,
                                @RequestParam Long prendaCalzadoId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        Prenda prendaSuperiorPrenda = prendaService.buscarPrendaPorId(prendaSuperiorId).orElse(null);
        Prenda prendaInferiorPrenda = prendaService.buscarPrendaPorId(prendaInferiorId).orElse(null);
        Prenda prendaCalzadoPrenda = prendaService.buscarPrendaPorId(prendaCalzadoId).orElse(null);

        if (prendaSuperiorPrenda instanceof PrendaSuperior &&
                prendaInferiorPrenda instanceof PrendaInferior &&
                prendaCalzadoPrenda instanceof PrendaCalzado) {

            // Verificar que las prendas pertenecen al usuario logueado
            if (!prendaSuperiorPrenda.getUsuario().getId().equals(usuario.getId()) ||
                    !prendaInferiorPrenda.getUsuario().getId().equals(usuario.getId()) ||
                    !prendaCalzadoPrenda.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "Una o más prendas no te pertenecen.");
                return "redirect:/conjuntos/crear";
            }

            PrendaSuperior prendaSuperior = (PrendaSuperior) prendaSuperiorPrenda;
            PrendaInferior prendaInferior = (PrendaInferior) prendaInferiorPrenda;
            PrendaCalzado prendaCalzado = (PrendaCalzado) prendaCalzadoPrenda;

            Conjunto nuevoConjunto = new Conjunto(nombre, usuario, descripcion, prendaSuperior, prendaInferior, prendaCalzado);
            conjuntoService.guardarConjunto(nuevoConjunto);
        } else {
            redirectAttributes.addFlashAttribute("error", "Los tipos de prenda seleccionados son incorrectos.");
            return "redirect:/conjuntos/crear";
        }
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
        } else {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar este conjunto.");
        }
        return "redirect:/conjuntos/";
    }
}
