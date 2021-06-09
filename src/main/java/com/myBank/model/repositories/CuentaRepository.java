package com.myBank.model.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.myBank.model.entities.Cuenta;
import com.myBank.model.entities.Usuario;

@Repository
public interface CuentaRepository extends CrudRepository<Cuenta, Integer> {
	
    public List<Cuenta> findByUsuario(Usuario usuario);
    
    public Cuenta findFirstByUsuarioOrderById(Usuario usuario);
    
    public Cuenta findByIban(String iban);
    
    public Cuenta findFirstByOrderByIdDesc();
}
