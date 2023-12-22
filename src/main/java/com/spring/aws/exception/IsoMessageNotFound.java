package com.spring.aws.exception;

public class IsoMessageNotFound extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public IsoMessageNotFound(String message) {
		super(message);
	}

}
