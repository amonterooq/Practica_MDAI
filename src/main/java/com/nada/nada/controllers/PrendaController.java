package com.nada.nada.controllers;

import com.nada.nada.data.model.*;
import com.nada.nada.data.services.PrendaService;
import com.nada.nada.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/armario")
public class PrendaController {

    private final PrendaService prendaService;
    private final UsuarioService usuarioService;

    // Directorio para guardar las imágenes subidas
    private static final String UPLOAD_DIR = "src/main/resources/static/images/uploads/";

    @Autowired
    public PrendaController(PrendaService prendaService, UsuarioService usuarioService) {
        this.prendaService = prendaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String mostrarArmario(Model model) {
        // Simulación de un ID de usuario logueado. Esto debería obtenerse de la sesión.
        Long usuarioId = 1L;
        List<Prenda> prendas = prendaService.buscarPrendasPorUsuarioId(usuarioId);
        model.addAttribute("prendas", prendas);
        model.addAttribute("totalPrendas", prendas.size());

        // Añadir categorías para los dropdowns del formulario
        model.addAttribute("categoriasSuperior", Arrays.asList(CategoriaSuperior.values()));
        model.addAttribute("categoriasInferior", Arrays.asList(CategoriaInferior.values()));
        model.addAttribute("categoriasCalzado", Arrays.asList(CategoriaCalzado.values()));

        return "prenda";
    }

    @PostMapping("/crear")
    public String crearPrenda(@RequestParam("nombre") String nombre,
                              @RequestParam("tipoPrenda") String tipoPrenda,
                              @RequestParam(value = "catSuperior", required = false) CategoriaSuperior catSuperior,
                              @RequestParam(value = "catInferior", required = false) CategoriaInferior catInferior,
                              @RequestParam(value = "catCalzado", required = false) CategoriaCalzado catCalzado,
                              @RequestParam("marca") String marca,
                              @RequestParam("talla") String talla,
                              @RequestParam("color") String color,
                              @RequestParam("imagen") MultipartFile imagen) {

        // Simulación de un usuario logueado
        Long usuarioId = 1L;
        Optional<Usuario> usuarioOpt = usuarioService.encontrarPorId(usuarioId);
        if (!usuarioOpt.isPresent()) {
            // Manejar el caso en que el usuario no se encuentra
            return "redirect:/error";
        }
        Usuario usuario = usuarioOpt.get();

        String imagenUrl = null;
        if (!imagen.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String originalFilename = imagen.getOriginalFilename();
                Path filePath = uploadPath.resolve(originalFilename);
                Files.copy(imagen.getInputStream(), filePath);
                imagenUrl = "/images/uploads/" + originalFilename;
            } catch (IOException e) {
                // Manejar error de subida de archivo
                e.printStackTrace();
            }
        }

        Prenda nuevaPrenda = null;
        switch (tipoPrenda) {
            case "superior":
                PrendaSuperior ps = new PrendaSuperior();
                ps.setCategoria(catSuperior);
                nuevaPrenda = ps;
                break;
            case "inferior":
                PrendaInferior pi = new PrendaInferior();
                pi.setCategoriaInferior(catInferior);
                nuevaPrenda = pi;
                break;
            case "calzado":
                PrendaCalzado pc = new PrendaCalzado();
                pc.setCategoria(catCalzado);
                nuevaPrenda = pc;
                break;
        }

        if (nuevaPrenda != null) {
            nuevaPrenda.setNombre(nombre);
            nuevaPrenda.setMarca(marca);
            nuevaPrenda.setTalla(talla);
            nuevaPrenda.setColor(color);
            nuevaPrenda.setUsuario(usuario);
            nuevaPrenda.setDirImagen(imagenUrl); // Usamos dirImagen para la URL
            prendaService.guardarPrenda(nuevaPrenda);
        }

        return "redirect:/armario/";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPrenda(@PathVariable("id") Long id) {
        prendaService.borrarPrenda(id);
        return "redirect:/armario/";
    }
}
