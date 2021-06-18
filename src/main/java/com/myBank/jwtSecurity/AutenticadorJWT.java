package com.myBank.jwtSecurity;

import java.security.Key;

import javax.servlet.http.HttpServletRequest;

import com.myBank.model.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class AutenticadorJWT {
	
	private static Key key = null; 
	
	public static String codificaJWT (Usuario u) {
		String jws = Jwts.builder().setSubject("" + u.getId()).
				signWith(SignatureAlgorithm.HS512, getGeneratedKey()).compact();
		return jws;
	}
	
	public static int getIdUsuarioDesdeJWT (String jwt) {
		try {
			String stringIdUsuario = Jwts.parser().setSigningKey(getGeneratedKey()).parseClaimsJws(jwt).getBody().getSubject();
			int idUsuario = Integer.parseInt(stringIdUsuario);
			return idUsuario;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	
	public static int getIdUsuarioDesdeJwtIncrustadoEnRequest (HttpServletRequest request) {
		String autHeader = request.getHeader("Authorization");
		if (autHeader != null && autHeader.length() > 8) {
			String jwt = autHeader.substring(7);
			return getIdUsuarioDesdeJWT(jwt);
		}
		else {
			return -1;
		}
	}
	
	private static Key getGeneratedKey () {
		if (key == null) {
			key = MacProvider.generateKey();
		}
		return key;
	}

}
