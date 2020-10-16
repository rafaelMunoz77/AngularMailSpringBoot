package com.angularMail.jwtSecurity;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/*")
public class MyWebFilter implements Filter{
	
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    /**
     * 
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    	HttpServletRequest request = (HttpServletRequest) servletRequest; // Obtengo un objeto request, con la petició recibida desde el cliente
    	String uriDePeticionWeb = request.getRequestURI(); // Obtengo la URL a la que se dirige la petición web
    	String metodoRequerido = request.getMethod(); // Obtengo el método de la petición: GET, POST, PUT, DELETE, OPTIONS, etc...
    	int idUsuarioAutenticadoMedianteJWT = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo un posible id de usuario
    		// contenido dentro de un JWT, guardado en un header del request realizado
    	
    	System.out.println("Log - request: " + uriDePeticionWeb + " - " + request.getMethod());
    	// Si se accede a la autenticación de usuario o ya existe un usuario autenticado, dejo pasar la petición
    	// También dejo pasar si se está requiriendo un contenido publicado en la carpeta /webapp (contenido estático: html, css, js, etc)
    	if (uriDePeticionWeb.startsWith("/webapp") ||     // Se intenta acceder a la carpeta de contenido estático "/webapp".
    			uriDePeticionWeb.equals("/usuario/autentica") || // Web de autenticado, aunque no traiga JWT en la cabecera se le permite pasar
    			idUsuarioAutenticadoMedianteJWT != -1) {     // Cualquier petición con un JWT válido, que tenga un id de usuario encriptado
    		filterChain.doFilter(servletRequest, servletResponse);  // Permito que la ejecución del request continúe su curso
    	}
    	else {
        	// En caso contrario, deniego el acceso
        	HttpServletResponse response = (HttpServletResponse) servletResponse; // Obtengo el objeto response de la petición request
			response.sendError(403, "No autorizado");   // Establezco un estado de 403 - Acceso prohibido.
    	}
    }
 
    @Override
    public void destroy() {
    }
}