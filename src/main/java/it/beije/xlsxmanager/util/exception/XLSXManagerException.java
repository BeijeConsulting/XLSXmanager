/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.util.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XLSXManagerException extends RuntimeException{

    XLSXManagerException(String msg){
        super(msg);
    }
}
