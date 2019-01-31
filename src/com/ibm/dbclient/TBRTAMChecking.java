package com.ibm.dbclient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TBRTAMChecking {
	static String strYantraMessageGroupID;
	static Properties prop = new Properties();
	ArrayList<String> alInvType = new ArrayList<>();
	HashMap<String, String> mapInvSupplyTempList = new HashMap<>();
	HashMap<String, String> mapInvSupplyList = new HashMap<>();
	boolean boolInventorySync = false;

	/*
	 * public void verifyInventorySync() throws Exception { FileInputStream fis =
	 * new FileInputStream("config.properties");
	 * 
	 * prop.load(fis);
	 * 
	 * executeSqlQueriesFromFile("C:/Users/KiranSuresh/Desktop/testQuery.txt"); }
	 */

	/*public static void main(String[] args) throws Exception {
		// TBRTAMChecking.verifyInventorySync();
		TBRTAMChecking rtam = new TBRTAMChecking();
		//rtam.executeSqlQueriesFromFile(args[0]);
		rtam.executeSqlQueriesFromFile("C:/Users/KiranSuresh/Desktop/testQuery.txt");
		;
	}*/

	public String executeSqlQueriesFromFile(String pathname) throws Exception {
		String strResult = null;
		BufferedReader br;
		HashMap<Integer, String> mapQueryList = new HashMap<>();
		boolean boolIsInvSyncStarted = false;
		try {
			br = new BufferedReader(new FileReader(pathname));
			String query = null;
			int maxrow = 1;
			int i = 1;
			while ((query = br.readLine()) != null) {
				maxrow = Integer.parseInt(br.readLine());
				mapQueryList.put(i, query);
				i++;
			}
			int j = 1;
			ArrayList<String> alSupplyTemp = new ArrayList<>();
			ArrayList<String> alSupply = new ArrayList<>();
			String fileTemp = null;
			boolean disablepagination = true;
			FileInputStream fis = new FileInputStream("config.properties");

			prop.load(fis);

			String https_url = prop.getProperty("url");
			HTTPSDBClient db = new HTTPSDBClient(https_url, prop.getProperty("userid", "admin"),
					prop.getProperty("password", "password"));
			for (Entry<Integer, String> entry : mapQueryList.entrySet()) {
				if (j == 1) {
					strResult = db.invokeDBClientWrapper(entry.getValue(), 1, disablepagination, fileTemp);
					Document document = Jsoup.parse(strResult);
					Elements eles = document.select("table[id=QueryResult] tr");
					int iTemp = 1;
					String strDate;
					for (Element eleTd : eles.select("td")) {
						switch (iTemp) {
						case 1:
							strYantraMessageGroupID = eleTd.text();
							iTemp++;
							break;
						case 2:
							strDate = eleTd.text().substring(0, eleTd.text().indexOf(" "));
							int icreatets = Integer.parseInt(strDate.replace("-", ""));
							//int icreatets=20190128;
							int iTodaysDate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
							if (icreatets == iTodaysDate)
								boolIsInvSyncStarted = true;
							iTemp++;
							break;
						default:
							break;
						}
					}
				}
				if (j == 2) {
					alSupply = postResultInArrayList(db, strResult, entry.getValue(), alSupply);
				}
				if (j == 3) {
					alSupplyTemp = postResultInArrayList(db, strResult, entry.getValue(), alSupplyTemp);

				}
				j++;
				if (!boolIsInvSyncStarted)
				{
					SendEmail objSendEmail=new SendEmail("Kirsures@in.ibm.com", "Inv Sync For TB Failed", "Hi All,"+'\n'+"Inventory Sync got Failed. Kindly do necessary steps", null,null);
					throw new Exception("Inv Sync Not Started");
				}

			}
			mapInvSupplyList = MapTheValues(alSupply);
			mapInvSupplyTempList = MapTheValues(alSupplyTemp);

			if (verifyItemResults())
				System.out.println("Inventory Sync happened sucessfully ");
			else
				throw new Exception("Inv Sync Failed");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println(e1.getMessage());

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		System.out.println("yantra is " + strYantraMessageGroupID);
		return strResult;

	}

	public boolean verifyItemResults() {
		alInvType.add("ONHAND");
		alInvType.add("PO_INTRANSIT.ex");
		for (String strInvType : alInvType) {
			if (strInvType.equalsIgnoreCase("ONHAND")) {
				if (Integer.parseInt(mapInvSupplyList.get(strInvType))
						- (Integer.parseInt(mapInvSupplyTempList.get(strInvType))) == 1)
					boolInventorySync = true;
			} else {
				if (Integer.parseInt(mapInvSupplyList.get(strInvType))
						- (Integer.parseInt(mapInvSupplyTempList.get(strInvType))) == 0)
					boolInventorySync = true;
			}
		}
		return boolInventorySync;
	}

	private HashMap<String, String> MapTheValues(ArrayList<String> alTemp) {
		HashMap<String, String> mapTemp = new HashMap<String, String>();
		int iSize = alTemp.size();
		String strKey = null;
		String strValue = null;
		for (int i = 0; i < iSize; i++) {
			strKey = alTemp.get(i);
			i++;
			if (i < iSize)
				strValue = alTemp.get(i);
			if (StringUtils.isNotEmpty(strKey) && StringUtils.isNotEmpty(strValue))
				mapTemp.put(strKey, strValue);
		}

		return mapTemp;
	}

	public ArrayList<String> postResultInArrayList(HTTPSDBClient db, String strResult, String query,
			ArrayList<String> alSupplyTemp) {

		strResult = db.invokeDBClientWrapper(query.replace("yantra_message_group_id=''",
				"yantra_message_group_id='" + strYantraMessageGroupID + "'"), 4, true, null);
		Document document = Jsoup.parse(strResult);
		Elements eles = document.select("table[id=QueryResult] tr");
		for (Element eleTd : eles.select("td")) {
			alSupplyTemp.add(eleTd.text());
		}

		return alSupplyTemp;
	}

}
