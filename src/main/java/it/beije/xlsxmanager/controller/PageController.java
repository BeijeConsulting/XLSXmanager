package it.beije.xlsxmanager.controller;


import com.google.gson.Gson;
import it.beije.xlsxmanager.exception.StorageFileNotFoundException;
import it.beije.xlsxmanager.model.Gruppo;
import it.beije.xlsxmanager.service.storage.StorageService;
import it.beije.xlsxmanager.util.XLSXManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		storageService.store(file);
		redirectAttributes.addFlashAttribute("success", "You successfully uploaded " + file.getOriginalFilename() + "!");

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
	@GetMapping("/jsonfile")
	public ResponseEntity<InputStreamResource> index() throws IOException {
		log.debug("GET generateJsonFile");

		XLSXManager x= new XLSXManager(ResourceUtils.getFile("classpath:static/Esempio_del_file_excel_esportato_da_cassa_19_Luglio_2022.xlsx"));



		Gson gson= new Gson();
		HashMap<String,Object > l = new HashMap<>();
		List<Gruppo> r = x.getGruppiConArticoli();
		l.put("listaGruppi",r);

		Double totaleImporto=0.0;
		Integer totaleQuantita=0;
		for (Gruppo temp :r) {
			totaleImporto+=temp.getImportoTotale();
			totaleQuantita+=temp.getQuantitaTotale();
		}
		l.put("articoli",totaleImporto);
		l.put("totaleImportoGruppi",totaleImporto);
		l.put("totaleQuantitaGruppi",totaleQuantita);

		String forFile=  gson.toJson(l);



		return ResponseEntity
				.ok()
				.contentLength(  forFile.getBytes().length)
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.header("Content-Disposition", "attachment; filename=\"listaGruppi.json\"")
				.body(new InputStreamResource(new ByteArrayInputStream(  forFile.getBytes())));


	}
}
