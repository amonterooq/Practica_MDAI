package com.nada.nada.controllers;

import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios") // Dominio
public class UserController {


    private UsuarioService usuarioService;

    @Autowired
    public UserController(UsuarioService userService) {
        this.usuarioService = userService;
    }

    @GetMapping("/")
    public String listUsers(Model model) {
        model.addAttribute("usuarios", usuarioService.buscarTodos());
        return "usuarios";
    }

    @GetMapping("/new")
    public String showUserForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "formularioUsuario";
    }

    @PostMapping("/crear")
    public String createUser(@ModelAttribute Usuario usuario) {
        usuarioService.crearUsuario(usuario);
        return "redirect:/usuarios/";
    }

    /*
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.encontrarPorId(id).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            return "userForm";
        } else {
            return "redirect:/users";
        }
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        user.setId(id);
        userService.guardar(user);
        return "redirect:/users/";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.eliminarPorId(id);
        return "redirect:/users/";
    }*/
}