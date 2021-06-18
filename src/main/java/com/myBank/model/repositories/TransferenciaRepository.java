package com.myBank.model.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.myBank.model.entities.Transferencia;

@Repository
public interface TransferenciaRepository extends CrudRepository<Transferencia, Integer> {
	
	@Query(value = "SELECT distinct t.* FROM Transferencia as t, Cuenta as c "
			+ "where c.id = ? and (t.cuenta_origen = c.id or t.cuenta_destino = c.id) "
			+ "and t.importe > 0 and estado != 0 and (t.importe = ? or ? < 0) and ((day(t.fecha) = ? and month(t.fecha) = ? and year(t.fecha) = ?) or ? < 0) order by t.fecha asc limit ?, ?", nativeQuery = true)
    public List<Transferencia> getAllTransferenciasPaginacionConFiltros(int idCuenta, float importe, float importe2, int dia, int mes, int anno, int dia2, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct t.id) FROM Transferencia as t, Cuenta as c where c.id = ? and (t.cuenta_origen = c.id or t.cuenta_destino = c.id) "
			+ "and t.importe > 0 and estado != 0 and (t.importe = ? or ? < 0) and ((day(t.fecha) = ? and month(t.fecha) = ? and year(t.fecha) = ?) or ? < 0)", nativeQuery = true)
	public long getCountTransferenciasCuentaConFiltros(int idcuenta, float importe, float importe2, int dia, int mes, int anno, int dia2);
	
	@Query(value = "SELECT distinct t.* FROM Transferencia as t, Cuenta as c "
			+ "where c.id = ? and (t.cuenta_origen = c.id or t.cuenta_destino = c.id) "
			+ "and t.importe > 0 and estado != 1 and (t.importe = ? or ? < 0) and ((day(t.fecha) = ? and month(t.fecha) = ? and year(t.fecha) = ?) or ? < 0) order by t.fecha asc limit ?, ?", nativeQuery = true)
    public List<Transferencia> getAllPeticionesPaginacionConFiltros(int idCuenta, float importe, float importe2, int dia, int mes, int anno, int dia2, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct t.id) FROM Transferencia as t, Cuenta as c where c.id = ? and (t.cuenta_origen = c.id or t.cuenta_destino = c.id) "
			+ "and t.importe > 0 and estado != 1 and (t.importe = ? or ? < 0) and ((day(t.fecha) = ? and month(t.fecha) = ? and year(t.fecha) = ?) or ? < 0)", nativeQuery = true)
	public long getCountPeticionesCuentaConFiltros(int idcuenta, float importe, float importe2, int dia, int mes, int anno, int dia2);
			
	@Query(value = "SELECT distinct t.* FROM Transferencia as t, Cuenta as c "
			+ "where c.id = ? and t.cuenta_origen = c.id "
			+ "and t.importe > 0 and estado != 1 and notificada != 1", nativeQuery = true)
    public List<Transferencia> getPeticionesANotificar(int idCuenta);
	
}
