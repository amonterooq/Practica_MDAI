// java
package com.nada.nada.controllers;

import com.nada.nada.data.model.*;
import com.nada.nada.data.model.enums.*;
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
import java.util.*;

/**
 * Controlador encargado de gestionar el armario de prendas del usuario.
 * Permite crear, actualizar, eliminar y filtrar prendas de ropa.
 */
@Controller
@RequestMapping("/armario")
public class PrendaController {

    private static final Logger logger = LoggerFactory.getLogger(PrendaController.class);

    /** Directorio base donde se almacenan las imágenes de prendas (volumen Docker) */
    private static final String UPLOAD_BASE_DIR = "/app/static/images";

    private final PrendaService prendaService;
    private final ConjuntoService conjuntoService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param prendaService servicio para gestionar prendas
     * @param conjuntoService servicio para gestionar conjuntos
     */
    @Autowired
    public PrendaController(PrendaService prendaService, ConjuntoService conjuntoService) {
        this.prendaService = prendaService;
        this.conjuntoService = conjuntoService;
    }

    /**
     * Redirección para URLs sin barra final.
     *
     * @return redirección a /armario/
     */
    @GetMapping
    public String redirigirArmarioRaiz() {
        return "redirect:/armario/";
    }

    /**
     * Muestra el armario del usuario con sus prendas.
     * Soporta múltiples filtros de búsqueda.
     *
     * @param session sesión HTTP con el usuario logueado
     * @param model modelo para pasar datos a la vista
     * @param nombreEmpiezaPor filtro por nombre (contiene)
     * @param tipoPrenda filtro por tipo (superior, inferior, calzado)
     * @param categoria filtro por categoría específica
     * @param color filtro por color
     * @param marca filtro por marca
     * @param talla filtro por talla
     * @return vista del armario o redirección al login
     */
    @GetMapping("/")
    public String verArmario(HttpSession session, Model model,
                             @RequestParam(value = "q", required = false) String nombreEmpiezaPor,
                             @RequestParam(value = "tipo", required = false) String tipoPrenda,
                             @RequestParam(value = "categoria", required = false) String categoria,
                             @RequestParam(value = "color", required = false) String color,
                             @RequestParam(value = "marca", required = false) String marca,
                             @RequestParam(value = "talla", required = false) String talla) {

        // Verificar sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Long usuarioId = usuarioLogueado.getId();

        // Añadir username para el saludo del chat IA
        model.addAttribute("username", usuarioLogueado.getUsername());

        // Obtener prendas filtradas
        List<Prenda> prendas = prendaService.buscarPrendasFiltradas(
                usuarioId, nombreEmpiezaPor, tipoPrenda, categoria, color, marca, talla);
        long totalPrendas = prendaService.contarPrendasPorUsuario(usuarioId);

        // Preparar atributos del modelo
        model.addAttribute("prendas", prendas);
        model.addAttribute("totalPrendas", totalPrendas);

        // Categorías por tipo de prenda
        model.addAttribute("categoriasSuperior", CategoriaSuperior.values());
        model.addAttribute("categoriasInferior", CategoriaInferior.values());
        model.addAttribute("categoriasCalzado", CategoriaCalzado.values());

        // Tallas por tipo de prenda
        model.addAttribute("tallasSuperior", TallaSuperior.values());
        model.addAttribute("tallasInferior", TallaInferior.values());
        model.addAttribute("tallasCalzado", TallaCalzado.values());

        // Marcas y colores para el formulario de creación
        model.addAttribute("marcas", Marca.values());
        model.addAttribute("colores", Color.values());

        // Combinar marcas/colores del enum con los personalizados del usuario
        List<String> marcasFiltro = combinarMarcasParaFiltro(usuarioId);
        List<String> coloresFiltro = combinarColoresParaFiltro(usuarioId);
        model.addAttribute("marcasFiltro", marcasFiltro);
        model.addAttribute("coloresFiltro", coloresFiltro);

        // Mantener valores de filtros en la UI
        model.addAttribute("filtroNombre", nombreEmpiezaPor);
        model.addAttribute("filtroTipo", tipoPrenda);
        model.addAttribute("filtroCategoria", categoria);
        model.addAttribute("filtroColor", color);
        model.addAttribute("filtroMarca", marca);
        model.addAttribute("filtroTalla", talla);

        return "prenda";
    }

    /**
     * Crea una nueva prenda en el armario del usuario.
     *
     * @param nombre nombre de la prenda
     * @param tipoPrenda tipo (superior, inferior, calzado)
     * @param catSuperior categoría si es prenda superior
     * @param catInferior categoría si es prenda inferior
     * @param catCalzado categoría si es calzado
     * @param marca marca de la prenda
     * @param talla talla de la prenda
     * @param color color de la prenda
     * @param imagenRecortada imagen en formato base64
     * @param session sesión HTTP
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al armario
     */
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

        // Verificar sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Long usuarioId = usuarioLogueado.getId();

        // Normalizar la marca (ej: "zAra" -> "Zara")
        String marcaNormalizada = prendaService.normalizarMarca(marca);

        // Validar campos obligatorios
        try {
            prendaService.validarDatosNuevaPrenda(nombre, tipoPrenda, catSuperior, catInferior,
                    catCalzado, marcaNormalizada, talla, color);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/armario/";
        }

        // Procesar imagen si se proporcionó
        String dirImagen = null;
        try {
            if (imagenRecortada != null && !imagenRecortada.trim().isEmpty()) {
                dirImagen = guardarImagenBase64EnDisco(imagenRecortada, usuarioId);
            }
        } catch (IOException e) {
            logger.error("Error guardando imagen para usuarioId={}", usuarioId, e);
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo guardar la imagen. La prenda se guardará sin imagen.");
        }

        // Crear la prenda según su tipo
        Prenda prenda = crearPrendaSegunTipo(tipoPrenda, nombre, color, marcaNormalizada, talla,
                usuarioLogueado, dirImagen, catSuperior, catInferior, catCalzado);

        if (prenda == null) {
            redirectAttributes.addFlashAttribute("error", "Tipo de prenda no válido.");
            return "redirect:/armario/";
        }

        logger.info("Prenda creada id={} tipo={} usuarioId={}", prenda.getId(), tipoPrenda, usuarioId);
        redirectAttributes.addFlashAttribute("success", "Prenda añadida correctamente.");

        return "redirect:/armario/";
    }

    /**
     * Actualiza los datos de una prenda existente.
     *
     * @param id ID de la prenda a actualizar
     * @param nombre nuevo nombre
     * @param tipoPrenda tipo de prenda
     * @param catSuperior nueva categoría superior (si aplica)
     * @param catInferior nueva categoría inferior (si aplica)
     * @param catCalzado nueva categoría calzado (si aplica)
     * @param marca nueva marca
     * @param talla nueva talla
     * @param color nuevo color
     * @param session sesión HTTP
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al armario
     */
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

        // Verificar sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        // Validar nombre obligatorio
        if (nombre == null || nombre.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El nombre de la prenda es obligatorio.");
            return "redirect:/armario/";
        }

        // Normalizar marca y color
        String marcaNormalizada = normalizarMarca(marca);
        String colorNormalizado = normalizarColor(color);

        logger.info("Actualizando prenda id={} nombre='{}' marca='{}' talla='{}' color='{}' tipo={}",
                id, nombre, marcaNormalizada, talla, colorNormalizado, tipoPrenda);

        // Cargar la prenda existente
        Prenda prendaExistente = prendaService.buscarPrendaPorId(id).orElse(null);
        if (prendaExistente == null) {
            redirectAttributes.addFlashAttribute("error", "La prenda que intentas editar no existe.");
            return "redirect:/armario/";
        }

        // Verificar permisos
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
        actualizarCategoriaSegunTipo(prendaExistente, catSuperior, catInferior, catCalzado);

        try {
            prendaService.actualizarPrenda(prendaExistente);
            redirectAttributes.addFlashAttribute("success", "Prenda actualizada correctamente.");
        } catch (Exception e) {
            logger.error("Error al actualizar prenda id={}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la prenda.");
        }

        return "redirect:/armario/";
    }

    /**
     * Endpoint AJAX para obtener información de conjuntos afectados al eliminar una prenda.
     *
     * @param prendaId ID de la prenda
     * @param session sesión HTTP
     * @return mapa con cantidad y nombres de conjuntos afectados
     */
    @GetMapping("/conjuntos-afectados/{prendaId}")
    @ResponseBody
    public Map<String, Object> obtenerConjuntosAfectados(@PathVariable Long prendaId, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return Map.of("error", "No autenticado");
        }

        try {
            // Buscar conjuntos que usan esta prenda
            List<Conjunto> conjuntosAfectados = conjuntoService.buscarConjuntosConPrenda(prendaId);

            // Filtrar solo los conjuntos del usuario (seguridad adicional)
            conjuntosAfectados = conjuntosAfectados.stream()
                .filter(c -> c.getUsuario() != null && c.getUsuario().getId().equals(usuarioLogueado.getId()))
                .toList();

            // Extraer nombres
            List<String> nombresConjuntos = conjuntosAfectados.stream()
                .map(Conjunto::getNombre)
                .toList();

            return Map.of(
                "cantidad", conjuntosAfectados.size(),
                "nombres", nombresConjuntos
            );
        } catch (Exception e) {
            logger.error("Error al obtener conjuntos afectados para prendaId={}", prendaId, e);
            return Map.of("error", "Error al obtener información");
        }
    }

    /**
     * Elimina una prenda del armario del usuario.
     *
     * @param id ID de la prenda a eliminar
     * @param session sesión HTTP
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al armario
     */
    @PostMapping("/eliminar")
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

    // =====================================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // =====================================================================

    /**
     * Combina las marcas del enum con las personalizadas del usuario para los filtros.
     */
    private List<String> combinarMarcasParaFiltro(Long usuarioId) {
        Set<String> marcasSet = new HashSet<>();

        // Añadir marcas del enum
        for (Marca m : Marca.values()) {
            marcasSet.add(m.getEtiqueta());
        }

        // Añadir marcas personalizadas del usuario
        marcasSet.addAll(prendaService.obtenerMarcasDelUsuario(usuarioId));

        // Ordenar alfabéticamente
        List<String> resultado = new ArrayList<>(marcasSet);
        Collections.sort(resultado);
        return resultado;
    }

    /**
     * Combina los colores del enum con los personalizados del usuario para los filtros.
     */
    private List<String> combinarColoresParaFiltro(Long usuarioId) {
        Set<String> coloresSet = new HashSet<>();

        // Añadir colores del enum
        for (Color c : Color.values()) {
            coloresSet.add(c.getEtiqueta());
        }

        // Añadir colores personalizados del usuario
        coloresSet.addAll(prendaService.obtenerColoresDelUsuario(usuarioId));

        // Ordenar alfabéticamente
        List<String> resultado = new ArrayList<>(coloresSet);
        Collections.sort(resultado);
        return resultado;
    }

    /**
     * Crea una instancia de prenda según el tipo especificado.
     */
    private Prenda crearPrendaSegunTipo(String tipoPrenda, String nombre, String color,
                                         String marca, String talla, Usuario usuario,
                                         String dirImagen, CategoriaSuperior catSuperior,
                                         CategoriaInferior catInferior, CategoriaCalzado catCalzado) {
        switch (tipoPrenda) {
            case "superior" -> {
                PrendaSuperior superior = new PrendaSuperior();
                superior.setNombre(nombre);
                superior.setColor(color);
                superior.setMarca(marca);
                superior.setTalla(talla);
                superior.setUsuario(usuario);
                superior.setCategoria(catSuperior);
                superior.setDirImagen(dirImagen);
                return prendaService.guardarPrendaSuperior(superior);
            }
            case "inferior" -> {
                PrendaInferior inferior = new PrendaInferior();
                inferior.setNombre(nombre);
                inferior.setColor(color);
                inferior.setMarca(marca);
                inferior.setTalla(talla);
                inferior.setUsuario(usuario);
                inferior.setCategoriaInferior(catInferior);
                inferior.setDirImagen(dirImagen);
                return prendaService.guardarPrendaInferior(inferior);
            }
            case "calzado" -> {
                PrendaCalzado calzado = new PrendaCalzado();
                calzado.setNombre(nombre);
                calzado.setColor(color);
                calzado.setMarca(marca);
                calzado.setTalla(talla);
                calzado.setUsuario(usuario);
                calzado.setCategoria(catCalzado);
                calzado.setDirImagen(dirImagen);
                return prendaService.guardarPrendaCalzado(calzado);
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Actualiza la categoría de una prenda según su tipo.
     */
    private void actualizarCategoriaSegunTipo(Prenda prenda, CategoriaSuperior catSuperior,
                                               CategoriaInferior catInferior, CategoriaCalzado catCalzado) {
        if (prenda instanceof PrendaSuperior superior && catSuperior != null) {
            superior.setCategoria(catSuperior);
        } else if (prenda instanceof PrendaInferior inferior && catInferior != null) {
            inferior.setCategoriaInferior(catInferior);
        } else if (prenda instanceof PrendaCalzado calzado && catCalzado != null) {
            calzado.setCategoria(catCalzado);
        }
    }

    /**
     * Guarda una imagen en formato base64 en el disco y devuelve la ruta pública.
     *
     * @param dataUrl imagen en formato data URL (base64)
     * @param usuarioId ID del usuario propietario
     * @return ruta relativa de la imagen guardada
     * @throws IOException si hay error al escribir el archivo
     */
    private String guardarImagenBase64EnDisco(String dataUrl, Long usuarioId) throws IOException {
        if (dataUrl == null || dataUrl.trim().isEmpty()) {
            return null;
        }

        // Extraer la parte base64 del data URL
        int commaIndex = dataUrl.indexOf(",");
        if (commaIndex < 0) {
            logger.warn("Formato data URL inválido: no se encontró coma");
            return null;
        }

        String base64Part = dataUrl.substring(commaIndex + 1);
        byte[] imageBytes = Base64.getDecoder().decode(base64Part);

        // Crear directorio del usuario si no existe
        Path userDir = Paths.get(UPLOAD_BASE_DIR, String.valueOf(usuarioId));
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
            logger.info("Directorio de imágenes creado: {}", userDir.toAbsolutePath());
        }

        // Generar nombre único y guardar
        String fileName = "prenda_" + System.currentTimeMillis() + ".jpg";
        Path filePath = userDir.resolve(fileName);
        Files.write(filePath, imageBytes);

        logger.info("Imagen guardada en: {}", filePath.toAbsolutePath());

        // Devolver ruta relativa para uso en la vista
        return "/images/" + usuarioId + "/" + fileName;
    }

    /**
     * Normaliza una marca: si coincide con el enum, usa la etiqueta estándar.
     */
    private String normalizarMarca(String marcaEscrita) {
        if (marcaEscrita == null || marcaEscrita.trim().isEmpty()) {
            return marcaEscrita;
        }

        String marcaTrim = marcaEscrita.trim();
        String marcaLower = marcaTrim.toLowerCase();

        for (Marca m : Marca.values()) {
            if (m.getEtiqueta().toLowerCase().equals(marcaLower)) {
                return m.getEtiqueta();
            }
        }

        return marcaTrim;
    }

    /**
     * Normaliza un color: si coincide con el enum, usa la etiqueta estándar.
     */
    private String normalizarColor(String colorEscrito) {
        if (colorEscrito == null || colorEscrito.trim().isEmpty()) {
            return colorEscrito;
        }

        String colorTrim = colorEscrito.trim();
        String colorLower = colorTrim.toLowerCase();

        for (Color c : Color.values()) {
            if (c.getEtiqueta().toLowerCase().equals(colorLower)) {
                return c.getEtiqueta();
            }
        }

        return colorTrim;
    }
}
