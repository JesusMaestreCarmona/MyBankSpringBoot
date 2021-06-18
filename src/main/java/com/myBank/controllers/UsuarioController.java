package com.myBank.controllers;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.myBank.jwtSecurity.AutenticadorJWT;
import com.myBank.model.entities.Usuario;
import com.myBank.model.repositories.UsuarioRepository;

@CrossOrigin
@RestController
public class UsuarioController {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@PostMapping("/usuario/autenticar")
	public DTO autenticaUsuario (@RequestBody DatosAutenticacionUsuario datos) {
		DTO dto = new DTO();

		Usuario usuAutenticado = usuarioRepository.findByEmailAndPassword(datos.email, datos.password);
		if (usuAutenticado != null) {
			dto.put("jwt", AutenticadorJWT.codificaJWT(usuAutenticado));
		}

		return dto;
	}
	
	@GetMapping("/usuario/getAutenticado")
	public DTO getUsuarioAutenticado (boolean imagen, HttpServletRequest request) {
		DTO dtoResultado = null;
		
		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
		
		if (idUsuAutenticado != -1) {
			Usuario usuAutenticado = usuarioRepository.findById(idUsuAutenticado).get();
			dtoResultado = DTO.getDTOFromUsuario(usuAutenticado, imagen);
		}

		return dtoResultado;
	}
	
	@PutMapping("/usuario/actualizarDatosUsuario")
	public DTO actualizarDatosUsuario(@RequestBody DatosUsuario datosNuevoUsuario, HttpServletRequest request) {
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
			if (idUsuAutenticado != -1) {
				Usuario usuAutenticado = usuarioRepository.findById(idUsuAutenticado).get();
				usuAutenticado.setNombre(datosNuevoUsuario.nombre);
				usuAutenticado.setApellido1(datosNuevoUsuario.apellido1);
				usuAutenticado.setApellido2(datosNuevoUsuario.apellido2);
				if (datosNuevoUsuario.imagen != null) usuAutenticado.setImagen(Base64.decodeBase64(datosNuevoUsuario.imagen));
				usuAutenticado.setFechaNac(new Date(datosNuevoUsuario.fecha_nac));
				usuAutenticado.setTelefono(datosNuevoUsuario.telefono);
				usuAutenticado.setDireccion(datosNuevoUsuario.direccion);
				usuAutenticado.setLocalidad(datosNuevoUsuario.localidad);
				usuAutenticado.setCodigoPostal(datosNuevoUsuario.codigo_postal);
				if (datosNuevoUsuario.password != "") usuAutenticado.setPassword(datosNuevoUsuario.password);
				this.usuarioRepository.save(usuAutenticado);
			}
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	@GetMapping("/usuario/buscarEmail")
	public DTO buscarEmail(String email) {
		DTO dto = new DTO();
		boolean emailEncontrado = false;
		if (this.usuarioRepository.findByEmail(email) != null) emailEncontrado = true;
		dto.put("emailEncontrado", emailEncontrado);
		return dto;
	}
	
	@PutMapping("/usuario/registrarUsuario")
	public DTO registrarUsuario (@RequestBody DatosUsuario datosNuevoUsuario) {
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			Usuario usuarioARegistrar = new Usuario();
			usuarioARegistrar.setNombre(datosNuevoUsuario.nombre);
			usuarioARegistrar.setApellido1(datosNuevoUsuario.apellido1);
			usuarioARegistrar.setApellido2(datosNuevoUsuario.apellido2);
			usuarioARegistrar.setEmail(datosNuevoUsuario.email);
			usuarioARegistrar.setPassword(datosNuevoUsuario.password);
			if (datosNuevoUsuario.imagen != null) usuarioARegistrar.setImagen(Base64.decodeBase64(datosNuevoUsuario.imagen));
			usuarioARegistrar.setFechaNac(new Date(datosNuevoUsuario.fecha_nac));
			usuarioARegistrar.setTelefono(datosNuevoUsuario.telefono);
			usuarioARegistrar.setDireccion(datosNuevoUsuario.direccion);
			usuarioARegistrar.setLocalidad(datosNuevoUsuario.localidad);
			usuarioARegistrar.setCodigoPostal(datosNuevoUsuario.codigo_postal);
			this.usuarioRepository.save(usuarioARegistrar);
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
		
}

class DatosAutenticacionUsuario {
	String email;
	String password;

	public DatosAutenticacionUsuario(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}
}

class DatosUsuario {
	
	String email;
	String password;
	String nombre;
	String apellido1;
	String apellido2;
	String imagen;
	long fecha_nac;
	String telefono;
	String direccion;
	String localidad;
	String codigo_postal;
	
	public DatosUsuario(String email, String password, String nombre, String apellido1, String apellido2, String imagen,
			String telefono, String direccion, String localidad, String codigo_postal, long fecha_nac) {
		super();
		this.email = email;
		this.password = password;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.imagen = imagen;
		this.telefono = telefono;
		this.direccion = direccion;
		this.localidad = localidad;
		this.codigo_postal = codigo_postal;
		this.fecha_nac = fecha_nac;
	}
}
