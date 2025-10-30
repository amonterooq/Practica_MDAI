package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

	public HomeController() {
		// TODO Auto-generated constructor stub
		System.out.println("\t Builder of " + this.getClass().getSimpleName());
	}
	@GetMapping("/")
	public String index() {
		System.out.println("\t Recogo la peticion a / y devuelvo la vista myIndex.html");
		return "myIndex.html";
	}
	

}
