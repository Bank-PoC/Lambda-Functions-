package com.spring.aws.function;

import com.spring.aws.service.IsoMessageService;
import com.spring.aws.utilities.ResponseManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.spring.aws.exception.IsoMessageBadRequest;
import com.spring.aws.exception.IsoMessageConfict;
import com.spring.aws.exception.IsoMessageNotContent;
import com.spring.aws.exception.IsoMessageNotFound;
import com.spring.aws.domain.IsoMessage;
import com.spring.aws.dto.IsoMsgDTO;

@Configuration
public class IsoMsgsFunction {
	
	@Autowired
	private IsoMessageService isoService;

	@Autowired
	private ResponseManager responseManager;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@Bean
	public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> saveTrama() {
	    return event -> {
	        IsoMessage isoMsg = null; 

	        try {
	            String body = event.getBody();
	            
	            isoMsg = objectMapper.readValue(body, IsoMessage.class);

	            IsoMessage msg = isoService.saveTrama(isoMsg);
	            //IsoMsgDTO resp = new IsoMsgDTO(HttpStatus.CREATED.value(),"Trama IsoMessage Guardada Con Exito");
	            String json = objectMapper.writeValueAsString(msg);
	        
	            APIGatewayProxyResponseEvent response = responseManager.customResponse(HttpStatus.CREATED.value(), json);
	            return response;
	            
	        } catch (IsoMessageConfict ex) {
	        	IsoMsgDTO resp = new IsoMsgDTO(HttpStatus.CONFLICT.value(),ex.getMessage());
	            String json = null;
				try {
					json = objectMapper.writeValueAsString(resp);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	APIGatewayProxyResponseEvent response = responseManager.customResponse(
	        			HttpStatus.CONFLICT.value(),json);
	        
	            return response;
	       
	        } catch (IsoMessageBadRequest ex) {
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
	public Function<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> getTramaById(){
		return (event)->{
			try {
				String entityId=event.getQueryStringParameters().get("entityId");
				
				List<IsoMessage> resp = isoService.getTramaById(entityId);
				
				for(IsoMessage obj: resp) {
					String msg = obj.getIsoParentMessage();
					
					ResponseEntity<String> decodeMsg = isoService.sendDecodeMSj("{\"isoMessage\":\""+msg+"\"}");
					 // Crear un escritor con formato de impresión legible
	                //ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
					
					APIGatewayProxyResponseEvent response = responseManager.customResponse(200, decodeMsg.getBody());

	                return response;
				}
  
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
			return null;
		};
	}
	
	
    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getTramaByAtt() {
        return (event) -> {
            try {
                String financialEntity = event.getQueryStringParameters().get("financialEntity");
                String typeMessage = event.getQueryStringParameters().get("typeMessage");
                String productOrigin = event.getQueryStringParameters().get("productOrigin");
                String typeTransaction = event.getQueryStringParameters().get("typeTransaction");

        		List<IsoMessage> tramas = isoService.getTramaByAtt(financialEntity, typeMessage, productOrigin, typeTransaction);
        		List<IsoMessage> listaOrdenada = tramas.stream()
        				.sorted(java.util.Comparator.comparing(IsoMessage::getDateRegisterMessage).reversed())
        				.collect(Collectors.toList());
                // Convertir la lista en una cadena JSON legible
                List<ObjectNode> jsonTramas = new ArrayList<>();
                ObjectMapper objectMapper = new ObjectMapper();
                for (IsoMessage trama : listaOrdenada) {
                    ObjectNode jsonT = objectMapper.valueToTree(trama);
                    jsonTramas.add(jsonT);
                }

                // Crear un escritor con formato de impresión legible
                ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
				String bodyResponse = writer.writeValueAsString(jsonTramas);
				APIGatewayProxyResponseEvent response = responseManager.customResponse(200, bodyResponse);

                return response;
                
            }catch (IsoMessageNotContent ex) {
            	IsoMsgDTO resp = new IsoMsgDTO(HttpStatus.NO_CONTENT.value(),ex.getMessage());
	            String json = null;
				try {
					json = objectMapper.writeValueAsString(resp);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	APIGatewayProxyResponseEvent response = responseManager.customResponse(
	        			HttpStatus.NO_CONTENT.value(),json);
	        
	            return response;

	        } catch (IsoMessageBadRequest ex) {
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
            }
            catch (Exception ex) {
                // Manejar la excepción y devolver una respuesta de error
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
	public Function<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> deleteTrama(){
		return(event)->{
			try {
				Map<String, String> pathParameters = event.getPathParameters();
				String entityId = pathParameters.get("entityId");
				
				isoService.deleteTrama(entityId);
				
	            String body = event.getBody();
	            
	            IsoMsgDTO resp = new IsoMsgDTO(HttpStatus.OK.value(),"Trama ISO8583 eliminada con exito");
	            String json = objectMapper.writeValueAsString(resp);
	        
				APIGatewayProxyResponseEvent response = responseManager.customResponse(200, json);
				
	                return response;
	                
				} catch (IsoMessageNotFound ex) {
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
