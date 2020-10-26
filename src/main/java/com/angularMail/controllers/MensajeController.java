package com.angularMail.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angularMail.jwtSecurity.AutenticadorJWT;
import com.angularMail.model.entities.DestinatarioMensaje;
import com.angularMail.model.entities.Mensaje;
import com.angularMail.model.entities.Usuario;
import com.angularMail.model.repositories.DestinatarioMensajeRepository;
import com.angularMail.model.repositories.MensajeRepository;
import com.angularMail.model.repositories.UsuarioRepository;

@CrossOrigin
@RestController
public class MensajeController {
	
	@Autowired MensajeRepository mensajeRepo;
	@Autowired UsuarioRepository usuRep;
	@Autowired DestinatarioMensajeRepository destMensajeRep;
	
	// Constantes para definir el tipo de listado de mensajes que se desee obtener
	public static final int RECIBIDOS = 0;
	public static final int ENVIADOS = 1;
	public static final int SPAM = 2;
	public static final int ARCHIVADOS = 3;

	
	/**
	 * Este método no se utiliza en los últimos vídeos de angularMail. Mantengo este método únicamente
	 * para que la última versión de este servidor Spring Boot sea compatible con cualquier versión del cliente
	 * @param pagina
	 * @param mensajesPorPagina
	 * @param request
	 * @return
	 */
	@GetMapping("/mensajes/recibidos")
	public List<Mensaje> mensajesRecibidosParaUsuarioAutenticado (int pagina, int mensajesPorPagina, HttpServletRequest request) {
		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
		return this.mensajeRepo.getMensajesRecibidosDeUsuario(idUsuAutenticado, pagina, mensajesPorPagina);
	}
	
	
	
	/**
	 * Voy a obtener un listado de mensajes desde la bbdd. Para cada mensaje voy a rellenar una serie de valores, dentro de un DTO,
	 * y a enviarlos al cliente.
	 * @param pagina
	 * @param mensajesPorPagina
	 * @param request
	 * @return
	 */
	@GetMapping("/mensajes/listadoPorTipo")
	public DTO mensajesPorTipoParaUsuarioAutenticado (int tipo, int pagina, int mensajesPorPagina, HttpServletRequest request) {
		// Obtengo el id del usuario autenticado, mediante un JWT.
		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
		
		DTO dtoResultado = new DTO(); // Creo un nuevo DTO, que voy a devolver al cliente y asigno un fallo como salida
		dtoResultado.put("result", "fail"); // por defecto. Si todo va bien cambiará a "ok"
		
		// Voy a construir una lista de mensajes, eso va a ser un array de DTO's porque la estructura que quiero devolver al cliente
		// no es exactamente la misma que tiene la entidad "Mensaje", que es la de la tabla "mensaje".
		// Esta lista de mensajes después se incorporará al "dtoResultado".
		List<DTO> listaMensajesEnDTO = new ArrayList<DTO>();
		// Obtengo el total de mensajes que habría en la lista a devolver, si no hubiera paginación
		long countMensajes = 0;
		
		try {
			List<Mensaje> mensajes = new ArrayList<Mensaje>();
			// Obtengo los mensajes del servidor, en función del tipo de mensajes que se desea
			switch (tipo) {
			case RECIBIDOS:  // Lista de mensajes recibidos
				mensajes = this.mensajeRepo.getMensajesRecibidosDeUsuario(idUsuAutenticado, pagina, mensajesPorPagina);
				countMensajes = this.mensajeRepo.countMensajesRecibidosDeUsuario(idUsuAutenticado);
				break;
			case ENVIADOS: 
				mensajes = this.mensajeRepo.getMensajesEnviadosDeUsuario(idUsuAutenticado, pagina, mensajesPorPagina);
				countMensajes = this.mensajeRepo.countMensajesEnviadosDeUsuario(idUsuAutenticado);
				break;
			case ARCHIVADOS:
				mensajes = this.mensajeRepo.getMensajesArchivadosDeUsuario(idUsuAutenticado, pagina, mensajesPorPagina);
				countMensajes = this.mensajeRepo.countMensajesArchivadosDeUsuario(idUsuAutenticado);
				break;
			case SPAM:
				mensajes = this.mensajeRepo.getMensajesSpamDeUsuario(idUsuAutenticado, pagina, mensajesPorPagina);
				countMensajes = this.mensajeRepo.countMensajesSpamDeUsuario(idUsuAutenticado);
				break;
			}
			
			// Por cada mensaje de la lista, obtengo un DTO con los datos que realmente quiero enviar al cliente
			for (Mensaje m : mensajes) {
				// Agrego el dto con el mensaje completo, con todos los datos trabajado
				listaMensajesEnDTO.add(getDtoFromMensaje(m, idUsuAutenticado));
			}
			
			// Si llegamos hasta aquí sin errores, cambio el valor del resultado de la operación, para indicar éxito
			dtoResultado.put("result", "ok");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// En el DTO final envío la lista de mensajes y el número total de mensajes que habría sin paginación
		dtoResultado.put("mensajes", listaMensajesEnDTO);
		dtoResultado.put("totalMensajes", countMensajes);
		return dtoResultado;
	}
	
	
	/**
	 * 
	 * @param u
	 * @return
	 */
	private DTO getDtoFromUsuarioMinimosDatos (Usuario u) {
		DTO dto = new DTO();
		dto.put("id", u.getId());
		dto.put("nombre", u.getNombre());
		return dto;
	}
	
	
	/**
	 * Obtengo una estructura que se convertirá en JSON, con los datos que necesito de cada mensaje.
	 * @param m
	 * @return
	 */
	private DTO getDtoFromMensaje (Mensaje m, int idUsuAutenticado) {
		// Construyo un DTO a partir de un mensaje.
		DTO dto = new DTO();
		dto.put("id", m.getId());
		dto.put("remitente", getDtoFromUsuarioMinimosDatos(m.getUsuarioEmisor())); // Voy a enviar id y nombre de usuario del remitente
		dto.put("fecha", m.getFecha());
		dto.put("asunto", m.getAsunto());
		dto.put("cuerpo", m.getCuerpo());
		// Para el usuario autenticado, debo localizar el registro de la tabla "destinatarioMensaje". Dentro de esa tabla está la 
		// información sobre si un usuario ha archivado, marcado como SPAM o eliminado un mensaje
		DestinatarioMensaje dm = destMensajeRep.getFromIdUsuarioAndIdMensaje(idUsuAutenticado, m.getId());
		if (dm != null) {
			dto.put("leido", dm.getLeido());
			dto.put("archivado", dm.getArchivado());
			dto.put("fechaEliminacion", dm.getFechaEliminacion());
			dto.put("spam", dm.getSpam());
		}
		// Agrego un listado de todos los destinatarios de un mensaje, sólo el id y el nombre del usuario.
		List destinatarios = new ArrayList();
		for (int i = 0; i < m.getDestinatarioMensaje().size(); i++) {
			destinatarios.add(getDtoFromUsuarioMinimosDatos(m.getDestinatarioMensaje().get(i).getUsuario()));
		}
		// Agrego los destinatarios al dto a devolver
		dto.put("destinatarios", destinatarios);
		
		return dto;
	}
}