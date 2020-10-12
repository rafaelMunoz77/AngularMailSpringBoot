package com.angularMail.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.angularMail.model.entities.TipoSexo;


@Repository
public interface tipoSexoRepository extends CrudRepository<TipoSexo, Integer> {


}
