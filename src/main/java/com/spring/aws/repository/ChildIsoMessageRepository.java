package com.spring.aws.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.spring.aws.domain.ChildIsoMessage;


@Repository
public class ChildIsoMessageRepository {
	@Autowired
	private DynamoDBMapper dynamoDBMapper;
	
	public ChildIsoMessage save(ChildIsoMessage isoMsgChild) {
		// log.trace("Metodo SaveIsoMessage");
		dynamoDBMapper.save(isoMsgChild);
		return isoMsgChild;
	}
	
	
	public List<ChildIsoMessage> GetTramaChildById(String entityId) {

		Map<String, AttributeValue> attrValue = new HashMap<>();
		
		attrValue.put(":entityId", new AttributeValue().withS(entityId));

		String filter = "entityId = :entityId";
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression(filter)
				.withExpressionAttributeValues(attrValue);

		List<ChildIsoMessage> response = dynamoDBMapper.scan(ChildIsoMessage.class, scanExpression);
		return response;

	}

}
