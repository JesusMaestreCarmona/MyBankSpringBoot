package com.myBank.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping("/transferencia/getAllTransferenciasPaginacion")
	public DTO getAllTransferenciasPaginacion (int idCuenta, int pagina, int elementosPorPagina) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaTransferenciasEnDTO = new ArrayList<DTO>();
			List<Transferencia> transferencias = new ArrayList<Transferencia>();
			transferencias = (List<Transferencia>) this.transferenciaRepository.getAllTransferenciasPaginacion(idCuenta, pagina * elementosPorPagina, elementosPorPagina);
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
	
	@GetMapping("/transferencia/getAllPeticionesPaginacion")
	public DTO getAllPeticionesPaginacion (int idCuenta, int pagina, int elementosPorPagina) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaTransferenciasEnDTO = new ArrayList<DTO>();
			List<Transferencia> transferencias = new ArrayList<Transferencia>();
			transferencias = (List<Transferencia>) this.transferenciaRepository.getAllPeticionesPaginacion(idCuenta, pagina * elementosPorPagina, elementosPorPagina);
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
			dto.put("totalPeticiones", this.transferenciaRepository.getCountPeticionesCuenta(idCuenta));
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}	
					
}

