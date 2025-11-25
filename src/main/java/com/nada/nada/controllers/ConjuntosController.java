package com.nada.nada.controllers;

import com.nada.nada.data.model.Conjunto;
import com.nada.nada.data.model.Prenda;
import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.services.ConjuntoService;
import com.nada.nada.data.services.PrendaService;
import com.nada.nada.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ConjuntosController {

    @Autowired
    private ConjuntoService conjuntoService;

    @Autowired
    private PrendaService prendaService;

    @Autowired
    private UsuarioService usuarioService;

    // Simulación de un ID de usuario logueado. En una aplicación real, esto se manejaría con Spring Security.
    private final Long USUARIO_ID_SIMULADO = 1L;

    @GetMapping("/conjuntos")
    public String verConjuntos(Model model) {
        List<Conjunto> conjuntos = conjuntoService.buscarConjuntosPorUsuarioId(USUARIO_ID_SIMULADO);
        model.addAttribute("conjuntos", conjuntos);
        return "conjuntos";
    }

    @GetMapping("/conjuntos/crear")
    public String mostrarFormularioCrear(Model model) {
        List<Prenda> prendas = prendaService.buscarPrendasPorUsuarioId(USUARIO_ID_SIMULADO);
        model.addAttribute("prendasSuperiores", prendas.stream().filter(p -> p instanceof com.nada.nada.data.model.PrendaSuperior).collect(Collectors.toList()));
        model.addAttribute("prendasInferiores", prendas.stream().filter(p -> p instanceof com.nada.nada.data.model.PrendaInferior).collect(Collectors.toList()));
        model.addAttribute("prendasCalzados", prendas.stream().filter(p -> p instanceof com.nada.nada.data.model.PrendaCalzado).collect(Collectors.toList()));
        model.addAttribute("conjunto", new Conjunto());
        return "formularioConjunto"; // Se usará una vista de formulario dedicada
    }

    @PostMapping("/conjuntos/crear")
    public String crearConjunto(@RequestParam String nombre,
                                @RequestParam String descripcion,
                                @RequestParam Long prendaSuperiorId,
                                @RequestParam Long prendaInferiorId,
                                @RequestParam Long prendaCalzadoId) {
        Optional<Usuario> usuarioOpt = usuarioService.encontrarPorId(USUARIO_ID_SIMULADO);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Prenda prendaSuperiorPrenda = prendaService.buscarPrendaPorId(prendaSuperiorId).orElse(null);
            Prenda prendaInferiorPrenda = prendaService.buscarPrendaPorId(prendaInferiorId).orElse(null);
            Prenda prendaCalzadoPrenda = prendaService.buscarPrendaPorId(prendaCalzadoId).orElse(null);

            if (prendaSuperiorPrenda instanceof com.nada.nada.data.model.PrendaSuperior &&
                prendaInferiorPrenda instanceof com.nada.nada.data.model.PrendaInferior &&
                prendaCalzadoPrenda instanceof com.nada.nada.data.model.PrendaCalzado) {

                com.nada.nada.data.model.PrendaSuperior prendaSuperior = (com.nada.nada.data.model.PrendaSuperior) prendaSuperiorPrenda;
                com.nada.nada.data.model.PrendaInferior prendaInferior = (com.nada.nada.data.model.PrendaInferior) prendaInferiorPrenda;
                com.nada.nada.data.model.PrendaCalzado prendaCalzado = (com.nada.nada.data.model.PrendaCalzado) prendaCalzadoPrenda;

                Conjunto nuevoConjunto = new Conjunto(nombre, usuario, descripcion, prendaSuperior, prendaInferior, prendaCalzado);
                conjuntoService.guardarConjunto(nuevoConjunto);
            } else {
                // Opcional: manejar el caso de error donde las prendas no son del tipo correcto
                return "redirect:/conjuntos/crear?error=tipo_prenda_incorrecto";
            }
        }
        return "redirect:/conjuntos";
    }

    @GetMapping("/conjuntos/eliminar/{id}")
    public String eliminarConjunto(@PathVariable Long id) {
        conjuntoService.borrarConjunto(id);
        return "redirect:/conjuntos";
    }
}

