package com.nada.nada.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador personalizado para el manejo de errores.
 * Implementa ErrorController para interceptar errores HTTP (404, 500, etc.)
 * y evitar que se muestre la página de error por defecto de Spring Boot (Whitelabel Error Page).
 */
@Controller
public class ErrorControllerCustom implements ErrorController {

    /**
     * Maneja todas las peticiones que resultan en error.
     * Redirige al usuario a la página principal en lugar de mostrar una página de error.
     *
     * @param request la petición HTTP que causó el error
     * @return redirección a la página principal
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Se podría distinguir entre códigos de error leyendo:
        // Object status = request.getAttribute("jakarta.servlet.error.status_code");
        // Por simplicidad, siempre redirigimos a la página principal
        return "redirect:/";
    }
}
