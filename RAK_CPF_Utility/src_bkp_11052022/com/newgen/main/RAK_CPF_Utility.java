/*
---------------------------------------------------------------------------------------------------------
                  NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group                   : Application - Projects
Project/Product			: RAK BPM
Application				: RAK BPM Utility
Module					: Main
File Name				: RAKBankUtility.java
Author 					: Sakshi Grover
Date (DD/MM/YYYY)		: 30/04/2019

---------------------------------------------------------------------------------------------------------
                 	CHANGE HISTORY
---------------------------------------------------------------------------------------------------------

Problem No/CR No        Change Date           Changed By             Change Description
---------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------
*/


package com.newgen.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import com.newgen.CPF.CPFMain;
import com.newgen.common.CommonConnection;
import com.newgen.encryption.DataEncryption;

public class RAK_CPF_Utility
{
	private static Map<String, String> mainPropMap= new HashMap<String, String>();
	private static String loggerName = "MainLogger";
	private static org.apache.log4j.Logger MainLogger = org.apache.log4j.Logger.getLogger(loggerName);

	 static
	 {
			setLogger();
	 }

	public static void main(String[] args)
	{
		System.out.println("Starting utility...");
		MainLogger.info("Starting Utility");
		int mainPropFileReadCode = readMainPropFile();

		if(mainPropFileReadCode!=0)
		{
			System.out.println("Error in Readin Main Property FIle");
			MainLogger.error("Error in Readin Main Property FIle "+mainPropFileReadCode);
			return;
		}

		try
		{
			int socketPort =  Integer.parseInt(mainPropMap.get("Utility_Port"));
			if(socketPort==0)
			{
				System.out.println("Not able to Get Utility Port");
				MainLogger.error("Not able to Get Utility Port "+socketPort);
				return;
			}
			ServerSocket serverSocket = new ServerSocket(socketPort);



			CommonConnection.setUsername(mainPropMap.get("UserName"));
			CommonConnection.setPassword(DataEncryption.decrypt(mainPropMap.get("Password")));
			CommonConnection.setJTSIP(mainPropMap.get("JTSIP"));
			CommonConnection.setJTSPort(mainPropMap.get("JTSPort"));
			CommonConnection.setsSMSPort(mainPropMap.get("SMSPort"));
			CommonConnection.setCabinetName(mainPropMap.get("CabinetName"));
			CommonConnection.setsVolumeID(mainPropMap.get("VolumeID"));
			CommonConnection.setsSiteID(mainPropMap.get("SiteID"));
			
			CommonConnection.setOFCabinetName(mainPropMap.get("OFCabinetName"));
			CommonConnection.setOFBAISProcessDefId(mainPropMap.get("OFBAISProcessDefId"));
			CommonConnection.setOFJTSIP(mainPropMap.get("OFJTSIP"));
			CommonConnection.setOFJTSPort(mainPropMap.get("OFJTSPort"));
			CommonConnection.setOFVOLUMNID(mainPropMap.get("OFVOLUMNID"));
			CommonConnection.setOFUserName(mainPropMap.get("OFUserName"));
			CommonConnection.setOFPassword(mainPropMap.get("OFPassword"));
			
			String sessionID = CommonConnection.getSessionID(MainLogger,false);

			if(sessionID==null || sessionID.equalsIgnoreCase("") || sessionID.equalsIgnoreCase("null"))
			{
				MainLogger.info("Could Not Get Session ID "+sessionID);
				return;
			}
			if(mainPropMap.get("CPFCommunication")!=null && mainPropMap.get("CPFCommunication").equalsIgnoreCase("Y"))
			{
				Thread CPFThread = new Thread(new CPFMain());
				CPFThread.start();
				System.out.println("DormancyChanges Started");
				MainLogger.info("DormancyChangesProcess Started");
			}
			
			
			
		}
		catch (Exception e)
		{
			if(e.getMessage().toUpperCase().startsWith("Address already in use".toUpperCase()))
			{
				System.out.println("Utility Instance Already Running");
				MainLogger.error("Utility Instance Already Running");
			}
			else
			{
				e.printStackTrace();
				MainLogger.error("Exception Occurred in Main Thread: "+e);
				final Writer result = new StringWriter();
				final PrintWriter printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);
				MainLogger.error("Exception Occurred in Main Thread : "+result);
			}
			return;
		}
		finally
		{
			System.gc();
		}
	}

	private static void setLogger()
	{
		try
		{
			Date date = new Date();
			DateFormat logDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Properties p = new Properties();
			p.load(new FileInputStream(System.getProperty("user.dir")+ File.separator + "log4jFiles"+ File.separator+ "Main_log4j.properties"));
			String dynamicLog = null;
			String orgFileName = null;
			File d = null;
			File fl = null;

			dynamicLog = "Logs/Main_Logs/"+logDateFormat.format(date)+"/Main_Log.xml";
			orgFileName = p.getProperty("log4j.appender."+loggerName+".File");
			if(!(orgFileName==null || orgFileName.equalsIgnoreCase("")))
			{
				dynamicLog = orgFileName.substring(0,orgFileName.lastIndexOf("/")+1)+logDateFormat.format(date)+orgFileName.substring(orgFileName.lastIndexOf("/"));
			}
			d = new File(dynamicLog.substring(0,dynamicLog.lastIndexOf("/")));
			d.mkdirs();
			fl = new File(dynamicLog);
			if(!fl.exists())
				fl.createNewFile();
			p.put("log4j.appender."+loggerName+".File", dynamicLog );

			PropertyConfigurator.configure(p);
			//System.out.println("Dynamic Logger Created");
		}
		catch(Exception e)
		{
			System.out.println("Exception in creating dynamic log :"+e);
			e.printStackTrace();
		}
	}

	private static int readMainPropFile()
	{
		Properties p = null;
		try {

			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "Main_Config.properties")));

			Enumeration<?> names = p.propertyNames();

			while (names.hasMoreElements())
			{
			    String name = (String) names.nextElement();
			    mainPropMap.put(name, p.getProperty(name));
			}

		} catch (Exception e) {

			return -1 ;
		}
		return 0;
	}
}