/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.util;

import it.beije.xlsxmanager.model.InfoGeneriche;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;


@Slf4j
public class XLSXManager {

	public void reader(List<String> s) {


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
			}*/

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public static void main(String[] args) {

		//Categoria principale= 12 bold
		//Categoria secondaria= 10 bold
		//Stringa->attributi  valori->numerici

			XLSXManager x= new XLSXManager();
			x.reader(null);






	}

}
