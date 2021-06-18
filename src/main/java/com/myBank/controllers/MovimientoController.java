package com.myBank.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
		
	@PostMapping("/movimiento/getAllMovimientosPaginacion")
	public DTO getAllMovimientosPaginacion (@RequestBody DatosListadoMovimiento datos) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaMovimientosEnDTO = new ArrayList<DTO>();
			List<Movimiento> movimientos = new ArrayList<Movimiento>();
			float importe = -1;
			int dia = -1, mes = 1, anno = 0;
			for (Filtro filtro : datos.filtros) {
				if (filtro.name.equals("importe") && filtro.value != null) importe = Float.parseFloat(filtro.value);
				if (filtro.name.equals("fecha") && filtro.value != null) { 
					Date date = new Date(Long.parseLong(filtro.value));
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					dia = cal.get(Calendar.DAY_OF_MONTH);
					mes += cal.get(Calendar.MONTH);
					anno = cal.get(Calendar.YEAR);
				}
			}
			movimientos = (List<Movimiento>) this.movimientoRepository.getAllMovimientosPaginacionConFiltros(datos.idCuenta, importe, importe, dia, mes, anno, dia, datos.pagina * datos.elementosPorPagina, datos.elementosPorPagina);
			for (Movimiento movimiento : movimientos) {
				listaMovimientosEnDTO.add(DTO.getDTOFromMovimiento(movimiento));
			}
			dto.put("movimientos", listaMovimientosEnDTO);
			dto.put("totalMovimientos", this.movimientoRepository.getCountMovimientosCuentaConFiltros(datos.idCuenta, importe, importe, dia, mes, anno, dia));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	@PutMapping("/movimiento/realizar")
	public DTO realizarMovimiento(@RequestBody DatosMovimiento datos) {
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
	
	private void realizarTransferencia(DatosMovimiento datos) {
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
	
	private void realizarPeticion(DatosMovimiento datos) {
		Date fecha = new Date();
		Cuenta cuenta_origen = this.cuentaRepository.findByIban(datos.iban);
		Cuenta cuenta_destino = this.cuentaRepository.findById(datos.idCuenta).get();
						
		this.insertarNuevaTransferencia(datos.importe, datos.descripcion, fecha, cuenta_origen, cuenta_destino, false, false);
	}
		
	private void retirarDinero(DatosMovimiento datos) {
		Date fecha = new Date();
		
		Cuenta cuenta = this.cuentaRepository.findById(datos.idCuenta).get();
		cuenta.setSaldo(cuenta.getSaldo() - datos.importe);
		this.cuentaRepository.save(cuenta);
		
		this.insertarNuevoMovimiento(datos.importe, datos.descripcion, fecha, cuenta, "Retiro");
	}
	
	private void ingresarDinero(DatosMovimiento datos) {
		Date fecha = new Date();
		
		Cuenta cuenta = this.cuentaRepository.findById(datos.idCuenta).get();
		cuenta.setSaldo(cuenta.getSaldo() + datos.importe);
		this.cuentaRepository.save(cuenta);
		
		this.insertarNuevoMovimiento(datos.importe, datos.descripcion, fecha, cuenta, "Ingreso");
	}
	
	private void aceptarPeticion (DatosMovimiento datos) {		
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
	
	private void rechazarPeticion (DatosMovimiento datos) {		 
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
		movimiento.setDivisa(cuenta.getDivisa());
		movimiento.setSaldo(cuenta.getSaldo());
		movimiento.setDescripcion(descripcion);
		this.movimientoRepository.save(movimiento);
	}
	
	private void actualizarCuentas(Cuenta cuenta_origen, Cuenta cuenta_destino) {
		this.cuentaRepository.save(cuenta_origen);
		this.cuentaRepository.save(cuenta_destino);
	}

}

class DatosMovimiento {
	
	int idCuenta;
	String tipo;
	String iban;
	String descripcion;
	float importe;
	
	public DatosMovimiento(int idCuenta, String tipo, String iban, String descripcion, float importe) {
		super();
		this.idCuenta = idCuenta;
		this.tipo = tipo;
		this.iban = iban;
		this.descripcion = descripcion;
		this.importe = importe;
	}
	
}

class DatosListadoMovimiento {
	
	int idCuenta;
	int pagina;
	int elementosPorPagina;
	Filtro[] filtros;
	
	public DatosListadoMovimiento(int idCuenta, int pagina, int elementosPorPagina, Filtro[] filtros) {
		super();
		this.idCuenta = idCuenta;
		this.pagina = pagina;
		this.elementosPorPagina = elementosPorPagina;
		this.filtros = filtros;
	}
	
}

