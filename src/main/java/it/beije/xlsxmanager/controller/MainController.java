/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.controller;

import it.beije.xlsxmanager.service.ServiceProva;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Deprecated
public class MainController {

    @Autowired
    private ServiceProva serviceProva;

    @GetMapping("/h")
    public String helloWord(){
        log.debug("Prova Debug");
        return "Hello Word: "+serviceProva.salutami();
    }

}
