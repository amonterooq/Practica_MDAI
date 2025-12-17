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

/**
 * Controlador encargado de gestionar las operaciones relacionadas con usuarios.
 * Incluye registro, login, logout, visualización de perfil y gestión de cuenta.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioService servicio que contiene la lógica de negocio de usuarios
     */
    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todos los usuarios del sistema.
     *
     * @param model modelo para pasar datos a la vista
     * @return vista con la lista de usuarios
     */
    @GetMapping("/")
    public String listarUsuarios(Model model) {
        // Carga todos los usuarios y los añade al modelo para la vista
        model.addAttribute("usuarios", usuarioService.buscarTodos());
        return "usuarios";
    }

    /**
     * Muestra el formulario de registro de nuevo usuario.
     *
     * @param model modelo para pasar el objeto Usuario vacío a la vista
     * @return vista del formulario de registro
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        // Prepara un objeto Usuario vacío para el binding del formulario (th:object)
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    /**
     * Procesa el registro de un nuevo usuario.
     *
     * @param usuario datos del usuario recibidos del formulario
     * @param session sesión HTTP para almacenar el usuario logueado
     * @param redirectAttributes atributos flash para mensajes de error
     * @return redirección al armario si éxito, o al formulario si hay error
     */
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            // Delegar la creación al servicio, que valida y persiste
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);

            // Guardar en sesión para considerar al usuario como logueado
            session.setAttribute("usuarioLogueado", nuevoUsuario);

            return "redirect:/armario/";
        } catch (IllegalArgumentException e) {
            // Mostrar error de validación y volver al formulario
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/registro";
        }
    }

    /**
     * Muestra el formulario de inicio de sesión.
     *
     * @return vista del formulario de login
     */
    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    /**
     * Procesa el inicio de sesión del usuario.
     *
     * @param username nombre de usuario
     * @param password contraseña
     * @param session sesión HTTP para almacenar el usuario
     * @param redirectAttributes atributos flash para mensajes de error
     * @return redirección al armario si éxito, o al login si falla
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username, @RequestParam String password,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        // Validar credenciales mediante el servicio
        Optional<Usuario> usuarioOpt = usuarioService.validarLogin(username, password);

        if (usuarioOpt.isPresent()) {
            // Credenciales válidas: guardar usuario en sesión
            session.setAttribute("usuarioLogueado", usuarioOpt.get());
            return "redirect:/armario/";
        } else {
            // Credenciales inválidas: mostrar mensaje de error
            redirectAttributes.addFlashAttribute("error", "Nombre de usuario o contraseña incorrectos.");
            return "redirect:/usuarios/login";
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     * Acepta tanto GET como POST para compatibilidad con diferentes formularios.
     *
     * @param session sesión HTTP a invalidar
     * @return redirección a la página principal
     */
    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session) {
        // Invalidar la sesión para cerrar la sesión del usuario
        session.invalidate();
        return "redirect:/";
    }

    /**
     * Muestra la página de perfil del usuario logueado.
     *
     * @param session sesión HTTP con el usuario logueado
     * @param model modelo para pasar datos a la vista
     * @return vista de perfil o redirección al login si no hay sesión
     */
    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        // Añadir usuario al modelo para mostrar sus datos en la vista
        model.addAttribute("usuario", usuarioLogueado);
        return "perfil";
    }

    /**
     * Procesa el cambio de contraseña del usuario.
     *
     * @param oldPassword contraseña actual
     * @param newPassword nueva contraseña
     * @param confirmPassword confirmación de la nueva contraseña
     * @param session sesión HTTP con el usuario logueado
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al perfil con mensaje de éxito o error
     */
    @PostMapping("/perfil/password")
    public String cambiarPassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                                  @RequestParam String confirmPassword, HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        // Validar que las nuevas contraseñas coincidan
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden.");
            return "redirect:/usuarios/perfil";
        }

        try {
            // Delegar la lógica de cambio al servicio
            usuarioService.cambiarPassword(usuarioLogueado.getId(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/usuarios/perfil";
    }

    /**
     * Elimina la cuenta del usuario logueado.
     *
     * @param session sesión HTTP a invalidar tras eliminar
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la página principal o al perfil si hay error
     */
    @PostMapping("/perfil/delete")
    public String eliminarCuenta(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        try {
            // Eliminar usuario de la base de datos
            usuarioService.eliminarUsuario(usuarioLogueado.getId());

            // Cerrar sesión después de borrar la cuenta
            session.invalidate();

            redirectAttributes.addFlashAttribute("success", "Tu cuenta ha sido eliminada.");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la cuenta.");
            return "redirect:/usuarios/perfil";
        }
    }
}
