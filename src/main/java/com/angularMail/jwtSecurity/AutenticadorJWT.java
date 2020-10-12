package com.angularMail.jwtSecurity;

import java.security.Key;

import javax.servlet.http.HttpServletRequest;

import com.angularMail.model.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class AutenticadorJWT {
	
	private static Key key = null; // Clave de encriptación que usaremos para generar el JWT. Diferente en cada ejecución
	
	/**
	 * A partir de un usuario, nos genera un JWT que contiene el id del mismo 
	 */
	public static String codificaJWT (Usuario u) {
		String jws = Jwts.builder().setSubject("" + u.getId()).
				signWith(SignatureAlgorithm.HS512, getGeneratedKey()).compact();
		return jws;
	}
	
	/**
	 * A partir de un jwt, nos localiza el id de usuario cuyo id mantiene en su interior
	 */
	public static int getIdUsuarioDesdeJWT (String jwt) {
		try {
			String stringIdUsuario = Jwts.parser().setSigningKey(getGeneratedKey()).parseClaimsJws(jwt).getBody().getSubject();
			int idUsuario = Integer.parseInt(stringIdUsuario);
			return idUsuario;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	
	/**
	 * Obtiene el id de un usuario almacenado en un JWT que proviene de un request
	 */
	public static int getIdUsuarioDesdeJwtIncrustadoEnRequest (HttpServletRequest request) {
		String autHeader = request.getHeader("Authorization");
		if (autHeader != null && autHeader.length() > 8) {
			String jwt = autHeader.substring(7);
			return getIdUsuarioDesdeJWT(jwt);
		}
		else {
			return -1; // Bandera, ha sido imposible obtener el id del usuario
		}
	}
	
	/**
	 * Genera una nueva clave, cada vez que se inicia el servidor, para encriptar los JWT
	 * @return
	 */
	private static Key getGeneratedKey () {
		if (key == null) {
			key = MacProvider.generateKey();
		}
		return key;
	}

}
