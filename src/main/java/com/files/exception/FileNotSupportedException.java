package com.files.exception;

public class FileNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public FileNotSupportedException() {
		super();
	}

	public FileNotSupportedException(String message) {
		super(message);
	}
}
