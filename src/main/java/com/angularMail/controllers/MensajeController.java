package com.angularMail.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angularMail.jwtSecurity.AutenticadorJWT;
import com.angularMail.model.entities.Mensaje;
import com.angularMail.model.repositories.MensajeRepository;

@RestController
public class MensajeController {
	  
	@Autowired MensajeRepository mensajeRepo;
	
	@GetMapping("/mensajes/recibidos")
	public List<Mensaje> mensajesRecibidosParaUsuarioAutenticado (int pagina, int mensajesPorPagina, HttpServletRequest request) {
		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
		return this.mensajeRepo.getMensajesRecibidosDeUsuarioDesdeCRUDRepository(idUsuAutenticado, pagina, mensajesPorPagina);
	}
}