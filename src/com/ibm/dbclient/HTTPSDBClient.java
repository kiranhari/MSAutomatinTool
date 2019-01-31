package com.ibm.dbclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.ibm.dbclient.CSVPojoClass;
import com.ibm.dbclient.PostResultInExcel;

/**
 * 
 * @author Devendra Malviya This class provides utility functions to fire sqls
 *         using rest client and formats output of the sqls.
 */
public class HTTPSDBClient {
	public String https_url = "/smcfs/interop/DBSelectQueryServlet";
	public static String queryPrefix = "YFSEnvironment.progId=SterlingDBQueryClient&PaginationAction=&DisablePagination=PAGINATE_FLAG&YFSEnvironment.userId=USER_NAME&YFSEnvironment.password=PWD&SQLStatement.maxRows=RESULT_ROWS&SQLStatement=";
	public String username;
	public String password;
	public String start_ts = null;
	public String end_ts = null;
	int retry = 0;

	/**
	 * Parameterized constructor: It initializes the class and disable SSL.
	 * 
	 * @param          host<IP:Port>
	 * @param username
	 * @param password
	 */
	public HTTPSDBClient(String host, String username, String password) {
		this.https_url = host + this.https_url;
		this.username = username;
		this.password = password;
		// PostResultInExcel obExcel=new PostResultInExcel();

		// -----------------Disable SSL
		// start--------------------------------------------
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub

			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		// -----------------Disable SSL End--------------------------------------------
	}

	/**
	 * This function creates the payload and do URL encode of the payload for the
	 * rest call.
	 * 
	 * @param query               - sql string
	 * @param maxresultrows
	 * @param ispaginationAllowed
	 * @throws ParseException
	 */
	public void invokeDBClientWrapper(CSVPojoClass objCSV, File fileTemp) throws ParseException {
		start_ts = objCSV.getStartTimeStamp();
		end_ts = objCSV.getEndTimeStamp();

		if (StringUtils.isEmpty(start_ts) || StringUtils.isEmpty(end_ts)) {
			SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
			formater.setTimeZone(TimeZone.getTimeZone("EST"));
			start_ts = formater.format(new Date()) + "05";
			System.out.println("Start Time is " + start_ts);
			SimpleDateFormat formaterGMT = new SimpleDateFormat("yyyyMMddHHmm");
			formaterGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
			end_ts = formaterGMT.format(new Date());
			System.out.println("End Time is " + end_ts);
		}
		String query = objCSV.getQuery();
		boolean ispaginationAllowed = objCSV.getPagination();
		int maxresultrows = objCSV.getNoOfRecords();
		query = query.replace("START_TS", start_ts);
		query = query.replace("END_TS", end_ts);
		query = query.toUpperCase();
		String payload = null;
		String result = null;
		try {
			payload = queryPrefix.replace("USER_NAME", URLEncoder.encode(username, "UTF-8"));
			payload = payload.replace("PWD", URLEncoder.encode(password, "UTF-8"));
			payload = payload + URLEncoder.encode(query, "UTF-8");
			payload = payload.replace("RESULT_ROWS", String.valueOf(maxresultrows));
			if (ispaginationAllowed || query.contains("ORDER BY"))
				payload = payload.replace("PAGINATE_FLAG", "Y");
			else
				payload = payload.replace("PAGINATE_FLAG", "N");
			// System.out.println(query);
			long startTime = System.currentTimeMillis();

			result = invokeDBClient(payload);

			long endTime = System.currentTimeMillis();

			System.out.println("That took " + (endTime - startTime) + " milliseconds");

			if (containsError(result)) {
				if (retry < 3) {
					System.out.println("Retry Count is " + retry);
					invokeDBClientWrapper(objCSV, fileTemp);
					retry++;
				}
				return;
			}
			retry = 0;
			if (objCSV.getAppendRequired())
				writeInTempFile(fileTemp, result);
			else {
				boolean boolIsExcelRequired = objCSV.getExcelFileNeeded();
				String strFullFilePath = objCSV.getFilePath() + objCSV.getFileName();
				PostResultInExcel objPostExcel = new PostResultInExcel();
				objPostExcel.getResultsToPublishInExcel(result, "table[id=QueryResult] tr", strFullFilePath,
						boolIsExcelRequired, objCSV.getAppendRequired());
			}
			printResults(result, "table[id=QueryResult] tr");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error Occured while Invoking DB Client!!!!\n" + e.getMessage());
			return;
		}

	}

	private void writeInTempFile(File fileTemp2, String result) throws Exception {
		// TODO Auto-generated method stub
		FileWriter fw = new FileWriter(fileTemp2, true);
		result = result.trim();
		fw.write(result);
		fw.write('\n');
		fw.flush();
		fw.close();

	}

	/**
	 * This function hits url along with payload.
	 * 
	 * @payload
	 */
	public String invokeDBClient(String payload) {
		URL url;
		String s = null;
		try {
			url = new URL(https_url);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			con.setRequestMethod("POST");
			con.setRequestProperty("accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			con.setRequestProperty("content-Type", "application/x-www-form-urlencoded");
			con.setDoOutput(true);
			con.setDoInput(true);
			DataOutputStream output = new DataOutputStream(con.getOutputStream());
			output.writeBytes(payload);
			output.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			char c[] = new char[10000];
			br.read(c, 0, 10000);
			br.close();
			con.disconnect();
			// System.out.println(i);
			s = new String(c);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error: " + e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}

		return s;

	}

	/**
	 * This function checks whether output string contains Error.
	 * 
	 * @param output
	 * @return
	 */
	public boolean containsError(String output) {
		if (output.contains("Error")) {
			System.out.println("Error Occured while Invoking DB Client!!!!\n" + output);
			return true;
		}
		return false;
	}

	/**
	 * This function prints sql result on the console
	 * 
	 * @param output
	 * @param cssqurey
	 */
	public void printResults(String output, String cssqurey) {
		org.jsoup.nodes.Document doc = Jsoup.parse(output);
		Elements eles = doc.select(cssqurey);
		if (eles.size() == 0) {
			System.out.println("No Result Found");
		} else {
			int size[] = null;
			for (int j = 0; j < eles.size(); j++) {

				String format = "%s";
				Elements childs = eles.get(j).getElementsByTag("td");
				if (childs.size() == 0) {
					childs = eles.get(j).getElementsByTag("th");
					size = new int[childs.size()];
				}
				int no_of_columns = childs.size();
				// System.out.println(no_of_columns);
				Object result[] = new Object[no_of_columns];
				for (int i = 1; i < no_of_columns; i++)
					format += "%40s";
				format += "\n";

				for (int i = 0; i < no_of_columns; i++) {
					if (childs.get(i).nodeName().equals("th"))
						size[i] = childs.get(i).text().length();
					if (childs.get(i).text().length() < size[i]) {
						result[i] = appendSpacesAtEnd(childs.get(i).text(), size[i] - childs.get(i).text().length());
					} else
						result[i] = childs.get(i).text();
				}

				System.out.format(format, result);
			}
			System.out.println("--------------------------------------------------------------------");
		}

	}

	/**
	 * This function appends spaces to the input string
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public String appendSpacesAtEnd(String str, int len) {
		String temp = str;
		for (int i = 0; i < len; i++) {
			temp += " ";
		}
		return temp;
	}

	/**
	 * This function reads sql strings from a file and execute them one by one using
	 * DB rest client.
	 * 
	 * @param pathname<full path with file name>
	 */
	public String executeSqlQueriesFromFile(String pathname) {
		String strResult = null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(pathname));
			String query = null;
			int maxrow = 1;
			while ((query = br.readLine()) != null) {
				maxrow = Integer.parseInt(br.readLine());
				System.out.println("max:" + maxrow);
				// query = query.replace("START_TS", start_ts);
				// query = query.replace("END_TS", end_ts);
				System.out.println("Query:" + query);
				String fileTemp = null;
				boolean disablepagination = false;
				strResult = invokeDBClientWrapper(query, maxrow, disablepagination, fileTemp);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println(e1.getMessage());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return strResult;

	}

	public String invokeDBClientWrapper(String query, int maxrow, boolean disablepagination, String fileTemp) {
		query = query.toUpperCase();
		String payload = null;
		String result = null;
		try {
			payload = queryPrefix.replace("USER_NAME", URLEncoder.encode(username, "UTF-8"));
			payload = payload.replace("PWD", URLEncoder.encode(password, "UTF-8"));
			payload = payload + URLEncoder.encode(query, "UTF-8");
			payload = payload.replace("RESULT_ROWS", String.valueOf(maxrow));
			if (disablepagination || query.contains("ORDER BY"))
				payload = payload.replace("PAGINATE_FLAG", "Y");
			else
				payload = payload.replace("PAGINATE_FLAG", "N");
			// System.out.println(query);
			long startTime = System.currentTimeMillis();

			result = invokeDBClient(payload);

			long endTime = System.currentTimeMillis();

			System.out.println("That took " + (endTime - startTime) + " milliseconds");

			printResults(result, "table[id=QueryResult] tr");
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error Occured while Invoking DB Client!!!!\n" + e.getMessage());
			return result;
		}

	}

}
