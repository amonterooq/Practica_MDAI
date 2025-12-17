package com.nada.nada.controllers;

import com.nada.nada.data.model.Conjunto;
import com.nada.nada.data.model.PrendaCalzado;
import com.nada.nada.data.model.PrendaInferior;
import com.nada.nada.data.model.PrendaSuperior;
import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.services.ConjuntoService;
import com.nada.nada.data.services.PrendaService;
import com.nada.nada.data.services.RecomendadorConjuntosService;
import com.nada.nada.dto.chat.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para el chatbot de recomendación de conjuntos.
 * Proporciona endpoints API para la comunicación del widget de chat
 * y la generación de recomendaciones personalizadas.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final RecomendadorConjuntosService recomendadorConjuntosService;
    private final PrendaService prendaService;
    private final ConjuntoService conjuntoService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param recomendadorConjuntosService servicio de recomendación de conjuntos
     * @param prendaService servicio para gestionar prendas
     * @param conjuntoService servicio para gestionar conjuntos
     */
    public ChatController(RecomendadorConjuntosService recomendadorConjuntosService,
                          PrendaService prendaService,
                          ConjuntoService conjuntoService) {
        this.recomendadorConjuntosService = recomendadorConjuntosService;
        this.prendaService = prendaService;
        this.conjuntoService = conjuntoService;
    }

    /**
     * Endpoint principal del chat para mensajes de texto libre.
     * Analiza el mensaje del usuario y devuelve una respuesta contextual.
     *
     * @param request DTO con el mensaje del usuario
     * @return respuesta del chatbot
     */
    @PostMapping
    public ResponseEntity<ChatResponseDto> chat(@RequestBody ChatRequestDto request) {
        // Validar que el mensaje no esté vacío
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ChatResponseDto("Necesito que escribas algún mensaje para poder ayudarte."));
        }

        String message = request.getMessage().toLowerCase();
        String reply;

        // Analizar el contenido del mensaje y generar respuesta apropiada
        if (message.contains("recomienda") || message.contains("conjunto") || message.contains("look")) {
            reply = "Puedo recomendarte conjuntos usando tu armario. Pulsa el botón 'Recomendar conjunto' para empezar.";
        } else if (message.contains("frío") || message.contains("invierno")) {
            reply = "Parece que hace frío. Te recomendaría combinar capas: una prenda superior abrigada, pantalones largos y un calzado cerrado.";
        } else if (message.contains("calor") || message.contains("verano")) {
            reply = "Para días de calor, busca prendas ligeras y transpirables, colores claros y calzado cómodo, como zapatillas o sandalias cerradas.";
        } else if (message.contains("cita") || message.contains("noche")) {
            reply = "Para una cita de noche, puedes elegir una parte de arriba más elegante, una parte de abajo neutra y unos zapatos que destaquen sin perder comodidad.";
        } else {
            reply = "De momento soy un prototipo, pero ya puedo recomendarte conjuntos usando las prendas de tu armario. Pulsa el botón 'Recomendar conjunto'.";
        }

        return ResponseEntity.ok(new ChatResponseDto(reply));
    }

    /**
     * Obtiene las opciones de filtrado disponibles en el armario del usuario.
     * Devuelve los colores y marcas únicos de sus prendas.
     *
     * @param session sesión HTTP con el usuario logueado
     * @return mapa con listas de colores y marcas disponibles
     */
    @GetMapping("/opciones-armario")
    public ResponseEntity<?> obtenerOpcionesArmario(HttpSession session) {
        // Verificar autenticación
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long usuarioId = usuarioLogueado.getId();

        // Obtener opciones únicas del armario del usuario
        List<String> colores = prendaService.obtenerColoresDelUsuario(usuarioId);
        List<String> marcas = prendaService.obtenerMarcasDelUsuario(usuarioId);

        // Construir respuesta
        Map<String, Object> body = new HashMap<>();
        body.put("colores", colores);
        body.put("marcas", marcas);

        return ResponseEntity.ok(body);
    }

    /**
     * Obtiene las prendas del usuario filtradas por tipo.
     * Usado por el chat para mostrar opciones de selección.
     *
     * @param tipo tipo de prenda (SUPERIOR, INFERIOR, CALZADO)
     * @param session sesión HTTP con el usuario logueado
     * @return lista de prendas del tipo especificado
     */
    @GetMapping("/prendas-por-tipo")
    public ResponseEntity<?> obtenerPrendasPorTipo(@RequestParam("tipo") String tipo, HttpSession session) {
        // Verificar autenticación
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long usuarioId = usuarioLogueado.getId();
        List<Map<String, Object>> prendas;

        // Filtrar y mapear prendas según el tipo solicitado
        switch (tipo.toUpperCase()) {
            case "SUPERIOR":
                prendas = prendaService.buscarPrendasPorUsuarioId(usuarioId).stream()
                    .filter(p -> p instanceof PrendaSuperior)
                    .map(p -> mapearPrendaSuperiorAMap((PrendaSuperior) p))
                    .collect(Collectors.toList());
                break;
            case "INFERIOR":
                prendas = prendaService.buscarPrendasPorUsuarioId(usuarioId).stream()
                    .filter(p -> p instanceof PrendaInferior)
                    .map(p -> mapearPrendaInferiorAMap((PrendaInferior) p))
                    .collect(Collectors.toList());
                break;
            case "CALZADO":
                prendas = prendaService.buscarPrendasPorUsuarioId(usuarioId).stream()
                    .filter(p -> p instanceof PrendaCalzado)
                    .map(p -> mapearPrendaCalzadoAMap((PrendaCalzado) p))
                    .collect(Collectors.toList());
                break;
            default:
                return ResponseEntity.badRequest().body(Map.of("error", "Tipo de prenda no válido"));
        }

        return ResponseEntity.ok(prendas);
    }

    /**
     * Genera una recomendación de conjunto basada en las preferencias del usuario.
     *
     * @param request DTO con las preferencias de recomendación
     * @param session sesión HTTP con el usuario logueado
     * @return DTO con la recomendación generada
     */
    @PostMapping("/recomendar-conjunto")
    public ResponseEntity<RecomendacionConjuntoResponseDto> recomendarConjunto(
            @RequestBody RecomendacionConjuntoRequestDto request,
            HttpSession session) {
        // Verificar autenticación
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            RecomendacionConjuntoResponseDto dto = new RecomendacionConjuntoResponseDto();
            dto.setMensaje("Debes iniciar sesión para que pueda revisar tu armario y recomendarte conjuntos.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dto);
        }

        // Delegar la generación de recomendación al servicio especializado
        Long usuarioId = usuarioLogueado.getId();
        RecomendacionConjuntoResponseDto dto = recomendadorConjuntosService.recomendarConjunto(usuarioId, request);

        return ResponseEntity.ok(dto);
    }

    /**
     * Guarda un conjunto recomendado por el chat en el armario del usuario.
     *
     * @param request DTO con los datos del conjunto a guardar
     * @param session sesión HTTP con el usuario logueado
     * @return respuesta indicando éxito o error
     */
    @PostMapping("/guardar-conjunto")
    public ResponseEntity<ChatActionResponseDto> guardarConjuntoDesdeChat(
            @RequestBody GuardarConjuntoChatRequestDto request,
            HttpSession session) {
        // Verificar autenticación
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ChatActionResponseDto(false, "Debes iniciar sesión para guardar conjuntos."));
        }

        // Validar que se han proporcionado todas las prendas
        if (request == null || request.getPrendaSuperiorId() == null
                || request.getPrendaInferiorId() == null || request.getPrendaCalzadoId() == null) {
            return ResponseEntity.badRequest()
                    .body(new ChatActionResponseDto(false, "Faltan prendas para poder guardar el conjunto."));
        }

        // Validar nombre del conjunto
        String nombre = request.getNombre() != null ? request.getNombre().trim() : "";
        if (nombre.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ChatActionResponseDto(false, "El conjunto necesita un nombre."));
        }

        Long usuarioId = usuarioLogueado.getId();

        // Recuperar y validar cada prenda (verificando tipo y pertenencia)
        PrendaSuperior superior = (PrendaSuperior) prendaService.buscarPrendaPorId(request.getPrendaSuperiorId())
                .filter(p -> p instanceof PrendaSuperior && p.getUsuario() != null && usuarioId.equals(p.getUsuario().getId()))
                .orElse(null);
        PrendaInferior inferior = (PrendaInferior) prendaService.buscarPrendaPorId(request.getPrendaInferiorId())
                .filter(p -> p instanceof PrendaInferior && p.getUsuario() != null && usuarioId.equals(p.getUsuario().getId()))
                .orElse(null);
        PrendaCalzado calzado = (PrendaCalzado) prendaService.buscarPrendaPorId(request.getPrendaCalzadoId())
                .filter(p -> p instanceof PrendaCalzado && p.getUsuario() != null && usuarioId.equals(p.getUsuario().getId()))
                .orElse(null);

        // Verificar que todas las prendas existen y pertenecen al usuario
        if (superior == null || inferior == null || calzado == null) {
            return ResponseEntity.badRequest()
                    .body(new ChatActionResponseDto(false, "Alguna de las prendas seleccionadas ya no existe o no pertenece a tu armario."));
        }

        // Crear y configurar el conjunto
        Conjunto conjunto = new Conjunto();
        conjunto.setNombre(nombre);
        conjunto.setDescripcion(request.getNotas());
        conjunto.setUsuario(usuarioLogueado);
        conjunto.setPrendaSuperior(superior);
        conjunto.setPrendaInferior(inferior);
        conjunto.setPrendaCalzado(calzado);

        // Persistir el conjunto
        try {
            Conjunto guardado = conjuntoService.guardarConjunto(conjunto);
            return ResponseEntity.ok(new ChatActionResponseDto(true,
                    "Conjunto guardado correctamente.", guardado.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatActionResponseDto(false, "Ha ocurrido un error al guardar el conjunto. Inténtalo de nuevo más tarde."));
        }
    }

    // =====================================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // =====================================================================

    /**
     * Convierte una PrendaSuperior a un Map para serialización JSON.
     */
    private Map<String, Object> mapearPrendaSuperiorAMap(PrendaSuperior ps) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", ps.getId());
        dto.put("nombre", ps.getNombre());
        dto.put("color", ps.getColor());
        dto.put("marca", ps.getMarca());
        dto.put("categoria", ps.getCategoria() != null ? ps.getCategoria().getEtiqueta() : null);
        dto.put("imagenUrl", ps.getDirImagen());
        return dto;
    }

    /**
     * Convierte una PrendaInferior a un Map para serialización JSON.
     */
    private Map<String, Object> mapearPrendaInferiorAMap(PrendaInferior pi) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", pi.getId());
        dto.put("nombre", pi.getNombre());
        dto.put("color", pi.getColor());
        dto.put("marca", pi.getMarca());
        dto.put("categoria", pi.getCategoriaInferior() != null ? pi.getCategoriaInferior().getEtiqueta() : null);
        dto.put("imagenUrl", pi.getDirImagen());
        return dto;
    }

    /**
     * Convierte una PrendaCalzado a un Map para serialización JSON.
     */
    private Map<String, Object> mapearPrendaCalzadoAMap(PrendaCalzado pc) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", pc.getId());
        dto.put("nombre", pc.getNombre());
        dto.put("color", pc.getColor());
        dto.put("marca", pc.getMarca());
        dto.put("categoria", pc.getCategoria() != null ? pc.getCategoria().getEtiqueta() : null);
        dto.put("imagenUrl", pc.getDirImagen());
        return dto;
    }
}
