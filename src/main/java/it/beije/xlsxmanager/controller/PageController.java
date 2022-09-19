package it.beije.xlsxmanager.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import it.beije.xlsxmanager.exception.StorageException;
import it.beije.xlsxmanager.exception.StorageFileAlderyException;
import it.beije.xlsxmanager.exception.StorageFileNotFoundException;
import it.beije.xlsxmanager.service.storage.StorageService;
import it.beije.xlsxmanager.util.JsonToExcelConverter;
import it.beije.xlsxmanager.util.XLSXManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class PageController {
	private final StorageService storageService;

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public PageController(StorageService storageService) {
		this.storageService = storageService;
	}


	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {
		log.debug("GET index");

		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(PageController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));
		return "index";
	}

	@PostMapping("/convert")
	public String convertJsonToXLSX(@RequestParam("file1") MultipartFile jsonfile, RedirectAttributes redirectAttributes) throws IOException {

		log.debug("Post convert");
		try{

			JsonToExcelConverter jsonToExcelConverter = new JsonToExcelConverter();

			Path pathUpload= storageService.store(jsonfile);
			log.debug(pathUpload.toString());
			Resource fileLoad = storageService.loadAsResource(pathUpload.getFileName().toString());

			File converted = jsonToExcelConverter.jsonFileToExcelFile(fileLoad.getFile(),".xlsx");


			log.debug("Convert done");
		}catch (StorageException exception){
			redirectAttributes.addFlashAttribute("failed", "Failed Upload for " + jsonfile.getName() + "! "+exception.getMessage());

		}catch (StorageFileAlderyException exception){
			redirectAttributes.addFlashAttribute("warning", "Failed Upload for " + jsonfile.getName() + "! "+exception.getMessage());

		} finally {
			return "redirect:/";
		}
	}



	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile fileExcell, RedirectAttributes redirectAttributes) {

		log.debug("Post upload");

		try {
			Path pathUpload= storageService.store(fileExcell);
			log.debug(pathUpload.toString());
			Resource fileLoad = storageService.loadAsResource(pathUpload.getFileName().toString());

			XLSXManager reader= new XLSXManager(fileLoad.getFile());

			log.debug("finito di leggere il file");
			String nameWithoutExstension=pathUpload.getFileName().toString().substring(0,pathUpload.getFileName().toString().indexOf("."));
			log.debug("inizio creazione json ");
			storageService.store(reader.getMultipartFile(nameWithoutExstension));
			log.debug("fine creazione json ");
		}catch (StorageException exception){
			redirectAttributes.addFlashAttribute("failed", "Failed Upload for " + fileExcell.getOriginalFilename() + "! "+exception.getMessage());

		}catch (StorageFileAlderyException exception){
			redirectAttributes.addFlashAttribute("warning", "Failed Upload for " + fileExcell.getOriginalFilename() + "! "+exception.getMessage());

		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("failed", "Error System: "+e.getMessage());
			throw new RuntimeException(e);
		}finally {
			return "redirect:/";
		}

	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		log.debug("get file");

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
