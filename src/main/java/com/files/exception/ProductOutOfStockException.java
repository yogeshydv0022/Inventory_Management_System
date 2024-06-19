package com.files.exception;
public class ProductOutOfStockException extends RuntimeException {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProductOutOfStockException() {
		super();
	}

	public ProductOutOfStockException(String message) {
		super(message);
	}
	}