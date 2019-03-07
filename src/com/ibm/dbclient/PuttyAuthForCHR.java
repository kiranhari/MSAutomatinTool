package com.ibm.dbclient;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class PuttyAuthForCHR {
	public static Session getTunnel() throws Exception {
		Session session=null;
		try {
			JSch jsch = new JSch();
			String user = "devema12";
			String host = "jumphostwest.cocdevops.ibmcloud.com";
			int port = 20220;
			//String privateKey="C:/MSAutomation/CHRMonitoringFiles/Code/devendra.ppk";
			String privateKey="C:/Users/KiranSuresh/Documents/SametimeFileTransfers/devendra.ppk";
			//String privateKey = "/tmp/CHR/devendra.ppk";
			jsch.addIdentity(privateKey, "Cooldev12");
			System.out.println("identity added ");
			session = jsch.getSession(user, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			System.out.println("session created.");
			// L50000=pudal10chrdomdbs01.dal10.caas.local:50000,L9443=pudal10chrdomapp01.dal10.caas.local:9443
			// -Prod
			
			session.setPortForwardingL(9443, "pudal10chrdomapp01.dal10.caas.local", 9443);
			session.setPortForwardingL(56634, "pudal10chrdomdbs01.dal10.caas.local", 20220);
			session.connect();
			System.out.println(session.getHost());
			System.out.println("session connected.....");
			Channel channel = session.openChannel("sftp");
			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			channel.connect();
			System.out.println("shell channel connected....");
			System.out.println("End");
			//session.disconnect();
			
			
		} catch (Exception e) {
			System.err.println(e);
			throw new Exception(e);
		}
		return session;
		
	}
}
