// java
package com.nada.nada.controllers;

import com.nada.nada.data.model.*;
import com.nada.nada.data.services.ConjuntoService;
import com.nada.nada.data.services.PrendaService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    // Logger para información y errores
    private Logger logger = LoggerFactory.getLogger(PrendaController.class);

    // Carpeta base dentro del contenedor Docker donde se guardan las imágenes
    private String UPLOAD_BASE_DIR = "/app/static/images";

    private PrendaService prendaService;
    private ConjuntoService conjuntoService;

    @Autowired
    public PrendaController(PrendaService prendaService, ConjuntoService conjuntoService) {
        this.prendaService = prendaService;
        this.conjuntoService = conjuntoService;
    }

    // Maneja /armario sin barra final redirigiendo a /armario/
    @GetMapping
    public String redirigirArmarioRaiz() {
        return "redirect:/armario/";
    }

    // Nota: La clase se dejara asi de momento porque funciona, falta perfeccionarla y mejorar el codigo.
    @GetMapping("/")
    public String verArmario(HttpSession session, Model model,
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

        // Obtener id de usuario para filtrar prendas
        Long usuarioId = usuarioLogueado.getId();

        // Llamada al servicio para obtener las prendas filtradas por los parámetros recibidos
        List<Prenda> prendas = prendaService.buscarPrendasFiltradas(usuarioId, nombreEmpiezaPor, tipoPrenda, categoria, color, marca, talla);
        long totalPrendas = prendaService.contarPrendasPorUsuario(usuarioId);

        // Preparar atributos del modelo para la vista Thymeleaf
        model.addAttribute("prendas", prendas);
        model.addAttribute("totalPrendas", totalPrendas);
        model.addAttribute("categoriasSuperior", CategoriaSuperior.values());
        model.addAttribute("categoriasInferior", CategoriaInferior.values());
        model.addAttribute("categoriasCalzado", CategoriaCalzado.values());
        // Listas de tallas predefinidas
        model.addAttribute("tallasSuperior", TallaSuperior.values());
        model.addAttribute("tallasInferior", TallaInferior.values());
        model.addAttribute("tallasCalzado", TallaCalzado.values());

        // Listas de marcas y colores para formulario de creación (enums)
        model.addAttribute("marcas", Marca.values());
        model.addAttribute("colores", Color.values());

        // OPCIÓN C: Combinar enums con valores del usuario para los filtros
        // Esto asegura que siempre haya opciones disponibles, incluso para usuarios nuevos

        // Obtener marcas y colores personalizados del usuario
        List<String> marcasUsuario = prendaService.obtenerMarcasDelUsuario(usuarioId);
        List<String> coloresUsuario = prendaService.obtenerColoresDelUsuario(usuarioId);

        // Crear sets para combinar y evitar duplicados
        java.util.Set<String> marcasSet = new java.util.HashSet<>();
        java.util.Set<String> coloresSet = new java.util.HashSet<>();

        // Agregar todas las marcas del enum
        for (Marca m : Marca.values()) {
            marcasSet.add(m.getEtiqueta());
        }

        // Agregar todas las colores del enum
        for (Color c : Color.values()) {
            coloresSet.add(c.getEtiqueta());
        }

        // Agregar marcas y colores personalizados del usuario (si los tiene)
        marcasSet.addAll(marcasUsuario);
        coloresSet.addAll(coloresUsuario);

        // Convertir a listas y ordenar alfabéticamente
        List<String> marcasFiltro = new java.util.ArrayList<>(marcasSet);
        List<String> coloresFiltro = new java.util.ArrayList<>(coloresSet);
        java.util.Collections.sort(marcasFiltro);
        java.util.Collections.sort(coloresFiltro);

        model.addAttribute("marcasFiltro", marcasFiltro);
        model.addAttribute("coloresFiltro", coloresFiltro);

        // Volver a pintar los valores de filtro en la UI
        model.addAttribute("filtroNombre", nombreEmpiezaPor);
        model.addAttribute("filtroTipo", tipoPrenda);
        model.addAttribute("filtroCategoria", categoria);
        model.addAttribute("filtroColor", color);
        model.addAttribute("filtroMarca", marca);
        model.addAttribute("filtroTalla", talla);

        // Devolver la plantilla que muestra el armario
        return "prenda";
    }

    @PostMapping("/crear")
    public String crearPrenda(@RequestParam("nombre") String nombre, @RequestParam("tipoPrenda") String tipoPrenda,
                              @RequestParam(value = "catSuperior", required = false) CategoriaSuperior catSuperior,
                              @RequestParam(value = "catInferior", required = false) CategoriaInferior catInferior,
                              @RequestParam(value = "catCalzado", required = false) CategoriaCalzado catCalzado,
                              @RequestParam(value = "marca", required = false) String marca,
                              @RequestParam(value = "talla", required = false) String talla,
                              @RequestParam(value = "color", required = false) String color,
                              @RequestParam(value = "imagenRecortada", required = false) String imagenRecortada,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        // Verificar sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Long usuarioId = usuarioLogueado.getId();

        // Normalizar la marca (mapea zAra -> Zara si existe en la lista)
        String marcaNormalizada = prendaService.normalizarMarca(marca);

        // Validación de campos obligatorios delegada al servicio
        try {
            prendaService.validarDatosNuevaPrenda(nombre, tipoPrenda, catSuperior, catInferior, catCalzado, marcaNormalizada, talla, color);
        } catch (IllegalArgumentException e) {
            // Si hay un error de validación, añadimos un flash y redirigimos
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/armario/";
        }

        String dirImagen = null;
        try {
            // Si el formulario envía una imagen en base64 la guardamos en disco
            if (imagenRecortada != null && !imagenRecortada.trim().isEmpty()) {
                dirImagen = guardarImagenBase64EnDisco(imagenRecortada, usuarioId);
            }
        } catch (IOException e) {
            // En caso de fallo al escribir la imagen, lo registramos y permitimos guardar la prenda sin imagen
            logger.error("Error guardando imagen en disco para usuarioId={}", usuarioId, e);
            redirectAttributes.addFlashAttribute("error", "No se pudo guardar la imagen. La prenda se guardará sin imagen.");
            dirImagen = null;
        }

        Prenda prenda;

        // Según el tipo de prenda, creamos la subclase correspondiente y la persistimos mediante el servicio
        switch (tipoPrenda) {
            case "superior" -> {
                PrendaSuperior superior = new PrendaSuperior();
                superior.setNombre(nombre);
                superior.setColor(color);
                superior.setMarca(marcaNormalizada);
                superior.setTalla(talla);
                superior.setUsuario(usuarioLogueado);
                superior.setCategoria(catSuperior);
                superior.setDirImagen(dirImagen);
                prenda = prendaService.guardarPrendaSuperior(superior);
            }
            case "inferior" -> {
                PrendaInferior inferior = new PrendaInferior();
                inferior.setNombre(nombre);
                inferior.setColor(color);
                inferior.setMarca(marcaNormalizada);
                inferior.setTalla(talla);
                inferior.setUsuario(usuarioLogueado);
                inferior.setCategoriaInferior(catInferior);
                inferior.setDirImagen(dirImagen);
                prenda = prendaService.guardarPrendaInferior(inferior);
            }
            case "calzado" -> {
                PrendaCalzado calzado = new PrendaCalzado();
                calzado.setNombre(nombre);
                calzado.setColor(color);
                calzado.setMarca(marcaNormalizada);
                calzado.setTalla(talla);
                calzado.setUsuario(usuarioLogueado);
                calzado.setCategoria(catCalzado);
                calzado.setDirImagen(dirImagen);
                prenda = prendaService.guardarPrendaCalzado(calzado);
            }
            default -> {
                redirectAttributes.addFlashAttribute("error", "Tipo de prenda no válido.");
                return "redirect:/armario/";
            }
        }

        logger.info("Prenda creada id={} tipo={} usuarioId={}", prenda.getId(), tipoPrenda, usuarioId);

        redirectAttributes.addFlashAttribute("success", "Prenda añadida correctamente.");
        return "redirect:/armario/";
    }

    @PostMapping("/actualizar")
    public String actualizarPrenda(@RequestParam("id") Long id,
                                   @RequestParam("nombre") String nombre,
                                   @RequestParam("tipoPrenda") String tipoPrenda,
                                   @RequestParam(value = "catSuperior", required = false) CategoriaSuperior catSuperior,
                                   @RequestParam(value = "catInferior", required = false) CategoriaInferior catInferior,
                                   @RequestParam(value = "catCalzado", required = false) CategoriaCalzado catCalzado,
                                   @RequestParam(value = "marca", required = false) String marca,
                                   @RequestParam(value = "talla", required = false) String talla,
                                   @RequestParam(value = "color", required = false) String color,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        // Procesa la actualización de una prenda existente

        // Comprobar sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        // Validación ligera: solo nombre obligatorio en la edición
        if (nombre == null || nombre.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El nombre de la prenda es obligatorio.");
            return "redirect:/armario/";
        }

        // Normalizar marca y color (si el usuario escribe algo que coincide con el enum, usamos el valor del enum)
        String marcaNormalizada = normalizarMarca(marca);
        String colorNormalizado = normalizarColor(color);

        logger.info("Actualizando prenda id={} nombre='{}' marca='{}' talla='{}' color='{}' tipo={}",
                id, nombre, marcaNormalizada, talla, colorNormalizado, tipoPrenda);

        // Cargar la prenda y comprobar permisos
        Prenda prendaExistente = prendaService.buscarPrendaPorId(id).orElse(null);
        if (prendaExistente == null) {
            redirectAttributes.addFlashAttribute("error", "La prenda que intentas editar no existe.");
            return "redirect:/armario/";
        }

        if (prendaExistente.getUsuario() == null ||
                !prendaExistente.getUsuario().getId().equals(usuarioLogueado.getId())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar esta prenda.");
            return "redirect:/armario/";
        }

        // Actualizar campos básicos
        prendaExistente.setNombre(nombre);
        prendaExistente.setMarca(marcaNormalizada);
        prendaExistente.setTalla(talla);
        prendaExistente.setColor(colorNormalizado);

        // Actualizar categoría específica según la subclase
        if (prendaExistente instanceof PrendaSuperior superior) {
            if (catSuperior != null) {
                superior.setCategoria(catSuperior);
            }
        } else if (prendaExistente instanceof PrendaInferior inferior) {
            if (catInferior != null) {
                inferior.setCategoriaInferior(catInferior);
            }
        } else if (prendaExistente instanceof PrendaCalzado calzado) {
            if (catCalzado != null) {
                calzado.setCategoria(catCalzado);
            }
        }

        try {
            // Delegar persistencia al servicio
            prendaService.actualizarPrenda(prendaExistente);
            redirectAttributes.addFlashAttribute("success", "Prenda actualizada correctamente.");
        } catch (Exception e) {
            logger.error("Error al actualizar prenda id={}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la prenda.");
            return "redirect:/armario/";
        }

        return "redirect:/armario/";
    }

    @GetMapping("/conjuntos-afectados/{prendaId}")
    @ResponseBody
    public java.util.Map<String, Object> obtenerConjuntosAfectados(@PathVariable Long prendaId, HttpSession session) {
        // Devuelve información sobre los conjuntos que serán eliminados si se borra esta prenda

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return java.util.Map.of("error", "No autenticado");
        }

        try {
            List<Conjunto> conjuntosAfectados = conjuntoService.buscarConjuntosConPrenda(prendaId);

            // Filtrar solo los conjuntos del usuario (seguridad adicional)
            conjuntosAfectados = conjuntosAfectados.stream()
                .filter(c -> c.getUsuario() != null && c.getUsuario().getId().equals(usuarioLogueado.getId()))
                .toList();

            // Extraer solo los nombres de los conjuntos
            List<String> nombresConjuntos = conjuntosAfectados.stream()
                .map(Conjunto::getNombre)
                .toList();

            return java.util.Map.of(
                "cantidad", conjuntosAfectados.size(),
                "nombres", nombresConjuntos
            );
        } catch (Exception e) {
            logger.error("Error al obtener conjuntos afectados para prendaId={}", prendaId, e);
            return java.util.Map.of("error", "Error al obtener información");
        }
    }

    @PostMapping("/eliminar")
    public String eliminarPrenda(@RequestParam("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        // Elimina una prenda si pertenece al usuario logueado

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

    private String guardarImagenBase64EnDisco(String dataUrl, Long usuarioId) throws IOException {
        // Convierte un data URL (base64) en archivo en disco y devuelve la ruta pública relativa

        if (dataUrl == null || dataUrl.trim().isEmpty()) {
            return null;
        }

        int commaIndex = dataUrl.indexOf(",");
        if (commaIndex < 0) {
            logger.warn("Formato data URL inválido: no se encontró coma");
            return null;
        }

        // metadata contiene algo como: data:image/jpeg;base64
        String metadata = dataUrl.substring(0, commaIndex);      // ej: data:image/jpeg;base64
        String base64Part = dataUrl.substring(commaIndex + 1);   // solo la parte base64

        logger.debug("Metadata imagen: {}", metadata);

        byte[] imageBytes = Base64.getDecoder().decode(base64Part);

        // Crear carpeta del usuario dentro del directorio base si no existe
        Path userDir = Paths.get(UPLOAD_BASE_DIR, String.valueOf(usuarioId));
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
            logger.info("Directorio de imágenes creado: {}", userDir.toAbsolutePath());
        }

        // Generar nombre único por timestamp y escribir fichero
        String fileName = "prenda_" + System.currentTimeMillis() + ".jpg";
        Path filePath = userDir.resolve(fileName);

        Files.write(filePath, imageBytes);
        logger.info("Imagen escrita en disco: {}", filePath.toAbsolutePath());

        // Devolvemos la ruta relativa que usará la vista para mostrar la imagen
        return "/images/" + usuarioId + "/" + fileName;
    }

    /**
     * Normaliza una marca escrita por el usuario.
     * Si coincide (ignorando mayúsculas) con alguna del enum, devuelve la etiqueta correcta del enum.
     * Si no coincide, devuelve el valor tal cual (marca personalizada).
     */
    private String normalizarMarca(String marcaEscrita) {
        if (marcaEscrita == null || marcaEscrita.trim().isEmpty()) {
            return marcaEscrita;
        }

        String marcaTrim = marcaEscrita.trim();
        String marcaLower = marcaTrim.toLowerCase();

        // Buscar en el enum si coincide (ignorando mayúsculas)
        for (Marca m : Marca.values()) {
            if (m.getEtiqueta().toLowerCase().equals(marcaLower)) {
                logger.debug("Marca '{}' normalizada a '{}'", marcaEscrita, m.getEtiqueta());
                return m.getEtiqueta();
            }
        }

        // No coincide con ninguna del enum, devolver tal cual (marca personalizada)
        logger.debug("Marca '{}' es personalizada, se mantiene tal cual", marcaEscrita);
        return marcaTrim;
    }

    /**
     * Normaliza un color escrito por el usuario.
     * Si coincide (ignorando mayúsculas) con alguno del enum, devuelve la etiqueta correcta del enum.
     * Si no coincide, devuelve el valor tal cual (color personalizado).
     */
    private String normalizarColor(String colorEscrita) {
        if (colorEscrita == null || colorEscrita.trim().isEmpty()) {
            return colorEscrita;
        }

        String colorTrim = colorEscrita.trim();
        String colorLower = colorTrim.toLowerCase();

        // Buscar en el enum si coincide (ignorando mayúsculas)
        for (Color c : Color.values()) {
            if (c.getEtiqueta().toLowerCase().equals(colorLower)) {
                logger.debug("Color '{}' normalizado a '{}'", colorEscrita, c.getEtiqueta());
                return c.getEtiqueta();
            }
        }

        // No coincide con ninguno del enum, devolver tal cual (color personalizado)
        logger.debug("Color '{}' es personalizado, se mantiene tal cual", colorEscrita);
        return colorTrim;
    }
}
