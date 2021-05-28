package com.myBank.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.myBank.model.entities.Transferencia;

@Repository
public interface TransferenciaRepository extends CrudRepository<Transferencia, Integer> {

	@Query(value = "SELECT distinct t.* FROM Transferencia as t, Cuenta as c "
			+ "where c.id = ? and (t.cuenta_origen = c.id or t.cuenta_destino = c.id) "
			+ "and t.importe > 0 and estado != 0 order by t.fecha asc limit ?, ?", nativeQuery = true)
    public List<Transferencia> getTransferenciasCuenta(int idCuenta, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct t.id) FROM Transferencia as t, Cuenta as c where c.id = ? and (t.cuenta_origen = c.id or t.cuenta_destino = c.id) "
			+ "and t.importe > 0 and estado != 0", nativeQuery = true)
	public long getCountTransferenciasCuenta(int idcuenta);
	
	@Query(value = "SELECT distinct t.* FROM Transferencia as t, Cuenta as c "
			+ "where c.id = ? and t.cuenta_destino = c.id "
			+ "and t.importe > 0 and estado != 1 order by t.fecha asc limit ?, ?", nativeQuery = true)
    public List<Transferencia> getPeticionesCuenta(int idCuenta, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct t.id) FROM Transferencia as t, Cuenta as c where c.id = ? and t.cuenta_destino = c.id "
			+ "and t.importe > 0 and estado != 1", nativeQuery = true)
	public long getCountPeticionesCuenta(int idcuenta);

}
