/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.controller;

import com.google.gson.Gson;
import it.beije.xlsxmanager.model.Gruppo;
import it.beije.xlsxmanager.service.ServiceProva;
import it.beije.xlsxmanager.util.XLSXManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@Deprecated
public class MainController {

    @Autowired
    private ServiceProva serviceProva;

    @Value("${xlsx.rows}")
    private List<String> rows;


    @GetMapping("/gruppiarticoli")
    public  HashMap<String,Object > index() throws IOException {
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
        return l;
    }



}
