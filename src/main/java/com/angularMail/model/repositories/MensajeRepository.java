package com.angularMail.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.angularMail.model.entities.Mensaje;

@Repository
public interface MensajeRepository extends CrudRepository<Mensaje, Integer> {


}
