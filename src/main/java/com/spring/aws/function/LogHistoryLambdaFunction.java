package com.spring.aws.function;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.spring.aws.domain.ChildIsoMessage;
import com.spring.aws.domain.IsoMessage;
import com.spring.aws.domain.LogHistory;
import com.spring.aws.dto.IsoMsgDTO;
import com.spring.aws.dto.ResponseId;
import com.spring.aws.exception.IsoMessageBadRequest;
import com.spring.aws.exception.IsoMessageNotFound;
import com.spring.aws.service.ChildIsoMsgService;
import com.spring.aws.service.logHistoryService;
import com.spring.aws.utilities.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class LogHistoryLambdaFunction {

	@Autowired
	private logHistoryService logService;

	@Autowired
	private ResponseManager responseManager;
	
	ObjectMapper objectMapper = new ObjectMapper();

	@Bean
	public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> logHistory() {
	    return (event) -> {
	        try {
	            String financialEntity = event.getQueryStringParameters().get("financialEntity");
	            String typeMessage = event.getQueryStringParameters().get("typeMessage");
	            String productOrigin = event.getQueryStringParameters().get("productOrigin");
	            String typeTransaction = event.getQueryStringParameters().get("typeTransaction");

	            List<LogHistory> logs = logService.getLogsByFilters(financialEntity, typeMessage, productOrigin, typeTransaction);

	            // Convertir la lista en una cadena JSON legible

	            List<ObjectNode> jsonLogs = new ArrayList<>();
	            ObjectMapper objectMapper = new ObjectMapper();
	            for (LogHistory log : logs) {
	                ObjectNode jsonLog = objectMapper.valueToTree(log);
	                jsonLogs.add(jsonLog);
	            }

	            // Crear un escritor con formato de impresión legible
	            ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
				String bodyResponse = writer.writeValueAsString(jsonLogs);
				APIGatewayProxyResponseEvent response = responseManager.customResponse(200, bodyResponse);

	            return response;
	        } catch (IsoMessageBadRequest br) {
                APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
                errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                errorResponse.setBody("Los Campos: Entidad financiera, Tipo de Mensaje, Producto de Origen y Tipo de Transacción son requeridos como parámetros de consulta");
                return errorResponse;
                
            }catch (IsoMessageNotFound nf) {
	            APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
	            errorResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
	            errorResponse.setBody("No hay tramas que cumplan con los parametros especificados");
	            return errorResponse;
	        }catch (Exception e) {
	            APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
	            errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            errorResponse.setBody("Error en el servidor: " + e.getMessage());

	            return errorResponse;
	        }
	        
	    };
	}

	@Bean
	public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> sendAuth() {
	    return (event) -> {
			String entityIdSentParent=event.getQueryStringParameters().get("entityIdSentParent");
			String entityIdSentChild=event.getQueryStringParameters().get("entityIdSentChild");

	        LogHistory logHistory = null; 
			
	        try {
	        	IsoMsgDTO reponseAuth=logService.saveLogHistory(entityIdSentParent, entityIdSentChild);	           
	            String json = objectMapper.writeValueAsString(reponseAuth);
	            
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

	@Bean
	public Function<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> getLogHistoryById(){
		return(event)->{
			try {
				Map<String, String> pathParameters = event.getPathParameters();
				String entityId = pathParameters.get("entityId");
				
				Object resp=logService.GetLogHistoryById(entityId);
								
	            String json = objectMapper.writeValueAsString(resp);

				// Crear un escritor con formato de impresión legible
	            //ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
					
				APIGatewayProxyResponseEvent response = responseManager.customResponse(200, resp.toString());
	                return response;
  
			}catch (IsoMessageNotFound ex) {
				IsoMsgDTO resp = new IsoMsgDTO(HttpStatus.NOT_FOUND.value(),ex.getMessage());
	            String json = null;
				try {
					json = objectMapper.writeValueAsString(resp);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	APIGatewayProxyResponseEvent response = responseManager.customResponse(
	        			HttpStatus.NOT_FOUND.value(),json);
	        
	            return response;

	        } catch (Exception ex) {
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
