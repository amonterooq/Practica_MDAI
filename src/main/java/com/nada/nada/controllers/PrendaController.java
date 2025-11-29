// java
package com.nada.nada.controllers;

import com.nada.nada.data.model.*;
import com.nada.nada.data.services.PrendaService;
import com.nada.nada.data.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/armario")
public class PrendaController {

    private static final Logger logger = LoggerFactory.getLogger(PrendaController.class);
    // Carpeta base dentro del contenedor Docker. Debe coincidir con el volumen /app/static/images
    private static final String UPLOAD_BASE_DIR = "/app/static/images";

    private final PrendaService prendaService;
    private final UsuarioService usuarioService;

    @Autowired
    public PrendaController(PrendaService prendaService, UsuarioService usuarioService) {
        this.prendaService = prendaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String verArmario(HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(value = "q", required = false) String nombreEmpiezaPor,
                             @RequestParam(value = "tipo", required = false) String tipoPrenda,
                             @RequestParam(value = "categoria", required = false) String categoria,
                             @RequestParam(value = "color", required = false) String color,
                             @RequestParam(value = "marca", required = false) String marca,
                             @RequestParam(value = "talla", required = false) String talla) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Long usuarioId = usuarioLogueado.getId();

        List<Prenda> prendas = prendaService.buscarPrendasFiltradas(
                usuarioId,
                nombreEmpiezaPor,
                tipoPrenda,
                categoria,
                color,
                marca,
                talla
        );
        long totalPrendas = prendaService.contarPrendasPorUsuario(usuarioId);

        model.addAttribute("prendas", prendas);
        model.addAttribute("totalPrendas", totalPrendas);
        model.addAttribute("categoriasSuperior", CategoriaSuperior.values());
        model.addAttribute("categoriasInferior", CategoriaInferior.values());
        model.addAttribute("categoriasCalzado", CategoriaCalzado.values());

        // Volver a pintar los filtros seleccionados en el formulario
        model.addAttribute("filtroNombre", nombreEmpiezaPor);
        model.addAttribute("filtroTipo", tipoPrenda);
        model.addAttribute("filtroCategoria", categoria);
        model.addAttribute("filtroColor", color);
        model.addAttribute("filtroMarca", marca);
        model.addAttribute("filtroTalla", talla);

        return "prenda";
    }

    @PostMapping("/crear")
    public String crearPrenda(@RequestParam("nombre") String nombre,
                              @RequestParam("tipoPrenda") String tipoPrenda,
                              @RequestParam(value = "catSuperior", required = false) CategoriaSuperior catSuperior,
                              @RequestParam(value = "catInferior", required = false) CategoriaInferior catInferior,
                              @RequestParam(value = "catCalzado", required = false) CategoriaCalzado catCalzado,
                              @RequestParam(value = "marca", required = false) String marca,
                              @RequestParam(value = "talla", required = false) String talla,
                              @RequestParam(value = "color", required = false) String color,
                              @RequestParam(value = "imagenRecortada", required = false) String imagenRecortada,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Long usuarioId = usuarioLogueado.getId();
        String dirImagen = null;

        try {
            // si viene base64 del \`input hidden\`, la guardamos en disco
            if (imagenRecortada != null && !imagenRecortada.trim().isEmpty()) {
                dirImagen = guardarImagenBase64EnDisco(imagenRecortada, usuarioId);
            }
        } catch (IOException e) {
            logger.error("Error guardando imagen en disco para usuarioId={}", usuarioId, e);
            redirectAttributes.addFlashAttribute("error", "No se pudo guardar la imagen. La prenda se guardará sin imagen.");
            dirImagen = null;
        }

        Prenda prenda;

        switch (tipoPrenda) {
            case "superior":
                if (catSuperior == null) {
                    redirectAttributes.addFlashAttribute("error", "Debes seleccionar una categoría para la prenda superior.");
                    return "redirect:/armario/";
                }
                PrendaSuperior superior = new PrendaSuperior();
                superior.setNombre(nombre);
                superior.setColor(color);
                superior.setMarca(marca);
                superior.setTalla(talla);
                superior.setUsuario(usuarioLogueado);
                superior.setCategoria(catSuperior);
                superior.setDirImagen(dirImagen);
                prenda = prendaService.guardarPrendaSuperior(superior);
                break;

            case "inferior":
                if (catInferior == null) {
                    redirectAttributes.addFlashAttribute("error", "Debes seleccionar una categoría para la prenda inferior.");
                    return "redirect:/armario/";
                }
                PrendaInferior inferior = new PrendaInferior();
                inferior.setNombre(nombre);
                inferior.setColor(color);
                inferior.setMarca(marca);
                inferior.setTalla(talla);
                inferior.setUsuario(usuarioLogueado);
                inferior.setCategoriaInferior(catInferior);
                inferior.setDirImagen(dirImagen);
                prenda = prendaService.guardarPrendaInferior(inferior);
                break;

            case "calzado":
                if (catCalzado == null) {
                    redirectAttributes.addFlashAttribute("error", "Debes seleccionar una categoría para el calzado.");
                    return "redirect:/armario/";
                }
                PrendaCalzado calzado = new PrendaCalzado();
                calzado.setNombre(nombre);
                calzado.setColor(color);
                calzado.setMarca(marca);
                calzado.setTalla(talla);
                calzado.setUsuario(usuarioLogueado);
                calzado.setCategoria(catCalzado);
                calzado.setDirImagen(dirImagen);
                prenda = prendaService.guardarPrendaCalzado(calzado);
                break;

            default:
                redirectAttributes.addFlashAttribute("error", "Tipo de prenda no válido.");
                return "redirect:/armario/";
        }

        logger.info("Prenda creada id={} tipo={} usuarioId={}",
                prenda.getId(), tipoPrenda, usuarioId);

        redirectAttributes.addFlashAttribute("success", "Prenda añadida correctamente.");
        return "redirect:/armario/";
    }

    @GetMapping("/eliminar")
    public String eliminarPrenda(@RequestParam("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        try {
            boolean borrada = prendaService.borrarPrenda(id);
            if (borrada) {
                redirectAttributes.addFlashAttribute("success", "Prenda eliminada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la prenda.");
            }
        } catch (Exception e) {
            logger.error("Error al eliminar prenda id={}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la prenda.");
        }

        return "redirect:/armario/";
    }

    /**
     * Recibe un data URL en base64 (por ejemplo: `data:image/jpeg;base64,...`)
     * escribe la imagen en `/app/static/images/{usuarioId}/`
     * y devuelve la ruta accesible desde el navegador `/images/{usuarioId}/fichero.jpg`.
     */
    private String guardarImagenBase64EnDisco(String dataUrl, Long usuarioId) throws IOException {
        if (dataUrl == null || dataUrl.trim().isEmpty()) {
            return null;
        }

        int commaIndex = dataUrl.indexOf(",");
        if (commaIndex < 0) {
            logger.warn("Formato data URL inválido: no se encontró coma");
            return null;
        }

        String metadata = dataUrl.substring(0, commaIndex);      // ej: data:image/jpeg;base64
        String base64Part = dataUrl.substring(commaIndex + 1);   // solo la parte base64

        logger.debug("Metadata imagen: {}", metadata);

        byte[] imageBytes = Base64.getDecoder().decode(base64Part);

        Path userDir = Paths.get(UPLOAD_BASE_DIR, String.valueOf(usuarioId));
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
            logger.info("Directorio de imágenes creado: {}", userDir.toAbsolutePath());
        }

        String fileName = "prenda_" + System.currentTimeMillis() + ".jpg";
        Path filePath = userDir.resolve(fileName);

        Files.write(filePath, imageBytes);
        logger.info("Imagen escrita en disco: {}", filePath.toAbsolutePath());

        return "/images/" + usuarioId + "/" + fileName;
    }
}
