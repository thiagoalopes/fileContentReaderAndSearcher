package br.gov.rr.segad.enquadramento.resource.handler;

import java.io.Serializable;

public class ErrorModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}