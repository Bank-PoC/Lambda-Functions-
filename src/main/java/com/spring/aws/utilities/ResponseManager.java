package com.spring.aws.utilities;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseManager {
    /**
     * Se encarga de parsear una fecha de formato String a LocalDateTime
     *
     * @param statusCode Codigo Http para la respuesta
     * @param bodyValue Contenido
     * @return Objeto de tipo APIGatewayProxyResponseEvent con respuesta para el cliente
     */
    public APIGatewayProxyResponseEvent customResponse(Integer statusCode,  String bodyValue){
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(bodyValue);

        Map<String, String> headers = new HashMap();
        headers.put("Access-Control-Allow-Origin","*");
        headers.put("Access-Control-Allow-Credentials","true");
        response.setHeaders(headers);

        return response;
    }
}