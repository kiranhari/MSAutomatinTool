package com.ibm.dbclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;

public class SendEmail {

	public static final String SAMPLE_XLSX_FILE_PATH = null;

	final String senderEmailID = "amitakumar.s@acuverconsulting.com";
	final String senderPassword = "Prince@123";
	final String emailSMTPserver = "smtp.gmail.com";
	final String emailServerPort = "465";
	static String receiverEmailID = "kirsures@in.ibm.com";// "IBM-RSC-MS@wwpdl.vnet.ibm.com";
	static Date date = new Date();
	static String strDateFormat = "YYYY-MM-DD HH:MM";
	static DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
	static String strDate = dateFormat.format(date);

	static String emailSubject = "TB Automation Report" + strDate;
	static String emailBody = "This is the email body";

	public SendEmail(String receiverEmailID, String Subject, String Body, String strAppendQueryResultFile,
			String strExcelFile, HashMap<String, String> mapFileLocation,String strReceiverList,String strBccList) {
		Set<String> alFileLocation = new TreeSet<>();
		String strAppendFile=null;

		for (Entry<String, String> entry : mapFileLocation.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("Append")) {
				//alFileLocation.add(entry.getValue());
				strAppendFile=entry.getValue();
				
			} else {
				String strFileLocation = entry.getValue();
				String[] strArrFileLocation = strFileLocation.split(",");
				for (String Temp : strArrFileLocation)
					alFileLocation.add(Temp);
			}

		}
		for (String strTemp : alFileLocation) {
			System.out.println(strTemp);
		}
		if (StringUtils.isNotEmpty(receiverEmailID))
			SendEmail.receiverEmailID = receiverEmailID;
		if (StringUtils.isNotEmpty(Subject))
			SendEmail.emailSubject = Subject;
		if (StringUtils.isNotEmpty(Body))
			SendEmail.emailBody = Body;
		Properties props = new Properties();
		props.put("mail.smtp.user", senderEmailID);
		props.put("mail.smtp.host", emailSMTPserver);
		props.put("mail.smtp.port", emailServerPort);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", emailServerPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		SecurityManager security = System.getSecurityManager();
		try {
			System.out.println("Mail Server started");
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);
			Message msg = new MimeMessage(session);
			msg.setText(emailBody);

			msg.setSubject(emailSubject);
			msg.setFrom(new InternetAddress(senderEmailID));
			if(strReceiverList.isEmpty())
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(SendEmail.receiverEmailID));
			else
			{
				String[] strArReceiver=strReceiverList.split(",");
				for(String Temp:strArReceiver)
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(Temp));					
			}
			if(StringUtils.isNotEmpty(strBccList))
			{
				String[] strArReceiver=strBccList.split(",");
				for(String Temp:strArReceiver)
					msg.addRecipient(Message.RecipientType.CC, new InternetAddress(Temp));					
			}

			Multipart multipart = new MimeMultipart();

			// add the body message
			BodyPart bodyPart = new MimeBodyPart();
			String str = null;
			PrintWriter pw = new PrintWriter(new FileWriter("test.html"));
			// Workbook workbook = ;

			for (String strTemp : alFileLocation) {
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(strTemp)));
				HSSFWorkbook workbookAppend = new HSSFWorkbook(fs);
				
				for(int i=0;i<workbookAppend.getNumberOfSheets();i++)
				{
					Sheet sheetAppend = workbookAppend.getSheetAt(i);
					DataFormatter dataFormatter = new DataFormatter();
					pw.println("<br>");
					pw.println("<br><br>");
					pw.println("<TABLE BORDER>");
					// pw.println("<caption>1. Order Monitoring Details</caption>");
					for (Row row : sheetAppend) {

						pw.print("<tr>");
						for (Cell cell : row) {
							String cellValue = dataFormatter.formatCellValue(cell);
							pw.print("<TD>" + cellValue + "<TD>");

						}
						pw.print("</tr>");
					}
					pw.println("</TABLE>");
				}
				
			}
		
			
			if(StringUtils.isNotEmpty(strAppendFile))
			{
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(strAppendFile)));
				HSSFWorkbook workbookAppend = new HSSFWorkbook(fs);
				workbookAppend.setMissingCellPolicy(MissingCellPolicy.RETURN_BLANK_AS_NULL);
				DataFormatter fmt = new DataFormatter();
				
				for (int sn = 0; sn < workbookAppend.getNumberOfSheets(); sn++) {
					Sheet sheet = workbookAppend.getSheetAt(sn);
					pw.print("<table  align='center' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white; width:100%'>");
					for (int rn = sheet.getFirstRowNum(); rn <= sheet.getLastRowNum(); rn++) {
						Row row = sheet.getRow(rn);
						if (row == null) {
							// There is no data in this row, handle as needed
							for (int cn = 0; cn < row.getLastCellNum(); cn++) {
								Cell cellValue = row.getCell(cn);
								if (cellValue == null) {
									// This cell is empty/blank/un-used, handle as
									// needed
									System.out.println("cell value is empty");
									pw.print("<TD>" + 0 + "</TD>");
								} else {
									String cellStr = fmt.formatCellValue(cellValue);
									// Do something with the value
									pw.print("<TD>" + cellStr + "</TD>");
								}
							}
							pw.print("</tr>");
						} else {
							pw.print("<tr>");
							// Row "rn" has data
							for (int cn = 0; cn < row.getLastCellNum(); cn++) {
								Cell cellValue = row.getCell(cn);
								if (cellValue == null) {
									// This cell is empty/blank/un-used, handle as
									// needed
									System.out.println("cell value is empty");
									pw.print("<TD>" + 0 + "</TD>");
								} else {
									String cellStr = fmt.formatCellValue(cellValue);
									// Do something with the value
									pw.print("<TD>" + cellStr + "</TD>");
								}
							}
							pw.print("</tr>");

						}

					}
					pw.print("</table>");

					pw.print("<br><br><br>");

				}
			}
			pw.close();
			str = new String(Files.readAllBytes(Paths.get("test.html")));
			//System.out.println(str);
			str = "Please Find the below Monitoring Deatils \n \n \n" + str;
			str = str + "<Div>Thank You.. \n </Div>" + " <Div>\nIBM Managed Service Team</Div>";
			msg.setContent(str, "text/html");
			multipart.addBodyPart(bodyPart);

			if (StringUtils.isNotEmpty(strAppendQueryResultFile)) {
				str = str + "Thank You.. \n \n " + " \n IBM Managed Service Team";
				msg.setContent(str, "text/html");
			}

			multipart.addBodyPart(bodyPart);

			// attach the file
			MimeBodyPart mimeBodyPart = new MimeBodyPart();

			Transport.send(msg);
			System.out.println("Message send Successfully:)");
		}

		catch (Exception mex) {
			mex.printStackTrace();
		}

	}

	public class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(senderEmailID, senderPassword);
		}
	}

}