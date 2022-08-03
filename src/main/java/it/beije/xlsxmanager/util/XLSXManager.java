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

	public 	XLSXManager(File f) throws IOException {
		FileInputStream fis= new FileInputStream(f);
		 workbook = new XSSFWorkbook(fis);
		 fis.close();
	}
	/*public void reader(List<String> s) {


		FileInputStream fis;
		try {
			fis = new FileInputStream(ResourceUtils.getFile("classpath:static/Esempio_del_file_excel_esportato_da_cassa_19_Luglio_2022.xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);

			try {
				System.out.println(setInfoGeneriche(sheet));
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}



	/*		for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
				XSSFRow row = sheet.getRow(i);

				for (int j = 0; row != null && j < row.getLastCellNum(); j++) {

					if (row.getCell(j) != null) {
						String name = row.getCell(j).getCellType().name();

						System.out.println("FONT: "+row.getCell(j).getCellStyle().getFont().getFontHeightInPoints());

						switch (name) {
							case "NUMERIC": {
								System.out.print(row.getCell(j).getNumericCellValue() + "\t\t"); // System.out.print((int)row.getCell(j).getNumericCellValue()+"\t\t");
								break;
							}

							case "STRING": {
								System.out.print(row.getCell(j).getStringCellValue() + "\t\t");
								break;
							}
						}
					}
				}
				System.out.println("");
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
*/

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

		public HashMap<String, List<XSSFRow>>  reader() throws IOException {


			List<XSSFRow> righeDiSezioni= new ArrayList<>();



			XSSFSheet sheet = workbook.getSheetAt(0);

			String key="";
			for (int r = 1; r < sheet.getLastRowNum() + 1; r++) {
				XSSFRow row = sheet.getRow(r);

				if(row!=null ) {

					/*boolean startSkip=false;
					if(row.getCell(row.getFirstCellNum()).toString().equals("Dettagli")){
						startSkip=true;
					}
					if(startSkip){
						if(!row.getCell(row.getFirstCellNum()).toString().equals("Pagamenti")){
							break;
						}else{
							startSkip=false;
						}
					}else*/

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



						//row.getCell(c)!=null && row.getCell(c).getCellStyle().getFont().getBold()
			return sezioni;
		}

		public 	List<Gruppo> getGruppiArticoli(){
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

		//Categoria principale= 12 bold
		//Categoria secondaria= 10 bold
		//Stringa->attributi  valori->numerici
		try {
			XLSXManager x= new XLSXManager(ResourceUtils.getFile("classpath:static/Esempio_del_file_excel_esportato_da_cassa_19_Luglio_2022.xlsx"));

			HashMap<String, List<XSSFRow>> sezioni = x.reader();
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


			Gson gson= new Gson();
			HashMap<String,Object > l = new HashMap<>();
			List<Gruppo> r = x.getGruppiArticoli();
			l.put("GruppiArticoli",r);

			Double totaleImporto=0.0;
			Integer totaleQuantita=0;
			for (Gruppo temp :r) {
				totaleImporto+=temp.getImportoTotale();
				totaleQuantita+=temp.getQuantitaTotale();
			}

			l.put("totaleImportoGruppi",totaleImporto);
			l.put("totaleQuantitaGruppi",totaleQuantita);

			String json = gson.toJson(l);


			System.out.println(json);
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
