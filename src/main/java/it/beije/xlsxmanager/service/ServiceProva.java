/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Deprecated
public class ServiceProva {

    public String salutami(){
        log.debug("Service Salutami");

        return "Ciao Mondo";
    }
}
