package com.spring.aws.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.spring.aws.exception.IsoMessageNotFound;

@ControllerAdvice
public class ErrorHandler {

	// Controlar errores NOTFOUND 404
	@ExceptionHandler(IsoMessageNotFound.class)
	public ResponseEntity<ErrorResponse> NotFoundIsoError(IsoMessageNotFound e) {
		ErrorResponse respError = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respError);

	}
	
	@ExceptionHandler(IsoMessageConfict.class)
	public ResponseEntity<ErrorResponse> ConflictError(IsoMessageConfict e) {
		ErrorResponse respError = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(respError);

	}
	
	@ExceptionHandler(IsoMessageBadRequest.class)
	public ResponseEntity<ErrorResponse> BadRequestError(IsoMessageBadRequest e) {
		ErrorResponse respError = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respError);

	}
	
	@ExceptionHandler(IsoMessageNotContent.class)
	public ResponseEntity<ErrorResponse> NotContentError(IsoMessageNotContent e) {
		ErrorResponse respError = new ErrorResponse(HttpStatus.NO_CONTENT.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respError);

	}

}
