/**
 * @author Giuseppe Raddato
 * Data: 03 ago 2022
 */
package it.beije.xlsxmanager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Articolo {
    private String codice;
    private String descrizione;
    private short quantita;
    private double importo;

}
