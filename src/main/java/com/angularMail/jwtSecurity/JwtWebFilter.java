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
public class JwtWebFilter implements Filter{
	
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    	HttpServletRequest request = (HttpServletRequest) servletRequest; // Obtengo un objeto request, con la petició hecha
    	String uriDePeticionWeb = request.getRequestURI(); // Obtengo la URL a la que se dirige la petición web
    	int idUsuarioAutenticadoMedianteJWT = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo un posible id de usuario
    		// contenido dentro de un JWT, guardado en un header del request realizado
    	
    	// Si se accede a la autenticación de usuario o ya existe un usuario autenticado, dejo pasar la petición
    	if (uriDePeticionWeb.equals("/usuario/autentica") || idUsuarioAutenticadoMedianteJWT != -1) {
    		filterChain.doFilter(servletRequest, servletResponse);
    	}
    	else {
        	// En caso contrario, deniego el acceso
        	HttpServletResponse response = (HttpServletResponse) servletResponse;
			response.sendError(403, "No autorizado");    		
    	}

    }
    @Override
    public void destroy() {
    }
}