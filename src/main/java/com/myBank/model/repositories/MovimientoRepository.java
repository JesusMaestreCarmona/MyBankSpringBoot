package com.myBank.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.myBank.model.entities.Movimiento;

@Repository
public interface MovimientoRepository extends CrudRepository<Movimiento, Integer> {

	@Query(value = "SELECT distinct m.* FROM Movimiento as m, Cuenta as c "
			+ "where c.id = ? and m.cuenta_id = c.id "
			+ "and m.importe > 0 and (m.tipo = 'Ingreso' or m.tipo = 'Retiro') and (m.importe = ? or ? < 0) and ((day(m.fecha) = ? and month(m.fecha) = ? and year(m.fecha) = ?) or ? < 0) order by m.fecha asc limit ?, ?", nativeQuery = true)
    public List<Movimiento> getAllMovimientosPaginacionConFiltros(int idCuenta, float importe, float importe2, int dia, int mes, int anno, int dia2, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct m.id) FROM Movimiento as m, Cuenta as c where c.id = ? and m.cuenta_id = c.id "
			+ "and m.importe > 0 and (m.tipo = 'Ingreso' or m.tipo = 'Retiro') and (m.importe = ? or ? < 0) and ((day(m.fecha) = ? and month(m.fecha) = ? and year(m.fecha) = ?) or ? < 0)", nativeQuery = true)
	public long getCountMovimientosCuentaConFiltros(int idCuenta, float importe, float importe2, int dia, int mes, int anno, int dia2);
	
}
