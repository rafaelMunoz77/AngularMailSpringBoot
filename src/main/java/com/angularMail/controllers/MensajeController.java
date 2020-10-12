package com.angularMail.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angularMail.model.entities.Mensaje;
import com.angularMail.model.repositories.MensajeRepository;
import com.angularMail.model.services.MensajeService;

@RestController
public class MensajeController {
	
	@Autowired MensajeRepository mensajeRepo;	
	@Autowired MensajeService mensajeService;
	
	@GetMapping("/mensajes/recibidos")
	public List<Mensaje> mensajesRecibidosParaUsuarioAutenticado (int pagina, int mensajesPorPagina) {
//		return this.mensajeService.findMensajesRecibidosDeUsuario(1, pagina, mensajesPorPagina);
		return this.mensajeRepo.getMensajesRecibidosDeUsuarioDesdeCRUDRepository(1, pagina, mensajesPorPagina);
	}
}
