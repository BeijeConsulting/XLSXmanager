/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.util;

import com.google.gson.Gson;
import it.beije.xlsxmanager.model.Articolo;
import it.beije.xlsxmanager.model.Gruppo;
import it.beije.xlsxmanager.model.InfoGeneriche;
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

	public static void main(String[] args) {

		try {
			XLSXManager x= new XLSXManager(ResourceUtils.getFile("classpath:static/Esempio_del_file_excel_esportato_da_cassa_19_Luglio_2022.xlsx"));

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
