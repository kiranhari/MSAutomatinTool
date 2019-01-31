package com.ibm.dbclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class TestClass {
//	HashMap<String, String> mapExcelValueToUpdate = new HashMap<>();

	static HashMap<String, Integer> mapGetRowNumber = new HashMap<>();
	int row_num = 0;
	int iLastColumn = 0;
	boolean boolIsAppend = false;
	HSSFWorkbook workbook = null;

	public static void main(String[] args) throws Exception {
		String start_ts=null;
		String end_ts=null;
		long startTime = System.currentTimeMillis();
		//ReadCSVToGetDetails objReadCSV=new ReadCSVToGetDetails(args[0],args[1],args[2],args[3]);
		ReadCSVToGetDetails objReadCSV=new ReadCSVToGetDetails("C:/Users/KiranSuresh/Documents/Book1.csv","OutputQuery","C:/config.properties","C:/Users/KiranSuresh/Documents/Template.xls");
		objReadCSV.readFileFromCSV();
		long endTime = System.currentTimeMillis();
		System.out.println("Whole System took " + (endTime - startTime) + " milliseconds");
		
		}

	

}