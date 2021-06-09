package com.myBank.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myBank.model.entities.Divisa;
import com.myBank.model.repositories.DivisaRepository;
import com.myBank.model.services.RestService;

@CrossOrigin
@RestController
public class DivisaController {
	
	@Autowired
	DivisaRepository divisaRepository;
	@Autowired
	RestService restService;
	
	@GetMapping("/divisa/all")
	public DTO getAllDivisas (HttpServletRequest request) {		
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			List<DTO> listaDivisasEnDTO = new ArrayList<DTO>();
			List<Divisa> divisas = new ArrayList<Divisa>();
			divisas = (List<Divisa>) this.divisaRepository.findAll();
			for (Divisa divisa : divisas) {
				listaDivisasEnDTO.add(DTO.getDTOFromDivisa(divisa));
			}
			dto.put("divisas", listaDivisasEnDTO);
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@GetMapping("/divisa/getCambioMoneda")
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

}
