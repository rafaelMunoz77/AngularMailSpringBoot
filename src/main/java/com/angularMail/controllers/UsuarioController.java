package com.angularMail.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.angularMail.jwtSecurity.AutenticadorJWT;
import com.angularMail.model.entities.Usuario;
import com.angularMail.model.repositories.NacionalidadRepository;
import com.angularMail.model.repositories.UsuarioRepository;
import com.angularMail.model.repositories.TipoSexoRepository;

@CrossOrigin
@RestController
public class UsuarioController {

	@Autowired
	UsuarioRepository usuRep;
	@Autowired
	NacionalidadRepository nacionalidadRep;
	@Autowired
	TipoSexoRepository tipoSexoRep;
	
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
	 * Recibe una nueva password para el usuario autenticado y la modifica en la unidad de persistencia
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
	
	
	

	/**
	 * Recibe los datos personales del usuario y los modifica en la unidad de persistencia
	 */
	@PostMapping("/usuario/update")
	public DTO modificaDatosUsuario (@RequestBody DTO dtoRecibido, HttpServletRequest request) {
		DTO dto = new DTO(); // Voy a devolver un dto
		dto.put("result", "fail"); // Asumo que voy a fallar, si todo va bien se sobrescribe este valor

		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT

		try {
			Usuario usuarioAutenticado = usuRep.findById(idUsuAutenticado).get(); // Localizo al usuario
			// Cargo los datos recibidos en el usuario localizado por su id.
			usuarioAutenticado.setUsuario((String) dtoRecibido.get("usuario"));
			usuarioAutenticado.setEmail((String) dtoRecibido.get("email"));
			usuarioAutenticado.setNombre((String) dtoRecibido.get("nombre"));
			usuarioAutenticado.setFechaNacimiento(new Date((long)dtoRecibido.get("fechaNacimiento")));
			usuarioAutenticado.setNacionalidad(this.nacionalidadRep.findById((int) dtoRecibido.get("nacionalidad")).get());
			usuarioAutenticado.setTipoSexo(this.tipoSexoRep.findById((int) dtoRecibido.get("sexo")).get());
			usuarioAutenticado.setImagen(Base64.decodeBase64((String) dtoRecibido.get("imagen")));
			usuRep.save(usuarioAutenticado);  // Guardo el usuario en la unidad de persistencia
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
