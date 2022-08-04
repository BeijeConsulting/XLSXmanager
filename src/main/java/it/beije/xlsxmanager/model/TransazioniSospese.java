package it.beije.xlsxmanager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransazioniSospese {
    private String sala;
    private String tavolo;
    private String conto;
    private String ospiti;
    private String subTotale;

}
