package com.angularMail.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

	@GetMapping("/usuario")
	public String doGet () {
		return "Get - Hola Mundo";
	}
	
	@PostMapping("/usuario")
	public String doPost () {
		return "Post - Hola Mundo";
	}
	
}
