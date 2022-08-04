package it.beije.xlsxmanager.controller;


import it.beije.xlsxmanager.exception.StorageException;
import it.beije.xlsxmanager.exception.StorageFileNotFoundException;
import it.beije.xlsxmanager.service.storage.StorageService;
import it.beije.xlsxmanager.util.XLSXManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class PageController {
	private final StorageService storageService;

	@Autowired
	public PageController(StorageService storageService) {
		this.storageService = storageService;
	}


	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(PageController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));

		return "index";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile fileExcell, RedirectAttributes redirectAttributes) {

		try {
			Path pathUpload= storageService.store(fileExcell);
			Resource fileLoad = storageService.loadAsResource(pathUpload.getFileName().toString());

			XLSXManager reader= new XLSXManager(fileLoad.getFile());
			byte[] fileJson = reader.getStreamJSON();

			storageService.store(new MultipartFile() {
				@Override
				public String getName() {
					return pathUpload.getFileName()+"json";
				}

				@Override
				public String getOriginalFilename() {
					return pathUpload.getFileName().toString().substring(0,pathUpload.getFileName().toString().indexOf("."))+".json";
				}

				@Override
				public String getContentType() {
					return "json";
				}

				@Override
				public boolean isEmpty() {
					return fileJson.length==0;
				}

				@Override
				public long getSize() {
					return fileJson.length;
				}

				@Override
				public byte[] getBytes() throws IOException {
					return fileJson;
				}

				@Override
				public InputStream getInputStream() throws IOException {
					return new ByteArrayInputStream(fileJson);
				}

				@Override
				public void transferTo(File dest) throws IOException, IllegalStateException {
					log.debug("trasfert"+dest);
				}
			});


		}catch (StorageException exception){
			redirectAttributes.addFlashAttribute("failed", "Failed Upload for " + fileExcell.getOriginalFilename() + "! "+exception.getMessage());
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("failed", "Error System: "+e.getMessage());
			throw new RuntimeException(e);
		}

		return "redirect:/";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}



	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
/*	@GetMapping("/jsonfile")
	public ResponseEntity<InputStreamResource> index() throws IOException {
		log.debug("GET generateJsonFile");






		return ResponseEntity
				.ok()
				.contentLength(  forFile.getBytes().length)
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.header("Content-Disposition", "attachment; filename=\"listaGruppi.json\"")
				.body(new InputStreamResource(new ByteArrayInputStream(  forFile.getBytes())));


	}*/
}
