package com.myBank.controllers;


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
import com.myBank.model.repositories.RolRepository;
import com.myBank.model.repositories.UsuarioRepository;

@CrossOrigin
@RestController
public class UsuarioController {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	@Autowired
	RolRepository rolRepository;
	
	/**
	 * Autentica un usuario, dados su datos de acceso: nombre de usuario y contraseña
	 */
	@PostMapping("/usuario/autenticar")
	public DTO autenticaUsuario (@RequestBody DatosAutenticacionUsuario datos) {
		DTO dto = new DTO(); // Voy a devolver un dto

		// Intento localizar un usuario a partir de su nombre de usuario y su password
		Usuario usuAutenticado = usuarioRepository.findByEmailAndPassword(datos.email, datos.password);
		if (usuAutenticado != null) {
			dto.put("jwt", AutenticadorJWT.codificaJWT(usuAutenticado));
		}

		// Finalmente devuelvo el JWT creado, puede estar vacío si la autenticación no ha funcionado
		return dto;
	}
	
	@GetMapping("/usuario/getAutenticado")
	public DTO getUsuarioAutenticado (boolean imagen, HttpServletRequest request) {
		DTO dtoResultado = null;
		
		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT
		
		// Intento localizar un usuario a partir de su id
		if (idUsuAutenticado != -1) {
			Usuario usuAutenticado = usuarioRepository.findById(idUsuAutenticado).get();
			dtoResultado = DTO.getDTOFromUsuario(usuAutenticado, imagen);
		}

		// Finalmente devuelvo el JWT creado, puede estar vacío si la autenticación no ha funcionado
		return dtoResultado;
	}
	
	@PutMapping("/usuario/actualizarDatosUsuario")
	public DTO actualizarDatosUsuario(@RequestBody DatosParaNuevoUsuario datosNuevoUsuario, HttpServletRequest request) {
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
				usuAutenticado.setTelefono(datosNuevoUsuario.telefono);
				usuAutenticado.setDireccion(datosNuevoUsuario.direccion);
				usuAutenticado.setLocalidad(datosNuevoUsuario.localidad);
				usuAutenticado.setCodigoPostal(datosNuevoUsuario.codigo_postal);
				usuAutenticado.setEstado(datosNuevoUsuario.estado);
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
	public DTO registrarUsuario (@RequestBody DatosParaNuevoUsuario datosNuevoUsuario) {
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			Usuario usuarioARegistrar = new Usuario();
			usuarioARegistrar.setNombre(datosNuevoUsuario.nombre);
			usuarioARegistrar.setApellido1(datosNuevoUsuario.apellido1);
			usuarioARegistrar.setApellido2(datosNuevoUsuario.apellido2);
			usuarioARegistrar.setEmail(datosNuevoUsuario.email);
			usuarioARegistrar.setPassword(datosNuevoUsuario.password);
			usuarioARegistrar.setRol(this.rolRepository.findById(2).get());
			if (datosNuevoUsuario.imagen != null) usuarioARegistrar.setImagen(Base64.decodeBase64(datosNuevoUsuario.imagen));
			usuarioARegistrar.setTelefono(datosNuevoUsuario.telefono);
			usuarioARegistrar.setDireccion(datosNuevoUsuario.direccion);
			usuarioARegistrar.setLocalidad(datosNuevoUsuario.localidad);
			usuarioARegistrar.setCodigoPostal(datosNuevoUsuario.codigo_postal);
			usuarioARegistrar.setEstado(datosNuevoUsuario.estado);
			this.usuarioRepository.save(usuarioARegistrar);
			dto.put("result", "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
		
}

/**
 * Clase que contiene los datos de autenticacion del usuario
 */
class DatosAutenticacionUsuario {
	String email;
	String password;

	/**
	 * Constructor
	 */
	public DatosAutenticacionUsuario(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}
}

class DatosParaNuevoUsuario {
	
	String email;
	String password;
	String nombre;
	String apellido1;
	String apellido2;
	String imagen;
	String telefono;
	String direccion;
	String localidad;
	String codigo_postal;
	String estado;
	
	public DatosParaNuevoUsuario(String email, String password, String nombre, String apellido1, String apellido2, String imagen,
			String telefono, String direccion, String localidad, String codigo_postal, String estado) {
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
		this.estado = estado;
	}
}
