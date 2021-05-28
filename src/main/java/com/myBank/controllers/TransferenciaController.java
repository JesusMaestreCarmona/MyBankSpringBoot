package com.myBank.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.myBank.jwtSecurity.AutenticadorJWT;
import com.myBank.model.entities.Cuenta;
import com.myBank.model.entities.Movimiento;
import com.myBank.model.entities.Transferencia;
import com.myBank.model.entities.Usuario;
import com.myBank.model.repositories.CuentaRepository;
import com.myBank.model.repositories.MovimientoRepository;
import com.myBank.model.repositories.TransferenciaRepository;
import com.myBank.model.repositories.UsuarioRepository;
import com.myBank.model.services.RestService;

@CrossOrigin
@RestController
public class TransferenciaController {

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
	
	@GetMapping("/transferencia/getAllRealizadas")
	public DTO getAllRealizadas (int idCuenta, int pagina, int elementosPorPagina) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaTransferenciasEnDTO = new ArrayList<DTO>();
			List<Transferencia> transferencias = new ArrayList<Transferencia>();
			transferencias = (List<Transferencia>) this.transferenciaRepository.getTransferenciasCuenta(idCuenta, pagina, elementosPorPagina);
			for (Transferencia transferencia : transferencias) {
				listaTransferenciasEnDTO.add(DTO.getDTOFromTransferencia(transferencia));
			}
			dto.put("transferencias", listaTransferenciasEnDTO);
			dto.put("totalTransferencias", this.transferenciaRepository.getCountTransferenciasCuenta(idCuenta));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@GetMapping("/transferencia/getAllPeticiones")
	public DTO getAllPeticiones (int idCuenta, int pagina, int elementosPorPagina) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaTransferenciasEnDTO = new ArrayList<DTO>();
			List<Transferencia> transferencias = new ArrayList<Transferencia>();
			transferencias = (List<Transferencia>) this.transferenciaRepository.getPeticionesCuenta(idCuenta, pagina, elementosPorPagina);
			for (Transferencia transferencia : transferencias) {
				listaTransferenciasEnDTO.add(DTO.getDTOFromTransferencia(transferencia));
			}
			dto.put("peticiones", listaTransferenciasEnDTO);
			dto.put("totalPeticiones", this.transferenciaRepository.getCountPeticionesCuenta(idCuenta));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@GetMapping("/transferencia/getCountPeticiones")
	public DTO getCountPeticiones (int idCuenta) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			dto.put("totalPeticiones", this.transferenciaRepository.getCountPeticionesCuenta(idCuenta));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@PutMapping("/transferencia/realizarTransferencia")
	public DTO realizarTransferencia (@RequestBody DatosNuevaTransferenciaOPeticion datos, HttpServletRequest request) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT
			
			Usuario usuAutenticado = (idUsuAutenticado != -1) ? usuarioRepository.findById(idUsuAutenticado).get() : null;
			
			if (usuAutenticado != null) {
				Date fecha = new Date();
				Cuenta cuenta_origen = this.cuentaRepository.findById(datos.idCuenta).get();
				Cuenta cuenta_destino = this.cuentaRepository.findByIban(datos.iban);
						
				float cambioMoneda = this.restService.getCambioMoneda(cuenta_origen.getDivisa().getDescripcion(), cuenta_destino.getDivisa().getDescripcion());
				cuenta_origen.setSaldo(cuenta_origen.getSaldo() - datos.importe);
				cuenta_destino.setSaldo(cuenta_destino.getSaldo() + datos.importe * cambioMoneda);
				
				this.insertarNuevaTransferencia(datos.importe, datos.descripcion, fecha, cuenta_origen, cuenta_destino, true);
				this.insertarNuevosMovimientos(datos.importe, datos.descripcion, fecha, cuenta_origen, cuenta_destino, "Envío");
				this.actualizarCuentas(cuenta_origen, cuenta_destino);
				dto.put("result", "ok");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@PutMapping("/transferencia/realizarPeticion")
	public DTO realizarPeticion (@RequestBody DatosNuevaTransferenciaOPeticion datos, HttpServletRequest request) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT
			
			Usuario usuAutenticado = (idUsuAutenticado != -1) ? usuarioRepository.findById(idUsuAutenticado).get() : null;
			
			if (usuAutenticado != null) {
				Date fecha = new Date();
				Cuenta cuenta_origen = this.cuentaRepository.findById(datos.idCuenta).get();
				Cuenta cuenta_destino = this.cuentaRepository.findByIban(datos.iban);
								
				this.insertarNuevaTransferencia(datos.importe, datos.descripcion, fecha, cuenta_origen, cuenta_destino, false);
				dto.put("result", "ok");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@GetMapping("/transferencia/aceptarPeticion")
	public DTO aceptarPeticion (int idTransferencia) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			Transferencia transferencia = this.transferenciaRepository.findById(idTransferencia).get();
			transferencia.setEstado(true);
			transferencia.setFecha(new Date());
			this.transferenciaRepository.save(transferencia);

			Cuenta cuenta_origen = transferencia.getCuenta1();
			Cuenta cuenta_destino = transferencia.getCuenta2();

			float cambioMoneda = this.restService.getCambioMoneda(cuenta_origen.getDivisa().getDescripcion(), cuenta_destino.getDivisa().getDescripcion());
			cuenta_destino.setSaldo(cuenta_destino.getSaldo() - transferencia.getImporte() * cambioMoneda);
			cuenta_origen.setSaldo(cuenta_origen.getSaldo() + transferencia.getImporte());
			
			this.insertarNuevosMovimientos(transferencia.getImporte(), transferencia.getDescripcion(), new Date(), cuenta_origen, cuenta_destino, "Solicitud");
			this.actualizarCuentas(cuenta_origen, cuenta_destino);

			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@DeleteMapping("/transferencia/rechazarPeticion")
	public DTO rechazarPeticion (int idTransferencia) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			this.transferenciaRepository.deleteById(idTransferencia);
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@GetMapping("/transferencia/getCambioMoneda")
	public DTO getCambioMoneda (String divisa_origen, String divisa_destino) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {

			float cambioMoneda = this.restService.getCambioMoneda(divisa_origen, divisa_destino);
			dto.put("cambioMoneda", cambioMoneda);
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	private void insertarNuevaTransferencia(float importe, String descripcion, Date fecha, Cuenta cuenta_origen, Cuenta cuenta_destino, boolean estado) {
		Transferencia nuevaTransferencia = new Transferencia();
		nuevaTransferencia.setCuenta1(cuenta_origen);
		nuevaTransferencia.setCuenta2(cuenta_destino);
		nuevaTransferencia.setImporte(importe);
		nuevaTransferencia.setFecha(fecha);
		nuevaTransferencia.setDescripcion(descripcion);
		nuevaTransferencia.setEstado(estado);
		this.transferenciaRepository.save(nuevaTransferencia);
	}
	
	private void insertarNuevosMovimientos(float importe, String descripcion, Date fecha, Cuenta cuenta_origen, Cuenta cuenta_destino, String tipo) {
		Movimiento movimiento_origen = new Movimiento();
		movimiento_origen.setCuenta(cuenta_origen);
		movimiento_origen.setTipo(tipo);
		movimiento_origen.setFecha(fecha);
		movimiento_origen.setImporte(importe);
		movimiento_origen.setSaldo(cuenta_origen.getSaldo());
		movimiento_origen.setDescripcion(descripcion);
		movimiento_origen.setEstado(true);
		this.movimientoRepository.save(movimiento_origen);
		
		Movimiento movimiento_destino = new Movimiento();
		movimiento_destino.setCuenta(cuenta_destino);
		movimiento_destino.setTipo(tipo);
		movimiento_destino.setFecha(fecha);
		movimiento_destino.setImporte(importe);
		movimiento_destino.setSaldo(cuenta_destino.getSaldo());
		movimiento_destino.setDescripcion(descripcion);
		movimiento_destino.setEstado(true);
		this.movimientoRepository.save(movimiento_destino);
	}
	
	private void actualizarCuentas(Cuenta cuenta_origen, Cuenta cuenta_destino) {
		this.cuentaRepository.save(cuenta_origen);
		this.cuentaRepository.save(cuenta_destino);
	}
				
}

class DatosNuevaTransferenciaOPeticion {
	
	int idCuenta;
	String iban;
	String descripcion;
	int importe;

	/**
	 * Constructor
	 */
	public DatosNuevaTransferenciaOPeticion(int idCuenta, String iban, String descripcion, int importe) {
		super();
		this.idCuenta = idCuenta;
		this.iban = iban;
		this.descripcion = descripcion;
		this.importe = importe;
	}

}

