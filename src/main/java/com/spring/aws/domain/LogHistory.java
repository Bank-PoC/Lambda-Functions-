package com.spring.aws.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.spring.aws.domain.LogHistory.logChildEntry;
import com.spring.aws.domain.LogHistory.logParentEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@DynamoDBTable(tableName="logHistory")
public class LogHistory {
	
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
	
    @DynamoDBAttribute(attributeName="logChildEntry")
    private logChildEntry  logChildEntry;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @DynamoDBDocument
    public static class logChildEntry{
    	
    	@DynamoDBAttribute(attributeName = "dateSentChildMsg")
    	private String dateSentChildMsg;
    	
    	@DynamoDBAttribute(attributeName = "dateAnsChildMsg")
    	private String dateAnsChildMsg;
    	
    	@DynamoDBAttribute(attributeName = "entryChildMsg")
    	private String entryChildMsg;
    	
    	@DynamoDBAttribute(attributeName = "ansChildMsg")
    	private String ansChildMsg;
    	
    }

    @DynamoDBAttribute(attributeName="logParentEntry")
    private logParentEntry  logParentEntry;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @DynamoDBDocument
    public static class logParentEntry{
    	
    	@DynamoDBAttribute(attributeName = "dateSentParentMsg")
    	private String dateSentParentMsg;
    	    	
    	@DynamoDBAttribute(attributeName = "dateAnsParentMsg")
    	private String dateAnsParentMsg;
    	    	
    	@DynamoDBAttribute(attributeName = "entryParentMsg")
    	private String entryParentMsg;
    	
    	@DynamoDBAttribute(attributeName = "ansParentMsg")
    	private String ansParentMsg;

    }

}

