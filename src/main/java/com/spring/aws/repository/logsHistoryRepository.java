package com.spring.aws.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.spring.aws.domain.LogHistory;

@Repository
public class logsHistoryRepository{

	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	public List<LogHistory> getDataByFilters(String financialEntity,String typeMessage,String productOrigin, String typeTransaction) {
		
	    List<LogHistory> logHistoryList = new ArrayList<>();

	    // Agregamos los valores de los atributos de la consulta.
	    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
	    expressionAttributeValues.put(":financialEntity", new AttributeValue().withS(financialEntity));
	    expressionAttributeValues.put(":typeMessage", new AttributeValue().withS(typeMessage));
	    expressionAttributeValues.put(":productOrigin", new AttributeValue().withS(productOrigin));
	    expressionAttributeValues.put(":typeTransaction", new AttributeValue().withS(typeTransaction));

	    // Construir la expresión para la consulta
	    String filterExpression = "financialEntity = :financialEntity and typeMessage = :typeMessage and productOrigin = :productOrigin and typeTransaction = :typeTransaction";
	    
	    //Creamos una DynamoDBScanExpression
	    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
	            .withFilterExpression(filterExpression)
	            .withExpressionAttributeValues(expressionAttributeValues);

	    // Lista que representa el scanExpression.
	    PaginatedScanList<LogHistory> scanResult = dynamoDBMapper.scan(LogHistory.class, scanExpression);

	    // Se agregan los resultados a la lista inicial
	    logHistoryList.addAll(scanResult);

	    return logHistoryList;
		}
	
	public LogHistory save(LogHistory logHistory) {
		dynamoDBMapper.save(logHistory);
		return logHistory;
	}
	
	public List<LogHistory> GetLogHistoryById(String entityId) {

		Map<String, AttributeValue> attrValue = new HashMap<>();

		attrValue.put(":entityId", new AttributeValue().withS(entityId));

		String filter = "entityId = :entityId";

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression(filter)
				.withExpressionAttributeValues(attrValue);

		List<LogHistory> response = dynamoDBMapper.scan(LogHistory.class, scanExpression);
		return response;

	}
	
}
