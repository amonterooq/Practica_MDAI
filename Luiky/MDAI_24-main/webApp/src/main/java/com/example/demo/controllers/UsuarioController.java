package com.example.demo.controllers;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.domain.Usuario;
import com.example.demo.services.UsuarioService;

@Controller
public class UsuarioController {
	
	
	private final UsuarioService usuarioService;
	
	public UsuarioController(UsuarioService usuarioService) {
		// TODO Auto-generated constructor stub
		System.out.println("\t Builder of " + this.getClass().getSimpleName());
		this.usuarioService=usuarioService;
	}
	
	@GetMapping("/listUsuarios")
	public String listUsuariosPage(Model model) {
		System.out.println("\n\t Recogo la peticion a @GetMapping(\"/listUsuarios\")");		
		
		model.addAttribute("listaUsuarios",usuarioService.findAllUsers());
		return "listUsuariosBasico";		
	}
	
	 /**
    ADD USUARIO     
   */
    // Metodo para mostrar el formulario de aniadir usuario
    @GetMapping("/addUsuario")
    public String mostrarFormularioAddUsuario(Model model) {
    	System.out.println("\n\t @GetMapping(\"/addUsuario\")");
    	// Se pasa un objeto vacío al modelo
        model.addAttribute("usuario", new Usuario()); 
        return "addUsuario"; // Esto coincide con el nombre de tu plantilla Thymeleaf (addUsuario.html)
    }
    
    // Metodo para procesar la solicitud de aniadir usuario
    @PostMapping("/guardarUsuario")
    public String guardarUsuario(Usuario usuario, Model model) {
        // Asumimos que tienes un servicio llamado usuarioService 
    	// para gestionar los usuarios que contiene la logica
    	System.out.println("\n\t @PostMapping(\"/guardarUsuario\")");
    	usuarioService.crearUsuario(usuario);
 
    	// Se puede incluir otro html de confirmacion que indique que se ha guardado con exito
        // Yo lo voy a redirigir a la lista de usuarios directamente despues de agregar uno nuevo.
    	// Lo redirijo a la URL no a la vista .html para que vuelva a hacer el proceso.
        return "redirect:/listUsuarios";
    }
	

	

}
