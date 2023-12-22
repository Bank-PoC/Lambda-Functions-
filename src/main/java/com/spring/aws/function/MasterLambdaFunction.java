package com.spring.aws.function;

import com.spring.aws.domain.Master;
import com.spring.aws.exception.IsoMessageBadRequest;
import com.spring.aws.exception.IsoMessageNotFound;
import com.spring.aws.service.IsoMessageService;

import com.spring.aws.utilities.ResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Configuration
public class MasterLambdaFunction {

	@Autowired
	private IsoMessageService isoService;

	@Autowired
	private ResponseManager responseManager;

	@Bean
	public Function<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> getMasters(){
		return(event)->{
			try {
				String typeMaster=event.getQueryStringParameters().get("typeMaster");
				String masterCode=event.getQueryStringParameters().get("masterCode");
				
				List<Master> resp = isoService.getMaster(typeMaster, masterCode);
				
	            // Convertir la lista en una cadena JSON legible
	            List<ObjectNode> jsonMasters = new ArrayList<>();
	            ObjectMapper objectMapper = new ObjectMapper();
	            for (Master master : resp) {
	                ObjectNode jsonMaster = objectMapper.valueToTree(master);
	                jsonMasters.add(jsonMaster);
	            }

	            // Crear un escritor con formato de impresión legible
	            ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
				String bodyResponse = writer.writeValueAsString(jsonMasters);
				APIGatewayProxyResponseEvent response = responseManager.customResponse(200, bodyResponse);

	            return response;
			} catch (IsoMessageBadRequest br) {
                APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
                errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                errorResponse.setBody("Los Campos: Codigo maestro es requerido como parámetro de consulta");
                return errorResponse;
                
            }catch (IsoMessageNotFound nf) {
	            APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
	            errorResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
	            errorResponse.setBody("No hay campos base con el parámetro de consulta especificado");
	            return errorResponse;
	        }catch (Exception e) {
	            APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
	            errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            errorResponse.setBody("Error en el servidor: " + e.getMessage());

	            return errorResponse;
	        }
		};
	}

}
