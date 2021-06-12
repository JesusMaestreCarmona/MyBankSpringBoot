package com.myBank.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.myBank.model.entities.Cuenta;
import com.myBank.model.entities.Movimiento;
import com.myBank.model.entities.Transferencia;
import com.myBank.model.repositories.CuentaRepository;
import com.myBank.model.repositories.MovimientoRepository;
import com.myBank.model.repositories.TransferenciaRepository;
import com.myBank.model.repositories.UsuarioRepository;
import com.myBank.model.services.RestService;

@CrossOrigin
@RestController
public class MovimientoController {
	
	@Autowired
	TransferenciaRepository transferenciaRepository;
	@Autowired
	UsuarioRepository usuarioRepository;
	@Autowired
	CuentaRepository cuentaRepository;
	@Autowired
	MovimientoRepository movimientoRepository;
	@Autowired
	RestService restService;
	
	@GetMapping("/movimiento/getAllMovimientosPaginacion")
	public DTO getAllMovimientosPaginacion (int idCuenta, int pagina, int elementosPorPagina) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaTransferenciasEnDTO = new ArrayList<DTO>();
			List<Movimiento> movimientos = new ArrayList<Movimiento>();
			movimientos = (List<Movimiento>) this.movimientoRepository.getAllMovimientosPaginacion(idCuenta, pagina * elementosPorPagina, elementosPorPagina);
			for (Movimiento movimiento : movimientos) {
				listaTransferenciasEnDTO.add(DTO.getDTOFromMovimiento(movimiento));
			}
			dto.put("movimientos", listaTransferenciasEnDTO);
			dto.put("totalMovimientos", this.movimientoRepository.getCountMovimientosCuenta(idCuenta));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	@PutMapping("/movimiento/realizar")
	public DTO realizarMovimiento(@RequestBody DatosNuevoMovimiento datos) {
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			switch (datos.tipo) {
				case "Enviar dinero":
					this.realizarTransferencia(datos);
					break;
				case "Solicitar dinero":
					this.realizarPeticion(datos);
					break;
				case "Retirar dinero":
					this.retirarDinero(datos);
					break;
				case "Ingresar dinero":
					this.ingresarDinero(datos);
					break;
				case "Aceptar petición":
					this.aceptarPeticion(datos);
					break;
				case "Rechazar petición":
					this.rechazarPeticion(datos);
					break;
			}
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	private void realizarTransferencia(DatosNuevoMovimiento datos) {
		Date fecha = new Date();
		Cuenta cuenta_origen = this.cuentaRepository.findById(datos.idCuenta).get();
		Cuenta cuenta_destino = this.cuentaRepository.findByIban(datos.iban);
				
		float cambioMoneda = this.restService.getCambioMoneda(cuenta_origen.getDivisa().getDescripcion(), cuenta_destino.getDivisa().getDescripcion());
		cuenta_origen.setSaldo(cuenta_origen.getSaldo() - datos.importe);
		cuenta_destino.setSaldo(cuenta_destino.getSaldo() + datos.importe * cambioMoneda);
		
		this.insertarNuevaTransferencia(datos.importe, datos.descripcion, fecha, cuenta_origen, cuenta_destino, true, true);
		this.insertarNuevoMovimiento(datos.importe, datos.descripcion, fecha, cuenta_origen, "Transferencia");
		this.insertarNuevoMovimiento(datos.importe, datos.descripcion, fecha, cuenta_destino, "Transferencia");
		this.actualizarCuentas(cuenta_origen, cuenta_destino);
	}
	
	private void realizarPeticion(DatosNuevoMovimiento datos) {
		Date fecha = new Date();
		Cuenta cuenta_origen = this.cuentaRepository.findByIban(datos.iban);
		Cuenta cuenta_destino = this.cuentaRepository.findById(datos.idCuenta).get();
						
		this.insertarNuevaTransferencia(datos.importe, datos.descripcion, fecha, cuenta_origen, cuenta_destino, false, false);
	}
		
	private void retirarDinero(DatosNuevoMovimiento datos) {
		Date fecha = new Date();
		
		Cuenta cuenta = this.cuentaRepository.findById(datos.idCuenta).get();
		cuenta.setSaldo(cuenta.getSaldo() - datos.importe);
		this.cuentaRepository.save(cuenta);
		
		this.insertarNuevoMovimiento(datos.importe, datos.descripcion, fecha, cuenta, "Retiro");
	}
	
	private void ingresarDinero(DatosNuevoMovimiento datos) {
		Date fecha = new Date();
		
		Cuenta cuenta = this.cuentaRepository.findById(datos.idCuenta).get();
		cuenta.setSaldo(cuenta.getSaldo() + datos.importe);
		this.cuentaRepository.save(cuenta);
		
		this.insertarNuevoMovimiento(datos.importe, datos.descripcion, fecha, cuenta, "Ingreso");
	}
	
	private void aceptarPeticion (DatosNuevoMovimiento datos) {		
		Transferencia transferencia = this.transferenciaRepository.findById(datos.idCuenta).get();
		transferencia.setEstado(true);
		transferencia.setFecha(new Date());
		this.transferenciaRepository.save(transferencia);

		Cuenta cuenta_destino = transferencia.getCuenta1();
		Cuenta cuenta_origen = transferencia.getCuenta2();

		float cambioMoneda = this.restService.getCambioMoneda(cuenta_origen.getDivisa().getDescripcion(), cuenta_destino.getDivisa().getDescripcion());
		cuenta_destino.setSaldo(cuenta_destino.getSaldo() - transferencia.getImporte() * cambioMoneda);
		cuenta_origen.setSaldo(cuenta_origen.getSaldo() + transferencia.getImporte());
		
		this.insertarNuevoMovimiento(transferencia.getImporte(), transferencia.getDescripcion(), new Date(), cuenta_destino, "Petición");
		this.insertarNuevoMovimiento(transferencia.getImporte(), transferencia.getDescripcion(), new Date(), cuenta_origen, "Petición");
		this.actualizarCuentas(cuenta_origen, cuenta_destino);
	}
	
	private void rechazarPeticion (DatosNuevoMovimiento datos) {		 
		this.transferenciaRepository.deleteById(datos.idCuenta);
	}
	
	private void insertarNuevaTransferencia(float importe, String descripcion, Date fecha, Cuenta cuenta_origen, Cuenta cuenta_destino, boolean estado, boolean notificada) {
		Transferencia transferencia = new Transferencia();
		transferencia.setCuenta1(cuenta_origen);
		transferencia.setCuenta2(cuenta_destino);
		transferencia.setImporte(importe);
		transferencia.setFecha(fecha);
		transferencia.setDescripcion(descripcion);
		transferencia.setEstado(estado);
		transferencia.setNotificada(notificada);
		this.transferenciaRepository.save(transferencia);
	}
	
	private void insertarNuevoMovimiento(float importe, String descripcion, Date fecha, Cuenta cuenta, String tipo) {
		Movimiento movimiento = new Movimiento();
		movimiento.setCuenta(cuenta);
		movimiento.setTipo(tipo);
		movimiento.setFecha(fecha);
		movimiento.setImporte(importe);
		movimiento.setSaldo(cuenta.getSaldo());
		movimiento.setDescripcion(descripcion);
		movimiento.setEstado(true);
		this.movimientoRepository.save(movimiento);
	}
	
	private void actualizarCuentas(Cuenta cuenta_origen, Cuenta cuenta_destino) {
		this.cuentaRepository.save(cuenta_origen);
		this.cuentaRepository.save(cuenta_destino);
	}

}

class DatosNuevoMovimiento {
	
	int idCuenta;
	String tipo;
	String iban;
	String descripcion;
	float importe;
	int divisa;
	
	public DatosNuevoMovimiento(int idCuenta, String tipo, String iban, String descripcion, float importe, int divisa) {
		super();
		this.idCuenta = idCuenta;
		this.tipo = tipo;
		this.iban = iban;
		this.descripcion = descripcion;
		this.importe = importe;
		this.divisa = divisa;
	}
	
}
