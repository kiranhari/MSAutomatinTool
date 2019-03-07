/**
 * 
 */
package com.ibm.dbclient;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author KiranSuresh
 *
 */
public class CSVPojoClass {
	
	String Query;
	int NoOfRecords;
	boolean Pagination;
	String FilePath;
	String FileName;
	
	String MaxThreshold;
	String MinThreshold;
	String StartTimeStamp;
	String EndTimeStamp;
	String PropertyFile;
	/*String HTTPUrlServelt;
	String UserId;
	String Password;*/
	boolean ExcelFileNeeded;
	boolean TextFileNeeded;
	boolean SendEmail;
	boolean AppendRequired;
	String Elements;
	int ColumnToConsider;
	
	/**
	 * @return the elements
	 */
	public String getElements() {
		return Elements;
	}
	/**
	 * @param elements the elements to set
	 */
	public void setElements(String elements) {
		Elements = elements;
	}
	/**
	 * @return the columnToConsider
	 */
	public int getColumnToConsider() {
		return ColumnToConsider;
	}
	/**
	 * @param columnToConsider the columnToConsider to set
	 */
	public void setColumnToConsider(int columnToConsider) {
		ColumnToConsider = columnToConsider;
	}
	/**
	 * @return the query
	 */
	public String getQuery() {
		return Query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		Query = query;
	}
	/**
	 * @return the noOfRecords
	 */
	public int getNoOfRecords() {
		return NoOfRecords;
	}
	/**
	 * @param noOfRecords the noOfRecords to set
	 */
	public void setNoOfRecords(int noOfRecords) {
		NoOfRecords = noOfRecords;
	}
	/**
	 * @return the pagination
	 */
	public boolean getPagination() {
		return Pagination;
	}
	/**
	 * @param pagination the pagination to set
	 */
	public void setPagination(boolean pagination) {
		Pagination = pagination;
	}
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return FilePath;
	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		String strYear = new SimpleDateFormat("yyyy").format(new Date());
		String strMonth = new SimpleDateFormat("MM").format(new Date());
		String strDay = new SimpleDateFormat("dd").format(new Date());
		filePath=filePath+strYear+'/'+strMonth+'/'+strDay+'/';
		File file= new File(filePath);
		file.mkdirs();
		FilePath = filePath;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return FileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		FileName = fileName;
	}
	/**
	 * @return the maxThreshold
	 */
	public String getMaxThreshold() {
		return MaxThreshold;
	}
	/**
	 * @param maxThreshold the maxThreshold to set
	 */
	public void setMaxThreshold(String maxThreshold) {
		MaxThreshold = maxThreshold;
	}
	/**
	 * @return the minThreshold
	 */
	public String getMinThreshold() {
		return MinThreshold;
	}
	/**
	 * @param minThreshold the minThreshold to set
	 */
	public void setMinThreshold(String minThreshold) {
		MinThreshold = minThreshold;
	}
	/**
	 * @return the startTimeStamp
	 */
	public String getStartTimeStamp() {
		return StartTimeStamp;
	}
	/**
	 * @param startTimeStamp the startTimeStamp to set
	 */
	public void setStartTimeStamp(String startTimeStamp) {
		StartTimeStamp = startTimeStamp;
	}
	/**
	 * @return the endTimeStamp
	 */
	public String getEndTimeStamp() {
		return EndTimeStamp;
	}
	/**
	 * @param endTimeStamp the endTimeStamp to set
	 */
	public void setEndTimeStamp(String endTimeStamp) {
		EndTimeStamp = endTimeStamp;
	}
	/**
	 * @return the propertyFile
	 */
	public String getPropertyFile() {
		return PropertyFile;
	}
	/**
	 * @param propertyFile the propertyFile to set
	 */
	public void setPropertyFile(String propertyFile) {
		PropertyFile = propertyFile;
	}
	/**
	 * @return the excelFileNeeded
	 */
	public boolean getExcelFileNeeded() {
		return ExcelFileNeeded;
	}
	/**
	 * @param excelFileNeeded the excelFileNeeded to set
	 */
	public void setExcelFileNeeded(boolean excelFileNeeded) {
		ExcelFileNeeded = excelFileNeeded;
	}
	/**
	 * @return the textFileNeeded
	 */
	public boolean getTextFileNeeded() {
		return TextFileNeeded;
	}
	/**
	 * @param textFileNeeded the textFileNeeded to set
	 */
	public void setTextFileNeeded(boolean textFileNeeded) {
		TextFileNeeded = textFileNeeded;
	}
	/**
	 * @return the sendEmail
	 */
	public boolean getSendEmail() {
		return SendEmail;
	}
	/**
	 * @param sendEmail the sendEmail to set
	 */
	public void setSendEmail(boolean sendEmail) {
		SendEmail = sendEmail;
	}
	/**
	 * @return the appendRequired
	 */
	public boolean getAppendRequired() {
		return AppendRequired;
	}
	/**
	 * @param appendRequired the appendRequired to set
	 */
	public void setAppendRequired(boolean appendRequired) {
		AppendRequired = appendRequired;
	}


}
