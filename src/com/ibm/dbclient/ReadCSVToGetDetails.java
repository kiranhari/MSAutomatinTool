/**
 * 
 */
package com.ibm.dbclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;



/**
 * @author KiranSuresh
 *
 */
public class ReadCSVToGetDetails {

	/**
	 * @param args
	 * @throws Exception 
	 */
/*	public static void main(String[] args) throws Exception {
		HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(new File("C:/Users/KiranSuresh/Documents/OutputAppend20190128.xls")));
		HSSFSheet sheet=workbook.getSheetAt(0);
		Row FirstRow =sheet.getRow(sheet.getFirstRowNum());
		Cell cellTimeValue =FirstRow.getCell(FirstRow.getLastCellNum()-1);
		String strTime=cellTimeValue.getStringCellValue();
		int iPreviousTimeStamp = Integer.parseInt(new SimpleDateFormat("HHmmss").format(new SimpleDateFormat("hh:mm aaa z").parse(strTime)));
		int iCurrentISTTime=Integer.parseInt(new SimpleDateFormat("HHmmss").format(new Date()));
		Properties prop = new Properties();
	    InputStream input =new FileInputStream("config.properties");
	    prop.load(input);
	    int iPropsTime=Integer.parseInt(prop.getProperty("NextDayTime"));
	    System.out.println("PreviousTimeStamp is "+iPreviousTimeStamp);
		System.out.println("PropsTime is "+iPropsTime);
		System.out.println("Current Time is"+iCurrentISTTime);
	    if((iPreviousTimeStamp < iPropsTime) && (iCurrentISTTime >iPropsTime))
			System.out.println("done");
	    else {
			System.out.println("something is wrong");
			
		}	

	}*/
	public String FileName;
	public String strfileTemp;
	public String strPropFile;
	public String strTemplateFile;
	
	public ReadCSVToGetDetails(String FileName,String strfileTemp,String strPropFile,String strTemplateFile) {
		this.FileName=FileName;
		this.strfileTemp=strfileTemp;
		this.strPropFile=strPropFile;
		this.strTemplateFile=strTemplateFile;
	}
	
	
	public void readFileFromCSV() throws Exception {
		Properties prop = new Properties();
	    InputStream input =new FileInputStream(strPropFile);
	    prop.load(input);
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
		File fileTemp=new File(strfileTemp+timeStamp+".txt");
		fileTemp.createNewFile();
		System.out.println(fileTemp.getAbsolutePath());
		File file=new File(FileName);
		BufferedReader br=new BufferedReader(new FileReader(file));
		ArrayList<CSVPojoClass> alCSVPojo=new ArrayList<>();			
		String strLine = br.readLine();
		while((strLine = br.readLine()) != null )
		{
			String [] strArray=strLine.split(";");
			alCSVPojo=postInObject(strArray,alCSVPojo);
		}
		br.close();
		String https_url=prop.getProperty("url");
		HTTPSDBClient db = new HTTPSDBClient(https_url, prop.getProperty("userid", "admin"), prop.getProperty("password", "password"));
		boolean boolIsAppend=false;
		String strAppendFileName=null;
		int iNoOfQueryToBeAppended=0;
		String[] strExcelValues=new String[alCSVPojo.size()];
		
		for(CSVPojoClass objCSV:alCSVPojo)
		{
			db.invokeDBClientWrapper(objCSV,fileTemp);
			if(objCSV.getAppendRequired())
			{
				strAppendFileName=objCSV.getFilePath()+objCSV.getFileName();
				boolIsAppend=true;
				strExcelValues[iNoOfQueryToBeAppended]=objCSV.getElements();
				iNoOfQueryToBeAppended++;
				
			}
		}
		if(boolIsAppend)
		{
			String strDateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
			
			strAppendFileName=strAppendFileName.concat(strDateStamp);
			PostResultInExcelTemp objPostResultExcel=new PostResultInExcelTemp();
			BufferedReader brTempFileReader=new BufferedReader(new FileReader(fileTemp));
			String strTempResult=null;
			int iNoOfQueriesAppended=0;
			while ((strTempResult=brTempFileReader.readLine())!=null)
			{
				objPostResultExcel.getResultsToPublishInExcel(strTempResult, "table[id=QueryResult] tr",strAppendFileName,true,true,strExcelValues[iNoOfQueriesAppended],0,strPropFile,strTemplateFile);
				iNoOfQueriesAppended++;
			}
			System.out.println("Actual Query To be Appened is "+iNoOfQueryToBeAppended +"and the query result is "+iNoOfQueriesAppended);
			objPostResultExcel.UpdateInExcel(objPostResultExcel.workbook, objPostResultExcel.mapExcelValueToUpdate, objPostResultExcel.iLastColumn, objPostResultExcel.mapGetRowNumber,strPropFile);
			brTempFileReader.close();
			if(iNoOfQueryToBeAppended==iNoOfQueriesAppended)
			{
				fileTemp.delete();
			}
			else
			{
				SendEmail objmail=new SendEmail(null,"Order Monitoring Report for TB is Failed for"+strDateStamp, null, fileTemp.getAbsolutePath(), null);	
			}
			if(Boolean.parseBoolean(prop.getProperty("SendEmail")))
			{
				SendEmail objmail=new SendEmail(null,"Order Monitoring Report for TB -"+strDateStamp, null, null, objPostResultExcel.fileName);
			}
			
		}
		
	}
	private static ArrayList<CSVPojoClass> postInObject(String[] strArray,ArrayList<CSVPojoClass>alCVSPojo) {			
		CSVPojoClass csvObj=new CSVPojoClass();
		for(int i=0;i<strArray.length;i++)
		{
			switch(i)
			{
			case 0:
				csvObj.setQuery(strArray[i]);						
				break;
			case 1:
				csvObj.setNoOfRecords(Integer.parseInt(strArray[i].trim()));
				break;
			case 2:
				csvObj.setPagination(Boolean.parseBoolean(strArray[i].trim()));
				break;
			case 3:
				csvObj.setFilePath(strArray[i]);
				break;
			case 4:
				csvObj.setFileName(strArray[i]);
				break;
			case 5:
				csvObj.setMaxThreshold(strArray[i]);
				break;
			case 6:
				csvObj.setMinThreshold(strArray[i]);
				break;
			case 7:
				csvObj.setStartTimeStamp(strArray[i]);
				break;
			case 8:
				csvObj.setEndTimeStamp(strArray[i]);
				break;
			case 9:
				csvObj.setPropertyFile(strArray[i]);
				break;
			case 10:
				csvObj.setExcelFileNeeded(Boolean.parseBoolean(strArray[i].trim()));
				break;
			case 11:
				csvObj.setTextFileNeeded(Boolean.parseBoolean(strArray[i].trim()));
				break;
			case 12:
				csvObj.setSendEmail(Boolean.parseBoolean(strArray[i].trim()));
				break;
			case 13:
				csvObj.setAppendRequired(Boolean.parseBoolean(strArray[i].trim()));
				break;
			case 14:
				csvObj.setElements(strArray[i].trim());
				break;
			case 15:
				csvObj.setColumnToConsider(Integer.parseInt(strArray[1].trim()));;
				break;	
			default:
				break;				
			}
		}
		alCVSPojo.add(csvObj);
		return alCVSPojo;
	}

}
