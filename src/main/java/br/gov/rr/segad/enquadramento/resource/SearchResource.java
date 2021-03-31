package br.gov.rr.segad.enquadramento.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.rr.segad.enquadramento.dto.FileNameAndContentDTO;
import br.gov.rr.segad.enquadramento.repository.IndexerRepository;

@RestController
@RequestMapping("api/v1/search")
public class SearchResource {
	
	@Autowired
	private IndexerRepository idx;
	
	@GetMapping
	public ResponseEntity<List<FileNameAndContentDTO>> consulta(@RequestParam String word, @RequestParam Boolean resumed) {
		if(word == null || word.equals("") || word.trim().length() == 0) {
			return ResponseEntity.ok(new ArrayList<>());
		}
		return ResponseEntity.ok(this.idx.searchFileContentFromWord(word, resumed));
	}
	
	@GetMapping("download/{fileName}")
	public void downloadFile(@PathVariable String fileName, HttpServletResponse response) {
		 try {
			IOUtils.copy(this.idx.getFile(fileName), response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
