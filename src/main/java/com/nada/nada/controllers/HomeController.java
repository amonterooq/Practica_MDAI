package com.nada.nada.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la página principal de la aplicación.
 * Gestiona las peticiones a la raíz del sitio web.
 */
@Controller
public class HomeController {

    /**
     * Maneja las peticiones GET a la raíz de la aplicación.
     *
     * @return nombre de la vista "index" que será renderizada por Thymeleaf
     */
    @GetMapping("/")
    public String home() {
        // Renderiza la plantilla templates/index.html
        return "index";
    }
}