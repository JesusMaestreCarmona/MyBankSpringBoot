package com.myBank.controllers;

import java.util.HashMap;

import com.myBank.model.entities.Cuenta;
import com.myBank.model.entities.Divisa;
import com.myBank.model.entities.Movimiento;
import com.myBank.model.entities.Transferencia;
import com.myBank.model.entities.Usuario;

public class DTO extends HashMap<String, Object> {

	public DTO () {
		super();
	}
	
	public static DTO getDTOFromMovimiento(Movimiento movimiento) {
		DTO dto = new DTO();
		if (movimiento != null) {
			dto.put("id", movimiento.getId());
			dto.put("cuenta", getDTOFromCuenta(movimiento.getCuenta()));
			dto.put("tipo", movimiento.getTipo());
			dto.put("fecha", movimiento.getFecha());
			dto.put("importe", movimiento.getImporte());
			dto.put("divisa", getDTOFromDivisa(movimiento.getDivisa()));
			dto.put("saldo", movimiento.getSaldo());
			dto.put("descripcion", movimiento.getDescripcion());
		}
		return dto;
	}
	
	public static DTO getDTOFromTransferencia(Transferencia transferencia) {
		DTO dto = new DTO();
		if (transferencia != null) {
			dto.put("id", transferencia.getId());
			dto.put("cuenta_origen", getDTOFromCuenta(transferencia.getCuenta1()));
			dto.put("cuenta_destino", getDTOFromCuenta(transferencia.getCuenta2()));
			dto.put("importe", transferencia.getImporte());
			dto.put("fecha", transferencia.getFecha());
			dto.put("descripcion", transferencia.getDescripcion());
			dto.put("estado", transferencia.getEstado());
		}
		return dto;
	}
	
	public static DTO getDTOFromCuenta(Cuenta cuenta) {
		DTO dto = new DTO();
		if (cuenta != null) {
			dto.put("id", cuenta.getId());
			dto.put("iban", cuenta.getIban());
			dto.put("saldo", cuenta.getSaldo());
			dto.put("titular", getDTOFromUsuario(cuenta.getUsuario(), true));
			dto.put("descripcion", cuenta.getDescripcion());
			dto.put("divisa", getDTOFromDivisa(cuenta.getDivisa()));
		}
		return dto;
	}
	
	public static DTO getDTOFromUsuario (Usuario usuario, boolean imagen) {
		DTO dto = new DTO();
		if (usuario != null) {
			dto.put("id", usuario.getId());
			dto.put("nombre", usuario.getNombre());
			dto.put("apellido1", usuario.getApellido1());
			dto.put("apellido2", usuario.getApellido2());
			dto.put("email", usuario.getEmail());
			dto.put("password", usuario.getPassword());
			if (imagen) dto.put("imagen", usuario.getImagen());
			dto.put("fecha_nac", usuario.getFechaNac());
			dto.put("telefono", usuario.getTelefono());
			dto.put("direccion", usuario.getDireccion());
			dto.put("localidad", usuario.getLocalidad());
			dto.put("codigo_postal", usuario.getCodigoPostal());
		}
		return dto;
	}
	
	public static DTO getDTOFromDivisa (Divisa divisa) {
		DTO dto = new DTO();
		if (divisa != null) {
			dto.put("id", divisa.getId());
			dto.put("descripcion", divisa.getDescripcion());
		}
		return dto;
	}

}
