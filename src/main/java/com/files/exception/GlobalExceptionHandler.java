package com.files.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public String ResourcerNotFoundException(ResourceNotFoundException ex) {
		return ex.getMessage();
	}

	@ExceptionHandler(FileNotSupportedException.class)
	public String FileNotSupportedException(FileNotSupportedException ex) {
		return ex.getMessage();
	}

	 @ExceptionHandler(UserNotFoundException.class)
	    public ResponseEntity<String> UserNotFoundException(UserNotFoundException ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	    }

	    @ExceptionHandler(ProductNotFoundException.class)
	    public ResponseEntity<String> ProductNotFoundException(ProductNotFoundException ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	    }

	    @ExceptionHandler(ProductOutOfStockException.class)
	    public ResponseEntity<String> ProductOutOfStockException(ProductOutOfStockException ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(CartEmptyException.class)
	    public ResponseEntity<String> CartEmptyException(CartEmptyException ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<String> GenericException(Exception ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
	    @ExceptionHandler(BillNotFoundException.class)
	    public ResponseEntity<String> BillNotFoundException(BillNotFoundException ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	    
	    
}
