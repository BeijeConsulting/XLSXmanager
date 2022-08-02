/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.controller;

import it.beije.xlsxmanager.model.Utenti;
import it.beije.xlsxmanager.service.ServiceProva;
import it.beije.xlsxmanager.util.XLSXManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.classfile.EnumElementValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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



}
