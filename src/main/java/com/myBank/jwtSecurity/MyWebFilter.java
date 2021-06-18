package com.myBank.jwtSecurity;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/*")
public class MyWebFilter implements Filter{
	
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    	HttpServletRequest request = (HttpServletRequest) servletRequest; 
    	String uriDePeticionWeb = request.getRequestURI(); 
    	String metodoRequerido = request.getMethod(); 
    	int idUsuarioAutenticadoMedianteJWT = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
    	     
    	
    	System.out.println("Log - request: " + uriDePeticionWeb + " - " + request.getMethod());  
    	if (metodoRequerido.equalsIgnoreCase("OPTIONS") || 
    			uriDePeticionWeb.startsWith("/webapp") || 
    			uriDePeticionWeb.startsWith("/mybank") || 
    			uriDePeticionWeb.equals("/usuario/autenticar") ||
    			uriDePeticionWeb.equals("/usuario/getAutenticado") || 
    			uriDePeticionWeb.equals("/usuario/buscarEmail") ||
    			uriDePeticionWeb.equals("/divisa/all") ||
    			uriDePeticionWeb.equals("/usuario/registrarUsuario") ||
    			uriDePeticionWeb.equals("/cuenta/crear") || 
    			idUsuarioAutenticadoMedianteJWT != -1) {   
    		if (uriDePeticionWeb.equals("/mybank") ||
    				uriDePeticionWeb.equals("/mybank/") ||
    				uriDePeticionWeb.equals("/mybank/login") ||
    				uriDePeticionWeb.equals("/mybank/registro") ||
    				uriDePeticionWeb.equals("/mybank/listado-transferencias") ||
    				uriDePeticionWeb.equals("/mybank/listado-peticiones") ||
    				uriDePeticionWeb.equals("/mybank/listado-movimientos") ||
    				uriDePeticionWeb.equals("/mybank/seleccion-movimiento") ||
    				uriDePeticionWeb.equals("/mybank/modificar-datos")) {
            	HttpServletResponse response = (HttpServletResponse) servletResponse;
            	response.sendRedirect("/mybank/index.html");
    		}
    		filterChain.doFilter(servletRequest, servletResponse);
    	}
    	else {
        	HttpServletResponse response = (HttpServletResponse) servletResponse;
			response.sendError(403, "No autorizado"); 
    	}
    }
 
    @Override
    public void destroy() {
    }
}