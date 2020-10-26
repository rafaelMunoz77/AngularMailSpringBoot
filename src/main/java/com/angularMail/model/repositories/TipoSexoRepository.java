package com.angularMail.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.angularMail.model.entities.TipoSexo;


@Repository
public interface TipoSexoRepository extends CrudRepository<TipoSexo, Integer> {


}
