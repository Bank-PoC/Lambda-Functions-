package com.spring.aws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseId {
	private String idChild;
	private String idParent;
	private String message;
	private int httpStatus;
}
