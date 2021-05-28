package com.myBank.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.myBank.model.entities.Rol;

@Repository
public interface RolRepository extends CrudRepository<Rol, Integer> {

}
