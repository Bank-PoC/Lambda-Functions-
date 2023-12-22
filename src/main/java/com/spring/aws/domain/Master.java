package com.spring.aws.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@DynamoDBTable(tableName = "mastersTable")
public class Master {
	
	@DynamoDBHashKey(attributeName = "typeMaster")
	private String typeMaster;
	
	@DynamoDBRangeKey(attributeName = "masterCode")
	private String masterCode;
	
	@DynamoDBAttribute(attributeName = "description")
	private String description;
	
}
