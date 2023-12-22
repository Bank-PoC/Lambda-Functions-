package com.spring.aws.function;

import java.util.Map;
import java.util.function.Function;
import com.spring.aws.dto.ResponseId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.aws.domain.ChildIsoMessage;
import com.spring.aws.domain.IsoMessage;
import com.spring.aws.dto.IsoMsgDTO;
import com.spring.aws.exception.IsoMessageBadRequest;
import com.spring.aws.exception.IsoMessageConfict;
import com.spring.aws.service.ChildIsoMsgService;
import com.spring.aws.service.IsoMessageService;
import com.spring.aws.utilities.ResponseManager;

@Configuration
public class ChildMsgsFunction {
	
	//codifica el mensaje y lo guarda en base de datos tablaHija como parametro recibe el idParent y un IsoDecode
	//y se llama a la funcion childService.getTramaPadreEncode(isoDecode, IdParent);
	
	@Autowired
	private ChildIsoMsgService childIsoService;

	@Autowired
	private ResponseManager responseManager;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	
	@Bean
	public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> saveChildTrama() {
	    return (event) -> {
	        ChildIsoMessage isoChildMsg = null; 
	    	
			Map<String, String> pathParameters = event.getPathParameters();
			String idParent = pathParameters.get("idParent");
			
	        try {
	        	ResponseId childId= childIsoService.getTramaPadreEncode(event.getBody(), idParent);
	           
	            String json = objectMapper.writeValueAsString(childId);
	            
	            APIGatewayProxyResponseEvent response = responseManager.customResponse(
	        			HttpStatus.CREATED.value(), json);
	            return response;
	            
	        }catch (IsoMessageBadRequest ex) {
	        	IsoMsgDTO resp = new IsoMsgDTO(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
	            String json = null;
				try {
					json = objectMapper.writeValueAsString(resp);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	APIGatewayProxyResponseEvent response = responseManager.customResponse(
	        			HttpStatus.BAD_REQUEST.value(),json);
	        
	            return response;
	       
	        }catch (Exception ex) {
	        	IsoMsgDTO resp = new IsoMsgDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage());
	            String json = null;
				try {
					json = objectMapper.writeValueAsString(resp);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	APIGatewayProxyResponseEvent response = responseManager.customResponse(
	        			HttpStatus.INTERNAL_SERVER_ERROR.value(),json);
	        
	            return response;
	       
	        }
	    };
	}


}
