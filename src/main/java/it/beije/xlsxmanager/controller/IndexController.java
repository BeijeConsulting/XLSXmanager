/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.controller;

import com.google.gson.Gson;
import it.beije.xlsxmanager.model.Gruppo;
import it.beije.xlsxmanager.model.Utenti;
import it.beije.xlsxmanager.util.XLSXManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Controller
@Slf4j
public class IndexController {


    @GetMapping("/")
    public String index(Model model){
        log.debug("GET Index Page");

        List<Utenti> l= new ArrayList<>();
       int numElementValue= new Random().nextInt(50);

       log.debug("Numero di elementi: "+numElementValue);

        for (int i = 0; i< numElementValue; i++) {
            Utenti utenti= new Utenti();
            utenti.setNome("Nome "+i);
            utenti.setCognome("Cognome "+i);
            utenti.setEta("Eta "+i);
            utenti.setTelefono("Telefono "+i);
            utenti.setCitta("Citta "+i);
            l.add(utenti);
        }

        model.addAttribute("utenti",l);


        return "index";
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
