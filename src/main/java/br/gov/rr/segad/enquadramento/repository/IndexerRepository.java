package br.gov.rr.segad.enquadramento.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.gov.rr.segad.enquadramento.dto.FileNameAndContentDTO;
import br.gov.rr.segad.enquadramento.dto.NotReadableFilesDTO;
import br.gov.rr.segad.enquadramento.repository.constant.FileExtensionsConstants;
import br.gov.rr.segad.enquadramento.repository.exception.IndexerNotLoadedForTheFirstTime;
import br.gov.rr.segad.enquadramento.repository.exception.NotFoundReadableFile;

@Component
public final class IndexerRepository {

	@Value("${highlight.html.format}")
	private String highlightHtmlFormat;
	private final String REPOSITORY_PATH = "/home/thiagolopes/repositorio";
	
	@Value("${reindex.time}")
	private int reidexTimeIntervalInSeconds;
	
	//DataBase
	private Map<String, String> fileData;
	private List<NotReadableFilesDTO> notReadableFile;
	
	public IndexerRepository() {
		this.fileTextIndexer();
	}
	
	public void reindexFiles() {
		this.fileTextIndexer();
	}

	private void fileTextIndexer() {
		
		new Thread() {
			public void run() {
				
				while (true) {
					
				Map<String, String> fileDataAux = new HashMap<>();
				List<NotReadableFilesDTO> notReadableFileAux = new ArrayList<>();
				
				File repository = new File(REPOSITORY_PATH);
				File[] listOfFiles = repository.listFiles();
				
				for (File file : listOfFiles) {
					
					if (file.isFile()) {
						try {
							
							switch (FilenameUtils.getExtension(file.getName())) {
								case FileExtensionsConstants.PDF:
									
									PDDocument doc = PDDocument.load(file);
									PDFTextStripper striper = new PDFTextStripper();
									fileDataAux.put(file.getName(), striper.getText(doc).toLowerCase());
									doc.close();
									
									break;
									
								case FileExtensionsConstants.TXT:
									fileDataAux.put(file.getName(), new String(FileUtils.readFileToByteArray(file)));
									
									break;
			
								default:
									notReadableFileAux.add(new NotReadableFilesDTO(file.getName()));
									System.out.println("Arquivo não identificado ou não lido: " + file.getName());
									break;
							}
							
							
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println("Error: Not is possible read the repository files");
						}
						
					  } else if (file.isDirectory()) {
					    System.out.println("This is a directory " + file.getName());
					  }
					
					
				}
				
				//Copy tempData from fileDataAux to prevines exception exception on index or reindex
				Map<String, String> tempData = new HashMap<String, String>();
				for (Entry<String, String> data : fileDataAux.entrySet()) {
					tempData.put(data.getKey(), data.getValue());
				}
				
				//Copy tempNotReadable from notReadableFileAux to prevines exception on index or reindex
				List<NotReadableFilesDTO> tempNotReadable = new ArrayList<NotReadableFilesDTO>();
				for (NotReadableFilesDTO data : notReadableFileAux) {
					tempNotReadable.add(data);
				}
				
				//Finaly sets object property
				fileData = tempData;
				notReadableFile = tempNotReadable;
				
					try {
						Thread.sleep(reidexTimeIntervalInSeconds * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			
		}.start();
	}
	
	private String normalizeString(String src) {
		return Normalizer.normalize(src, Normalizer.Form.NFD)
		.replaceAll("[^\\p{ASCII}]", "");
	}
	
	public List<FileNameAndContentDTO> searchFileContentFromWord(String word, Boolean isResumedContent) {
		word = word.toLowerCase();
		List<FileNameAndContentDTO> listOfFiles = new ArrayList<FileNameAndContentDTO>();
		
		if(fileData != null) {
			for (Entry<String, String> file : this.fileData.entrySet()) {
				if(this.normalizeString(file.getValue()).contains(this.normalizeString(word))) {
					if(!isResumedContent) {
						listOfFiles.add(new FileNameAndContentDTO(this.normalizeString(file.getKey()),
								this.highLightHtmlText(this.normalizeString(file.getValue()), this.normalizeString(word))));
					} else {
						listOfFiles.add(new FileNameAndContentDTO(this.normalizeString(file.getKey()),
								this.highLightHtmlText(this.resumeContentFromWord(this.normalizeString(file.getValue()),this.normalizeString(word)), this.normalizeString(word))));
					}
				}
			}
			return listOfFiles;
		}
		
		throw new IndexerNotLoadedForTheFirstTime("Oops! application has not yet fully loaded!");
	}
	
	public void removeNotReadableFileByName(String fileName) {
	    if(this.notReadableFile != null && this.notReadableFile.size() > 0) {
			for (NotReadableFilesDTO notReadableFilesDTO : this.notReadableFile) {
				if(fileName != null && fileName.equals(notReadableFilesDTO.getFileName())) {
					File file = new File(REPOSITORY_PATH+"/"+notReadableFilesDTO.getFileName());
					file.delete();
					break;
				} else {
					throw new NotFoundReadableFile("The file not exist");
				}
			} 
		} else {
			throw new NotFoundReadableFile("The file not exist");
		}
	}
	
	private String resumeContentFromWord(String text, String word) {
		
		if((text.indexOf(word) + 200) <= text.length()) {
			return ".." + text.substring(text.indexOf(word), text.indexOf(word) + 200) + "...";
		}

		return ".." + text.substring(0, 200) + "...";
		
		/*if((text.indexOf(word) - 100) > 0) {
			if((text.indexOf(word) + word.length()) > (text.lastIndexOf(text) - 100)) {
				return text.substring((text.indexOf(word) - 100), text.indexOf(word) + 100);
			}
			return text.substring((0), (text.indexOf(word) + word.length()));
		} else {
			return text;
		}*/
	}

	public List<NotReadableFilesDTO> getNotReadableFiles(){
		return this.notReadableFile;
	}
	
	public InputStream getFile(String fileName) {
	    try {
			InputStream targetStream = new FileInputStream(new File(REPOSITORY_PATH+"/"+fileName));
			return targetStream;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String highLightHtmlText(String text, String word ) {
			String highLightedWord = this.highlightHtmlFormat.replace("value:", word); 
			text = text.replaceAll(word, highLightedWord);
		
		return text;
	}

	public void renameNotReadableFileByName(String fileName, String newFileName) {
	    
	    if(this.notReadableFile != null && this.notReadableFile.size() > 0) {
			for (NotReadableFilesDTO notReadableFilesDTO : this.notReadableFile) {
				if(fileName != null && fileName.equals(notReadableFilesDTO.getFileName())) {
					Path source = Paths.get(REPOSITORY_PATH+"/"+notReadableFilesDTO.getFileName());
				    try {
						Files.move(source, source.resolveSibling(newFileName));
					} catch (IOException e) {
						e.printStackTrace();
						throw new NotFoundReadableFile("The file not exist");
					}
					break;
				} else {
					throw new NotFoundReadableFile("The file not exist");
				}
			} 
		} else {
			throw new NotFoundReadableFile("The file not exist");
		}
	}
}
