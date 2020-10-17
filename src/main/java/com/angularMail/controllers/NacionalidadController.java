package com.angularMail.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angularMail.model.entities.Nacionalidad;
import com.angularMail.model.repositories.NacionalidadRepository;

@CrossOrigin
@RestController
public class NacionalidadController {

	@Autowired
	NacionalidadRepository nacionalidadRep;
	
	@GetMapping("nacionalidad/all")
	public Iterable<Nacionalidad> getAllNacionalidades () {
		return this.nacionalidadRep.findAll();
	}
}
