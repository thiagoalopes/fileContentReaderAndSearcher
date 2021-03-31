package br.gov.rr.segad.enquadramento.repository.exception;

import java.io.Serializable;

public class NotFoundReadableFile extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public NotFoundReadableFile(String message) {
		super(message);
	}
	
	public NotFoundReadableFile(String message, Throwable cause) {
		super(message, cause);
	}
}
