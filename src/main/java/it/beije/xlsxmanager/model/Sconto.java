package it.beije.xlsxmanager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Sconto {
    private String descrizione;
    private short quantita;
    private double importo;
}
