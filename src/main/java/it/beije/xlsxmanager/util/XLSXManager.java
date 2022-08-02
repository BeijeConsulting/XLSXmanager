/**
 * @author Giuseppe Raddato
 * Data: 02 ago 2022
 */
package it.beije.xlsxmanager.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;


@Slf4j
public class XLSXManager {

	public static void reader(List<String> s) {

		log.debug("ROW" + s.toString());

	}

	public static void main(String[] args) {
		FileInputStream fis;
		try {
//			fis = new FileInputStream(new File("/Users/matteoprovezza/Desktop/xlsxManager.xlsx"));

			fis = new FileInputStream(ResourceUtils.getFile("classpath:static/Esempio_del_file_excel_esportato_da_cassa_19_Luglio_2022.xlsx"));

			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);

			for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
				XSSFRow row = sheet.getRow(i);

				for (int j = 0; row != null && j < row.getLastCellNum(); j++) {

					if (row.getCell(j) != null) {
						String name = row.getCell(j).getCellType().name();
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

}
