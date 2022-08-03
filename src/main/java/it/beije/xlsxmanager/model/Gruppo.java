/**
 * @author Giuseppe Raddato
 * Data: 03 ago 2022
 */
package it.beije.xlsxmanager.model;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Gruppo {

    private String codice;
    private String descrizione;

    private List<Articolo> listaArticoli=new ArrayList<>();


    private short quantitaTotale;
    private double importoTotale;

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void addArticolo(Articolo articolo){
        listaArticoli.add(articolo);
        quantitaTotale+=articolo.getQuantita();
        importoTotale+=articolo.getImporto();


    }

    public List<Articolo> getListaArticoli() {
        return listaArticoli;
    }


    public short getQuantitaTotale() {
        return quantitaTotale;
    }

    public double getImportoTotale() {
       return importoTotale;
    }
}
