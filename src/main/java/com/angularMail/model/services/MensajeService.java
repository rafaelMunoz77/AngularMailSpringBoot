package com.angularMail.model.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angularMail.model.entities.Mensaje;
import com.angularMail.model.repositories.MensajeRepository;


@Service
public class MensajeService {

	@Autowired
	private MensajeRepository mensajeRep;
	
	@PersistenceContext
	EntityManager em;
	
	
	/**
	 * 
	 * @param idUsuario
	 * @param pagina  				Página dentro de una búsqueda de registros por paginación
	 * @param elementosPorPagina
	 * @return
	 */
	public List<Mensaje> findMensajesRecibidosDeUsuario (int idUsuario, int pagina, int elementosPorPagina) {
		List<Mensaje> entities = new ArrayList<Mensaje>();
		try {			
			Query q = em.createNativeQuery("SELECT distinct m.* FROM Mensaje as m, destinatarioMensaje as d where "
					+ "d.idMensaje = m.id and d.idDestinatario = ? and "
					+ "d.archivado = 0 and d.spam = 0 and d.fechaEliminacion is null order by m.fecha desc limit ?, ?", Mensaje.class);
			q.setParameter(1, idUsuario);
			q.setParameter(2, pagina);
			q.setParameter(3, elementosPorPagina);
			entities = (List<Mensaje>) q.getResultList();
		}
		catch (NoResultException nrEx) {
		}
		em.close();
		return entities;
	}

}
