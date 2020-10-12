package com.angularMail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan  // Necesario para habilitar el filtro web que se encuentra en com.angularMail.jwtSecurity.JwtWebFilter.java
@SpringBootApplication
public class AngularMailSpringBootApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(AngularMailSpringBootApplication.class, args);
	}  

}
