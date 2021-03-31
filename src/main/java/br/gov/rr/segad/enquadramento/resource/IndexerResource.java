package br.gov.rr.segad.enquadramento.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.rr.segad.enquadramento.dto.NotReadableFilesDTO;
import br.gov.rr.segad.enquadramento.repository.IndexerRepository;

@RestController
@RequestMapping("api/v1/indexer")
public class IndexerResource {
	
	@Autowired
	private IndexerRepository idx;

	@GetMapping
	public ResponseEntity<Void> reindex(){
		this.idx.reindexFiles();
		return ResponseEntity.ok(null);
	}

	@GetMapping("fails")
	public ResponseEntity<List<NotReadableFilesDTO>> notReadableFiles(){
		return ResponseEntity.ok(this.idx.getNotReadableFiles());
	}
	
	@PutMapping("fails/{fileName}")
	public ResponseEntity<Void> renameNotReadableFile(@RequestBody String newFileName, @PathVariable String filename){
		this.idx.renameNotReadableFileByName(filename, newFileName);
		this.idx.reindexFiles();
		return ResponseEntity.ok(null);
	}
	
	@DeleteMapping("fails/{fileName}")
	public ResponseEntity<Void> removeNotReadableFile(@PathVariable String fileName){
		this.idx.removeNotReadableFileByName(fileName);
		this.idx.reindexFiles();
		return ResponseEntity.ok(null);
	}
}
