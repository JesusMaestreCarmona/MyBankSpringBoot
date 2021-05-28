package com.myBank.model.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestService {

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public JsonNode getJSONFromRequest(String url) {
        ResponseEntity<String> response = this.restTemplate.getForEntity(url, String.class);
        if(response.getStatusCode() == HttpStatus.OK) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				return mapper.readTree(response.getBody());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
        } else {
            return null;
        }
    }
    
	public float getCambioMoneda(String moneda_origen, String moneda_destino) {
		String urlAPI = "https://free.currconv.com/api/v7/convert?q=" + moneda_origen + "_" + moneda_destino + "&compact=ultra&apiKey=bea75c50ebac07d53ecf";
		JsonNode json = this.getJSONFromRequest(urlAPI);
		return Float.parseFloat("" + json.path(moneda_origen + "_" + moneda_destino));
	}
	
}
