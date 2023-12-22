package com.spring.aws.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@DynamoDBTable(tableName = "parentMsgs")
public class IsoMessage {

	@DynamoDBHashKey(attributeName = "financialEntity")
	private String financialEntity;

	@DynamoDBRangeKey(attributeName = "entityId")
	@DynamoDBAutoGeneratedKey
	private String entityId;

	@DynamoDBIndexHashKey(globalSecondaryIndexName = "gsiTypeMessage", attributeName = "typeMessage")
	private String typeMessage;


	@DynamoDBIndexHashKey(globalSecondaryIndexName = "gsiProductOrigin", attributeName = "productOrigin")
	private String productOrigin;

	@DynamoDBIndexHashKey(globalSecondaryIndexName = "gsiTypeTransaction", attributeName = "typeTransaction")
	private String typeTransaction;
	
	@DynamoDBAttribute(attributeName = "dateRegisterMessage")
	private String dateRegisterMessage;
	
	@DynamoDBAttribute(attributeName = "amountTransaction")
	private String amountTransaction;

	@DynamoDBAttribute(attributeName = "isoParentMessage")
	private String isoParentMessage;

}
