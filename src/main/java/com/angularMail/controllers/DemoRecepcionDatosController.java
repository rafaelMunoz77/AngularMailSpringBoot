package com.angularMail.controllers;

import java.util.HashMap;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoRecepcionDatosController {

	/**
	 * Recepción de variables por Path y por QueryParams
	 */
	@GetMapping("/demoRecepcionDatos/{idUsuario}")
	public String ejemploGet (@PathVariable("idUsuario") int idUsuario, String nombre, int otroId) {
		return "Get - Hola Mundo - idUsuario: " + idUsuario + " - Nombre: " + nombre + " - otroId: " + otroId;
	}
	
	/**
	 * Recepción de un JSON sin un formato determinado, recibido en un HashMap
	 */
	@PostMapping("/demoRecepcionDatos")
	public String ejemploPost (@RequestBody HashMap<String, Object> hm) {
		return "Post - Hola Mundo - HashMap: " + hm.get("idUsuario") + " - " + hm.get("nombre");
	}
	
	/**
	 * Recepción de JSON dentro de una clase con un formato determinado, muyyyyy cómodo
	 */
	@PutMapping("/demoRecepcionDatos")
	public String ejemploPut (@RequestBody DatosUsuario datos) {
		return "Put - Hola Mundo - Datos: " + datos.idUsuario + " - " + datos.nombre; 
	}
	
	/**
	 * Recepción de datos en JSON y emisión de datos en JSON
	 */
	@DeleteMapping("/demoRecepcionDatos")
	public DTO ejemploDelete (@RequestBody DatosUsuario datos) {
		DTO dto = new DTO();
		dto.put("idUsuario", datos.idUsuario);
		dto.put("nombre", datos.nombre);
		dto.put("Mensaje", "Genial!!!!");
		return dto;
	}
}

/**
 * Encapsulamiento de datos de un usuario
 */
class DatosUsuario {
	int idUsuario;
	String nombre;
	
	public DatosUsuario(int idUsuario, String nombre) {
		super();
		this.idUsuario = idUsuario;
		this.nombre = nombre;
	}
	
	
}

