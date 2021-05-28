package com.myBank.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.myBank.model.entities.Divisa;

@Repository
public interface DivisaRepository extends CrudRepository<Divisa, Integer> {

}
