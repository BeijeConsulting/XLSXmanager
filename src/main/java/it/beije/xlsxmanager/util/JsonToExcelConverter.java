package it.beije.xlsxmanager.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import it.beije.xlsxmanager.model.*;
import it.beije.xlsxmanager.service.storage.MutipartFileFromJson;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author javacodepoint.com
 */
@Slf4j
public class JsonToExcelConverter {

    private XSSFWorkbook workbook = new XSSFWorkbook();

    private Sheet sheet = workbook.createSheet("Foglio");
    private ObjectMapper mapper = new ObjectMapper();

    private static final String KEY_INFO_GENERALI = "info_generali";
    private static final String KEY_GRUPPI_E_ARTICOLI = "gruppi_e_articoli";
    private static final String KEY_TRANSAZIONI_SOSPESE = "transazioni_sospese";
    private static final String KEY_TIPI_SERVIZIO = "tipi_di_servizio";
    private static final String KEY_PAGAMENTI = "pagamenti";
    private static final String KEY_SCONTI = "sconti";
    private static final int NUM_OF_COLUMNS = 4;

    public File jsonFileToExcelFile(File srcFile, String targetFileExtension) throws IOException {
        try {

            if (!srcFile.getName().endsWith(".json")) {
                throw new IllegalArgumentException("The source file should be .json file only");
            } else {

                //Reading the json file
                ObjectNode jsonData = (ObjectNode) mapper.readTree(srcFile);

                System.out.println();

                int countRow = 0;


                countRow = getInfoGenerali( jsonData.get(KEY_INFO_GENERALI), KEY_INFO_GENERALI,countRow);

                countRow = getTipiDiServizio( jsonData.get(KEY_TIPI_SERVIZIO), KEY_TIPI_SERVIZIO, countRow);

                countRow = getSconti( jsonData.get(KEY_SCONTI), KEY_SCONTI, countRow);

                countRow = getPagamenti( jsonData.get(KEY_PAGAMENTI), KEY_PAGAMENTI, countRow);

                countRow = getTransazioniSospese( jsonData.get(KEY_TRANSAZIONI_SOSPESE), KEY_TRANSAZIONI_SOSPESE, countRow);

                countRow = getGruppiEArticoli( jsonData.get(KEY_GRUPPI_E_ARTICOLI), KEY_GRUPPI_E_ARTICOLI, countRow);

                for (int i = 0; i<=NUM_OF_COLUMNS;i++)   sheet.autoSizeColumn(i);

            }

            //creating a target file
            String filename = srcFile.getName();
            filename = filename.substring(0, filename.lastIndexOf(".json")) + targetFileExtension;
            File targetFile = new File(srcFile.getParent(), filename);

            // write the workbook into target file
            FileOutputStream fos = new FileOutputStream(targetFile);
            workbook.write(fos);

            //close the workbook and fos
            workbook.close();
            fos.close();
            return targetFile;
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getInfoGenerali( JsonNode sheetData, String header, int countRow) {


        System.out.println("SHEET NAME: " + header);

        System.out.println("SHEET DATA: " + sheetData);


        //Creating cell style for header to make it bold
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Aharoni");
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        headerStyle.setFont(font);


        Row row = sheet.createRow(countRow++);
        Cell cell = row.createCell(0);
        cell.setCellValue(header);

        //apply the bold style to headers
        cell.setCellStyle(headerStyle);

        //creating the header into the sheet
        Iterator<String> it = sheetData.fieldNames();

        ArrayList<String> s = new ArrayList<String>();



        CellStyle subtitlesStyle = workbook.createCellStyle();
        Font font1 = workbook.createFont();
        font1.setBold(true);
        subtitlesStyle.setFont(font1);


        while (it.hasNext()) {
            Row row1 = sheet.createRow(countRow++);


            String headerName = it.next();
            System.out.println("HEADER NAME: " + headerName);
            s.add(headerName);

            Cell cell1 = row1.createCell(0);
            cell1.setCellValue(headerName);
            //apply the bold style to headers
            cell1.setCellStyle(subtitlesStyle);

            Cell cell2 = row1.createCell(1);
            cell2.setCellValue(sheetData.get(headerName).asText());
        }
        return countRow;
    }

    public int getTipiDiServizio( JsonNode sheetData, String header, int countRow) {

        //Iterating over the each sheets


        System.out.println("SHEET NAME: " + header);

        System.out.println("SHEET DATA: " + sheetData);


        //Creating cell style for header to make it bold
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Aharoni");
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        headerStyle.setFont(font);


        Row row = sheet.createRow(countRow++);


        Cell cell = row.createCell(0);
        cell.setCellValue(header);
        //apply the bold style to headers
        cell.setCellStyle(headerStyle);
        //creating the header into the sheet

        Iterator<String> it = sheetData.fieldNames();

        ArrayList<String> s = new ArrayList<String>();
        while (it.hasNext()) {
            Row row1 = sheet.createRow(countRow++);


            String headerName = it.next();
            System.out.println("HEADER NAME: " + headerName);
            s.add(headerName);

            CellStyle subtitlesStyle = workbook.createCellStyle();
            Font font1 = workbook.createFont();
            font1.setBold(true);
            subtitlesStyle.setFont(font1);

            if (headerName.equals("lista_servizi")) {
                //CELLA HEADER LISTA SERVIZI
                Cell cell1 = row1.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);

                JsonNode rowData = sheetData.get(headerName);
                System.out.println("ROWDATA INTERNO AL FOR: " + rowData);

                Row row2 = sheet.createRow(countRow++);


                Iterator<String> its = rowData.get(0).fieldNames();

                int idx = 0;
                ArrayList<String> f = new ArrayList<>();
                while (its.hasNext()) {
                    String campo = its.next();
                    System.out.println("HEADER NAME INTERNO: " + campo);
                    f.add(campo);
                    Cell cell2 = row2.createCell(idx++);
                    cell2.setCellValue(campo);
                    //apply the bold style to headers
                    cell2.setCellStyle(subtitlesStyle);
                }

                for (int i = 0; i < rowData.size(); i++) {
                    int id = 0;
                    Row row3 = sheet.createRow(countRow++);

                    for (int j = 0; j < f.size(); j++) {
                        System.out.println("COUNT: " + countRow);
                        JsonNode valore = rowData.get(i).get(f.get(j));
                        System.out.println("ROWDATA FOR FINALE: " + valore);
                        Cell cell3 = row3.createCell(id++);
                        cell3.setCellValue(valore.asText());

                    }
                }

            } else {
                Row row4 = sheet.createRow(countRow++);


                //CELLA HEADER TOTALE
                Cell cell1 = row4.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(headerStyle);

                String totale = sheetData.get(headerName).asText();

                Cell cell2 = row4.createCell(2);
                cell2.setCellValue(totale);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);


            }

        }

        return countRow;
    }

    public int getTransazioniSospese( JsonNode sheetData, String header, int countRow) {
        //Iterating over the each sheets


        System.out.println("SHEET NAME: " + header);

        System.out.println("SHEET DATA: " + sheetData);


        //Creating cell style for header to make it bold
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Aharoni");
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        headerStyle.setFont(font);


        Row row = sheet.createRow(countRow++);


        Cell cell = row.createCell(0);
        cell.setCellValue(header);
        //apply the bold style to headers
        cell.setCellStyle(headerStyle);
        //creating the header into the sheet

        Iterator<String> it = sheetData.fieldNames();

        ArrayList<String> s = new ArrayList<String>();
        while (it.hasNext()) {
            Row row1 = sheet.createRow(countRow++);


            String headerName = it.next();
            System.out.println("HEADER NAME: " + headerName);
            s.add(headerName);

            CellStyle subtitlesStyle = workbook.createCellStyle();
            Font font1 = workbook.createFont();
            font1.setBold(true);
            subtitlesStyle.setFont(font1);

            if (headerName.equals("lista_transazioniSospese")) {
                //CELLA HEADER LISTA SERVIZI
                Cell cell1 = row1.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);

                JsonNode rowData = sheetData.get(headerName);
                System.out.println("ROWDATA INTERNO AL FOR: " + rowData);

                Row row2 = sheet.createRow(countRow++);


                Iterator<String> its = rowData.get(0).fieldNames();

                int idx = 0;
                ArrayList<String> f = new ArrayList<>();
                while (its.hasNext()) {
                    String campo = its.next();
                    System.out.println("HEADER NAME INTERNO: " + campo);
                    f.add(campo);
                    Cell cell2 = row2.createCell(idx++);
                    cell2.setCellValue(campo);
                    //apply the bold style to headers
                    cell2.setCellStyle(subtitlesStyle);
                }

                for (int i = 0; i < rowData.size(); i++) {
                    int id = 0;
                    Row row3 = sheet.createRow(countRow++);

                    for (int j = 0; j < f.size(); j++) {
                        JsonNode valore = rowData.get(i).get(f.get(j));
                        System.out.println("ROWDATA FOR FINALE: " + valore);
                        Cell cell3 = row3.createCell(id++);
                        cell3.setCellValue(valore.asText());

                    }
                }

            } else {
                Row row4 = sheet.createRow(countRow++);


                //CELLA HEADER TOTALE
                Cell cell1 = row4.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(headerStyle);

                String totale = sheetData.get(headerName).asText();

                Cell cell2 = row4.createCell(4);
                cell2.setCellValue(totale);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);


            }

        }
        return countRow;
    }


    public int getPagamenti( JsonNode sheetData, String header, int countRow) {
        //Iterating over the each sheets


        System.out.println("SHEET NAME: " + header);

        System.out.println("SHEET DATA: " + sheetData);


        //Creating cell style for header to make it bold
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Aharoni");
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        headerStyle.setFont(font);


        Row row = sheet.createRow(countRow++);


        Cell cell = row.createCell(0);
        cell.setCellValue(header);
        //apply the bold style to headers
        cell.setCellStyle(headerStyle);
        //creating the header into the sheet

        Iterator<String> it = sheetData.fieldNames();

        ArrayList<String> s = new ArrayList<String>();
        while (it.hasNext()) {
            Row row1 = sheet.createRow(countRow++);


            String headerName = it.next();
            System.out.println("HEADER NAME: " + headerName);
            s.add(headerName);

            CellStyle subtitlesStyle = workbook.createCellStyle();
            Font font1 = workbook.createFont();
            font1.setBold(true);
            subtitlesStyle.setFont(font1);

            if (headerName.equals("lista_pagamenti")) {
                //CELLA HEADER LISTA SERVIZI
                Cell cell1 = row1.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);

                JsonNode rowData = sheetData.get(headerName);
                System.out.println("ROWDATA INTERNO AL FOR: " + rowData);

                Row row2 = sheet.createRow(countRow++);

                Iterator<String> its = rowData.get(0).fieldNames();

                int idx = 0;
                ArrayList<String> f = new ArrayList<>();
                while (its.hasNext()) {
                    String campo = its.next();
                    System.out.println("HEADER NAME INTERNO: " + campo);
                    f.add(campo);
                    Cell cell2 = row2.createCell(idx++);
                    cell2.setCellValue(campo);
                    //apply the bold style to headers
                    cell2.setCellStyle(subtitlesStyle);
                }

                for (int i = 0; i < rowData.size(); i++) {
                    int id = 0;
                    Row row3 = sheet.createRow(countRow++);

                    for (int j = 0; j < f.size(); j++) {
                        JsonNode valore = rowData.get(i).get(f.get(j));
                        System.out.println("ROWDATA FOR FINALE: " + valore);
                        Cell cell3 = row3.createCell(id++);
                        cell3.setCellValue(valore.asText());

                    }
                }

            } else {
                Row row4 = sheet.createRow(countRow++);


                //CELLA HEADER TOTALE
                Cell cell1 = row4.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(headerStyle);

                String totale = sheetData.get(headerName).asText();

                Cell cell2 = row4.createCell(2);
                cell2.setCellValue(totale);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);


            }

        }
        return countRow;
    }

    public int getSconti( JsonNode sheetData, String header, int countRow) {
        //Iterating over the each sheets


        System.out.println("SHEET NAME: " + header);

        System.out.println("SHEET DATA: " + sheetData);


        //Creating cell style for header to make it bold
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Aharoni");
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        headerStyle.setFont(font);


        Row row = sheet.createRow(countRow++);

        Cell cell = row.createCell(0);
        cell.setCellValue(header);
        //apply the bold style to headers
        cell.setCellStyle(headerStyle);
        //creating the header into the sheet

        Iterator<String> it = sheetData.fieldNames();

        ArrayList<String> s = new ArrayList<String>();
        while (it.hasNext()) {
            Row row1 = sheet.createRow(countRow);
            countRow = countRow + 1;


            String headerName = it.next();
            System.out.println("HEADER NAME: " + headerName);
            s.add(headerName);

            CellStyle subtitlesStyle = workbook.createCellStyle();
            Font font1 = workbook.createFont();
            font1.setBold(true);
            subtitlesStyle.setFont(font1);

            if (headerName.equals("lista_sconti")) {
                //CELLA HEADER LISTA SERVIZI
                Cell cell1 = row1.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);

                JsonNode rowData = sheetData.get(headerName);
                System.out.println("ROWDATA INTERNO AL FOR: " + rowData);

                Row row2 = sheet.createRow(countRow++);

                if (rowData.get(0) != null) {
                    Iterator<String> its = rowData.get(0).fieldNames();

                    int idx = 0;
                    ArrayList<String> f = new ArrayList<>();
                    while (its.hasNext()) {
                        String campo = its.next();
                        System.out.println("HEADER NAME INTERNO: " + campo);
                        f.add(campo);
                        Cell cell2 = row2.createCell(idx++);
                        cell2.setCellValue(campo);
                        //apply the bold style to headers
                        cell2.setCellStyle(subtitlesStyle);
                    }

                    for (int i = 0; i < rowData.size(); i++) {
                        int id = 0;
                        Row row3 = sheet.createRow(countRow++);

                        for (int j = 0; j < f.size(); j++) {
                            JsonNode valore = rowData.get(i).get(f.get(j));
                            System.out.println("ROWDATA FOR FINALE: " + valore);
                            Cell cell3 = row3.createCell(id++);
                            cell3.setCellValue(valore.asText());

                        }
                    }
                }


            } else {
                Row row4 = sheet.createRow(countRow++);


                //CELLA HEADER TOTALE
                Cell cell1 = row4.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(headerStyle);

                String totale = sheetData.get(headerName).asText();

                Cell cell2 = row4.createCell(1);
                cell2.setCellValue(totale);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);
            }

        }
        return countRow;
    }

    public int getGruppiEArticoli( JsonNode sheetData, String header, int countRow) {
        //Iterating over the each sheets


        System.out.println("SHEET NAME: " + header);

        System.out.println("SHEET DATA: " + sheetData);


        //Creating cell style for header to make it bold
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Aharoni");
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        headerStyle.setFont(font);


        Row row = sheet.createRow(countRow++);


        Cell cell = row.createCell(0);
        cell.setCellValue(header);
        //apply the bold style to headers
        cell.setCellStyle(headerStyle);
        //creating the header into the sheet

        Iterator<String> it = sheetData.fieldNames();

        ArrayList<String> s = new ArrayList<String>();
        while (it.hasNext()) {
            Row row1 = sheet.createRow(countRow);
            countRow = countRow + 1;


            String headerName = it.next();
            System.out.println("HEADER NAME: " + headerName);
            s.add(headerName);

            CellStyle subtitlesStyle = workbook.createCellStyle();
            Font font1 = workbook.createFont();
            font1.setBold(true);
            subtitlesStyle.setFont(font1);

            if (headerName.equals("gruppi_con_articoli")) {
                //CELLA HEADER LISTA SERVIZI
                Cell cell1 = row1.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);

                JsonNode rowData = sheetData.get(headerName);
                System.out.println("ROWDATA INTERNO AL FOR: " + rowData);

                Row row2 = sheet.createRow(countRow++);


                Iterator<String> its = rowData.get(0).fieldNames();

                int idx = 0;
                ArrayList<String> f = new ArrayList<>();
                while (its.hasNext()) {
                    String campo = its.next();
                    if (campo != "listaArticoli") {
                        System.out.println("HEADER NAME INTERNO: " + campo);
                        f.add(campo);
                        Cell cell2 = row2.createCell(idx++);
                        cell2.setCellValue(campo);
                        //apply the bold style to headers
                        cell2.setCellStyle(subtitlesStyle);
                    }

                }
                System.out.println("SIZE: " + rowData.size());
                for (int i = 0; i < rowData.size(); i++) {
                    int id = 0;
                    Row row3 = sheet.createRow(countRow++);

                    for (int j = 0; j < f.size(); j++) {
                        JsonNode valore = rowData.get(i).get(f.get(j));
                        System.out.println("ROWDATA FOR FINALE: " + valore);
                        Cell cell3 = row3.createCell(id++);
                        cell3.setCellValue(valore.asText());
                        cell3.setCellStyle(subtitlesStyle);
                    }

                    Iterator<String> iterator = rowData.get(0).get("listaArticoli").get(0).fieldNames();
                    ArrayList<String> list = new ArrayList<>();
                    while (iterator.hasNext()) {
                        list.add(iterator.next());
                    }

                    for (int j = 0; j < rowData.get(0).get("listaArticoli").get(0).size(); j++) {
                        Row row4 = sheet.createRow(countRow++);

                        int c = 0;
                        for (int is = 0; is < rowData.get(0).get("listaArticoli").get(0).size(); is++) {
                            JsonNode valore = rowData.get(0).get("listaArticoli").get(j).get(list.get(is));
                            System.out.println("ROWDATA FOR INTERNO FINALE: " + valore);
                            Cell cell3 = row4.createCell(c++);
                            cell3.setCellValue(valore.asText());
                        }


                    }

                }

            } else {
                Row row4 = sheet.createRow(countRow++);


                //CELLA HEADER ARTICOLI, totaleImportoGruppi E totaleQuantitaGruppi
                Cell cell1 = row4.createCell(0);
                cell1.setCellValue(headerName);
                //apply the bold style to headers
                cell1.setCellStyle(headerStyle);

                String totale = sheetData.get(headerName).asText();

                Cell cell2 = row4.createCell(1);
                cell2.setCellValue(totale);
                //apply the bold style to headers
                cell1.setCellStyle(subtitlesStyle);

            }

        }
        return countRow;
    }

    /**
     * Main method to test this converter
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {

        File srcFile = new File("C:/Users/aless_in4zoow/Downloads/test.json");
        JsonToExcelConverter converter = new JsonToExcelConverter();
        File xlsxFile = converter.jsonFileToExcelFile(srcFile, ".xlsx");
        System.out.println("Sucessfully converted JSON to Excel file at =" + xlsxFile.getAbsolutePath());

    }

}