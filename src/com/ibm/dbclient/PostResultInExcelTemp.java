package com.ibm.dbclient;
/**
 * 
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author KiranSuresh
 *
 */
public class PostResultInExcelTemp {

	/**
	 * @param args
	 */

	HashMap<String, String> mapExcelValueToUpdate = new HashMap<>();

	int row_num = 0;
	int iSqlWorksheetRowNum = 0;
	public String fileName;
	int iLastColumn = 0;
	boolean boolIsAppend = false;
	HSSFWorkbook workbook = null;
	private HSSFWorkbook workbookTemplate;
	private Workbook workbook2;

	public void getResultsToPublishInExcel(String output, String cssqurey, String strFullFilePath,
			boolean boolIsExcelRequired, boolean boolIsAppend, String strElements, int iValueToConsider,
			String strPropFile, String strTemplateFile, HashMap<String, String> mapFileLocationForMail)
			throws Exception {
		this.boolIsAppend = boolIsAppend;
		org.jsoup.nodes.Document doc = Jsoup.parse(output);
		Elements eles = doc.select(cssqurey);
		Elements eleQueryInput = doc.select("table[id=QueryInput]");
		if (eles.size() == 0) {
			System.out.println("No Result Found");
			throw new Exception("Empty HTML Result");
		} else {
			if (boolIsExcelRequired) {
				fileName = strFullFilePath + ".xls";
			}
			File f = new File(fileName);
			if (f.exists() && !f.isDirectory()) {
				System.out.println("File Name :" + f.getAbsolutePath());
				FileInputStream fos = new FileInputStream(f);
				 workbook = new HSSFWorkbook(fos);
				int iNoOfSheets = workbook.getNumberOfSheets();
				HSSFSheet sheet = workbook.createSheet("QueryResults" + iNoOfSheets);
				HSSFSheet sheetSqlQueryList = workbook.getSheet("SQL_Queries");
				HSSFCellStyle style = workbook.createCellStyle();
				HSSFFont font = workbook.createFont();
				font.setBold(true);
				style.setFont(font);
				style.setBorderBottom(BorderStyle.THICK);
				style.setBorderTop(BorderStyle.THICK);
				style.setBorderRight(BorderStyle.THICK);
				style.setBorderLeft(BorderStyle.THICK);				
				if (boolIsAppend) {
					AppendTheDailyTracker(sheet, eles, strElements, mapExcelValueToUpdate, iValueToConsider,
							strPropFile);
					boolIsAppend = false;
				} else {
					row_num = sheet.getLastRowNum();
					
						
					iSqlWorksheetRowNum = sheetSqlQueryList.getLastRowNum() + 1;
					for (Element eleTr : eleQueryInput.select("TR")) {
						int cell_num = 0;
						Row row = sheetSqlQueryList.createRow(iSqlWorksheetRowNum++);
						for (Element eleth : eleTr.select("th")) {
							Cell cell = row.createCell(cell_num++);
							cell.setCellValue(eleth.text());
							cell.setCellStyle(style);
						}
						for (Element eletd : eleTr.select("td")) {
							Cell cell = row.createCell(cell_num++);
							cell.setCellValue(eletd.text());
							cell.setCellStyle(style);
						}
					}
					
					for (Element eleTr : eles.select("tr")) {
						int cell_num = 0;
						Row row = sheet.createRow(row_num++);
						for (Element eleth : eleTr.select("th")) {
							Cell cell = row.createCell(cell_num++);
							cell.setCellValue(eleth.text());
							cell.setCellStyle(style);
						}
						for (Element eletd : eleTr.select("td")) {
							Cell cell = row.createCell(cell_num++);
							cell.setCellValue(eletd.text());
						}
					}
					String strTemp = mapFileLocationForMail.get("NonAppend");
					if (StringUtils.isEmpty(strTemp))
						mapFileLocationForMail.put("NonAppend", fileName);
					else {
						strTemp += "," + fileName;
						mapFileLocationForMail.put("NonAppend", strTemp);
					}
					FileOutputStream fileOut = new FileOutputStream(fileName);
					workbook.write(fileOut);
					fileOut.close();
					System.out.println("End");
				}
				// return workbook;
			} else {
				if (boolIsAppend) {
					String strDirectory = fileName.substring(0, fileName.lastIndexOf("/"));
					File fDirectory = new File(strDirectory);
					if (!fDirectory.mkdir())
						fDirectory.mkdirs();
					File file = new File(strTemplateFile);
					if (file.exists() && !file.isDirectory()) {
						FileInputStream fisTemplate = new FileInputStream(file);
						workbookTemplate = new HSSFWorkbook(fisTemplate);
						FileOutputStream fosTemplate = new FileOutputStream(fileName);
						workbookTemplate.write(fosTemplate);
						fosTemplate.close();

						getResultsToPublishInExcel(output, cssqurey, strFullFilePath, boolIsExcelRequired, boolIsAppend,
								strElements, iValueToConsider, strPropFile, strTemplateFile, mapFileLocationForMail);

					} else {
						throw new Exception("Template file not found");
					}

				} else {
					workbook2 = new HSSFWorkbook();
					int iNoOfSheets = workbook2.getNumberOfSheets();
					Sheet sheet = workbook2.createSheet("QueryResults" + iNoOfSheets);
					CellStyle style = workbook2.createCellStyle();
					Font font = workbook2.createFont();
					font.setBold(true);
					style.setFont(font);
					//row_num++;
					////
					// HSSFSheet sheet = workbook.createSheet("QueryResults"+iNoOfSheets);
					Sheet sheetSqlQueryList = workbook2.createSheet("SQL_Queries");
					iSqlWorksheetRowNum = sheetSqlQueryList.getLastRowNum();
					for (Element eleTr : eleQueryInput.select("TR")) {
						int cell_num = 0;
						Row row = sheetSqlQueryList.createRow(row_num);
						for (Element eleth : eleTr.select("TH")) {
							Cell cell = row.createCell(iSqlWorksheetRowNum++);
							cell.setCellValue(eleth.text());
							cell.setCellStyle(style);
						}
						for (Element eletd : eleTr.select("TD")) {
							Cell cell = row.createCell(cell_num++);
							cell.setCellValue(eletd.text());
							cell.setCellStyle(style);
						}
					}
					for (Element eleTr : eles.select("tr")) {
						int cell_num = 0;
						Row row = sheet.createRow(row_num++);
						for (Element eleth : eleTr.select("th")) {
							Cell cell = row.createCell(cell_num++);
							cell.setCellValue(eleth.text());
							cell.setCellStyle(style);
						}
						for (Element eletd : eleTr.select("td")) {
							Cell cell = row.createCell(cell_num++);
							cell.setCellValue(eletd.text());
						}
					}
					FileOutputStream fileOut = new FileOutputStream(fileName);
					workbook2.write(fileOut);
					String strTemp = mapFileLocationForMail.get("NonAppend");
					if (StringUtils.isEmpty(strTemp))
						mapFileLocationForMail.put("NonAppend", fileName);
					else {
						strTemp += "," + fileName;
						mapFileLocationForMail.put("NonAppend", strTemp);
					}

					fileOut.close();

				}

			}

		}

	}

	private HashMap<String, String> AppendTheDailyTracker(Sheet sheet, Elements eles, String strArray,
			HashMap<String, String> mapExcelValueToUpdate, int iValueToConsider, String strPropFile) throws Exception {
		int iNoOfColumn = 0;
		HashMap<String, String> mapGetExcelValues = new HashMap<>();
		mapGetExcelValues = getMapValueForExcel(strArray, strPropFile);
		for (Element eleTr : eles.select("tr")) {

			ArrayList<String> alTemp = new ArrayList<>();
			for (Element eleTh : eleTr.select("th")) {
				iNoOfColumn = eleTr.select("th").size();

				alTemp.add(eleTh.text());
			}
			if (iNoOfColumn > 2)
				putValueinMap(mapExcelValueToUpdate, alTemp);

			for (Element eleTd : eleTr.select("td")) {
				alTemp.add(eleTd.text());
			}
			int iSize = alTemp.size();
			if (mapGetExcelValues.size() > 1) {

				if (iSize > 1) {
					if (iNoOfColumn > 2) {
						putValueinMap(mapExcelValueToUpdate, alTemp);
					} else
					{
						if(StringUtils.isNotEmpty(mapGetExcelValues.get(alTemp.get(iSize - iValueToConsider - 1))))
						{
							mapExcelValueToUpdate.put(mapGetExcelValues.get(alTemp.get(iSize - iValueToConsider - 1)),
										alTemp.get(iValueToConsider));
						}
						else
							putValueinMap(mapExcelValueToUpdate, alTemp);
						//System.out.println("Map Values");
						//System.out.println(mapGetExcelValues.get(alTemp.get(iSize - iValueToConsider - 1)+":"+alTemp.get(iValueToConsider)));
						//mapExcelValueToUpdate.put(mapGetExcelValues.get(alTemp.get(iSize - iValueToConsider - 1)),
							//	alTemp.get(iValueToConsider));
					}
				}
			}
			if (mapGetExcelValues.size() == 1) {
				if (iSize == 1) {
					if (iNoOfColumn > 2) {
						putValueinMap(mapExcelValueToUpdate, alTemp);
					} else {
						for (String entry : mapGetExcelValues.keySet())
							mapExcelValueToUpdate.put(entry, alTemp.get(0));
					}
				}
				if (iSize > 1) {
					if (iNoOfColumn > 2) {
						putValueinMap(mapExcelValueToUpdate, alTemp);
					} else
					{
						System.out.println("Map Values");
						System.out.println(mapGetExcelValues.get(alTemp.get(iSize - iValueToConsider - 1)+":"+alTemp.get(iValueToConsider)));
						mapExcelValueToUpdate.put(mapGetExcelValues.get(alTemp.get(iSize - iValueToConsider - 1)),
								alTemp.get(iValueToConsider));
					}
				}
			}

		}
		return mapExcelValueToUpdate;

	}

	private HashMap<String, String> putValueinMap(HashMap<String, String> mapExcelValueToUpdate,
			ArrayList<String> alTemp) {
		if (alTemp.size() > 0) {
			String strValueToAppend = "";
			for (int i = 1; i < alTemp.size(); i++) {
				strValueToAppend += alTemp.get(i) + ',';
			}
			mapExcelValueToUpdate.put(alTemp.get(0), strValueToAppend);
		}
		return mapExcelValueToUpdate;

	}

	public void UpdateInExcel(HSSFWorkbook workbook, HashMap<String, String> mapExcelValueToUpdate, int iLastColNum,
			String strPropFile, HashMap<String, String> mapFileLocationForMail) throws Exception {
		FileInputStream fos = new FileInputStream(fileName);
		 workbook = new HSSFWorkbook(fos);

		Properties prop = new Properties();
		InputStream input = new FileInputStream(strPropFile);
		prop.load(input);
		int iPropsTime = Integer.parseInt(prop.getProperty("NextDayTime"));
		SimpleDateFormat indianformater = new SimpleDateFormat("HH:mm aaa z");
		indianformater.setTimeZone(TimeZone.getTimeZone("IST"));
		String strIndianTime = indianformater.format(new Date());
		SimpleDateFormat formater = new SimpleDateFormat("HH:mm aaa z");
		formater.setTimeZone(TimeZone.getTimeZone("EST"));
		String esttimestamp = formater.format(new Date());
		System.out.println("Size of Sheet is" + workbook.getNumberOfSheets());
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			System.out.println("Sheet Name" + workbook.getSheetName(i));
		}
		// System.out.println(workbook.);
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			HSSFSheet sheet = workbook.getSheetAt(i);
			iLastColNum = sheet.getRow(sheet.getLastRowNum()).getLastCellNum() - 1;
			Row FirstRow = sheet.getRow(sheet.getFirstRowNum());

			if (FirstRow.getLastCellNum() - 1 > 0) {
				Cell cellTimeValue = FirstRow.getCell(FirstRow.getLastCellNum() - 1);
				String strTime = cellTimeValue.getStringCellValue();
				int iPreviousTimeStamp = Integer.parseInt(
						new SimpleDateFormat("HHmmss").format(new SimpleDateFormat("HH:mm aaa z").parse(strTime)));
				int iCurrentISTTime = Integer.parseInt(new SimpleDateFormat("HHmmss").format(new Date()));
				iLastColNum = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
				if ((iPreviousTimeStamp < iPropsTime) && (iCurrentISTTime > iPropsTime))
					iLastColNum++;

			} else {
				iLastColNum++;
			}

			HashMap<String, Integer> mapGetRowNumber = new HashMap<>();
			for (Row row : sheet) {
				mapGetRowNumber.put(row.getCell(0).getStringCellValue(), row.getRowNum());
			}
			for (Entry<String, Integer> entry : mapGetRowNumber.entrySet()) {
				Row row = sheet.getRow(entry.getValue());
				String strCellValue = mapExcelValueToUpdate.get(entry.getKey());
				if (StringUtils.isNotEmpty(strCellValue)) {

					String[] strArrCellValue = strCellValue.split(",");
					if (strArrCellValue.length == 1) {
						Cell cell = row.createCell(iLastColNum);
						cell.setCellValue(strCellValue);

						mapExcelValueToUpdate.remove(entry.getKey());
					} else {
						UpdateCellValue(strArrCellValue, iLastColNum, row);
					}
				}

			}
			Row row0 = sheet.getRow(0);
			Cell cell0 = row0.createCell(iLastColNum);
			cell0.setCellValue(strIndianTime + " (" + esttimestamp + ")");
			cell0.setCellStyle(row0.getCell(0).getCellStyle());
		}
		FileOutputStream fileOut = new FileOutputStream(fileName);
		mapFileLocationForMail.put("Append", fileName);
		workbook.write(fileOut);
		fileOut.close();
		System.out.println("End");

	}

	private void UpdateCellValue(String[] strArrCellValue, int iLastColNum, Row row) {
		int iColNum = iLastColNum;
		for (int i = 0; i < strArrCellValue.length; i++) {
			iColNum += i;
			Cell cell = row.createCell(iColNum);
			cell.setCellValue(strArrCellValue[i]);
		}
	}

	private HashMap<String, String> getMapValueForExcel(String strArray2, String strPropFile) throws Exception {
		HashMap<String, String> mapGetValuesForExcel = new HashMap<>();
		Properties prop = new Properties();
		FileInputStream input = new FileInputStream(strPropFile);

		prop.load(input);
		if (StringUtils.isNotEmpty(strArray2)) {
			String[] strArray = strArray2.split(",");
			if (strArray.length > 0) {
				for (String strTemp : strArray) {
					strTemp = strTemp.replace(" ", "_");
					String strPropValue = prop.getProperty(strTemp);
					strTemp = strTemp.replace("_", " ");
					if (StringUtils.isNotEmpty(strPropValue))
						mapGetValuesForExcel.put(strPropValue, strTemp);
					else
						mapGetValuesForExcel.put(strTemp, "0");
				}
			}
		} else {
			throw new Exception("Excel Value Update was Missing");
		}
		input.close();
		return mapGetValuesForExcel;
	}

}
