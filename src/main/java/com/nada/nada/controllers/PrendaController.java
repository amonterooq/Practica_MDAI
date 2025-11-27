package com.nada.nada.controllers;

import com.nada.nada.data.model.*;
import com.nada.nada.data.services.PrendaService;
import com.nada.nada.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Import necesario para el upload

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/armario") // La URL es /armario
public class PrendaController {

    @Autowired
    private PrendaService prendaService;

    @Autowired
    private UsuarioService usuarioService;

    private final Long USUARIO_ID_SIMULADO = 1L;

    @GetMapping("") // Atiende a /armario o /armario/
    public String verArmario(Model model) {
        List<Prenda> prendas = prendaService.buscarPrendasPorUsuarioId(USUARIO_ID_SIMULADO);
        model.addAttribute("prendas", prendas);
        model.addAttribute("totalPrendas", prendas.size());

        // Enums para los desplegables
        model.addAttribute("categoriasSuperior", CategoriaSuperior.values());
        model.addAttribute("categoriasInferior", CategoriaInferior.values());
        model.addAttribute("categoriasCalzado", CategoriaCalzado.values());

        return "armario"; // IMPORTANTE: Debe coincidir con el nombre de tu archivo HTML
    }

    @PostMapping("/crear")
    public String guardarPrenda(@RequestParam String nombre,
                                @RequestParam String marca,
                                @RequestParam String color,
                                @RequestParam String talla,
                                @RequestParam String tipoPrenda,
                                // Cambiamos imagenUrl por MultipartFile si vas a implementar subida real,
                                // pero para mantener tu logica actual usaremos un String simulado o lógica futura.
                                // Aquí dejo la lógica original de URL para que no rompa, pero ajustada al formulario.
                                @RequestParam(required = false) MultipartFile imagen,
                                @RequestParam(required = false) CategoriaSuperior catSuperior,
                                @RequestParam(required = false) CategoriaInferior catInferior,
                                @RequestParam(required = false) CategoriaCalzado catCalzado) {

        Optional<Usuario> usuarioOpt = usuarioService.encontrarPorId(USUARIO_ID_SIMULADO);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Lógica temporal para la imagen hasta que implementes el servicio de almacenamiento
            String urlFinal = "/images/placeholder_cloth.png";
            if(imagen != null && !imagen.isEmpty()) {
                // Aquí iría tu lógica de guardar archivo y obtener URL
                // urlFinal = servicioDeArchivos.guardar(imagen);
            }

            switch (tipoPrenda) {
                case "superior":
                    if (catSuperior != null) {
                        PrendaSuperior ps = new PrendaSuperior();
                        configurarPrendaBase(ps, nombre, marca, color, talla, urlFinal, usuario);
                        ps.setCategoria(catSuperior);
                        prendaService.guardarPrendaSuperior(ps);
                    }
                    break;
                case "inferior":
                    if (catInferior != null) {
                        PrendaInferior pi = new PrendaInferior();
                        configurarPrendaBase(pi, nombre, marca, color, talla, urlFinal, usuario);
                        pi.setCategoriaInferior(catInferior);
                        prendaService.guardarPrendaInferior(pi);
                    }
                    break;
                case "calzado":
                    if (catCalzado != null) {
                        PrendaCalzado pc = new PrendaCalzado();
                        configurarPrendaBase(pc, nombre, marca, color, talla, urlFinal, usuario);
                        pc.setCategoria(catCalzado);
                        prendaService.guardarPrendaCalzado(pc);
                    }
                    break;
            }
        }
        return "redirect:/armario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPrenda(@PathVariable Long id) {
        prendaService.borrarPrenda(id);
        return "redirect:/armario";
    }

    private void configurarPrendaBase(Prenda prenda, String nombre, String marca, String color, String talla, String img, Usuario user) {
        prenda.setNombre(nombre);
        prenda.setMarca(marca);
        prenda.setColor(color);
        prenda.setTalla(talla);
        prenda.setDirImagen(img); // Asegúrate que tu modelo Prenda tenga setImagenUrl o setDirImagen y sea consistente
        prenda.setUsuario(user);
    }
}