package com.ibm.dbclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class SendEmail {

	public static final String SAMPLE_XLSX_FILE_PATH = null;

	final String senderEmailID = "amitakumar.s@acuverconsulting.com";
	final String senderPassword = "Prince@123";
	final String emailSMTPserver = "smtp.gmail.com";
	final String emailServerPort = "465";
	static String receiverEmailID ="IBM-RSC-MS@wwpdl.vnet.ibm.com"; //"kirsures@in.ibm.com";//"IBM-RSC-MS@wwpdl.vnet.ibm.com";
	static Date date = new Date();
	static String strDateFormat = "YYYY-MM-DD";
	static DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
	static String strDate = dateFormat.format(date);

	static String emailSubject = "Automation Append Function Got Failed" + strDate;
	static String emailBody = "This is the email body";
	static String strAppendQueryResultFile = "C:\\KiranSuresh\\eclipse-workspace\\MSAutomation\\OutputQuery20190128_1157.txt";

	/*
	 * public static void main(String[] args) { SendEmail sn = new
	 * SendEmail(receiverEmailID, emailSubject,
	 * emailBody,strAppendQueryResultFile,SAMPLE_XLSX_FILE_PATH); }
	 */

	public SendEmail(String receiverEmailID, String Subject, String Body, String strAppendQueryResultFile,
			String strExcelFile) {

		// Receiver Email Address
		if (StringUtils.isNotEmpty(receiverEmailID))
			SendEmail.receiverEmailID = receiverEmailID;
		// Subject
		if (StringUtils.isNotEmpty(Subject))
			SendEmail.emailSubject = Subject;
		// Body
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
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(SendEmail.receiverEmailID));

			Multipart multipart = new MimeMultipart();

			// add the body message
			BodyPart bodyPart = new MimeBodyPart();
			String str = null;
			if (StringUtils.isNotEmpty(strExcelFile))
			{
				//str = new String(Files.readAllBytes(Paths.get(strExcelFile)));		
							
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(strExcelFile)));
			    HSSFWorkbook workbook = new HSSFWorkbook(fs);
			//Workbook workbook = ;

				Sheet sheet = workbook.getSheetAt(0);

				DataFormatter dataFormatter = new DataFormatter();

				PrintWriter pw = new PrintWriter(
						new FileWriter(
								"test.html"));
				pw.println("<TABLE BORDER>");

				for (Row row : sheet) {

					pw.print("<tr>");
					for (Cell cell : row) {
						String cellValue = dataFormatter.formatCellValue(cell);
						pw.print("<TD>" + cellValue + "<TD>");

					}
					pw.print("</tr>");
				}
				pw.println("</TABLE>");
				pw.close();
				 str = new String(
							Files.readAllBytes(Paths
									.get("test.html")));

					str = "Please Find the below Monitoring Deatils \n \n \n" + str;
					str = str + "Thank You.. \n \n " + " \n IBM Managed Service Team";


				msg.setContent(str, "text/html");
			}

				multipart.addBodyPart(bodyPart);	
				

			if (StringUtils.isNotEmpty(strAppendQueryResultFile))
			{
				str = "Please Find the below File Path: " + strAppendQueryResultFile;
			str = str + "Thank You.. \n \n " + " \n IBM Managed Service Team";
			msg.setContent(str,"text/html");
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