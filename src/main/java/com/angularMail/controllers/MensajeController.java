package com.angularMail.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
				mensajes = this.mensajeRepo.getMensajesRecibidosDeUsuario(idUsuAutenticado, pagina * mensajesPorPagina, mensajesPorPagina);
				countMensajes = this.mensajeRepo.countMensajesRecibidosDeUsuario(idUsuAutenticado);
				break;
			case ENVIADOS: 
				mensajes = this.mensajeRepo.getMensajesEnviadosDeUsuario(idUsuAutenticado, pagina * mensajesPorPagina, mensajesPorPagina);
				countMensajes = this.mensajeRepo.countMensajesEnviadosDeUsuario(idUsuAutenticado);
				break;
			case ARCHIVADOS:
				mensajes = this.mensajeRepo.getMensajesArchivadosDeUsuario(idUsuAutenticado, pagina * mensajesPorPagina, mensajesPorPagina);
				countMensajes = this.mensajeRepo.countMensajesArchivadosDeUsuario(idUsuAutenticado);
				break;
			case SPAM:
				mensajes = this.mensajeRepo.getMensajesSpamDeUsuario(idUsuAutenticado, pagina * mensajesPorPagina, mensajesPorPagina);
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
		DestinatarioMensaje dm = destMensajeRep.findByIdUsuarioAndIdMensaje(idUsuAutenticado, m.getId());
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
	 * Este método permite marcar de diferentes maneras los mensajes de un usuario. Se basa en un número entero
	 * que actúa como "tipo de marca" y que tiene los siguientes valores:
	 * 		0 -> marca como mensajes leídos
	 * 		1 -> marca como mensajes archivados
	 * 		2 -> marca como mensajes spam
	 * 		3 -> marca como mensajes eliminados
	 * 		4 -> mueve el mensaje a "recibidos", elimina las marcas de "leído", "archivado", "spam" y "eliminado"
	 * @param dtoRecibido
	 * @param request
	 * @return
	 */
	@PostMapping("/mensajes/accionSobreMensajes")
	private DTO accionSobreMensajes (@RequestBody DatosAccionesSobreMensajes datosAcciones, HttpServletRequest request) {
		DTO dto = new DTO(); // Voy a devolver un dto
		dto.put("result", "fail"); // Asumo que voy a fallar, si todo va bien se sobrescribe este valor

		try {
			int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT
			for (int idMensaje : datosAcciones.ids) {
				DestinatarioMensaje dm = this.destMensajeRep.findByIdUsuarioAndIdMensaje(idUsuAutenticado, idMensaje);
				
				switch (datosAcciones.tipoAccion) {
				case 0: // marca como mensajes leídos
					dm.setLeido(true);
					break;
				case 1: // marca como mensajes archivados
					dm.setArchivado(true);
					break;
				case 2: // marca como mensajes spam
					dm.setSpam(true);
					break;
				case 3: // marca como mensajes eliminados
					dm.setFechaEliminacion(new Date());
					break;
				case 4: // mueve el mensaje a "recibidos", elimina las marcas de "archivado", "spam" y "eliminado"
					dm.setArchivado(false);
					dm.setSpam(false);
					dm.setFechaEliminacion(null);
					break; 
				}
				// Guardo en la unidad de persistencia el objeto dm
				this.destMensajeRep.save(dm);
			}
			dto.put("result", "ok"); 
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return dto;
	}
	
	
	/**
	 * Guarda un nuevo mensaje en BBDD, para los usuarios especificados
	 * @param datosNuevoMensaje
	 * @param request
	 * @return
	 */
	@PutMapping("/mensajes/nuevo")
	private DTO nuevoMensaje (@RequestBody DatosEnvioNuevoMensaje datosNuevoMensaje, HttpServletRequest request) {
		DTO dto = new DTO(); // Voy a devolver un dto
		dto.put("result", "fail"); // Asumo que voy a fallar, si todo va bien se sobrescribe este valor

		try {
			// Localizo el usuario autenticado, será el emisor del mensaje
			int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT
			Usuario usuAutenticado = this.usuRep.findById(idUsuAutenticado).get();
			
			// Necesito incluir en una lista cada destinatario del mensaje.
			List<DestinatarioMensaje> destinatarios = new ArrayList<DestinatarioMensaje>();
			for (int idDestinatario : datosNuevoMensaje.idsDestinatarios) {
		        // Para cada "id" que se encuentre en el array recibido
		    	if (idDestinatario > 0) {
		    		DestinatarioMensaje dm = new DestinatarioMensaje();
		    		dm.setArchivado(false);
		    		dm.setFechaEliminacion(null);
		    		dm.setLeido(false);
		    		dm.setSpam(false);
		    		dm.setUsuario(this.usuRep.findById(idDestinatario).get());
		    		destinatarios.add(dm);  
		    	}		    	
			}
			
		    // Si existen asunto, cuerpo y destinatarios, envío el nuevo mensaje
		    if (datosNuevoMensaje.asunto != null && datosNuevoMensaje.cuerpo != null && destinatarios.size() > 0) {
		    	Mensaje m = new Mensaje();
		    	m.setAsunto(datosNuevoMensaje.asunto);
		    	m.setCuerpo(datosNuevoMensaje.cuerpo);
		    	m.setFecha(new Date());
		    	m.setUsuarioEmisor(usuAutenticado);
		    	// Guardo el mensaje
		    	this.mensajeRepo.save(m);
		    	
		    	// Una vez guardado el mensaje, a cada entidad DestinatarioMensaje, le asigno el mensaje guardado
		    	for (DestinatarioMensaje dm : destinatarios) {
		    		dm.setMensaje(m);
		    		this.destMensajeRep.save(dm);
		    	}
		    	// Especifico los destinatarios del mensaje, en la lista que posee la entidad para esto
		    	m.setDestinatarioMensaje(destinatarios);
		    	this.mensajeRepo.save(m); // Guardo definitivamente el mensaje
		    	
		    	// indico que todo ha funcionado correctamente
				dto.put("result", "ok");
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dto;
	}
}

/**
 * Clase que contiene los datos para realizar acciones sobre mensajes
 */
class DatosAccionesSobreMensajes {
	int tipoAccion;  
	int[] ids;
	public DatosAccionesSobreMensajes(int tipoAccion, int[] ids) {
		super();
		this.tipoAccion = tipoAccion;
		this.ids = ids;
	}
	

}


/**
 * Clase que contiene los datos para guardar un nuevo mensaje
 */
class DatosEnvioNuevoMensaje {
	String asunto;  
	String cuerpo;
	int[] idsDestinatarios;
	
	public DatosEnvioNuevoMensaje(String asunto, String cuerpo, int[] idsDestinatarios) {
		super();
		this.asunto = asunto;
		this.cuerpo = cuerpo;
		this.idsDestinatarios = idsDestinatarios;
	}
}

