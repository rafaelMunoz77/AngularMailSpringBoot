package com.angularMail.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.angularMail.jwtSecurity.AutenticadorJWT;
import com.angularMail.model.entities.Usuario;
import com.angularMail.model.repositories.UsuarioRepository;

@CrossOrigin
@RestController
public class UsuarioController {

	@Autowired
	UsuarioRepository usuRep;
	
	/**
	 * 
	 */
	@GetMapping("/usuario/getAutenticado")
	public DTO getUsuarioAutenticado (boolean imagen, HttpServletRequest request) {
		DTO dto = new DTO(); // Voy a devolver un dto
		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT

		// Intento localizar un usuario a partir de su id
		Usuario usuAutenticado = usuRep.findById(idUsuAutenticado).get();
		if (usuAutenticado != null) {
			dto.put("id", usuAutenticado.getId());
			dto.put("nombre", usuAutenticado.getNombre());
			dto.put("usuario", usuAutenticado.getUsuario());
			dto.put("password", usuAutenticado.getPassword());
			dto.put("email", usuAutenticado.getEmail());
			dto.put("fechaNacimiento", usuAutenticado.getFechaNacimiento());
			dto.put("fechaEliminacion", usuAutenticado.getFechaEliminacion());
			dto.put("nacionalidad", usuAutenticado.getNacionalidad().getId());
			dto.put("sexo", usuAutenticado.getTipoSexo().getId());
			dto.put("imagen", imagen? usuAutenticado.getImagen() : "");
		}

		// Finalmente devuelvo el JWT creado, puede estar vacío si la autenticación no ha funcionado
		return dto;
	}
	
	
	
	/**
	 * 
	 */
	@PostMapping("/usuario/autentica")
	public DTO autenticaUsuario (@RequestBody DatosAutenticacionUsuario datos) {
		DTO dto = new DTO(); // Voy a devolver un dto

		// Intento localizar un usuario a partir de su nombre de usuario y su password
		Usuario usuAutenticado = usuRep.findByUsuarioAndPassword(datos.usuario, datos.password);
		if (usuAutenticado != null) {
			dto.put("jwt", AutenticadorJWT.codificaJWT(usuAutenticado));
		}

		// Finalmente devuelvo el JWT creado, puede estar vacío si la autenticación no ha funcionado
		return dto;
	}
	
	
	
	/**
	 * usado para comprobar si una contraseña es igual a la contraseña del usuario autenticado
	 */
	@PostMapping("/usuario/ratificaPassword")
	public DTO ratificaPassword (@RequestBody DTO dtoRecibido, HttpServletRequest request) {
		DTO dto = new DTO(); // Voy a devolver un dto
		dto.put("result", "fail"); // Asumo que voy a fallar, si todo va bien se sobrescribe este valor

		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT

		try {
			Usuario usuarioAutenticado = usuRep.findById(idUsuAutenticado).get(); // Localizo todos los datos del usuario
			String password = (String) dtoRecibido.get("password");  // Compruebo la contraseña
			if (password.equals(usuarioAutenticado.getPassword())) {
				dto.put("result", "ok"); // Devuelvo éxito, las contraseñas son iguales
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return dto;
	}
	
	
	

	/**
	 * 
	 */
	@PostMapping("/usuario/modificaPassword")
	public DTO modificaPassword (@RequestBody DTO dtoRecibido, HttpServletRequest request) {
		DTO dto = new DTO(); // Voy a devolver un dto
		dto.put("result", "fail"); // Asumo que voy a fallar, si todo va bien se sobrescribe este valor

		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT

		try {
			Usuario usuarioAutenticado = usuRep.findById(idUsuAutenticado).get(); // Localizo al usuario
			String password = (String) dtoRecibido.get("password");  // Recibo la password que llega en el dtoRecibido
			usuarioAutenticado.setPassword(password); // Modifico la password
			usuRep.save(usuarioAutenticado);  // Guardo el usuario, con nueva password, en la unidad de persistencia
			dto.put("result", "ok"); // Devuelvo éxito
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return dto;
	}
	
	
	

}


/**
 * Clase que contiene los datos de autenticacion del usuario
 */
class DatosAutenticacionUsuario {
	String usuario;
	String password;

	/**
	 * Constructor
	 */
	public DatosAutenticacionUsuario(String usuario, String password) {
		super();
		this.usuario = usuario;
		this.password = password;
	}
}
