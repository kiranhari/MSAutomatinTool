/**
 * 
 */
package com.ibm.dbclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
public class PostResultInExcel {

	/**
	 * @param args
	 */
	HashMap<String, String> mapGetExcelValues = getMapValueForExcel();
	HashMap<String, String> mapExcelValueToUpdate = new HashMap<>();
	HashMap<String, Integer> mapGetRowNumber = new HashMap<>();
	int row_num = 0;
	private String fileName;
	int iLastColumn = 0;
	boolean boolIsAppend = false;
	HSSFWorkbook workbook = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void getResultsToPublishInExcel(String output, String cssqurey, String strFullFilePath,
			boolean boolIsExcelRequired, boolean boolIsAppend) throws Exception {
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
				if (boolIsAppend) {
					AppendTheDailyTracker(sheet, eles);
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
					File file = new File("C:/Users/KiranSuresh/Documents/Template.xls");
					if (file.exists() && !file.isDirectory()) {
						FileInputStream fisTemplate = new FileInputStream(file);
						HSSFWorkbook workbookTemplate = new HSSFWorkbook(fisTemplate);
						HSSFSheet sheetTemplate = workbookTemplate.getSheetAt(0);
						FileOutputStream fosTemplate = new FileOutputStream(fileName);
						workbookTemplate.write(fosTemplate);
						fosTemplate.close();
						getResultsToPublishInExcel(output, cssqurey, strFullFilePath, boolIsExcelRequired,
								boolIsAppend);

					} else {
						throw new Exception("Template file not found");
					}

				} else {
					Workbook workbook = new HSSFWorkbook();
					Sheet sheet = workbook.createSheet("Sheet1");
					CellStyle style = workbook.createCellStyle();
					Font font = workbook.createFont();
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
					workbook.write(fileOut);
					fileOut.close();
					System.out.println("End");
				}
				// return (HSSFWorkbook) workbook;
			}

		}
		// return null;
		// Need to do modification.

	}

	private void AppendTheDailyTracker(Sheet sheet, Elements eles) {
		for (Row row : sheet) {
			mapGetRowNumber.put(row.getCell(0).getStringCellValue(), row.getRowNum());
		}
		if (sheet.getLastRowNum() >= 0) {
			Row row = sheet.getRow(sheet.getLastRowNum());
			iLastColumn = row.getLastCellNum();
			boolean boolOrderStatus = false;
			boolean boolReturnStatus = false;
			boolean boolInvoiceCount = false;
			boolean boolReleaseCount = false;
			boolean boolShipmentCount = false;
			for (Element eleTr : eles.select("tr")) {
				String strHeaderName = null;
				//int cell_num = row.getLastCellNum() + 1;
				for (Element eleth : eleTr.select("th")) {
					strHeaderName = eleth.text();
					if (strHeaderName.contains("STATUS_COUNT"))
						boolOrderStatus = true;
					if (strHeaderName.contains("RETURN_COUNT"))
						boolReturnStatus = true;
					if ("RELEASE_COUNT".equalsIgnoreCase(strHeaderName))
						boolReleaseCount = true;
					if ("SHIPMENT_COUNT".equalsIgnoreCase(strHeaderName))
						boolShipmentCount = true;
					if ("INVOICE_COUNT".equalsIgnoreCase(strHeaderName))
						boolInvoiceCount = true;
				}
				if (boolOrderStatus) {
					String strTemp = eleTr.select("td").text();
					if (!strTemp.isEmpty()) {
						String strStatus = strTemp.substring(strTemp.indexOf(" ") + 1, strTemp.length());
						String strStatusValue = strTemp.substring(0, strTemp.indexOf(" "));
						// System.out.println(strStatus);
						mapExcelValueToUpdate.put(mapGetExcelValues.get(strStatus), strStatusValue);
					}
				}
				if (boolReturnStatus) {
					if (!eleTr.select("td").text().isEmpty()) {
						String strTemp = eleTr.select("td").text();
						mapExcelValueToUpdate.put("Return Created", strTemp.substring(0, strTemp.indexOf(" ")));
					}
				}
				// System.out.println("Header Name :"+ strHeaderName);
				if (boolReleaseCount) {
					if (!eleTr.select("td").text().isEmpty()) {
						// String strTemp = eleTr.select("td").text();
						mapExcelValueToUpdate.put("No of Releases", eleTr.select("td").text());
					}
				}
				if (boolShipmentCount) {
					if (!eleTr.select("td").text().isEmpty()) {
						// String strTemp = eleTr.select("td").text();
						mapExcelValueToUpdate.put("No of Shipments", eleTr.select("td").text());
					}
				}
				if (boolInvoiceCount) {
					if (!eleTr.select("td").text().isEmpty()) {
						// String strTemp = eleTr.select("td").text();
						mapExcelValueToUpdate.put("No of Invoices", eleTr.select("td").text());
					}
				}
			}
		}
	}

	public void UpdateInExcel(HSSFWorkbook workbook, HashMap<String, String> mapExcelValueToUpdate, int iLastColNum,
			HashMap<String, Integer> mapGetRowNumber) throws Exception {
		// TODO Auto-generated method stub
		Date date = new Date();
		String strIndianTime = new SimpleDateFormat("hh:mm aaa z").format(new Date());
		SimpleDateFormat formater = new SimpleDateFormat("hh:mm aaa z");
		formater.setTimeZone(TimeZone.getTimeZone("EST"));
		// String dNewDate = formater.format(new Date());
		String esttimestamp = formater.format(new Date());
		// System.out.println("timeStamp -"+ strIndianTime);
		// System.out.println("esttimestamp -"+esttimestamp);
		// System.out.println(strIndianTime+" ("+esttimestamp+")");

		HSSFSheet sheet = workbook.getSheetAt(0);

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

	private static HashMap<String, String> getMapValueForExcel() {
		// TODO Auto-generated method stub
		HashMap<String, String> mapGetValuesForExcel = new HashMap<>();
		mapGetValuesForExcel.put("1100", "Orders Created");
		mapGetValuesForExcel.put("1500", "Orders Scheduled");
		mapGetValuesForExcel.put("3200", "Orders Released");
		mapGetValuesForExcel.put("3700", "Orders Shipped");
		mapGetValuesForExcel.put("9000", "Orders Cancelled");
		return mapGetValuesForExcel;
	}

}
