package br.gov.rr.segad.enquadramento.dto;

public class NotReadableFilesDTO {
	
	private String fileName;
	
	public NotReadableFilesDTO() {
	}

	public NotReadableFilesDTO(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
