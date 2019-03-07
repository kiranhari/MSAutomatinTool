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
import java.util.HashMap;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.jcraft.jsch.Session;

//import com.jcraft.jsch.Session;



/**
 * @author KiranSuresh
 *
 */
public class ReadCSVToGetDetails {

	/**
	 * @param args
	 * @throws Exception 
	 */

	public String FileName;
	public String strfileTemp;
	public String strPropFile;
	public String strTemplateFile;
	static HashMap<String,String> mapFileLocationForMail=new HashMap<>();
	public Session session;
	
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
		session=PuttyAuthForCHR.getTunnel();
		//session.disconnect();
		String https_url=prop.getProperty("url");
		HTTPSDBClient db = new HTTPSDBClient(https_url, prop.getProperty("userid", "admin"), prop.getProperty("password", "password"));
		boolean boolIsAppend=false;
		String strAppendFileName=null;
		int iNoOfQueryToBeAppended=0;
		ArrayList<String> strExcelValues=new ArrayList<>();
		
		for(CSVPojoClass objCSV:alCSVPojo)
		{
			db.invokeDBClientWrapper(objCSV,fileTemp,0);
			if(objCSV.getAppendRequired())
			{
				strAppendFileName=objCSV.getFilePath()+objCSV.getFileName();
				boolIsAppend=true;
				strExcelValues.add(objCSV.getElements());
				iNoOfQueryToBeAppended++;
				
			}
		}
		session.disconnect();
		if(boolIsAppend)
		{
			String strDateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String strDateTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
			
			strAppendFileName=strAppendFileName.concat(strDateStamp);
			PostResultInExcelTemp objPostResultExcel=new PostResultInExcelTemp();
			BufferedReader brTempFileReader=new BufferedReader(new FileReader(fileTemp));
			String strTempResult=null;
			int iNoOfQueriesAppended=0;
			while ((strTempResult=brTempFileReader.readLine())!=null)
			{
				objPostResultExcel.getResultsToPublishInExcel(strTempResult, "table[id=QueryResult] tr",strAppendFileName,true,true,strExcelValues.get(iNoOfQueriesAppended),0,strPropFile,strTemplateFile,mapFileLocationForMail);
				iNoOfQueriesAppended++;
			}
			System.out.println("Actual Query To be Appened is "+iNoOfQueryToBeAppended +"and the query result is "+iNoOfQueriesAppended);
			objPostResultExcel.UpdateInExcel(objPostResultExcel.workbook, objPostResultExcel.mapExcelValueToUpdate, objPostResultExcel.iLastColumn,strPropFile,mapFileLocationForMail);
			brTempFileReader.close();
			if(iNoOfQueryToBeAppended==iNoOfQueriesAppended)
			{
				fileTemp.delete();
			}
			else
			{
				SendEmail objmail=new SendEmail(null,prop.getProperty("ErrorEmailSubject")+strDateTimeStamp, null, fileTemp.getAbsolutePath(), null,null,prop.getProperty("ReceiverEmailid",null),prop.getProperty("BCCEmailid",null));	
			}
			if(Boolean.parseBoolean(prop.getProperty("SendEmail")))
			{
				SendEmail objmail=new SendEmail(null,prop.getProperty("EmailSubject")+strDateTimeStamp, null, null, objPostResultExcel.fileName,mapFileLocationForMail,prop.getProperty("ReceiverEmailid",null),prop.getProperty("BCCEmailid",null));
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
