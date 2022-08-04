/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.util;

import it.beije.xlsxmanager.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;

import javax.swing.text.Style;


@Slf4j
public class XLSXManager {

	private HashMap<String, List<XSSFRow>>sezioni= new HashMap<>();
	private XSSFWorkbook workbook;
	private  List<Gruppo>gruppiarticoli;

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

	private InfoGeneriche getInfoGeneriche() {
		List<XSSFRow> sezione = sezioni.get("");
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

		String key="";
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

	private List<TransazioniSospese> getTransazioniSospese(){
		List<XSSFRow> sezione = sezioni.get("Transazioni sospese");
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

	private List<TransazioniSospese> getTransazioniEliminate(){
		List<XSSFRow> sezione = sezioni.get("Transazioni eliminate");
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

	private Transazioni getTransazioni(XLSXManager x){
		List<XSSFRow> sezione = sezioni.get("Transazioni eliminate e sospese");
		Transazioni transazioni=new Transazioni();

		transazioni.setTransazioniEliminate(Double.parseDouble(sezione.get(0).getCell(2).toString()));
		transazioni.setTransazioniSospese(Double.parseDouble(sezione.get(1).getCell(2).toString()));

		return transazioni;
	}


	private List<TipoDiServizio> getTipiDiServizio(){
		List<XSSFRow> sezione = sezioni.get("Tipi di servizio");
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

	private List<Pagamento> getPagamenti(){
		List<XSSFRow> sezione = sezioni.get("Pagamenti");
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

	private List<Sconto> getSconti(){
		List<XSSFRow> sezione = sezioni.get("Sconti");
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




	public static void main(String[] args) {

		try {
			XLSXManager x= new XLSXManager(ResourceUtils.getFile("classpath:static/Esempio_del_file_excel_esportato_da_cassa_19_Luglio_2022.xlsx"));
			System.out.println(x.getInfoGeneriche());
			System.out.println(x.getTransazioniSospese());
			System.out.println(x.getTipiDiServizio());
			System.out.println(x.getPagamenti());
			System.out.println(x.getSconti());
			System.out.println(x.getTransazioni(x));




			//	HashMap<String, List<XSSFRow>> sezioni = x.reader();


			//System.out.println("================KEY:"+sezioni.keySet().toString());

			/*	sezioni.forEach((k,v)->{

				String rowS="";
				for(XSSFRow r:v){
					for (int j = 0; r != null && j < r.getLastCellNum(); j++) {
						rowS+=r.getCell(j)+" | ";
					}
					rowS+="\n";
				}
				System.out.println(">>>>>>>>>>>>>K:"+k+"   v:"+rowS);
			});*/

	/*		for (Gruppo g:x.getGruppiArticoli()){
				System.out.print("==============");
				System.out.print("Codice: "+g.getCodice()+"\t");
				System.out.print("Descrizione: "+g.getDescrizione()+"\t");
				System.out.print("ImportoTotale: "+g.getImportoTotale()+"\t");
				System.out.println("QuantitaTotale: "+g.getQuantitaTotale()+"");

				for (Articolo articolo:g.getLista()){
					System.out.println(articolo);
				}
				System.out.print("==============");
			}*/


		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}

}
