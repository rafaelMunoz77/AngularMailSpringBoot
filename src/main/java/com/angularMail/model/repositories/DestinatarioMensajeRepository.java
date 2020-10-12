package com.angularMail.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.angularMail.model.entities.DestinatarioMensaje;

@Repository
public interface DestinatarioMensajeRepository extends CrudRepository<DestinatarioMensaje, Integer> {


}
