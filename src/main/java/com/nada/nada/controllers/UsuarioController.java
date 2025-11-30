package com.nada.nada.controllers;

import com.nada.nada.data.model.Usuario;
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

    private UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        // Inyección del servicio encargado de la lógica de usuarios
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String listarUsuarios(Model model) {
        // Carga todos los usuarios y los pone en el modelo para la vista `usuarios.html`
        model.addAttribute("usuarios", usuarioService.buscarTodos());
        return "usuarios";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        // Prepara un objeto Usuario vacío para que el formulario lo enlace (th:object)
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, HttpSession session, RedirectAttributes redirectAttributes) {
        // Recibe los datos del formulario, delega la creación al servicio y almacena al usuario en sesión
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
            // Guardar en sesión para considerar al usuario como logueado después del registro
            session.setAttribute("usuarioLogueado", nuevoUsuario);
            // Redirigir al armario tras registrarse
            return "redirect:/armario/";
        } catch (IllegalArgumentException e) {
            // Si la validación del servicio lanza IllegalArgumentException mostramos el error y volvemos al formulario
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/registro";
        }
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        // Muestra la vista de login
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        // Valida credenciales mediante el servicio
        Optional<Usuario> u = usuarioService.validarLogin(username, password);

        if (u.isPresent()) {
            // Si las credenciales son válidas, guardar usuario en sesión y enviar al armario
            session.setAttribute("usuarioLogueado", u.get());
            return "redirect:/armario/";
        } else {
            // En caso contrario, mostrar mensaje y volver al login
            redirectAttributes.addFlashAttribute("error", "Nombre de usuario o contraseña incorrectos.");
            return "redirect:/usuarios/login";
        }
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST}) // Acepta GET y POST para logout
    // Evita problemas con formularios que no soportan POST
    public String logout(HttpSession session) {
        // Invalidar la sesión para desloguear al usuario
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        // Mostrar la página de perfil del usuario logueado
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            // Si no hay usuario en sesión, redirigir al login
            return "redirect:/usuarios/login";
        }
        // Añadir el usuario al modelo para que la vista pueda mostrar sus datos
        model.addAttribute("usuario", usuarioLogueado);
        return "perfil";
    }

    @PostMapping("/perfil/password")
    public String cambiarPassword(@RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String confirmPassword, HttpSession session, RedirectAttributes redirectAttributes) {
        // Maneja el cambio de contraseña del usuario en sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        // Comprobar que las nuevas contraseñas coinciden antes de delegar al servicio
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden.");
            return "redirect:/usuarios/perfil";
        }

        try {
            // Delegar la lógica de validación y persistencia al servicio
            usuarioService.cambiarPassword(usuarioLogueado.getId(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            // Si el servicio lanza IllegalArgumentException mostramos el mensaje
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios/perfil";
    }

    @PostMapping("/perfil/delete")
    public String eliminarCuenta(HttpSession session, RedirectAttributes redirectAttributes) {
        // Elimina la cuenta del usuario logueado y cierra la sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        try {
            // Delegar eliminación al servicio
            usuarioService.eliminarUsuario(usuarioLogueado.getId());
            // Invalidar sesión después de borrar la cuenta
            session.invalidate();
            redirectAttributes.addFlashAttribute("success", "Tu cuenta ha sido eliminada.");
            return "redirect:/";
        } catch (Exception e) {
            // Si hay cualquier error, mostrar mensaje y volver al perfil
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la cuenta.");
            return "redirect:/usuarios/perfil";
        }
    }
}
