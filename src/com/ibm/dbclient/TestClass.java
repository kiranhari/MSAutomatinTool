package com.ibm.dbclient;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class TestClass {
	static HashMap<String, Integer> mapGetRowNumber = new HashMap<>();
	int row_num = 0;
	int iLastColumn = 0;
	boolean boolIsAppend = false;
	HSSFWorkbook workbook = null;

	public static void main(String[] args) throws Exception {		
		long startTime = System.currentTimeMillis();
		//ReadCSVToGetDetails objReadCSV = new ReadCSVToGetDetails(args[0], args[1], args[2], args[3]);
		ReadCSVToGetDetails objReadCSV=new ReadCSVToGetDetails("C:/MSAutomation/CHRMonitoringFiles/Code/Book_test.csv","OutputQueryCHR","C:/MSAutomation/CHRMonitoringFiles/Code/config_chr.properties","C:/MSAutomation/CHRMonitoringFiles/Code/Template.xls");
		objReadCSV.readFileFromCSV();
		long endTime = System.currentTimeMillis();
		System.out.println("Whole System took " + (endTime - startTime) + " milliseconds");
	}
	private static Date yesterday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

}