package com.newgen.CPF;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.omni.wf.util.excp.NGException;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;
import com.newgen.wfdesktop.xmlapi.WFInputXml;
import com.newgen.wfdesktop.xmlapi.WFXmlList;
import com.newgen.wfdesktop.xmlapi.WFXmlResponse;

public class CPFMain implements Runnable {
	
	static Map<String, String> CPFConfigParamMap = new HashMap<String, String>();
	static String sessionID = "";
	static String cabinetName = "";
	static String jtsIP ="";
	static String jtsPort ="";
	int sleepIntervalInMin = 0;
	static String processName="";
	static String stage="";
	static String criteria="";
	static String frequency="";
	static String activityId="";
	static String ProcessDefID="";
	static String dataSource="";
	private static NGEjbClient ngEjbClientConnection;

	static
	{
		try
		{
			ngEjbClientConnection = NGEjbClient.getSharedInstance();
		}
		catch (NGException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() 
	{
		try
		{
			CPFLog.setLogger();
			
			int configReadStatus = readConfig();
			CPFLog.CPFLogger.debug("configReadStatus " + configReadStatus);
			if (configReadStatus != 0) 
			{
				CPFLog.CPFLogger.error("Could not Read Config Properties [CPF]");
				//return;
			}
			else
			{
				dataSource=CPFConfigParamMap.get("DataSourceLookup");
			}
			
			cabinetName = CommonConnection.getCabinetName();
			CPFLog.CPFLogger.debug("Cabinet Name: " + cabinetName);

			jtsIP = CommonConnection.getJTSIP();
			CPFLog.CPFLogger.debug("JTSIP: " + jtsIP);

			jtsPort = CommonConnection.getJTSPort();
			CPFLog.CPFLogger.debug("JTSPORT: " + jtsPort);

			sleepIntervalInMin = Integer.parseInt(CPFConfigParamMap.get("SleepIntervalInMin"));
			CPFLog.CPFLogger.debug("SleepIntervalInMin: " + sleepIntervalInMin);

			sessionID = CommonConnection.getSessionID(CPFLog.CPFLogger, false);

			if (sessionID.trim().equalsIgnoreCase(""))
			{
				CPFLog.CPFLogger.debug("Could Not Connect to Server!");
			} 
			else
			{
				CPFLog.CPFLogger.debug("Session ID found: " + sessionID);

				while (true) 
				{
					CPFLog.setLogger();
					CPFLog.CPFLogger.debug("CPF Process Starting...");
					startUtilityCPF(cabinetName, sessionID, jtsIP, jtsPort);
					CPFLog.CPFLogger.debug("No More CPF Case to Process, Sleeping!");
					System.out.println("No More CPF Case to Process, Sleeping!");
					Thread.sleep(sleepIntervalInMin * 60 * 1000);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			CPFLog.CPFLogger.error("Exception Occurred in CreateWIFromTextFie: " + e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			CPFLog.CPFLogger.error("Exception Occurred in CreateWIFromTextFie: " + result);
		}
	}
	
	private int readConfig() 
	{
		Properties p = null;
		try 
		{
			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "ConfigFiles"
					+ File.separator + "CPF_Config.properties")));

			Enumeration<?> names = p.propertyNames();

			while (names.hasMoreElements()) 
			{
				String name = (String) names.nextElement();
				CPFConfigParamMap.put(name, p.getProperty(name));
			}
		}
		catch (Exception e)
		{
			return -1;
		}
		return 0;
	}
	
	private static void startUtilityCPF(String cabinetName, String sessionId, String JtsIp,String JtsPort) 
	{
		CPFLog.CPFLogger.info("Inside startUtilityCPF..");
		try 
		{
			WFXmlList objWorkList=null;
			WFXmlResponse xmlParserData=new WFXmlResponse();
			XMLParser objXMLParser = new XMLParser();
			String Maincode="";
			List<String> WIList = new ArrayList<String>();
			
			String Query = "SELECT * FROM USR_0_CPF_FrequencyMaster with(nolock) WHERE IsActive='Y' ORDER BY executionorder";
			CPFLog.CPFLogger.info("Query to fetch Frequency = "+Query);
			String InputXML = CommonMethods.apSelectWithColumnNames(Query, cabinetName, sessionID);
			CPFLog.CPFLogger.info("Getting data from Frequency table InputXML = "+InputXML);
			String OutputXML=WFNGExecute(InputXML, jtsIP, jtsPort, 0 , CPFLog.CPFLogger);//CommonMethods.WFNGExecute(InputXML,jtsIP, jtsPort, 0);//ThreadConnect.WFNGExecutePD(InputXML,jtsIP,Integer.parseInt(jtsPort),1);
			CPFLog.CPFLogger.info("Getting data from Frequency table OutputXML = "+OutputXML);
	
			xmlParserData.setXmlString(OutputXML);
			Maincode=xmlParserData.getVal("MainCode");
			CPFLog.CPFLogger.info("Getting data from Frequency table Maincode = "+Maincode);
			if(Maincode.equalsIgnoreCase("0"))
			{
				objWorkList = xmlParserData.createList("Records", "Record");
				//CPFLog.CPFLogger.info("Getting data from EFMS table Maincode = "+objWorkList);
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
				{
					processName=objWorkList.getVal("ProcessName");
					stage=objWorkList.getVal("CommStage");
					criteria=objWorkList.getVal("CommStage");
					frequency=objWorkList.getVal("CommFrequencyInDays");
					
					WIList=getWorkItemListToProcess();
					
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			CPFLog.CPFLogger.error("Exception Occurred in CreateWIFromTextFie: " + e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			CPFLog.CPFLogger.error("Exception Occurred in CreateWIFromTextFie: " + result);
		}
	}
	
	protected static List<String> getWorkItemListToProcess()
	{
		List<String> WIList = new ArrayList<String>();
		
		String query="";
		//case on the basis of Process
		//case on the basis of stage
		if(processName.equalsIgnoreCase("AO"))
		{
			switch(stage)
			{
			case "Stage1":
			{
				query="SELECT Q.processinstanceid,Q.IntroductionDateTime,Q.activityid,Q.processdefid FROM QUEUEVIEW AS Q with(nolock) " + 
						"JOIN RB_AO_EXTTABLE AS EXT ON Q.processinstanceid = EXT.WI_NAME " + 
						"WHERE Q.ProcessName ='AO' AND Q.activityname ='Sys_CPF_Response' " + 
						"AND (EXT.CPFStage1Status IS NULL OR CPFStage1Status = '') " + 
						"AND (EXT.CPFDeclineStatus IS NULL OR EXT.CPFDeclineStatus ='') order by Q.EntryDateTime";
				break;
			}
			case "Stage2":
			{
				query="SELECT Q.processinstanceid,Q.IntroductionDateTime,Q.activityid,Q.processdefid FROM QUEUEVIEW AS Q with(nolock) " + 
						"JOIN RB_AO_EXTTABLE AS EXT ON Q.processinstanceid = EXT.WI_NAME " + 
						"WHERE Q.ProcessName ='AO' AND Q.activityname ='Sys_CPF_Response' " + 
						"AND (EXT.CPFStage2Status IS NULL OR CPFStage2Status = '') " + 
						"AND (EXT.CPFDeclineStatus IS NULL OR EXT.CPFDeclineStatus ='') order by Q.EntryDateTime";
				break;
			}
			case "Stage3":
			{
				query="SELECT Q.processinstanceid,Q.IntroductionDateTime,Q.activityid,Q.processdefid FROM QUEUEVIEW AS Q with(nolock) " + 
						"JOIN RB_AO_EXTTABLE AS EXT ON Q.processinstanceid = EXT.WI_NAME " + 
						"WHERE Q.ProcessName ='AO' AND Q.activityname ='Sys_CPF_Response' " + 
						"AND (EXT.CPFStage3Status IS NULL OR CPFStage3Status = '') " + 
						"AND (EXT.CPFDeclineStatus IS NULL OR EXT.CPFDeclineStatus ='') order by Q.EntryDateTime";
				break;
			}
			}
			
		}
		else if(processName.equalsIgnoreCase("BAIS"))
		{
			switch(stage)
			{
			case "Stage1":
			{
				query="SELECT Q.processinstanceid,Q.IntroductionDateTime,Q.activityid,Q.processdefid FROM QUEUEVIEW AS Q with(nolock)  " + 
						"JOIN RB_AO_EXTTABLE AS EXT ON Q.processinstanceid = EXT.WI_NAME " + 
						"WHERE Q.ProcessName ='BAIS' AND Q.activityname ='Sys_CPF_Response' " + 
						"AND (EXT.CPFStage1Status IS NULL OR CPFStage1Status = '') " + 
						"AND (EXT.CPFDeclineStatus IS NULL OR EXT.CPFDeclineStatus ='') order by Q.EntryDateTime";
			}
			case "Stage2":
			{
				query="SELECT Q.processinstanceid,Q.IntroductionDateTime,Q.activityid,Q.processdefid FROM QUEUEVIEW AS Q with(nolock) " + 
						"JOIN RB_AO_EXTTABLE AS EXT ON Q.processinstanceid = EXT.WI_NAME " + 
						"WHERE Q.ProcessName ='BAIS' AND Q.activityname ='Sys_CPF_Response' " + 
						"AND (EXT.CPFStage2Status IS NULL OR CPFStage2Status = '') " + 
						"AND (EXT.CPFDeclineStatus IS NULL OR EXT.CPFDeclineStatus ='') order by Q.EntryDateTime";
			}
			case "Stage3":
			{
				query="SELECT Q.processinstanceid,Q.IntroductionDateTime,Q.activityid,Q.processdefid FROM QUEUEVIEW AS Q with(nolock) " + 
						"JOIN RB_AO_EXTTABLE AS EXT ON Q.processinstanceid = EXT.WI_NAME " + 
						"WHERE Q.ProcessName ='BAIS' AND Q.activityname ='Sys_CPF_Response' " + 
						"AND (EXT.CPFStage3Status IS NULL OR CPFStage3Status = '') " + 
						"AND (EXT.CPFDeclineStatus IS NULL OR EXT.CPFDeclineStatus ='') order by Q.EntryDateTime";
			}
			}
			
		}
		
		try {
			
			WFXmlList objWorkList=null;
			WFXmlResponse xmlParserData=new WFXmlResponse();
			XMLParser objXMLParser = new XMLParser();
			String Maincode="";
			CPFLog.CPFLogger.info("Query to fetch WIList = "+query);
			String InputXML = CommonMethods.apSelectWithColumnNames(query, cabinetName, sessionID);
			CPFLog.CPFLogger.info("Getting data of Workitem InputXML = "+InputXML);
			String OutputXML=WFNGExecute(InputXML, jtsIP, jtsPort, 0 , CPFLog.CPFLogger);
			CPFLog.CPFLogger.info("Getting data from Queueview = "+OutputXML);
	
			xmlParserData.setXmlString(OutputXML);
			Maincode=xmlParserData.getVal("MainCode");
			CPFLog.CPFLogger.info("Getting data from Queueview table Maincode = "+Maincode);
			if(Maincode.equalsIgnoreCase("0"))
			{
				objWorkList = xmlParserData.createList("Records", "Record");
				//CPFLog.CPFLogger.info("Getting data from Queueview Maincode = "+objWorkList);
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
				{
					String WI_NAME=objWorkList.getVal("processinstanceid");
					String introductionDate=objWorkList.getVal("IntroductionDateTime");
					//String workStep = objWorkList.getVal("ActivityName");
					String age =getAgeOfWI(introductionDate);
					ProcessDefID=objWorkList.getVal("processdefid");
					activityId=objWorkList.getVal("Activityid");
					if(Integer.parseInt(age)>=Integer.parseInt(frequency))
					{
						List<String> templateType = new ArrayList<String>();
						String wsList = getWorkStepListForAWI(WI_NAME);
						
						if(wsList!=null && !wsList.equalsIgnoreCase(""))
						{
							templateType=getTemplateTypeList(wsList);	
						}
						if(templateType!=null && templateType.size()!=0)
						AddMailInMailQueue(WI_NAME,templateType);
					}
					
					updateEntryDateTime(WI_NAME);
					
				}
			}
			
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				CPFLog.CPFLogger.error("Exception Occurred in get WI_List: " + e);
			}
		return WIList;
		
	}
	
	protected static String getAgeOfWI(String creationDate)
	{
		try {
	    	 String newEntryDateTime="";
				if(!creationDate.equals(""))
				{
					 newEntryDateTime=creationDate.substring(0, creationDate.length()-3);
					 CPFLog.CPFLogger.info("newEntryDateTime -"+newEntryDateTime);
					 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    	 CPFLog.CPFLogger.info("getdateCurrentDateInSQLFormat -"+CommonMethods.getCurrentDateInSQLFormat());
			    	 Date d1 = format.parse(CommonMethods.getCurrentDateInSQLFormat());
			    	 Date d2 = format.parse(newEntryDateTime);//2021-12-19 16:57:34
			    	 CPFLog.CPFLogger.info("getdateCurrentDate after parsing -"+d1);
			    	 CPFLog.CPFLogger.info("newEntryDateTime after parsing -"+d2);
			    	 long age = (d1.getTime() / 1000 / 60 / 60) - (d2.getTime() / 1000 / 60 / 60);
			    	 CPFLog.CPFLogger.info("age in hours :-"+age);
			    	 age=age/24;
			    	 return Long.toString(age);
				}
				return "";
	    	 
	     } catch (Exception ex) {
	    	 CPFLog.CPFLogger.info("Exception in tatHours -"+ex);
	         return null;
	     }
	}
	public static void updateEntryDateTime(String WI_NAME)
	{
		try
		{
			String tableName="WFINSTRUMENTTABLE";
			String columnname="EntryDateTime";
			String sWhere="ProcessInstanceID ='"+WI_NAME+ "' AND activityname ='Sys_CPF_Response'"; 
			String value="getDate()";
			CPFCommunication.ExecuteQuery_APUpdate(tableName,columnname ,value, sWhere, sessionID);
		}
		catch(Exception e)
		{
			CPFLog.CPFLogger.info("Exception in updateEntryDateTime -"+e.toString());
		}
	}
	
	protected static String getWorkStepListForAWI(String WI_NAME)
	{
		String gwsListQuery="Select activityName from QUEUEVIEW with(nolock) where activityName NOT IN ('Distribute','Distribute_Archival','Sys_CPF_Response') \r\n" + 
				"and processinstanceid='"+WI_NAME+"'";
		String FinalactivityList="";
		
		try {
			
			WFXmlList objWorkList=null;
			WFXmlResponse xmlParserData=new WFXmlResponse();
			XMLParser objXMLParser = new XMLParser();
			String Maincode="";
			CPFLog.CPFLogger.info("Query to fetch WIList = "+gwsListQuery);
			String InputXML = CommonMethods.apSelectWithColumnNames(gwsListQuery, cabinetName, sessionID);
			CPFLog.CPFLogger.info("Getting data of Workitem InputXML = "+InputXML);
			String OutputXML=WFNGExecute(InputXML, jtsIP, jtsPort, 0 , CPFLog.CPFLogger);
			CPFLog.CPFLogger.info("Getting data from Queueview = "+OutputXML);
	
			xmlParserData.setXmlString(OutputXML);
			Maincode=xmlParserData.getVal("MainCode");
			CPFLog.CPFLogger.info("Getting data from Queueview table Maincode = "+Maincode);
			if(Maincode.equalsIgnoreCase("0"))
			{
				objWorkList = xmlParserData.createList("Records", "Record");
				//CPFLog.CPFLogger.info("Getting data from Queueview Maincode = "+objWorkList);
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
				{
					String temp=objWorkList.getVal("activityName");
					if(temp!=null && !temp.equalsIgnoreCase(""))
					{
						FinalactivityList+="'"+temp+"',";
					}
				}
				
				if(FinalactivityList!=null && !FinalactivityList.equalsIgnoreCase(""))
					FinalactivityList=FinalactivityList.substring(0, FinalactivityList.length()-1);
			}
			
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				CPFLog.CPFLogger.error("Exception Occurred in get WI_List: " + e);
			}
		return FinalactivityList;
	}
	protected static List<String> getTemplateTypeList(String workStep)
	{
		List<String> templateType = new ArrayList<String>();
		
		String queryToFetchTempType="SELECT QueueName,TemplateType FROM USR_0_CPF_QueueStatusMapping with(nolock) WHERE ProcessName='"+processName+"' \r\n" + 
				"AND QueueName IN("+workStep+") AND IsActive='Y'";
		
		try {
			
			WFXmlList objWorkList=null;
			WFXmlResponse xmlParserData=new WFXmlResponse();
			XMLParser objXMLParser = new XMLParser();
			String Maincode="";
			CPFLog.CPFLogger.info("Query to fetch WIList = "+queryToFetchTempType);
			String InputXML = CommonMethods.apSelectWithColumnNames(queryToFetchTempType, cabinetName, sessionID);
			CPFLog.CPFLogger.info("Getting data of Workitem InputXML = "+InputXML);
			String OutputXML=WFNGExecute(InputXML, jtsIP, jtsPort, 0 , CPFLog.CPFLogger);
			CPFLog.CPFLogger.info("Getting data from Queueview = "+OutputXML);
	
			xmlParserData.setXmlString(OutputXML);
			Maincode=xmlParserData.getVal("MainCode");
			CPFLog.CPFLogger.info("Getting data from Queueview table Maincode = "+Maincode);
			if(Maincode.equalsIgnoreCase("0"))
			{
				objWorkList = xmlParserData.createList("Records", "Record");
				//CPFLog.CPFLogger.info("Getting data from Queueview Maincode = "+objWorkList);
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
				{
					String wsName=objWorkList.getVal("QueueName");
					String temp=objWorkList.getVal("TemplateType");
					
					if(temp!=null && !temp.equalsIgnoreCase("") && wsName!=null && !wsName.equalsIgnoreCase(""))
					{
						templateType.add(wsName+"~"+temp);
					}
				}
				
			}
			
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				CPFLog.CPFLogger.error("Exception Occurred in get WI_List: " + e);
			}	
		
		return templateType;
	}
	protected static void AddMailInMailQueue(String WI_NAME,List<String> templateType)
	{
		int noOftemplatetype=templateType.size();
		String tempType="";
		String wsName="";
		
		try
		{ 
			
			if(noOftemplatetype==1)
			{
				String strArr [] = templateType.get(0).split("~");
				wsName=strArr[0];
				tempType=strArr[1];
			}
			else
			{
				String str=getCorrectTemplate(WI_NAME,templateType);
				String strArr [] = str.split("~");
				wsName=strArr[1];
				tempType=strArr[0];
			}
				
			
			String query2="SELECT MailTemplate,IsActiveMail,DefaultCCMail,FromMail,SMSEnglishTemplate,IsActiveEnglish,SMSArabicTemplate,isActiveSMSArabic\r\n" + 
				"  FROM USR_0_CPF_TemplateTypeTemplateMapping with(nolock) WHERE templatetype ='"+tempType+"' AND ProcessName='"+processName+"' AND CommStage='"+stage+"'";
		
			CPFLog.CPFLogger.info("Query to fetch template = "+query2);
			List<String> result = new ArrayList<>();
			//result=executSelectQuery(query2);
			result=executeQuery_AP_Select(query2);
			if(result!=null && result.size()>0)
			{

				String MailTemplate=result.get(0);
				String IsActiveMail=result.get(1);
				String ccMail=result.get(2);
				String fromMail=result.get(3);
				String SMSEnglishTemplate = result.get(4);
				String IsActiveEnglish=result.get(5);
				String SMSArabicTemplate=result.get(6);
				String isActiveSMSArabic =result.get(7);
				
				if(IsActiveMail.equals("Y")||IsActiveEnglish.equals("Y")||isActiveSMSArabic.equals("Y"))
				{
					List<String> widata = new ArrayList<String>();
					widata=getWIDataToCommunicate(WI_NAME);
					
					if(IsActiveMail.equals("Y"))
					{
						
						if(widata!=null && widata.size()!=0)
						CPFCommunication.sendMail(fromMail, widata.get(0),ccMail, cabinetName, sessionID, MailTemplate, ProcessDefID, activityId,stage,WI_NAME,wsName,processName,dataSource,tempType);
					}
					if(isActiveSMSArabic.equals("Y")||IsActiveEnglish.equals("Y"))
					{
						
						if(widata!=null && widata.size()!=0) {
						if(IsActiveEnglish.equals("Y"))
						{
							CPFCommunication.sendSMS(widata.get(1), SMSEnglishTemplate, cabinetName, sessionID, ProcessDefID,wsName,stage, WI_NAME, processName,dataSource,tempType);
						}
						if(isActiveSMSArabic.equals("Y"))
						{
							CPFCommunication.sendSMS(widata.get(1), SMSArabicTemplate, cabinetName, sessionID, ProcessDefID,wsName,stage, WI_NAME, processName,dataSource,tempType);
						}
						}
					}
				}
				else
				{
					CPFCommunication.UpdateCommFlag(WI_NAME,wsName,stage,processName,sessionID,"Email","'InActive'");
				}
			}
		}
		catch(Exception e)
		{
			CPFLog.CPFLogger.error("Exception Occurred in AddMailInMailQueue: " + e);
		}
	}
	
	protected static List<String> getWIDataToCommunicate(String WI_NAME)//yet to be completed
	{
		List<String> widata = new ArrayList<String>();
		String query="";
		try
		{
			switch(processName)
			{
				case "AO":
				{
					query="SELECT eMail,concat(Mobile_Country_Code,'',Mobile_Number) AS MobNo FROM RB_AO_EXTTABLE WITH(nolock) WHERE WI_NAME ='"+WI_NAME+"'";
				}
				case "BAIS":
				{
					
				}
			}
			String sInputXML=CommonMethods.apSelectWithColumnNames(query, cabinetName, sessionID);
			CPFLog.CPFLogger.debug("Input XML: "+sInputXML);
			String sOutputXML =  null;
			try
			{
				sOutputXML = WFNGExecute(sInputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 1 , CPFLog.CPFLogger);
				CPFLog.CPFLogger.debug("Output XML: "+sOutputXML);
			}
			
			catch (Exception e)
			{
				CPFLog.CPFLogger.error("Exception in checkExistingSession "+e);
				return null;
			}

			String eMail=CommonMethods.getTagValues(sOutputXML,"eMail");
			if(eMail!=null && !"".equalsIgnoreCase(eMail))
				widata.add(eMail);
			
			String MobNo=CommonMethods.getTagValues(sOutputXML,"MobNo");
			if(MobNo!=null && !"".equalsIgnoreCase(MobNo))
				widata.add(MobNo);
			
			return widata;
		}
		catch(Exception e)
		{
			CPFLog.CPFLogger.error("Exception Occurred in getWIDataToCommunicate: " + e);
			return null;
		}
	}
	
	
	protected static String getCorrectTemplate(String WI_NAME,List<String> templateType)//yet to be completed
	{
		String tempType="";
		String wsName="";
		
		for(int i=0;i<templateType.size();i++)
		{
			String strArr [] = templateType.get(i).split("~");
			wsName=strArr[0];
			tempType=strArr[1];
		if("AO".equalsIgnoreCase(processName))
		{
			if("CSO_Rejects".equalsIgnoreCase(wsName))
			{
				String query ="SELECT TOP(1) Decision FROM usr_0_ao_wihistory with(nolock) WHERE winame='"+WI_NAME+"' and wsname='CSO_Rejects' ORDER BY actiondatetime DESC ";
				String sInputXML=CommonMethods.apSelectWithColumnNames(query, cabinetName, sessionID);
				CPFLog.CPFLogger.debug("Input XML: "+sInputXML);
				String sOutputXML =  null;
				try
				{
					sOutputXML = WFNGExecute(sInputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 1 , CPFLog.CPFLogger);
					CPFLog.CPFLogger.debug("Output XML: "+sOutputXML);
				}
				
				catch (Exception e)
				{
					CPFLog.CPFLogger.error("Exception in checkExistingSession "+e);
					return "";
				}

				String decision=CommonMethods.getTagValues(sOutputXML,"Decision");
				if("Hold".equalsIgnoreCase(decision) && "Pending with Customer".equalsIgnoreCase(tempType))
					break;
				
			}
			else if("Reject".equalsIgnoreCase(wsName))
			{
				String query ="SELECT TOP(1) wsname,ChecklistData FROM usr_0_ao_wihistory with(nolock) WHERE winame='"+WI_NAME+"' AND ChecklistData IS NOT NULL AND CAST(ChecklistData AS NVARCHAR) !='' ORDER BY actiondatetime DESC";
				//SELECT TOP(1) wsname , ChecklistData FROM usr_0_ao_wihistory WHERE winame='AO-0000000787-Process'  AND ChecklistData IS NOT NULL AND CAST(ChecklistData AS NVARCHAR) !='' ORDER BY actiondatetime DESC

				String sInputXML=CommonMethods.apSelectWithColumnNames(query, cabinetName, sessionID);
				CPFLog.CPFLogger.debug("Input XML: "+sInputXML);
				String sOutputXML =  null;
				try
				{
					sOutputXML = WFNGExecute(sInputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 1 , CPFLog.CPFLogger);
					CPFLog.CPFLogger.debug("Output XML: "+sOutputXML);
				}
				
				catch (Exception e)
				{
					CPFLog.CPFLogger.error("Exception in checkExistingSession "+e);
					return "";
				}

				String workstep=CommonMethods.getTagValues(sOutputXML,"wsname");
				String ChecklistData=CommonMethods.getTagValues(sOutputXML,"ChecklistData");
				if(ChecklistData!=null && !"".equalsIgnoreCase(ChecklistData))
				{
					ChecklistData=ChecklistData.replace("#", "','");
					ChecklistData=ChecklistData.replace(":", "','");
					ChecklistData="'"+ChecklistData+"'";
					
					String rejectCategory=getRejectCategory(workstep,ChecklistData);
					
					if(rejectCategory!=null && !"".equalsIgnoreCase(rejectCategory))
					{
						if("POL".equalsIgnoreCase(rejectCategory))
						{
							tempType="Decline due to policy";
						}
						else if("COM".equalsIgnoreCase(rejectCategory))
						{
							tempType="Decline due to Compliance";
						}
						
						break;
					}
					
					
				}
				
				break;
				
			}
			else
			{
				if("Pending with Customer".equalsIgnoreCase(tempType))
				{
					break;
				}
				
			}
		}
		else if("BAIS".equalsIgnoreCase(processName))
		{
			
		}
		
		}		
				return tempType+"~"+wsName;
	}
	public static String getRejectCategory(String workstep,String ChecklistData)
	{
		String RejectCategory="COM";
		List<String> rc=new ArrayList<String>();
		try {
			
			WFXmlList objWorkList=null;
			WFXmlResponse xmlParserData=new WFXmlResponse();
			XMLParser objXMLParser = new XMLParser();
			String Maincode="";
			
			String Query = "SELECT DISTINCT CPFRejectCategory FROM USR_0_AO_Error_Desc_Master with(nolock) WHERE Item_Code IN("+ChecklistData+") AND WSName='"+workstep+"'";
			CPFLog.CPFLogger.info("Query to fetch RejectCategory = "+Query);
			String InputXML = CommonMethods.apSelectWithColumnNames(Query, cabinetName, sessionID);
			CPFLog.CPFLogger.info("Getting RejectCategory of Workitem InputXML = "+InputXML);
			String OutputXML=WFNGExecute(InputXML, jtsIP, jtsPort, 0 , CPFLog.CPFLogger);
			CPFLog.CPFLogger.info("Getting RejectCategory from USR_0_AO_Error_Desc_Master = "+OutputXML);
	
			xmlParserData.setXmlString(OutputXML);
			Maincode=xmlParserData.getVal("MainCode");
			CPFLog.CPFLogger.info("Getting data from USR_0_AO_Error_Desc_Master table Maincode = "+Maincode);
			if(Maincode.equalsIgnoreCase("0"))
			{
				objWorkList = xmlParserData.createList("Records", "Record");
				CPFLog.CPFLogger.info("Getting data from Queueview Maincode = "+objWorkList);
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
				{
					String temp=objWorkList.getVal("CPFRejectCategory");
					
					if(temp!=null && !"".equalsIgnoreCase(temp))
						rc.add(temp);
					
					
				}
				//default due to compliance for policy need to be checked
				if(rc!=null)
				{
					for(int i=0;i<rc.size();i++)
					{
						if("POL".equalsIgnoreCase(rc.get(i)))
						{
							RejectCategory="POL";
							break;
						}
						
					}
				}
			}
			
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				CPFLog.CPFLogger.error("Exception Occurred in get RejectCategory: " + e);
			}
		return RejectCategory;
	}
	protected static String WFNGExecute(String ipXML, String jtsServerIP, String serverPort,
			int flag, Logger ConnectionLogger) throws IOException, Exception
	{
		ConnectionLogger.debug("In WF NG Execute : " + serverPort);
		try
		{
			if (serverPort.startsWith("33"))
				return WFCallBroker.execute(ipXML, jtsServerIP,
						Integer.parseInt(serverPort), 1);
			else
				return ngEjbClientConnection.makeCall(jtsServerIP, serverPort,
						"WebSphere", ipXML);
		}
		catch (Exception e)
		{
			ConnectionLogger.debug("Exception Occured in WF NG Execute : "
					+ e.getMessage());
			e.printStackTrace();
			return "Error";
		}
	}
	
	protected static List<String> executeQuery_AP_Select(String sSQL)
	{

		List<String> values = new ArrayList<>();
		
		try{

			
			WFXmlList objWorkList=null;
			WFXmlResponse xmlParserData=new WFXmlResponse();
			XMLParser objXMLParser = new XMLParser();
			String Maincode="";
			//CPFLog.CPFLogger.info("Query to fetch mail template details = "+sSQL);
			String InputXML = CommonMethods.apSelectWithColumnNames(sSQL, cabinetName, sessionID);
			CPFLog.CPFLogger.info("Getting data of Workitem InputXML = "+InputXML);
			String OutputXML=WFNGExecute(InputXML, jtsIP, jtsPort, 0 , CPFLog.CPFLogger);
			CPFLog.CPFLogger.info("Getting data from Mail template table = "+OutputXML);
	
			xmlParserData.setXmlString(OutputXML);
			Maincode=xmlParserData.getVal("MainCode");
			CPFLog.CPFLogger.info("Getting data from Mail template table table Maincode = "+Maincode);
			if(Maincode.equalsIgnoreCase("0"))
			{
				objWorkList = xmlParserData.createList("Records", "Record");
				//CPFLog.CPFLogger.info("Getting data from Mail template table Maincode = "+objWorkList);
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
				{
					
					values.add(objWorkList.getVal("MailTemplate"));
					values.add(objWorkList.getVal("IsActiveMail"));
					values.add(objWorkList.getVal("DefaultCCMail"));
					values.add(objWorkList.getVal("FromMail"));
					values.add(objWorkList.getVal("SMSEnglishTemplate"));
					values.add(objWorkList.getVal("IsActiveEnglish"));
					values.add(objWorkList.getVal("SMSArabicTemplate"));
					values.add(objWorkList.getVal("isActiveSMSArabic"));
					
				}
				
			}
			
			
		}
		catch (Exception e)
		{
			CPFLog.CPFLogger.info(e.toString());
		}
		
		
		return values;
	
	}
	protected static List<String> executSelectQuery(String sSQL)
	{
		List<String> values = new ArrayList<>();
		Connection conn = null;
		//Statement stmt =null;
		PreparedStatement stmt=null;
		ResultSet result=null;
		try{		
			CPFLog.CPFLogger.info("data Source Name: "+dataSource);
			Context aContext = new InitialContext();
			DataSource aDataSource = (DataSource)aContext.lookup(dataSource);
			conn = (Connection)(aDataSource.getConnection());
			CPFLog.CPFLogger.info("got data source");
			//stmt = conn.createStatement();
			//Changed by Amandeep for CAPS database change
			//String sSQL = "select creditcardno,CARDTYPE,substring(convert(char,EXPIRYDATE,3),4,len(convert(char,EXPIRYDATE,3)))  from capsmain where elitecustomerno=(select elitecustomerno from capsmain where creditcardno='?') and  substring(CREDITCARDNO,14,1)!='0' ";
			CPFLog.CPFLogger.info("Execute Query..."+sSQL);
			stmt = conn.prepareStatement(sSQL);
			//stmt.setString(1,CrdtCN);
			result = stmt.executeQuery();
			
			if(result!=null)
			{
				ResultSetMetaData meta = result.getMetaData();
				int colCount = meta.getColumnCount();
				while (result.next())
				{
				    for (int col=1; col <= colCount; col++) 
				    {
				        Object value = result.getObject(col);
				        if (value != null) 
				        {
				        	values.add(value.toString());
				        }
				    }
				}
			}
			if(result != null)
			{
				result.close();
				result=null;
				CPFLog.CPFLogger.info("resultset Successfully closed"); 
			}
			if(stmt != null)
			{
				stmt.close();
				stmt=null;						
				CPFLog.CPFLogger.info("Stmt Successfully closed"); 
			}
			if(conn != null)
			{
				conn.close();
				conn=null;	
				CPFLog.CPFLogger.info("Conn Successfully closed"); 
			}
		}
		catch (Exception e)
		{
			CPFLog.CPFLogger.info(e.toString());
		}
		finally
		{
			if(result != null)
			{
				try {
					result.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result=null;
				CPFLog.CPFLogger.info("resultset Successfully closed"); 
			}
			if(stmt != null)
			{
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stmt=null;						
				CPFLog.CPFLogger.info("Stmt Successfully closed"); 
			}
			if(conn != null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn=null;	
				CPFLog.CPFLogger.info("Conn Successfully closed"); 
			}
		}
		
		return values;
	}
	
}
