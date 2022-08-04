/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.beije.xlsxmanager.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.*;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;


@Slf4j
public class XLSXManager {

	private HashMap<String, List<XSSFRow>>sezioni= new HashMap<>();
	private XSSFWorkbook workbook;
	private  List<Gruppo>gruppiarticoli;


	private static final String KEY_INFO_GENERALI="Info Generali";
	private static final String KEY_GRUPPI_E_ARTICOLI="Gruppi e articoli";
	private static final String KEY_TRANSAZIONI_SOSPESE="Transazioni sospese";
	private static final String KEY_TRANSAZIONI_ELIMINATE="Transazioni eliminate";
	private static final String KEY_TRANSAZIONI="Transazioni eliminate e sospese";
	private static final String KEY_TIPI_SERVIZIO="Tipi di servizio";
	private static final String KEY_PAGAMENTI="Pagamenti";
	private static final String KEY_SCONTI="Sconti";


	public 	XLSXManager(File f) throws IOException {
		FileInputStream fis= new FileInputStream(f);
		 workbook = new XSSFWorkbook(fis);
		 fis.close();
		 reader();
		 gruppiarticoli = getGruppiArticoli();


		System.out.println(sezioni.keySet());
	}

	private InfoGeneriche setInfoGeneriche(XSSFSheet sheet) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		InfoGeneriche infoGeneriche=new InfoGeneriche();

		List<String> metods=new ArrayList<>();
		for (int i = 0; i < infoGeneriche.getClass().getMethods().length; i++) {
			metods.add(infoGeneriche.getClass().getMethods()[i].getName().toLowerCase());
		}

		System.out.println(metods);
		for (int i=2; i<9;i++) {
			XSSFRow row = sheet.getRow(i);
			int j = 0;
			String nameToMethod=row.getCell(j).toString().replaceAll(" ","").toLowerCase();

			if(metods.contains("set"+nameToMethod)) {
					String nameM = nameToMethod.substring(0, 1).toUpperCase() + nameToMethod.substring(1);
					Method m = infoGeneriche.getClass().getMethod("set" + nameM, String.class);
					m.invoke(infoGeneriche, row.getCell(2).toString());
			}



			/*
				int j=2;
				if (row.getCell(0) != null) {
					switch (row.getCell(0).toString()){
						case "Data contabile":{
							infoGeneriche.setDataContabile(row.getCell(j).toString());
							break;
						}
						case "Stampato il":{
							infoGeneriche.setDataStampa(row.getCell(j).toString());
							break;
						}
						case "Numero stampe":{
							infoGeneriche.setNumeroCopie(row.getCell(j).toString());
							break;
						}
						case "Negozio":{
							infoGeneriche.setNomeNegozio(row.getCell(j).getStringCellValue());
							break;
						}
						case "Cassa":{
							infoGeneriche.setCassa(row.getCell(j).toString());
							break;
						}
						case "Turno di lavoro":{
							infoGeneriche.setTurnoDiLavoro(row.getCell(j).toString());
							break;
						}
				}
			}*/

		}
		return infoGeneriche;
	}

	public InfoGeneriche getInfoGeneriche() {
		List<XSSFRow> sezione = sezioni.get(KEY_INFO_GENERALI);
		InfoGeneriche infoGeneriche = new InfoGeneriche();

		for (int i=0; i<sezione.size();i++){
			if (i==0){
				infoGeneriche.setDatacontabile(sezione.get(i).getCell(2).toString());
			} else if (i==1) {
				infoGeneriche.setStampatoil(sezione.get(i).getCell(2).toString());
			} else if (i==2) {
				infoGeneriche.setNumerostampe(sezione.get(i).getCell(2).toString());
			} else if (i==3) {
				infoGeneriche.setNegozio(sezione.get(i).getCell(2).toString());
			} else if (i==4) {
				infoGeneriche.setCassa(sezione.get(i).getCell(2).toString());
			} else if (i==5) {
				infoGeneriche.setTurnodilavoro(sezione.get(i).getCell(2).toString());
			} else if (i==6) {
				infoGeneriche.setOperatore(sezione.get(i).getCell(2).toString());
			}
		}

		return infoGeneriche;
	}


	private HashMap<String, List<XSSFRow>>  reader() throws IOException {

		List<XSSFRow> righeDiSezioni= new ArrayList<>();

		XSSFSheet sheet = workbook.getSheetAt(0);

		String key=KEY_INFO_GENERALI;
		for (int r = 1; r < sheet.getLastRowNum() + 1; r++) {
			XSSFRow row = sheet.getRow(r);

			if(row!=null ) {
					if( row.getLastCellNum()==1 && row.getCell(row.getFirstCellNum()).getCellStyle().getFont().getFontHeightInPoints()==10 || r>=sheet.getLastRowNum()){
						if(!key.equals(row.getCell(row.getFirstCellNum()).toString())){
							sezioni.put(key,righeDiSezioni);
							righeDiSezioni=new ArrayList<>();
						}

						key=row.getCell(row.getFirstCellNum()).toString();

					}else if(row.getLastCellNum()==1 && row.getCell(row.getFirstCellNum()).getCellStyle().getFont().getFontHeightInPoints()!=10){

					}else {
						righeDiSezioni.add(row);
					}
			}
		}

		return sezioni;
	}

	public   List<Gruppo> getGruppiConArticoli(){
		return this.gruppiarticoli;
	}

	private  	List<Gruppo> getGruppiArticoli(){
		List<XSSFRow> sezione = sezioni.get("Gruppi e articoli");
		List<Gruppo> gruppos=new ArrayList<>();
		sezione.remove(0);

		Gruppo temp=null;

		for (XSSFRow row:sezione) {

				if (row.getCell(0).getCellStyle().getFont().getBold() ){
					temp=new Gruppo();
					temp.setCodice(row.getCell(0).toString());
					temp.setDescrizione(row.getCell(1).toString());
					gruppos.add(temp);
				}else{
					Articolo tempA=new Articolo();
					tempA.setCodice(row.getCell(0).toString());
					tempA.setDescrizione(row.getCell(1).toString());
					tempA.setQuantita(Short.parseShort(row.getCell(2).toString()));
					tempA.setImporto(Double.parseDouble(row.getCell(3).toString()));
					temp.addArticolo(tempA);
				}

		}

		return gruppos;
	}

	public List<TransazioniSospese> getTransazioniSospese(){
		List<XSSFRow> sezione = sezioni.get(KEY_TRANSAZIONI_SOSPESE);
		List<TransazioniSospese> transazioniSospese=new ArrayList<>();
		sezione.remove(0);
		sezione.remove(sezione.size()-1);

		TransazioniSospese temp;

		for (XSSFRow row:sezione) {
			temp = new TransazioniSospese();
			temp.setSala(row.getCell(0).toString());
			temp.setTavolo(row.getCell(1).toString());
			temp.setConto(Short.parseShort(row.getCell(2).toString()));
			temp.setOspiti(Short.parseShort(row.getCell(3).toString()));
			temp.setSubTotale(Double.parseDouble(row.getCell(4).toString()));

			transazioniSospese.add(temp);

		}

		return transazioniSospese;
	}

	public List<TransazioniSospese> getTransazioniEliminate(){
		List<XSSFRow> sezione = sezioni.get(KEY_TRANSAZIONI_ELIMINATE);
		List<TransazioniSospese> transazioniSospese=new ArrayList<>();
		sezione.remove(0);
		sezione.remove(sezione.size()-1);

		TransazioniSospese temp;


		for (XSSFRow row:sezione) {
			temp = new TransazioniSospese();
			temp.setSala(row.getCell(0).toString());
			temp.setTavolo(row.getCell(1).toString());
			temp.setConto(Short.parseShort(row.getCell(2).toString()));
			temp.setOspiti(Short.parseShort(row.getCell(3).toString()));
			temp.setSubTotale(Double.parseDouble(row.getCell(4).toString()));

			transazioniSospese.add(temp);

		}

		return transazioniSospese;
	}

	public Transazioni getTransazioni(){
		List<XSSFRow> sezione = sezioni.get(KEY_TRANSAZIONI);
		Transazioni transazioni=new Transazioni();

		transazioni.setTransazioniEliminate(Double.parseDouble(sezione.get(0).getCell(2).toString()));
		transazioni.setTransazioniSospese(Double.parseDouble(sezione.get(1).getCell(2).toString()));

		return transazioni;
	}


	public List<TipoDiServizio> getTipiDiServizio(){
		List<XSSFRow> sezione = sezioni.get(KEY_TIPI_SERVIZIO);
		List<TipoDiServizio> tipoDiServizioList = new ArrayList<>();
		sezione.remove(0);
		sezione.remove(sezione.size()-1);

		TipoDiServizio temp;

		for (XSSFRow row:sezione) {
			temp = new TipoDiServizio();
			temp.setDescrizione(row.getCell(0).toString());
			temp.setQuantita(Short.parseShort(row.getCell(1).toString()));
			temp.setImporto(Double.parseDouble(row.getCell(2).toString()));

			tipoDiServizioList.add(temp);

		}

		return tipoDiServizioList;
	}

	public List<Pagamento> getPagamenti(){
		List<XSSFRow> sezione = sezioni.get(KEY_PAGAMENTI);
		List<Pagamento> pagamenti = new ArrayList<>();
		sezione.remove(0);
		sezione.remove(sezione.size()-1);

		Pagamento temp;

		for (XSSFRow row:sezione) {
			temp = new Pagamento();
			temp.setDescrizione(row.getCell(0).toString());
			temp.setQuantita(Short.parseShort(row.getCell(1).toString()));
			temp.setImporto(Double.parseDouble(row.getCell(2).toString()));

			pagamenti.add(temp);

		}

		return pagamenti;
	}

	public List<Sconto> getSconti(){
		List<XSSFRow> sezione = sezioni.get(KEY_SCONTI);
		List<Sconto> sconti = new ArrayList<>();
		sezione.remove(0);
		sezione.remove(sezione.size()-1);

		Sconto temp;

		for (XSSFRow row:sezione) {
			temp = new Sconto();
			temp.setDescrizione(row.getCell(0).toString());
			temp.setQuantita(Short.parseShort(row.getCell(1).toString()));
			temp.setImporto(Double.parseDouble(row.getCell(2).toString()));

			sconti.add(temp);

		}

		return sconti;
	}
	public byte[] getStreamJSON() {
		Gson gson= new GsonBuilder().setPrettyPrinting().create();
		HashMap<String,Object > l = new LinkedHashMap<>();

		l.put(KEY_INFO_GENERALI.replaceAll(" ","_").toLowerCase(),getInfoGeneriche());


		//===============================TIPI DI SERVIZIO====================================================
		List<TipoDiServizio> listTipiDiServizio =getTipiDiServizio();
		HashMap<String, Object> hlistTipiDiServizio=new LinkedHashMap<>();
		double totImportoTipo=0.0;

		for (TipoDiServizio t:listTipiDiServizio) {
			totImportoTipo+=t.getImporto();
		}

		hlistTipiDiServizio.put("lista_servizi",listTipiDiServizio);
		hlistTipiDiServizio.put("totale",totImportoTipo);
		l.put(KEY_TIPI_SERVIZIO.replaceAll(" ","_").toLowerCase(), hlistTipiDiServizio);


		//===============================SCONTI====================================================
		List<Sconto> lsconti =getSconti();
		HashMap<String, Object> hsconti=new LinkedHashMap<>();
		Double totSconti=0.0;
		for (Sconto s:lsconti) {
			totImportoTipo+=s.getImporto();
		}
		hsconti.put("lista_sconti",lsconti);
		hsconti.put("totale",totSconti);
		l.put(KEY_SCONTI.replaceAll(" ","_").toLowerCase(),hsconti);

		//===============================PAGAMENTI====================================================
		List<Pagamento> lpagamenti =getPagamenti();
		HashMap<String, Object> hpagamenti=new LinkedHashMap<>();
		Double totPagamenti=0.0;
		for (Pagamento s:lpagamenti) {
			totPagamenti+=s.getImporto();
		}

		hpagamenti.put("lista_pagamenti",lpagamenti);
		hpagamenti.put("totale",totPagamenti);
		l.put(KEY_PAGAMENTI.replaceAll(" ","_").toLowerCase(),hpagamenti);


		//===============================TRANSAZIONI====================================================
		List<TransazioniSospese> ltransazioniSospese =getTransazioniSospese();
		HashMap<String, Object> htransazioniSospese=new LinkedHashMap<>();
		Double tottransazioniSospese=0.0;
		for (TransazioniSospese s:ltransazioniSospese) {
			tottransazioniSospese+=s.getSubTotale();
		}

		htransazioniSospese.put("lista_transazioniSospese",ltransazioniSospese);
		htransazioniSospese.put("totale",tottransazioniSospese);


		l.put(KEY_TRANSAZIONI_SOSPESE.replaceAll(" ","_").toLowerCase(),htransazioniSospese);

		//===============================Gruppi Con Articoli====================================================
		List<Gruppo> r = getGruppiConArticoli();
		HashMap<String, Object> gruppiArticoli=new LinkedHashMap<>();

		gruppiArticoli.put("gruppi_con_articoli",r);
		Double totaleImporto=0.0;
		Integer totaleQuantita=0;
		for (Gruppo temp :r) {
			totaleImporto+=temp.getImportoTotale();
			totaleQuantita+=temp.getQuantitaTotale();
		}
		gruppiArticoli.put("articoli",totaleImporto);
		gruppiArticoli.put("totaleImportoGruppi",totaleImporto);
		gruppiArticoli.put("totaleQuantitaGruppi",totaleQuantita);

		l.put(KEY_GRUPPI_E_ARTICOLI.replaceAll(" ","_").toLowerCase(),gruppiArticoli);

		String forFile=  gson.toJson(l);

		return  forFile.getBytes();
	}



	public static void main(String[] args) {

		try {
			XLSXManager x= new XLSXManager(ResourceUtils.getFile("classpath:static/test.xlsx"));
		//	XLSXManager x= new XLSXManager(ResourceUtils.getFile("classpath:static/fileTestPerException.xlsx"));

			System.out.println(x.getInfoGeneriche());

			System.out.println(x.getTransazioniSospese());
			System.out.println(x.getTipiDiServizio());
			System.out.println(x.getPagamenti());
			System.out.println(x.getSconti());
			System.out.println(x.getTransazioni());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}


}
