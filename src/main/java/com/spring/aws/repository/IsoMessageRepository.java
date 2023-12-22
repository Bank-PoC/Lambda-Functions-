package com.spring.aws.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.spring.aws.domain.IsoMessage;
import com.spring.aws.domain.Master;
import com.spring.aws.exception.IsoMessageNotFound;

@Repository
public class IsoMessageRepository {
	
	@Autowired
	private DynamoDBMapper dynamoDBMapper;
	
	public List<Master> getMaster(String typeMaster, String masterCode) {
		
		Master isoMsg = new Master();
		isoMsg.setTypeMaster(typeMaster);
		isoMsg.setMasterCode(masterCode);
		DynamoDBQueryExpression<Master> query = new DynamoDBQueryExpression<Master>().withHashKeyValues(isoMsg)
				.withConsistentRead(false);
		List<Master> response = dynamoDBMapper.query(Master.class, query);
		return response;
	}
	
	public boolean existeAtt(String isoParentMessage, String financialEntity ) {
		Map<String, AttributeValue> attValue = new HashMap<>();
		attValue.put(":val1", new AttributeValue().withS(isoParentMessage));
		attValue.put(":val2", new AttributeValue().withS(financialEntity));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression("isoParentMessage = :val1 and financialEntity = :val2")
				.withExpressionAttributeValues(attValue);

		
		List<IsoMessage> resp = dynamoDBMapper.scan(IsoMessage.class, scanExpression);
		return !resp.isEmpty();		
	}

	public IsoMessage save(IsoMessage isoMsg) {
		dynamoDBMapper.save(isoMsg);
		return isoMsg;
	}

	public List<IsoMessage> GetTramaById(String entityId) {

		Map<String, AttributeValue> attrValue = new HashMap<>();

		attrValue.put(":entityId", new AttributeValue().withS(entityId));

		String filter = "entityId = :entityId";

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression(filter)
				.withExpressionAttributeValues(attrValue);

		List<IsoMessage> response = dynamoDBMapper.scan(IsoMessage.class, scanExpression);
		return response;

	}

	public List<IsoMessage> GetTramaByAtt(String financialEntity, String typeMessage,
			String productOrigin, String typeTransaction) {

		Map<String, AttributeValue> attrValue = new HashMap<>();

		attrValue.put(":financialEntity", new AttributeValue().withS(financialEntity));
		attrValue.put(":typeMessage", new AttributeValue().withS(typeMessage));
		attrValue.put(":productOrigin", new AttributeValue().withS(productOrigin));
		attrValue.put(":typeTransaction", new AttributeValue().withS(typeTransaction));

		String filter = "financialEntity = :financialEntity and typeMessage = :typeMessage and productOrigin = :productOrigin and typeTransaction = :typeTransaction";

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression(filter)
				.withExpressionAttributeValues(attrValue);

		List<IsoMessage> response = dynamoDBMapper.scan(IsoMessage.class, scanExpression);
		return response;

	}


	public void deleteTrama(String entityId) {
		Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put(":val", new AttributeValue().withS(entityId));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression("entityId = :val")
				.withExpressionAttributeValues(expressionAttributeValues);

		List<IsoMessage> msg = dynamoDBMapper.scan(IsoMessage.class, scanExpression);

		if (!msg.isEmpty()) {
			IsoMessage deleteID = msg.get(0);
			dynamoDBMapper.delete(deleteID);
		} else {
			throw new IsoMessageNotFound("Trama IsoMessage No Encontrada");
		}
	}

}
