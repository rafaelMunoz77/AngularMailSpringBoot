package com.angularMail.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.angularMail.model.entities.Mensaje;

@Repository
public interface MensajeRepository extends CrudRepository<Mensaje, Integer> {

	@Query(value = "SELECT distinct m.* FROM Mensaje as m, destinatarioMensaje as d "
			+ "where d.idMensaje = m.id and d.idDestinatario = ? and d.archivado = 0 "
			+ "and d.spam = 0 and d.fechaEliminacion is null order by m.fecha desc limit ?, ?", nativeQuery = true)
	public List<Mensaje> getMensajesRecibidosDeUsuarioDesdeCRUDRepository(int idUsuario, int pagina, int elementosPorPagina);
}
