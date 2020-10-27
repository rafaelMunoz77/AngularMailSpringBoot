package com.angularMail.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.angularMail.model.entities.Mensaje;
import com.angularMail.model.entities.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {

	public Usuario findByNombre(String nombre);
    public Usuario findByUsuarioAndPassword(String name,String password);

	@Query(value = "SELECT * FROM Usuario where nombre like ? or email like ?", nativeQuery = true)
	public List<Usuario> filterByNombreOrEmail(String filtroNombre, String filtroEmail);

}
