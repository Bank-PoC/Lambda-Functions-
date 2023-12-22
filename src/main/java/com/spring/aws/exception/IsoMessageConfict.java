package com.spring.aws.exception;

public class IsoMessageConfict extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IsoMessageConfict(String message) {
		super(message);
	}

}
