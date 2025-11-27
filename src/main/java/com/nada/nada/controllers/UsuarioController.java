package com.nada.nada.controllers;

import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.UsuarioRepository;
import com.nada.nada.data.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
    public String procesarRegistro(@ModelAttribute Usuario usuario, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
            session.setAttribute("usuarioLogueado", nuevoUsuario);
            // Redirigir a la página de conjuntos
            return "redirect:/conjuntos/";
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
    public String procesarLogin(@RequestParam String username, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<Usuario> u = usuarioService.validarLogin(username, password);

        if (u.isPresent()) {
            session.setAttribute("usuarioLogueado", u.get());
            // Redirigir a la página de conjuntos
            return "redirect:/conjuntos/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Nombre de usuario o contraseña incorrectos.");
            return "redirect:/usuarios/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalida la sesión
        return "redirect:/"; // Redirige a la página de inicio
    }
}
