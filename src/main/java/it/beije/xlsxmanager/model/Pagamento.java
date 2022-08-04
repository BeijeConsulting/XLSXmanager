package it.beije.xlsxmanager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Pagamento {
    private String descrizione;
    private String quantita;
    private String importo;
}
