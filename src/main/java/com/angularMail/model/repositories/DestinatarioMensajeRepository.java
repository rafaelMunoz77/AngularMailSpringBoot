package com.angularMail.model.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.angularMail.model.entities.DestinatarioMensaje;

@Repository
public interface DestinatarioMensajeRepository extends CrudRepository<DestinatarioMensaje, Integer> {

	@Query(value = "SELECT distinct * FROM destinatarioMensaje as dm "
			+ "where dm.idDestinatario = ? and dm.idMensaje = ?", nativeQuery = true)
	public DestinatarioMensaje getFromIdUsuarioAndIdMensaje(int idUsuario, int idMensaje);

}
