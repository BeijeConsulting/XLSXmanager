/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.controller;

import com.google.gson.Gson;
import it.beije.xlsxmanager.model.Gruppo;
import it.beije.xlsxmanager.service.storage.StorageService;
import it.beije.xlsxmanager.util.XLSXManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
@Slf4j
public class RestController {




    @GetMapping("/gruppiarticoli")
    public  HashMap<String,Object > index() throws IOException {
        log.debug("GET generateJsonFile");

        XLSXManager x= new XLSXManager(ResourceUtils.getFile("classpath:static/test.xlsx"));


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
        return l;
    }




}
