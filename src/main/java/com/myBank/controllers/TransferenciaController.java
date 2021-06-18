package com.myBank.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.myBank.model.entities.Transferencia;
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
	
	@PostMapping("/transferencia/getAllTransferenciasPaginacion")
	public DTO getAllTransferenciasPaginacion (@RequestBody DatosListadoTransferencia datos) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaTransferenciasEnDTO = new ArrayList<DTO>();
			List<Transferencia> transferencias = new ArrayList<Transferencia>();
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
			transferencias = (List<Transferencia>) this.transferenciaRepository.getAllTransferenciasPaginacionConFiltros(datos.idCuenta, importe, importe, dia, mes, anno, dia, datos.pagina * datos.elementosPorPagina, datos.elementosPorPagina);
			for (Transferencia transferencia : transferencias) {
				listaTransferenciasEnDTO.add(DTO.getDTOFromTransferencia(transferencia));
			}
			dto.put("transferencias", listaTransferenciasEnDTO);
			dto.put("totalTransferencias", this.transferenciaRepository.getCountTransferenciasCuentaConFiltros(datos.idCuenta, importe, importe, dia, mes, anno, dia));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@PostMapping("/transferencia/getAllPeticionesPaginacion")
	public DTO getAllPeticionesPaginacion (@RequestBody DatosListadoTransferencia datos) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaTransferenciasEnDTO = new ArrayList<DTO>();
			List<Transferencia> transferencias = new ArrayList<Transferencia>();
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
			transferencias = (List<Transferencia>) this.transferenciaRepository.getAllPeticionesPaginacionConFiltros(datos.idCuenta, importe, importe, dia, mes, anno, dia, datos.pagina * datos.elementosPorPagina, datos.elementosPorPagina);
			for (Transferencia transferencia : transferencias) {
				listaTransferenciasEnDTO.add(DTO.getDTOFromTransferencia(transferencia));
			}
			dto.put("peticiones", listaTransferenciasEnDTO);
			dto.put("totalPeticiones", this.transferenciaRepository.getCountPeticionesCuentaConFiltros(datos.idCuenta, importe, importe, dia, mes, anno, dia));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@GetMapping("/transferencia/getPeticionesANotificar")
	public DTO getPeticionesANotificar (int idCuenta) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaTransferenciasEnDTO = new ArrayList<DTO>();
			List<Transferencia> transferencias = new ArrayList<Transferencia>();
			transferencias = (List<Transferencia>) this.transferenciaRepository.getPeticionesANotificar(idCuenta);
			for (Transferencia transferencia : transferencias) {
				transferencia.setNotificada(true);
				this.transferenciaRepository.save(transferencia);
				listaTransferenciasEnDTO.add(DTO.getDTOFromTransferencia(transferencia));
			}
			dto.put("peticiones", listaTransferenciasEnDTO);
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
			float importe = -1;
			int dia = -1, mes = 1, anno = 0;
			dto.put("totalPeticiones", this.transferenciaRepository.getCountPeticionesCuentaConFiltros(idCuenta, importe, importe, dia, mes, anno, dia));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}	
					
}

class Filtro {
	
	String name;
	String value;
	
	public Filtro(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
}

class DatosListadoTransferencia {
	
	int idCuenta;
	int pagina;
	int elementosPorPagina;
	Filtro[] filtros;
	
	public DatosListadoTransferencia(int idCuenta, int pagina, int elementosPorPagina, Filtro[] filtros) {
		super();
		this.idCuenta = idCuenta;
		this.pagina = pagina;
		this.elementosPorPagina = elementosPorPagina;
		this.filtros = filtros;
	}
	
}

