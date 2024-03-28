package com.newgen.CPF;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.wfdesktop.xmlapi.WFInputXml;

public class CPFCommunication {


	public static int sessionCheckInt=0;

	public static int loopCount=50;
	public static int waitLoop=50;
	
	
	public static void sendMail(String fromMailID, String toMailID,String mailCC, String cabinetName, String sessionId,String MailStr,String ProcessDefID,String activityId,String stage,String WI_NAME,String wsName,String processName,String dataSource,String category )throws Exception
	{
		XMLParser objXMLParser = new XMLParser();
		String sInputXML="";
		String sOutputXML="";
		String mainCodeforAPInsert=null;
		String insertQuery="";
		int result=0;
		String mailSubject = stage+" Communication";
		
		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			try
			{
				
				
				
				String columnName = "mailFrom,mailTo,mailCC,mailSubject,mailMessage,mailContentType,mailPriority,mailStatus,mailActionType,insertedTime,processDefId,processInstanceId,workitemId,activityId,noOfTrials,zipFlag";
				String strValues = "'"+fromMailID+"','"+toMailID+"','"+mailCC+"','"+mailSubject+"',N'"+MailStr+"','text/html;charset=UTF-8','1','N','TRIGGER','"+CommonMethods.getCurrentDateInSQLFormat()+"','"+ProcessDefID+"','"+WI_NAME+"','1','"+activityId+"','0','N'";
				//insertQuery="Insert into WFMAILQUEUETABLE ("+columnName+" ) values ("+strValues+")";
				//result = updateQuery(insertQuery,dataSource);
				sInputXML = "<?xml version=\"1.0\"?>" +
						"<APInsert_Input>" +
						"<Option>APInsert</Option>" +
						"<TableName>WFMAILQUEUETABLE</TableName>" +
						"<ColName>" + columnName + "</ColName>" +
						"<Values>" + strValues + "</Values>" +
						"<EngineName>" + cabinetName + "</EngineName>" +
						"<SessionId>" + sessionId + "</SessionId>" +
						"</APInsert_Input>";

				CPFLog.CPFLogger.info("Mail Insert InputXml::::::::::\n"+sInputXML);
				sOutputXML =CPFMain.WFNGExecute(sInputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 0 , CPFLog.CPFLogger);
				CPFLog.CPFLogger.info("Mail Insert OutputXml::::::::::\n"+sOutputXML);
				objXMLParser.setInputXML(sOutputXML);
				mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				CPFLog.CPFLogger.error("Exception in Sending mail", e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				continue;
			}
			if (mainCodeforAPInsert.equalsIgnoreCase("11")) 
			{
				CPFLog.CPFLogger.info("Invalid session in Sending mail");
				sessionCheckInt++;
				//ThreadConnect.sessionId = ThreadConnect.getSessionID(cabinetName, jtsIP, jtsPort, userName,password);
				CommonConnection.getSessionID(CPFLog.CPFLogger, true);
				continue;
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}
		if(mainCodeforAPInsert.equalsIgnoreCase("0"))//if(result>0)
		{
			CPFLog.CPFLogger.info("mail Insert Successful");
			UpdateCommFlag(WI_NAME,wsName,stage,processName,sessionId,"Email","'Done'");
			InsertRecordInCommHistory(WI_NAME,wsName,stage,processName,sessionId,"Email","Done",fromMailID,toMailID,mailCC,category,mailSubject,MailStr);
		}
		else
		{
			CPFLog.CPFLogger.info("mail Insert Unsuccessful");
		}
	}
	public static void sendSMS(String Mobile_No, String Alert_Text, String cabinetName, String sessionId,String ProcessDefID,String wsname,String stage,String WI_NAME,String processName,String dataSource ,String category)throws Exception
	{
		XMLParser objXMLParser = new XMLParser();
		String sInputXML="";
		String sOutputXML="";
		String mainCodeforAPInsert=null;
		String AlertName="CPFCommunication";
		String AlertCode="";
		String AlertStatus="";
		String insertQuery="";
		int result=0;
		String Alert_Subject = stage+" Communication";
		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			try
			{
				
				
				
				String columnName = "Alert_Name,Alert_Code,ALert_Status,Mobile_No,Alert_Text,Alert_Subject,WI_Name,Workstep_Name,Inserted_Date";
				String strValues = "'"+AlertName+"','"+AlertCode+"','"+AlertStatus+"','"+Mobile_No+"',N'"+Alert_Text+"','"+Alert_Subject+"','"+WI_NAME+"','"+wsname+"','"+CommonMethods.getCurrentDateInSQLFormat()+"'";
				//insertQuery="Insert into USR_0_BPM_SMSQUEUETABLE ("+columnName+" ) values ("+strValues+")";
				//result = updateQuery(insertQuery,dataSource);
				sInputXML = "<?xml version=\"1.0\"?>" +
						"<APInsert_Input>" +
						"<Option>APInsert</Option>" +
						"<TableName>USR_0_BPM_SMSQUEUETABLE</TableName>" +
						"<ColName>" + columnName + "</ColName>" +
						"<Values>" + strValues + "</Values>" +
						"<EngineName>" + cabinetName + "</EngineName>" +
						"<SessionId>" + sessionId + "</SessionId>" +
						"</APInsert_Input>";

				CPFLog.CPFLogger.info("SMS Insert InputXml::::::::::\n"+sInputXML);
				sOutputXML =CPFMain.WFNGExecute(sInputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 0 , CPFLog.CPFLogger);
				CPFLog.CPFLogger.info("SMS Insert OutputXml::::::::::\n"+sOutputXML);
				objXMLParser.setInputXML(sOutputXML);
				mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				CPFLog.CPFLogger.error("Exception in Sending SMS", e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				continue;
			}
			if (mainCodeforAPInsert.equalsIgnoreCase("11")) 
			{
				CPFLog.CPFLogger.info("Invalid session in Sending SMS");
				sessionCheckInt++;
				CommonConnection.getSessionID(CPFLog.CPFLogger, true);
				continue;
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}
		if(mainCodeforAPInsert.equalsIgnoreCase("0"))//if(result>0)
		{
			CPFLog.CPFLogger.info("SMS Insert Successful");
			UpdateCommFlag(WI_NAME,wsname,stage,processName,sessionId,"SMS","'Done'");
			InsertRecordInCommHistory(WI_NAME,wsname,stage,processName,sessionId,"SMS","Done","NA",Mobile_No,"NA",category,Alert_Subject,Alert_Text);
		}
		else
		{
			CPFLog.CPFLogger.info("SMS Insert Unsuccessful");
		}
	}
	
	public static void UpdateCommFlag(String WI_NAME,String wsname,String stage,String processName, String sessionId,String commType,String value)
	{
		String exttableName="";
		String columnname="CPF"+stage+"Status";
		String sWhere="";
		try {
			columnname="CPF"+stage+"Status";
			switch(processName)
			{
			case "AO":
			{
				exttableName="RB_AO_EXTTABLE";
				sWhere="WI_NAME = '"+WI_NAME+"'";
				break;
				
			}
			case "BAIS":
			{
				exttableName="RB_BAIS_EXTTABLE";
				sWhere="WI_NAME = '"+WI_NAME+"'";
				break;
			}
			}
			
			ExecuteQuery_APUpdate(exttableName,columnname,value,sWhere,sessionId);
			
		}
		catch(Exception e)
		{
			CPFLog.CPFLogger.error("Exception in UpdateCommFlag- "+e.toString());
		}
	}
	public static void InsertRecordInCommHistory(String WI_NAME,String wsname,String stage,String processName, String sessionId,String commType,String value,String fromMailID,String toMailID,String mailCC,String category,String sub,String content)
	{
		String historyTable="";
		
		try {
			switch(processName)
			{
			case "AO":
			{
				historyTable="USR_0_CPF_AO_COMM_HISTORY_GRID";
				break;
				
			}
			case "BAIS":
			{
				historyTable="";
				break;
			}
			}
			historyCaller(CommonConnection.getCabinetName(),sessionId,WI_NAME,historyTable,wsname,value,commType,toMailID,mailCC,fromMailID,category,sub,content);
			
		}
		catch(Exception e)
		{
			CPFLog.CPFLogger.error("Exception in UpdateCommFlag- "+e.toString());
		}
	}
	
	
	
	public static void ExecuteQuery_APUpdate(String tablename, String columnname,String sMessage, String sWhere,String sessionId) throws ParserConfigurationException, SAXException, IOException
	{
		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			try
			{
				XMLParser objXMLParser = new XMLParser();
				String inputXmlcheckAPUpdate =ExecuteQuery_APUpdate(tablename,columnname,sMessage,sWhere,CommonConnection.getCabinetName(),sessionId);
				CPFLog.CPFLogger.debug("inputXmlcheckAPUpdate : " + inputXmlcheckAPUpdate);
				String outXmlCheckAPUpdate=null;
				outXmlCheckAPUpdate=CPFMain.WFNGExecute(inputXmlcheckAPUpdate, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 0 , CPFLog.CPFLogger);//CommonMethods.WFNGExecute(inputXmlcheckAPUpdate,jtsIP, jtsPort, 1);//ThreadConnect.WFNGExecutePD(inputXmlcheckAPUpdate,jtsIP,Integer.parseInt(jtsPort),1);
				CPFLog.CPFLogger.info("outXmlCheckAPUpdate : " + outXmlCheckAPUpdate);
				objXMLParser.setInputXML(outXmlCheckAPUpdate);
				String mainCodeforCheckUpdate = null;
				mainCodeforCheckUpdate=objXMLParser.getValueOf("MainCode");
				if (!mainCodeforCheckUpdate.equalsIgnoreCase("0"))
				{
					CPFLog.CPFLogger.error("Exception in ExecuteQuery_APUpdate updating the table");
				}
				else
				{
					CPFLog.CPFLogger.error("Successfully updated table");
					
				}
				int mainCode=Integer.parseInt(mainCodeforCheckUpdate);
				if (mainCode == 11)
				{
					CommonConnection.getSessionID(CPFLog.CPFLogger, true);
				}
				else
				{
					sessionCheckInt++;
					break;
				}
			}
			catch(Exception e)
			{
				CPFLog.CPFLogger.error("Inside create ExecuteQuery_APUpdate exception"+e);
			}
		}
	}
	public static String ExecuteQuery_APUpdate(String tableName,String columnName,String strValues,String sWhere,String cabinetName,String sessionId)
	{
		
		System.out.println("inside ExecuteQuery_APUpdate");
		WFInputXml wfInputXml = new WFInputXml();
		if(strValues==null)
		{
			strValues = "''";
		}
		wfInputXml.appendStartCallName("APUpdate", "Input");
		wfInputXml.appendTagAndValue("TableName",tableName);
		wfInputXml.appendTagAndValue("ColName",columnName);
		wfInputXml.appendTagAndValue("Values",strValues);
		wfInputXml.appendTagAndValue("WhereClause",sWhere);
		wfInputXml.appendTagAndValue("EngineName",cabinetName);
		wfInputXml.appendTagAndValue("SessionId",sessionId);
		wfInputXml.appendEndCallName("APUpdate","Input");
		System.out.println("wfInputXml.toString()-------"+wfInputXml.toString());
		return wfInputXml.toString();
	}
	
	public static void historyCaller(String cabinetName, String sessionId,String wiName,String hist_table, String WSNAME,String status,String commType,String RECIPIENT,String COPIED_IDs,String FROM_ID,String category,String sub,String content)throws Exception
	{
		XMLParser objXMLParser = new XMLParser();
		String sInputXML="";
		String sOutputXML="";
		String mainCodeforAPInsert=null;
		String remarks="";
		
		String wsname="";
		
		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			try
			{
				if(wiName!=null)
				{
					
					String colName="WI_NAME,EMAIL_OR_SMS,SENT_DATE,RECIPIENT,COPIED_IDs,FROM_ID,DELIVERY_STATUS,CATEGORY,WSNAME,REMARKS,COMM_SUBJECT,COMM_CONTENT";
					
	                String values="'"+wiName+"','"+commType+"',getDate(),'"+RECIPIENT+"','"+COPIED_IDs+"','"+FROM_ID+"','"+status+"','"+category+"','"+wsname+"','"+remarks+"','"+sub+"',N'"+content+"'";
					
	                //CPFLog.CPFLogger.info("updated  USR_0_FPU_WIHISTORY for : "+wiName);

					//CPFLog.CPFLogger.info("Values for history : \n"+values);

					sInputXML = CommonMethods.apInsert(cabinetName, sessionId, colName, values, hist_table);

					CPFLog.CPFLogger.info("History_InputXml::::::::::\n"+sInputXML);
					sOutputXML=CPFMain.WFNGExecute(sInputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 0 , CPFLog.CPFLogger);// CommonMethods.WFNGExecute(sInputXML,jtsIP, jtsPort, 0);
					CPFLog.CPFLogger.info("History_OutputXml::::::::::\n"+sOutputXML);
					objXMLParser.setInputXML(sOutputXML);
					mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");

				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				CPFLog.CPFLogger.error("Exception in historyCaller of UpdateExpiryDate", e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				continue;
			}
			if (mainCodeforAPInsert.equalsIgnoreCase("11")) 
			{
				CPFLog.CPFLogger.info("Invalid session in historyCaller of UpdateExpiryDate");
				sessionCheckInt++;
				CommonConnection.getSessionID(CPFLog.CPFLogger, true);
				continue;
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}
		if(mainCodeforAPInsert.equalsIgnoreCase("0"))
		{
			CPFLog.CPFLogger.info("Insert Successful");
		}
		else
		{
			CPFLog.CPFLogger.info("Insert Unsuccessful");
		}
	}
	
	public static void waiteloopExecute(long wtime) 
	{
		try 
		{
			for (int i = 0; i < 10; i++) 
			{
				Thread.yield();
				Thread.sleep(wtime / 10);
			}
		} 
		catch (InterruptedException e) 
		{
		}
	}
	
	public static int updateQuery(String insertQuery,String dataSource )
	{

		int values =0;
		Connection conn = null;
		//Statement stmt =null;
		PreparedStatement stmt=null;
		try{			
			Context aContext = new InitialContext();
			DataSource aDataSource = (DataSource)aContext.lookup(dataSource);
			conn = (Connection)(aDataSource.getConnection());
			CPFLog.CPFLogger.info("got data source");
			//stmt = conn.createStatement();
			//Changed by Amandeep for CAPS database change
			//String sSQL = "select creditcardno,CARDTYPE,substring(convert(char,EXPIRYDATE,3),4,len(convert(char,EXPIRYDATE,3)))  from capsmain where elitecustomerno=(select elitecustomerno from capsmain where creditcardno='?') and  substring(CREDITCARDNO,14,1)!='0' ";
			CPFLog.CPFLogger.info("Execute Query..."+insertQuery);
			stmt = conn.prepareStatement(insertQuery);
			//stmt.setString(1,CrdtCN);
			values = stmt.executeUpdate();
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
