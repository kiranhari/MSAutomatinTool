/**
 * 
 */
package com.ibm.dbclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	HashMap<String, Integer> mapGetRowNumber = new HashMap<>();
	int row_num = 0;
	public String fileName;
	int iLastColumn = 0;
	boolean boolIsAppend = false;
	HSSFWorkbook workbook = null;
	private HSSFWorkbook workbookTemplate;
	private Workbook workbook2;

	public void getResultsToPublishInExcel(String output, String cssqurey, String strFullFilePath,
			boolean boolIsExcelRequired, boolean boolIsAppend, String strElements, int iValueToConsider,
			String strPropFile,String strTemplateFile) throws Exception {
		this.boolIsAppend = boolIsAppend;
		org.jsoup.nodes.Document doc = Jsoup.parse(output);
		Elements eles = doc.select(cssqurey);
		Elements eleQueryInput = doc.select("table[id=QueryInput]");
		if (eles.size() == 0) {
			System.out.println("No Result Found");
			throw new Exception("Empty HTML Result");
		} else {
			// fileName = "C:/Users/KiranSuresh/Documents/Book4.xls";
			if (boolIsExcelRequired)
				fileName = strFullFilePath + ".xls";
			// System.out.println("FileName: "+fileName);
			File f = new File(fileName);
			if (f.exists() && !f.isDirectory()) {
				FileInputStream fos = new FileInputStream(f);
				workbook = new HSSFWorkbook(fos);
				HSSFSheet sheet = workbook.getSheetAt(0);
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
					row_num = sheet.getLastRowNum() + 2;
					for (Element eleTr : eleQueryInput.select("TR")) {

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
					workbook.write(fileOut);
					fileOut.close();
					System.out.println("End");
				}
				// return workbook;
			} else {
				if (boolIsAppend) {
					File file = new File(strTemplateFile);
					if (file.exists() && !file.isDirectory()) {
						FileInputStream fisTemplate = new FileInputStream(file);
						workbookTemplate = new HSSFWorkbook(fisTemplate);
						FileOutputStream fosTemplate = new FileOutputStream(fileName);
						workbookTemplate.write(fosTemplate);
						fosTemplate.close();
						getResultsToPublishInExcel(output, cssqurey, strFullFilePath, boolIsExcelRequired, boolIsAppend,
								strElements, iValueToConsider, strPropFile,strTemplateFile);

					} else {
						throw new Exception("Template file not found");
					}

				} else {
					workbook2 = new HSSFWorkbook();
					Sheet sheet = workbook2.createSheet("Sheet1");
					CellStyle style = workbook2.createCellStyle();
					Font font = workbook2.createFont();
					font.setBold(true);
					style.setFont(font);

					for (Element eleTr : eleQueryInput.select("TR")) {
						int cell_num = 0;
						Row row = sheet.createRow(row_num++);
						for (Element eleth : eleTr.select("TH")) {
							Cell cell = row.createCell(cell_num++);
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
					fileOut.close();
					System.out.println("End");
				}
				// return (HSSFWorkbook) workbook;
			}

		}
		// return null;
		// Need to do modification.

	}

	private HashMap<String, String> AppendTheDailyTracker(Sheet sheet, Elements eles, String strArray,
			HashMap<String, String> mapExcelValueToUpdate, int iValueToConsider, String strPropFile) throws Exception {

		HashMap<String, String> mapGetExcelValues = new HashMap<>();
		mapGetExcelValues = getMapValueForExcel(strArray, strPropFile);

		for (Element eleTr : eles.select("tr")) {
			ArrayList<String> alTemp = new ArrayList<>();
			for (Element eleTd : eleTr.select("td")) {
				alTemp.add(eleTd.text());
			}
			int iSize = alTemp.size();
			if (mapGetExcelValues.size() > 1) {

				if (iSize > 1) {
					mapExcelValueToUpdate.put(mapGetExcelValues.get(alTemp.get(iSize - iValueToConsider - 1)),
							alTemp.get(iValueToConsider));
				}
			}
			if (mapGetExcelValues.size() == 1) {
				if (iSize == 1) {
					for (String entry : mapGetExcelValues.keySet())
						mapExcelValueToUpdate.put(entry, alTemp.get(0));
				}
				if (iSize > 1) {
					mapExcelValueToUpdate.put(mapGetExcelValues.get(alTemp.get(iSize - iValueToConsider - 1)),
							alTemp.get(iValueToConsider));
				}
			}

		}
		return mapExcelValueToUpdate;

	}

	public void UpdateInExcel(HSSFWorkbook workbook, HashMap<String, String> mapExcelValueToUpdate, int iLastColNum,
			HashMap<String, Integer> mapGetRowNumber, String strPropFile) throws Exception {
		// TODO Auto-generated method stub
		Properties prop = new Properties();
		InputStream input = new FileInputStream(strPropFile);
		prop.load(input);
		int iPropsTime = Integer.parseInt(prop.getProperty("NextDayTime"));
		String strIndianTime = new SimpleDateFormat("hh:mm aaa z").format(new Date());
		SimpleDateFormat formater = new SimpleDateFormat("hh:mm aaa z");
		formater.setTimeZone(TimeZone.getTimeZone("EST"));
		// String dNewDate = formater.format(new Date());
		String esttimestamp = formater.format(new Date());
		HSSFSheet sheet = workbook.getSheetAt(0);
		Row FirstRow = sheet.getRow(sheet.getFirstRowNum());
		System.out.println("First Row is "+FirstRow.getLastCellNum());
		if (FirstRow.getLastCellNum() - 1 > 0) {
			Cell cellTimeValue = FirstRow.getCell(FirstRow.getLastCellNum() - 1);
			String strTime = cellTimeValue.getStringCellValue();
			int iPreviousTimeStamp = Integer.parseInt(
					new SimpleDateFormat("HHmmss").format(new SimpleDateFormat("hh:mm aaa z").parse(strTime)));
			int iCurrentISTTime = Integer.parseInt(new SimpleDateFormat("HHmmss").format(new Date()));
			iLastColNum = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
			System.out.println("PreviousTimeStamp is " + iPreviousTimeStamp);
			System.out.println("PropsTime is " + iPropsTime);
			System.out.println("Current Time is" + iCurrentISTTime);

			if ((iPreviousTimeStamp < iPropsTime) && (iCurrentISTTime > iPropsTime))
				iLastColNum++;
			System.out.println("Last Column is " + iLastColNum);
		}
		else
		{
			iLastColNum++;
		}
		System.out.println("ILast Column is "+iLastColNum);
		
		for (Row row : sheet) {
			mapGetRowNumber.put(row.getCell(0).getStringCellValue(), row.getRowNum());
		}
		for (Entry<String, Integer> entry : mapGetRowNumber.entrySet()) {
			Row row = sheet.getRow(entry.getValue());
			Cell cell = row.createCell(iLastColNum);
			cell.setCellValue(mapExcelValueToUpdate.get(entry.getKey()));
		}
		Row row0 = sheet.getRow(0);
		Cell cell0 = row0.createCell(iLastColNum);
		cell0.setCellValue(strIndianTime + " (" + esttimestamp + ")");
		cell0.setCellStyle(row0.getCell(0).getCellStyle());
		;
		/*
		 * HSSFCellStyle style = workbook.createCellStyle();
		 * 
		 * HSSFFont font = workbook.createFont(); font.setFontHeightInPoints((short)
		 * 15); //font.setBoldweight(HSSFFont.); style.setFont(font);
		 */
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();
		System.out.println("End");

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
					// System.out.println("Key:" + strTemp + "Value:" + strPropValue);
				}
			}
		} else {
			throw new Exception("Excel Value Update was Missing");
		}
		input.close();
		return mapGetValuesForExcel;
	}

}
