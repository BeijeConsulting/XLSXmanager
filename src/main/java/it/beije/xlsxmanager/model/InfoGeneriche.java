/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InfoGeneriche {

    private String datacontabile;
    private String stampatoil;
    private String numerostampe;
    private String negozio;
    private String cassa;
    private String turnodilavoro;
    private String operatore;
}
