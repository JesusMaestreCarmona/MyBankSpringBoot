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
			+ "and m.importe > 0 and estado != 0 and (m.tipo = 'Ingreso' or m.tipo = 'Retiro') order by m.fecha asc limit ?, ?", nativeQuery = true)
    public List<Movimiento> getAllMovimientosPaginacion(int idCuenta, int pagina, int elementosPorPagina);
	
	@Query(value = "SELECT count(distinct m.id) FROM Movimiento as m, Cuenta as c where c.id = ? and m.cuenta_id = c.id "
			+ "and m.importe > 0 and estado != 0 and (m.tipo = 'Ingreso' or m.tipo = 'Retiro')", nativeQuery = true)
	public long getCountMovimientosCuenta(int idcuenta);
	
}
