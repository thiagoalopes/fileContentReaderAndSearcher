package br.gov.rr.segad.enquadramento.repository.exception;

import java.io.Serializable;

public class IndexerNotLoadedForTheFirstTime extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;

	public IndexerNotLoadedForTheFirstTime(String message) {
		super(message);
	}
	
	public IndexerNotLoadedForTheFirstTime(String message, Throwable cause) {
		super(message, cause);
	}
	
}
