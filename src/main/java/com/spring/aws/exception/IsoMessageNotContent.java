package com.spring.aws.exception;

public class IsoMessageNotContent extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public IsoMessageNotContent(String message) {
		super(message);
	}
}