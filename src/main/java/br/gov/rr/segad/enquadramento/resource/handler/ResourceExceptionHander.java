package br.gov.rr.segad.enquadramento.resource.handler;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.gov.rr.segad.enquadramento.repository.exception.IndexerNotLoadedForTheFirstTime;
import br.gov.rr.segad.enquadramento.repository.exception.NotFoundReadableFile;

@ControllerAdvice
public class ResourceExceptionHander {

	@ExceptionHandler(NotFoundReadableFile.class)
	public ResponseEntity<ErrorModel> notFoundReadableFileExceptionHandler(NotFoundReadableFile exception, HttpServletResponse response) {
		ErrorModel error = new ErrorModel();
		error.setMessage(exception.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(IndexerNotLoadedForTheFirstTime.class)
	public ResponseEntity<ErrorModel> IndexerNotLoadedForTheFirstTimeExceptionHandler(IndexerNotLoadedForTheFirstTime exception, HttpServletResponse response){
		ErrorModel error = new ErrorModel();
		error.setMessage(exception.getMessage());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
	}
}
