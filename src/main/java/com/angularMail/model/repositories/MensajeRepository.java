package com.angularMail.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.angularMail.model.entities.Mensaje;

@Repository
public interface MensajeRepository extends CrudRepository<Mensaje, Integer> {

	// Mensajes recibidos
	@Query(value = "SELECT distinct m.* FROM Mensaje as m, destinatarioMensaje as d "
			+ "where d.idMensaje = m.id and d.idDestinatario = ? and d.archivado = 0 "
			+ "and d.spam = 0 and d.fechaEliminacion is null order by m.fecha desc limit ?, ?", nativeQuery = true)
	public List<Mensaje> getMensajesRecibidosDeUsuario(int idUsuario, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct m.id) FROM Mensaje as m, destinatarioMensaje as d "
			+ "where d.idMensaje = m.id and d.idDestinatario = ? and d.archivado = 0 "
			+ "and d.spam = 0 and d.fechaEliminacion is null", nativeQuery = true)
	public long countMensajesRecibidosDeUsuario(int idUsuario);

	// Mensajes enviados
	@Query(value = "SELECT distinct m.* FROM Mensaje as m, destinatarioMensaje as d where d.idMensaje = m.id and m.idEmisor = ? and "
			+ " d.archivado = 0 and d.spam = 0 and d.fechaEliminacion is null order by m.fecha desc limit ?, ?", nativeQuery = true)
	public List<Mensaje> getMensajesEnviadosDeUsuario(int idUsuario, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct m.id) FROM Mensaje as m, destinatarioMensaje as d where d.idMensaje = m.id and m.idEmisor = ? and "
			+ " d.archivado = 0 and d.spam = 0 and d.fechaEliminacion is null", nativeQuery = true)
	public long countMensajesEnviadosDeUsuario(int idUsuario);

	// Mensajes archivados
	@Query(value = "SELECT distinct m.* FROM Mensaje as m, destinatarioMensaje as d where d.idMensaje = m.id and d.idDestinatario = ? and "
			+ " d.archivado = 1 and d.fechaEliminacion is null order by m.fecha desc limit ?, ?", nativeQuery = true)
	public List<Mensaje> getMensajesArchivadosDeUsuario(int idUsuario, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct m.id) FROM Mensaje as m, destinatarioMensaje as d where d.idMensaje = m.id and d.idDestinatario = ? and "
			+ "	d.archivado = 1 and d.fechaEliminacion is null", nativeQuery = true)
	public long countMensajesArchivadosDeUsuario(int idUsuario);


	// Mensajes SPAM
	@Query(value = "SELECT distinct m.* FROM Mensaje as m, destinatarioMensaje as d where d.idMensaje = m.id and d.idDestinatario = ? and "
			+ "	d.archivado = 0 and d.spam = 1 and d.fechaEliminacion is null order by m.fecha desc limit ?, ?", nativeQuery = true)
	public List<Mensaje> getMensajesSpamDeUsuario(int idUsuario, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct m.id) FROM Mensaje as m, destinatarioMensaje as d where d.idMensaje = m.id and d.idDestinatario = ? and "
			+ " d.archivado = 0 and d.spam = 1 and d.fechaEliminacion is null", nativeQuery = true)
	public long countMensajesSpamDeUsuario(int idUsuario);

}
