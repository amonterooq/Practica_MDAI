package com.nada.nada.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador global de errores para evitar la Whitelabel Error Page.
 * Cualquier error (incluido 404) redirige a la página principal.
 */
@Controller
public class ErrorControllerCustom implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Si quisieras distinguir códigos, puedes leer el status_code aquí:
        // Object status = request.getAttribute("jakarta.servlet.error.status_code");
        // Por simplicidad, siempre redirigimos a la home.
        return "redirect:/";
    }
}

