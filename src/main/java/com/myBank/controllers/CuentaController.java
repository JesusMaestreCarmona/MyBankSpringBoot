package com.myBank.controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.myBank.jwtSecurity.AutenticadorJWT;
import com.myBank.model.entities.Cuenta;
import com.myBank.model.entities.Divisa;
import com.myBank.model.entities.Usuario;
import com.myBank.model.repositories.CuentaRepository;
import com.myBank.model.repositories.DivisaRepository;
import com.myBank.model.repositories.UsuarioRepository;
import com.myBank.model.services.RestService;

@CrossOrigin
@RestController
public class CuentaController {

	@Autowired
	CuentaRepository cuentaRepository;
	@Autowired
	UsuarioRepository usuarioRepository;
	@Autowired
	DivisaRepository divisaRepository;
	@Autowired
	RestService restService;
	
	@GetMapping("/cuenta/all")
	public DTO getAllCuentas (HttpServletRequest request) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
			if (idUsuAutenticado != -1) {
				Usuario usuAutenticado = usuarioRepository.findById(idUsuAutenticado).get();
				List<DTO> listaCuentasEnDTO = new ArrayList<DTO>();
				List<Cuenta> cuentas = new ArrayList<Cuenta>();
				cuentas = (List<Cuenta>) this.cuentaRepository.findByUsuario(usuAutenticado);
				for (Cuenta cuenta : cuentas) {
					listaCuentasEnDTO.add(DTO.getDTOFromCuenta(cuenta));
				}
				dto.put("cuentas", listaCuentasEnDTO);
				dto.put("result", "ok");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@GetMapping("/cuenta/findById")
	public DTO getAllCuentas (int id, HttpServletRequest request) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			Cuenta cuenta = null;
			if (id != -1) {
				cuenta = this.cuentaRepository.findById(id).get();
			}
			else {
				int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
				if (idUsuAutenticado != -1) { 
					Usuario usuAutenticado = usuarioRepository.findById(idUsuAutenticado).get();
					cuenta = this.cuentaRepository.findFirstByUsuarioOrderById(usuAutenticado);
				}
			}
			dto.put("cuenta", DTO.getDTOFromCuenta(cuenta));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@GetMapping("/cuenta/buscarIban")
	public DTO ibanEncontrado(String iban) {
		DTO dto = new DTO();
		boolean ibanEncontrado = false;
		if (this.cuentaRepository.findByIban(iban) != null) ibanEncontrado = true;
		dto.put("ibanEncontrado", ibanEncontrado);
		return dto;
	}
	
	@PutMapping("/cuenta/actualizar")
	public DTO actualizarCuenta(@RequestBody DatosCuenta datos) {
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			Cuenta cuenta = this.cuentaRepository.findById(datos.idCuenta).get();
			Divisa nuevaDivisa = this.divisaRepository.findById(datos.idDivisa).get();
			
			float cambioMoneda = this.restService.getCambioMoneda(cuenta.getDivisa().getDescripcion(), nuevaDivisa.getDescripcion());
			
			cuenta.setSaldo(cuenta.getSaldo() * cambioMoneda);
			cuenta.setDescripcion(datos.descripcion);
			cuenta.setDivisa(nuevaDivisa);
			this.cuentaRepository.save(cuenta);
			dto.put("cuentaActualizada", DTO.getDTOFromCuenta(cuenta));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@PutMapping("/cuenta/crear")
	public DTO crearCuenta(@RequestBody DatosCuenta datos, HttpServletRequest request) {
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
			if (idUsuAutenticado != -1) { 
				Usuario usuAutenticado = usuarioRepository.findById(idUsuAutenticado).get();
				Cuenta cuenta = new Cuenta();
				Divisa divisa = this.divisaRepository.findById(datos.idDivisa).get();
	            
				cuenta.setIban("ES");			
				cuenta.setSaldo(0);
				cuenta.setUsuario(usuAutenticado);
				cuenta.setDescripcion(datos.descripcion);
				cuenta.setDivisa(divisa);
				this.cuentaRepository.save(cuenta);
				
				cuenta.setIban(cuenta.getIban() + new DecimalFormat("#.####################################").format(Math.floor(Math.random() * Math.pow(10, 14))) + "" + cuenta.getId());			
				this.cuentaRepository.save(cuenta);

				dto.put("result", "ok");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
				
}

class DatosCuenta {
	
	int idCuenta;
	String descripcion;
	int idDivisa;
	
	public DatosCuenta(int idCuenta, String descripcion, int idDivisa) {
		super();
		this.idCuenta = idCuenta;
		this.descripcion = descripcion;
		this.idDivisa = idDivisa;
	}
	
}

