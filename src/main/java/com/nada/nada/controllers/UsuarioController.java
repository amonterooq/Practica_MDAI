package com.nada.nada.controllers;

import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.UsuarioRepository;
import com.nada.nada.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.buscarTodos());
        return "usuarios";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.crearUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/usuarios/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/registro";
        }
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        Optional<Usuario> u = usuarioService.validarLogin(username, password);

        if (u.isPresent()) {
            // Login exitoso
            return "redirect:/usuarios/"; // Redirige a la lista de usuarios
        } else {
            redirectAttributes.addFlashAttribute("error", "Nombre de usuario o contraseña incorrectos.");
            return "redirect:/usuarios/login";
        }
    }
}
