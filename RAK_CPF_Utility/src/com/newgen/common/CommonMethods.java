/*
---------------------------------------------------------------------------------------------------------
                  NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group                   : Application - Projects
Project/Product			: RAK BPM
Application				: RAK BPM Utility
Module					: Common
File Name				: CommonMethods.java
Author 					: Sakshi Grover
Date (DD/MM/YYYY)		: 30/04/2019

---------------------------------------------------------------------------------------------------------
                 	CHANGE HISTORY
---------------------------------------------------------------------------------------------------------

Problem No/CR No        Change Date           Changed By             Change Description
---------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------
*/


package com.newgen.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;

import com.itextpdf.text.pdf.PdfReader;

import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.omni.wf.util.excp.NGException;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;


public class CommonMethods
{
	private static NGEjbClient ngEjbClientiRBLStatus;
	
	static
	{
	  try
      {
		  ngEjbClientiRBLStatus = NGEjbClient.getSharedInstance();
      }
    catch (NGException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
	}
	
	public static String connectCabinetInput(String cabinetName, String username, String password)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<WMConnect_Input>\n");
		ipXMLBuffer.append("<Option>WMConnect</Option>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<Participant>\n");
		ipXMLBuffer.append("<Name>");
		ipXMLBuffer.append(username);
		ipXMLBuffer.append("</Name>\n");
		ipXMLBuffer.append("<Password>");
		ipXMLBuffer.append(password);
		ipXMLBuffer.append("</Password>\n");
		ipXMLBuffer.append("<Scope></Scope>\n");
		ipXMLBuffer.append("<UserExist>N</UserExist>\n");
		ipXMLBuffer.append("<Locale>en-us</Locale>\n");
		ipXMLBuffer.append("<ParticipantType>U</ParticipantType>\n");
		ipXMLBuffer.append("</Particpant>\n");
		ipXMLBuffer.append("</WMConnect_Input>");

		return ipXMLBuffer.toString();

	}

	public static String disconnectCabinetInput(String cabinetName,String sessionID)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<NGODisconnectCabinet_Input>\n");
		ipXMLBuffer.append("<Option>NGODisconnectCabinet</Option>\n");
		ipXMLBuffer.append("<CabinetName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</CabinetName>\n");
		ipXMLBuffer.append("<UserDBId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</UserDBId>\n");
		ipXMLBuffer.append("</NGODisconnectCabinet_Input>");

		return ipXMLBuffer.toString();
	}

	public static String fetchWorkItemsInput(String cabinetName,String sessionID, String queueID )
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<WMFetchWorkItems_Input>\n");
		ipXMLBuffer.append("<Option>WMFetchWorkItem</Option>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionID>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionID>\n");
		ipXMLBuffer.append("<QueueId>");
		ipXMLBuffer.append(queueID);
		ipXMLBuffer.append("</QueueId>\n");
		ipXMLBuffer.append("<BatchInfo>\n");
		ipXMLBuffer.append("<NoOfRecordsToFetch>100</NoOfRecordsToFetch>\n");
		ipXMLBuffer.append("<LastWorkItem></LastWorkItem>\n");
		ipXMLBuffer.append("<LastValue></LastValue>\n");
		ipXMLBuffer.append("<LastProcessInstance></LastProcessInstance>\n");
		ipXMLBuffer.append("</BatchInfo>\n");
		ipXMLBuffer.append("</WMFetchWorkItems_Input>\n");
		return ipXMLBuffer.toString();

	}

	public static String apSelectWithColumnNames(String QueryString, String cabinetName, String sessionID)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<APSelect_Input>\n");
		ipXMLBuffer.append("<Option>APSelectWithColumnNames</Option>\n");
		ipXMLBuffer.append("<Query>");
		ipXMLBuffer.append(QueryString);
		ipXMLBuffer.append("</Query>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("</APSelect_Input>");

		return ipXMLBuffer.toString();
	}
	public static String apUpdateInput(String cabinetName,String sessionID, String tableName, String columnName,
			 String strValues,String sWhereClause)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<APUpdate_Input>\n");
		ipXMLBuffer.append("<Option>APUpdate</Option>\n");
		ipXMLBuffer.append("<TableName>");
		ipXMLBuffer.append(tableName);
		ipXMLBuffer.append("</TableName>\n");
		ipXMLBuffer.append("<ColName>");
		ipXMLBuffer.append(columnName);
		ipXMLBuffer.append("</ColName>\n");
		ipXMLBuffer.append("<Values>");
		ipXMLBuffer.append(strValues);
		ipXMLBuffer.append("</Values>\n");
		ipXMLBuffer.append("<WhereClause>");
		ipXMLBuffer.append(sWhereClause);
		ipXMLBuffer.append("</WhereClause>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("</APUpdate_Input>");

		return ipXMLBuffer.toString();

	 }
	
	public static String apDeleteInput(String cabinetName,String sessionID, String tableName, String sWhere)
	{ 	
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<APDelete_Input>\n");
		ipXMLBuffer.append("<Option>APDelete</Option>\n");
		ipXMLBuffer.append("<TableName>");
		ipXMLBuffer.append(tableName);
		ipXMLBuffer.append("</TableName>\n");
		ipXMLBuffer.append("<WhereClause>");
		ipXMLBuffer.append(sWhere);
		ipXMLBuffer.append("</WhereClause>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("</APDelete_Input>");

		return ipXMLBuffer.toString();
	}

	public static String assignWorkitemAttributeInput(String sCabinetName,String sessionID, String workItemName, String WorkItemID, String attributesTag)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<WMAssignWorkItemAttributes_Input>\n");
		ipXMLBuffer.append("<Option>WMAssignWorkItemAttributes</Option>");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(sCabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("<ProcessInstanceId>");
		ipXMLBuffer.append(workItemName);
		ipXMLBuffer.append("</ProcessInstanceId>\n");
		ipXMLBuffer.append("<WorkItemId>");
		ipXMLBuffer.append(WorkItemID);
		ipXMLBuffer.append("</WorkItemId>\n");
		ipXMLBuffer.append("<ActivityId>1</ActivityId>\n");
		ipXMLBuffer.append("<LastModifiedTime></LastModifiedTime>\n");
		ipXMLBuffer.append("<ActivityType>1</ActivityType>\n");
		ipXMLBuffer.append("<UserDefVarFlag>Y</UserDefVarFlag>\n");
		ipXMLBuffer.append("<Attributes>");
		ipXMLBuffer.append(attributesTag);
		ipXMLBuffer.append("</Attributes>\n");
		ipXMLBuffer.append("</WMAssignWorkItemAttributes_Input>");

		return ipXMLBuffer.toString();

	}
	public static String getWorkItemInput(String sCabinetName, String sessionID, String workItemName, String WorkItemID)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<WMGetWorkItem_Input>\n");
		ipXMLBuffer.append("<Option>WMGetWorkItem</Option>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(sCabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("<ProcessInstanceId>");
		ipXMLBuffer.append(workItemName);
		ipXMLBuffer.append("</ProcessInstanceId>\n");
		ipXMLBuffer.append("<WorkItemId>");
		ipXMLBuffer.append(WorkItemID);
		ipXMLBuffer.append("</WorkItemId>\n");
		ipXMLBuffer.append("</WMGetWorkItem_Input>");

		return ipXMLBuffer.toString();
	}	
	public static String getWorkitemDataExtInput(String sCabinetName, String sessionID, String workItemName, String WorkItemID)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<WFGetWorkitemDataExt_Input>\n");
		ipXMLBuffer.append("<Option>WFGetWorkitemDataExt</Option>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(sCabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("<ProcessInstanceId>");
		ipXMLBuffer.append(workItemName);
		ipXMLBuffer.append("</ProcessInstanceId>\n");
		ipXMLBuffer.append("<WorkItemId>");
		ipXMLBuffer.append(WorkItemID);
		ipXMLBuffer.append("</WorkItemId>\n");
		ipXMLBuffer.append("</WFGetWorkitemDataExt_Input>");

		return ipXMLBuffer.toString();
	}
	public static String completeWorkItemInput(String cabName, String sessionID, String workItemName, String WorkItemID){

		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<WMCompleteWorkItem_Input>\n");
		ipXMLBuffer.append("<Option>WMCompleteWorkItem</Option>");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("<ProcessInstanceId>");
		ipXMLBuffer.append(workItemName);
		ipXMLBuffer.append("</ProcessInstanceId>\n");
		ipXMLBuffer.append("<WorkItemId>");
		ipXMLBuffer.append(WorkItemID);
		ipXMLBuffer.append("</WorkItemId>\n");
		ipXMLBuffer.append("<AuditStatus></AuditStatus>\n");
		ipXMLBuffer.append("<Comments></Comments>\n");
		ipXMLBuffer.append("</WMCompleteWorkItem_Input>");

		return ipXMLBuffer.toString();
	}

	public static String apInsert(String sCabName, String sSessionId, String colNames, String colValues, String tableName)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<APInsertExtd_Input>\n");
		ipXMLBuffer.append("<Option>APInsert</Option>");
		ipXMLBuffer.append("<TableName>");
		ipXMLBuffer.append(tableName);
		ipXMLBuffer.append("</TableName>");
		ipXMLBuffer.append("<ColName>");
		ipXMLBuffer.append(colNames);
		ipXMLBuffer.append("</ColName>\n");
		ipXMLBuffer.append("<Values>");
		ipXMLBuffer.append(colValues);
		ipXMLBuffer.append("</Values>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(sCabName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sSessionId);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("</APInsertExtd_Input>");

		return ipXMLBuffer.toString();
	}
	
	public static String getCurrentDateInSQLFormat()
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(new Date());
	}

	public static String getAPUpdateIpXML(String tableName,String columnName,String strValues,String sWhere,String cabinetName,String sessionId)
	{
		if(strValues==null)
		{
			strValues = "''";
		}

		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<APUpdate_Input>\n");
		ipXMLBuffer.append("<Option>APUpdate</Option>");
		ipXMLBuffer.append("<TableName>");
		ipXMLBuffer.append(tableName);
		ipXMLBuffer.append("</TableName>\n");
		ipXMLBuffer.append("<ColName>");
		ipXMLBuffer.append(columnName);
		ipXMLBuffer.append("</ColName>\n");
		ipXMLBuffer.append("<Values>");
		ipXMLBuffer.append(strValues);
		ipXMLBuffer.append("</Values>\n");
		ipXMLBuffer.append("<WhereClause>");
		ipXMLBuffer.append(sWhere);
		ipXMLBuffer.append("</WhereClause>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionId);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("</APUpdate_Input>\n");

		return ipXMLBuffer.toString();
	}

	public static String getFetchWorkItemAttributesXML(String sCabinetName,String sessionID, String workItemName, String WorkItemID)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<WMFetchWorkItemAttributes_Input>\n");
		ipXMLBuffer.append("<Option>WMFetchWorkItemAttributes</Option>");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(sCabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("<ProcessInstanceId>");
		ipXMLBuffer.append(workItemName);
		ipXMLBuffer.append("</ProcessInstanceId>\n");
		ipXMLBuffer.append("<WorkItemId>");
		ipXMLBuffer.append(WorkItemID);
		ipXMLBuffer.append("</WorkItemId>\n");
		ipXMLBuffer.append("</WMFetchWorkItemAttributes_Input>");


		return ipXMLBuffer.toString();

	}

	public static String getFetchWorkItemsInputXML(String processInstanceId, String lastWorkItemId,  String sessionId, String cabinetName, String queueId)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<WMFetchWorkItems_Input>\n");
		ipXMLBuffer.append("<Option>WMFetchWorkItem</Option>");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionID>");
		ipXMLBuffer.append(sessionId);
		ipXMLBuffer.append("</SessionID>\n");
		ipXMLBuffer.append("<QueueId>");
		ipXMLBuffer.append(queueId);
		ipXMLBuffer.append("</QueueId>\n");
		ipXMLBuffer.append("<BatchInfo>\n");
		ipXMLBuffer.append("<NoOfRecordsToFetch>100</NoOfRecordsToFetch>\n");
		ipXMLBuffer.append("<LastWorkItem>");
		ipXMLBuffer.append(lastWorkItemId);
		ipXMLBuffer.append("</LastWorkItem>\n");
		ipXMLBuffer.append("<LastValue></LastValue>\n");
		ipXMLBuffer.append("<LastProcessInstance>");
		ipXMLBuffer.append(processInstanceId);
		ipXMLBuffer.append("</LastProcessInstance>\n");
		ipXMLBuffer.append("</BatchInfo>\n");
		ipXMLBuffer.append("</WMFetchWorkItems_Input>");

		return ipXMLBuffer.toString();
	}

	public static String NGOSearchDocument(String DataDefIndex,String IndexId,
			String IndexValue, String cabinetName, String sessionId)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<NGOSearchDocumentExt_Input>\n");
		ipXMLBuffer.append("<Option>NGOSearchDocumentExt</Option>\n");
		ipXMLBuffer.append("<CabinetName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</CabinetName>\n");
		ipXMLBuffer.append("<UserDBId>");
		ipXMLBuffer.append(sessionId);
		ipXMLBuffer.append("</UserDBId>\n");
		ipXMLBuffer.append("<Rank>Y</Rank>\n");
		ipXMLBuffer.append("<SearchOnPreviousVersions>N</SearchOnPreviousVersions>\n");
		ipXMLBuffer.append("<LookInFolder>0</LookInFolder>\n");
		ipXMLBuffer.append("<IncludeSubFolder>Y</IncludeSubFolder>\n");
		ipXMLBuffer.append("<DataDefCriterion>\n");
		ipXMLBuffer.append("<DataDefCriteria>\n");
		ipXMLBuffer.append("<DataDefIndex>");
		ipXMLBuffer.append(DataDefIndex);
		ipXMLBuffer.append("</DataDefIndex>\n");
		ipXMLBuffer.append("<IndexId>");
		ipXMLBuffer.append(IndexId);
		ipXMLBuffer.append("</IndexId>\n");
		ipXMLBuffer.append("<Operator>=</Operator>\n");
		ipXMLBuffer.append("<FieldSecureFlag>N</FieldSecureFlag>\n");
		ipXMLBuffer.append("<IndexValue>");
		ipXMLBuffer.append(IndexValue);
		ipXMLBuffer.append("</IndexValue>\n");
		ipXMLBuffer.append("<IndexType>S</IndexType>\n");
		ipXMLBuffer.append("<JoinCondition>AND</JoinCondition>\n");
		ipXMLBuffer.append("</DataDefCriteria>\n");
		ipXMLBuffer.append("</DataDefCriterion>\n");
		ipXMLBuffer.append("<SearchScope>0</SearchScope>\n");
		ipXMLBuffer.append("<SearchOnAlias>N</SearchOnAlias>\n");
		ipXMLBuffer.append("<ReferenceFlag>B</ReferenceFlag>\n");
		ipXMLBuffer.append("<SortOrder>A</SortOrder>\n");
		ipXMLBuffer.append("<GroupIndex>0</GroupIndex>\n");
		ipXMLBuffer.append("<StartFrom>1</StartFrom>\n");
		ipXMLBuffer.append("<NoOfRecordsToFetch>10000</NoOfRecordsToFetch>\n");
		ipXMLBuffer.append("<OrderBy>2</OrderBy>\n");
		ipXMLBuffer.append("<MaximumHitCountFlag>N</MaximumHitCountFlag>\n");
		ipXMLBuffer.append("<DataAlsoFlag>Y</DataAlsoFlag>\n");
		ipXMLBuffer.append("<AnnotationFlag>Y</AnnotationFlag>\n");
		ipXMLBuffer.append("<LinkDocFlag>Y</LinkDocFlag>\n");
		ipXMLBuffer.append("<PrevDocIndex>0</PrevDocIndex>\n");
		ipXMLBuffer.append("<IncludeSystemFolder>NN</IncludeSystemFolder>\n");
		ipXMLBuffer.append("<ReportFlag>N</ReportFlag>\n");
		ipXMLBuffer.append("<ThesaurusFlag>N</ThesaurusFlag>\n");
		ipXMLBuffer.append("</NGOSearchDocumentExt_Input>\n");
		return ipXMLBuffer.toString();
	}
	
	public static String getNGOAddDocument(String parentFolderIndex, String strDocumentName,String DocumentType,String strExtension,
			String sISIndex,String lstrDocFileSize, String volumeID, String cabinetName, String sessionId)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<NGOAddDocument_Input>\n");
		ipXMLBuffer.append("<Option>NGOAddDocument</Option>");
		ipXMLBuffer.append("<CabinetName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</CabinetName>\n");
		ipXMLBuffer.append("<UserDBId>");
		ipXMLBuffer.append(sessionId);
		ipXMLBuffer.append("</UserDBId>\n");
		ipXMLBuffer.append("<GroupIndex>0</GroupIndex>\n");
		ipXMLBuffer.append("<Document>\n");
		ipXMLBuffer.append("<VersionFlag>Y</VersionFlag>\n");
		ipXMLBuffer.append("<ParentFolderIndex>");
		ipXMLBuffer.append(parentFolderIndex);
		ipXMLBuffer.append("</ParentFolderIndex>\n");
		ipXMLBuffer.append("<DocumentName>");
		ipXMLBuffer.append(strDocumentName);
		ipXMLBuffer.append("</DocumentName>\n");
		ipXMLBuffer.append("<VolumeIndex>");
		ipXMLBuffer.append(volumeID);
		ipXMLBuffer.append("</VolumeIndex>\n");
		ipXMLBuffer.append("<ISIndex>");
		ipXMLBuffer.append(sISIndex);
		ipXMLBuffer.append("</ISIndex>\n");
		ipXMLBuffer.append("<NoOfPages>1</NoOfPages>\n");
		ipXMLBuffer.append("<DocumentType>");
		ipXMLBuffer.append(DocumentType);
		ipXMLBuffer.append("</DocumentType>\n");
		ipXMLBuffer.append("<DocumentSize>");
		ipXMLBuffer.append(lstrDocFileSize);
		ipXMLBuffer.append("</DocumentSize>\n");
		ipXMLBuffer.append("<CreatedByAppName>");
		ipXMLBuffer.append(strExtension);
		ipXMLBuffer.append("</CreatedByAppName>\n");
		ipXMLBuffer.append("</Document>\n");
		ipXMLBuffer.append("</NGOAddDocument_Input>\n");
		return ipXMLBuffer.toString();
    }

	public static String getNGOChangeDocument(String strDocumentIndex, String strDocumentName,String DocumentType,String strExtension,
			String sISIndex,String lstrDocFileSize, String volumeID, String cabinetName, String sessionId, String strCurrDateTime)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<NGOChangeDocumentProperty_Input>\n");
		ipXMLBuffer.append("<Option>NGOChangeDocumentProperty</Option>");
		ipXMLBuffer.append("<CabinetName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</CabinetName>\n");
		ipXMLBuffer.append("<UserDBId>");
		ipXMLBuffer.append(sessionId);
		ipXMLBuffer.append("</UserDBId>\n");
		ipXMLBuffer.append("<Document>\n");
		ipXMLBuffer.append("<DocumentIndex>");
		ipXMLBuffer.append(strDocumentIndex);
		ipXMLBuffer.append("</DocumentIndex>\n");
		ipXMLBuffer.append("<NoOfPages>1</NoOfPages>\n");
		ipXMLBuffer.append("<DocumentName>");
		ipXMLBuffer.append(strDocumentName);
		ipXMLBuffer.append("</DocumentName>\n");
		ipXMLBuffer.append("<AccessDateTime>");
		ipXMLBuffer.append(strCurrDateTime);
		ipXMLBuffer.append("</AccessDateTime>\n");
		ipXMLBuffer.append("<ExpiryDateTime>2099-12-12 0:0:0.0</ExpiryDateTime>\n");
		ipXMLBuffer.append("<CreatedByAppName>");
		ipXMLBuffer.append(strExtension);
		ipXMLBuffer.append("</CreatedByAppName>\n");
		ipXMLBuffer.append("<VersionFlag>N</VersionFlag>\n");
		ipXMLBuffer.append("<AccessType>S</AccessType>\n");
		ipXMLBuffer.append("<ISIndex>");
		ipXMLBuffer.append(sISIndex);
		ipXMLBuffer.append("</ISIndex>\n");
		ipXMLBuffer.append("<TextISIndex>0#0#</TextISIndex>\n");
		ipXMLBuffer.append("<DocumentType>");
		ipXMLBuffer.append(DocumentType);
		ipXMLBuffer.append("</DocumentType>\n");
		ipXMLBuffer.append("<DocumentSize>");
		ipXMLBuffer.append(lstrDocFileSize);
		ipXMLBuffer.append("</DocumentSize>\n");
		ipXMLBuffer.append("<Comment>");
		ipXMLBuffer.append(strExtension);
		ipXMLBuffer.append("</Comment>\n");
		ipXMLBuffer.append("<RetainAnnotation>N</RetainAnnotation>\n");
		ipXMLBuffer.append("</Document>\n");
		ipXMLBuffer.append("</NGOChangeDocumentProperty_Input>\n");
		return ipXMLBuffer.toString();
	}
	
	public static String getTagValues (String sXML, String sTagName)
	{
		String sTagValues = "";
		String sStartTag = "<" + sTagName + ">";
		String sEndTag = "</" + sTagName + ">";
		String tempXML = sXML;
	    try
	    {
			for(int i=0;i<sXML.split(sEndTag).length;i++)
			{
				if(tempXML.indexOf(sStartTag) != -1)
				{
					sTagValues += tempXML.substring(tempXML.indexOf(sStartTag) + sStartTag.length(), tempXML.indexOf(sEndTag));
					//System.//out.println("sTagValues"+sTagValues);
					tempXML=tempXML.substring(tempXML.indexOf(sEndTag) + sEndTag.length(), tempXML.length());
		        }
				if(tempXML.indexOf(sStartTag) != -1)
				{
					sTagValues +="`";
					//System.//out.println("sTagValues"+sTagValues);

				}
				//System.//out.println("sTagValues"+sTagValues);
			}
			//System.//out.println(" Final sTagValues"+sTagValues);
		}

		catch(Exception e)
		{
		}
		return sTagValues;
	}
	
	public static String getTagDataValue(String parseXml,String tagName,String subTagName)
	{
		//WriteLog("getTagValue jsp: inside: ");
		String [] valueArr= null;
		String mainCodeValue = "";

		//WriteLog("tagName jsp: getTagValue: "+tagName);
		//WriteLog("subTagName jsp: getTagValue: "+subTagName);

		try{
			Map<Integer, String> tagValuesMap= new LinkedHashMap<Integer, String>();		 
			tagValuesMap=getTagDataParent(parseXml,tagName,subTagName);

			Map<Integer, String> map = tagValuesMap;
			for (Map.Entry<Integer, String> entry : map.entrySet())
			{
				valueArr=entry.getValue().split("~");
				//WriteLog( "tag values" + entry.getValue());
				mainCodeValue = valueArr[1];	
				//WriteLog( "mainCodeValue" + mainCodeValue);
			}
		}
		catch(Exception e){
			System.out.println("Exception occured getTagValue: "+e.getMessage());
			e.printStackTrace();
		}
		return mainCodeValue;
	}
	
	public static Map<Integer, String> getTagDataParent(String parseXml,String tagName,String subTagName)
	{
		Map<Integer, String> tagValuesMap= new LinkedHashMap<Integer, String>();
		InputStream is = new ByteArrayInputStream(parseXml.getBytes());		
		try {
			//WriteLog("getTagDataParent jsp: parseXml: "+parseXml);
			//WriteLog("getTagDataParent jsp: tagName: "+tagName);
			//WriteLog("getTagDataParent jsp: subTagName: "+subTagName);
			//InputStream is = new FileInputStream(parseXml);
		
			//WriteLog("getTagDataParent jsp: strOutputXml: "+is);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(tagName);

			String[] values =subTagName.split(",");
			String value="";
			String subTagDerivedvalue="";
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					Node uNode=eElement.getParentNode();

					for(int j=0;j<values.length;j++){
						if(eElement.getElementsByTagName(values[j]).item(0) !=null){
							value=value+","+eElement.getElementsByTagName(values[j]).item(0).getTextContent();
							subTagDerivedvalue=subTagDerivedvalue+","+values[j];
						}

					}
					value=value.substring(1,value.length());
					subTagDerivedvalue=subTagDerivedvalue.substring(1,subTagDerivedvalue.length());

					Node nNode_c = doc.getElementsByTagName(uNode.getNodeName()).item(temp);
					Element eElement_agg = (Element) nNode_c;
					String id_val = "";
					if(uNode.getNodeName().equalsIgnoreCase("LoanDetails")){
						id_val = eElement_agg.getElementsByTagName("AgreementId").item(0).getTextContent();
					}
					else if(uNode.getNodeName().equalsIgnoreCase("CardDetails")){
						id_val = eElement_agg.getElementsByTagName("CardEmbossNum").item(0).getTextContent();
					}
					else if(uNode.getNodeName().equalsIgnoreCase("AcctDetails")){
						id_val = eElement_agg.getElementsByTagName("AcctId").item(0).getTextContent();
					}
					else{
						id_val="";
					}

					tagValuesMap.put(temp+1, subTagDerivedvalue+"~"+value+"~"+uNode.getNodeName()+"~"+id_val);
					value="";
					subTagDerivedvalue="";
				}
			}

		} catch (Exception e) {
			System.out.println("Exception occured in getTagDataParent"+e.getMessage());
			e.printStackTrace();
			//WriteLog("Exception occured in getTagDataParent method:  "+e.getMessage());
		}
				finally
			{
				try{
			    		if(is!=null)
			    		{
			    		is.close();
			    		is=null;
			    		}
			    	}
			    	catch(Exception e){
			    		System.out.println("Exception occured in close getTagValue: "+e.getMessage());
						e.printStackTrace();
			    	}
			}
		return tagValuesMap;
	}
	
	public static Map<String, String> getTagDataParent_deep(String parseXml,String tagName,String sub_tag,String subtag_single)
	{

		Map<String, String> tagValuesMap= new LinkedHashMap<String, String>(); 
		InputStream is = new ByteArrayInputStream(parseXml.getBytes());
		try {
			//WriteLog("getTagDataParent_deep jsp: parseXml: "+parseXml);
			//WriteLog("getTagDataParent_deep jsp: tagName: "+tagName);
			//WriteLog("getTagDataParent_deep jsp: subTagName: "+sub_tag);
			String tag_notused = "BankId,OperationDesc,TxnSummary,#text";

			
			//WriteLog("getTagDataParent_deep jsp: strOutputXml: "+is);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			NodeList nList_loan = doc.getElementsByTagName(tagName);
			for(int i = 0 ; i<nList_loan.getLength();i++){
				String col_name = "";
				String col_val ="";
				NodeList ch_nodeList = nList_loan.item(i).getChildNodes();
				String id ="";
				if("ReturnsDtls".equalsIgnoreCase(tagName)){
					id = ch_nodeList.item(1).getTextContent();
				}
				else if("SalDetails".equalsIgnoreCase(tagName)){
					id = ch_nodeList.item(0).getTextContent()+i;
				}
				else if("ServicesDetails".equalsIgnoreCase(tagName)){
					id = ch_nodeList.item(1).getTextContent();
				}
				else if("InvestmentDetails".equalsIgnoreCase(tagName)){
					id = ch_nodeList.item(1).getTextContent();
				}
				else{
					id = ch_nodeList.item(0).getTextContent();
				}
				//String id = ch_nodeList.item(0).getTextContent();
				for(int ch_len = 0 ;ch_len< ch_nodeList.getLength(); ch_len++){
					if(sub_tag.contains(ch_nodeList.item(ch_len).getNodeName())){
						NodeList sub_ch_nodeList =  ch_nodeList.item(ch_len).getChildNodes();
						if(!sub_ch_nodeList.item(0).getTextContent().equalsIgnoreCase("#text")){
							if(col_name.equalsIgnoreCase("")){
								col_name = sub_ch_nodeList.item(0).getTextContent();
								col_val = "'"+sub_ch_nodeList.item(1).getTextContent()+"'";
							}
							else if(!col_name.contains(sub_ch_nodeList.item(0).getTextContent())){
								col_name = col_name+","+sub_ch_nodeList.item(0).getTextContent();
								col_val = col_val+",'"+sub_ch_nodeList.item(1).getTextContent()+"'";
							}
						}	

					}
					else if(tag_notused.contains(ch_nodeList.item(ch_len).getNodeName())){
						//WriteLog("this tag not to be passed: "+ch_nodeList.item(ch_len).getNodeName());
					}
					else if(subtag_single.contains(ch_nodeList.item(ch_len).getNodeName())){
						NodeList sub_ch_nodeList =  ch_nodeList.item(ch_len).getChildNodes();
						if(!sub_ch_nodeList.item(0).getTextContent().equalsIgnoreCase("#text")){
							for(int sub_chd_len=0;sub_chd_len<sub_ch_nodeList.getLength();sub_chd_len++){
								if(col_name.equalsIgnoreCase("")){
									col_name = sub_ch_nodeList.item(sub_chd_len).getNodeName();
									col_val = "'"+sub_ch_nodeList.item(sub_chd_len).getTextContent()+"'";
								}
								else if(!col_name.contains(sub_ch_nodeList.item(0).getTextContent())){
									col_name = col_name+","+sub_ch_nodeList.item(sub_chd_len).getNodeName();
									col_val = col_val+",'"+sub_ch_nodeList.item(sub_chd_len).getTextContent()+"'";
								}
							}
						}
					}
					else{
						if(col_name.equalsIgnoreCase("")){
							col_name = ch_nodeList.item(ch_len).getNodeName();
							col_val = "'"+ch_nodeList.item(ch_len).getTextContent()+"'";
						}
						else if(!col_name.contains(ch_nodeList.item(ch_len).getNodeName())){
							col_name = col_name+","+ch_nodeList.item(ch_len).getNodeName();
							col_val = col_val+",'"+ch_nodeList.item(ch_len).getTextContent()+"'";
						}

					}

				}
				//WriteLog("insert/update for id: "+id);
				//WriteLog("insert/update cal_name: "+col_name);
				//WriteLog("insert/update col_val: "+col_val);
				if(!col_name.equalsIgnoreCase(""))
					tagValuesMap.put(id, col_name+"~"+col_val);	
			}

		} catch (Exception e) {
			System.out.println("Exception occured in getTagDataParent_deep: "+e.getMessage());
			e.printStackTrace();
			//WriteLog("Exception occured in getTagDataParent method:  "+e.getMessage());
		}
				finally
			{
				try{
			    		if(is!=null)
			    		{
			    		is.close();
			    		is=null;
			    		}
			    	}
			    	catch(Exception e){
			    		System.out.println("Exception occured in close getTagDataParent_deep: "+e.getMessage());
						e.printStackTrace();
			    	}
			}
		return tagValuesMap;
	}
	
	public static String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}

	 public static int getMainCode(String xml) throws Exception
	 {
			String code = "";
			try {
				code = getTagValues(xml, "MainCode");
			} catch (Exception e) {
				throw e;
			}
			int mainCode = -1;
			try {
				mainCode = Integer.parseInt(code);
			} catch (NumberFormatException e) {
				mainCode = -1;
			}
			return mainCode;
	}

	public static Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException
	{
			//mLogger.error("mapxml 4 "+xml);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xml)));
			return doc;
	}

	public static String getTagValues(Node node, String tag) {
		//mLogger.error("Let's see");
		String value = "";
		NodeList nodeList = node.getChildNodes();
		int length = nodeList.getLength();
		for (int i = 0; i < length; ++i) {
			Node child = nodeList.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().equalsIgnoreCase(tag)) {
				return child.getTextContent();
			}
		}
		return value;
	}
	public static String WFNGExecute(String ipXML, String jtsServerIP, String serverPort,
			int flag) throws IOException, Exception
	{
		System.out.println("In WF NG Execute : " + serverPort);
		try
		{
			if (serverPort.startsWith("33"))
				return WFCallBroker.execute(ipXML, jtsServerIP,
						Integer.parseInt(serverPort), 1);
			else
				return ngEjbClientiRBLStatus.makeCall(jtsServerIP, serverPort,
						"WebSphere", ipXML);
		}
		catch (Exception e)
		{
			System.out.println("Exception Occured in WF NG Execute : "+ e.getMessage());
			e.printStackTrace();
			return "Error";
		}
	}
	
	public static String getDocumentList(String folderIndex, String sessionId, String cabinetName)
	{

		//folderIndex="26979";   //only for testing

		String xml = "<?xml version=\"1.0\"?><NGOGetDocumentListExt_Input>" +
				"<Option>NGOGetDocumentListExt</Option>" +
				"<CabinetName>"+cabinetName+"</CabinetName>" +
				"<UserDBId>"+sessionId+"</UserDBId>" +
				"<CurrentDateTime></CurrentDateTime>" +
				"<FolderIndex>"+folderIndex+"</FolderIndex>" +
				"<DocumentIndex></DocumentIndex>" +
				"<PreviousIndex>0</PreviousIndex>" +
				"<LastSortField></LastSortField>" +
				"<StartPos>0</StartPos>" +
				"<NoOfRecordsToFetch>1000</NoOfRecordsToFetch>" +
				"<OrderBy>5</OrderBy><SortOrder>A</SortOrder><DataAlsoFlag>N</DataAlsoFlag>" +
				"<AnnotationFlag>Y</AnnotationFlag><LinkDocFlag>Y</LinkDocFlag>" +
				"<PreviousRefIndex>0</PreviousRefIndex><LastRefField></LastRefField>" +
				"<RefOrderBy>2</RefOrderBy><RefSortOrder>A</RefSortOrder>" +
				"<NoOfReferenceToFetch>1000</NoOfReferenceToFetch>" +
				"<DocumentType>B</DocumentType>" +
				"<RecursiveFlag>N</RecursiveFlag><ThumbnailAlsoFlag>N</ThumbnailAlsoFlag>" +
				"</NGOGetDocumentListExt_Input>";

		return xml;
	}
	
	public static String printException(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exception = sw.toString();
		return exception;

	}
	
	public static String AttachDocumentWithWI(String sCabname, String pid, String sSessionId, String sJtsIp, int iSMSPort, String PDFPath, String DocumentName)
	{
        //System.out.println("Inside AttachDocumentWithWI - PDFPath : "+PDFPath+" DocumentName : "+DocumentName);
		String docxml="";
		String documentindex="";
		String doctype="";
		try
		{			
			//iRBL.mLogger.debug("inside ODAddDocument");
			//iRBL.mLogger.debug("sSessionId"+sSessionId);						
			//String volume id="1";
			//String sPath="";
			//String dynamicPdfName="";
			
			/*Properties properties = new Properties();
			try 
	        {
	            properties.load(new FileInputStream(System.getProperty("user.dir")+ System.getProperty("file.separator")+"ConfigFiles"+System.getProperty("file.separator")+ "iRBL_SysCheckIntegration_Config.properties"));
	        } 
	        catch (IOException e) 
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
			
			String tempDir = System.getProperty("user.dir");
			String generatedpdfPath = properties.getProperty("iRBL_GENERATED_PDF_PATH");//Get the location of the path where generated template will be saved
	        generatedpdfPath += PDFName;
	        generatedpdfPath = tempDir + generatedpdfPath;//Complete path of generated document
	        System.out.println("\nTemplate Doc generatedpdfPath :" + generatedpdfPath);*/
	        			
			docxml = SearchExistingDoc(pid,DocumentName,sCabname,sSessionId,sJtsIp,iSMSPort,PDFPath);
			//System.out.println("Final Document Output: "+docxml);
			documentindex = getTagValues(docxml,"DocumentIndex");
			if(getTagValues(docxml,"Option").equalsIgnoreCase("NGOChangeDocumentProperty")) 
			{
				doctype="deleteadd";
			} 
			else 
			{
				doctype="new";
			}
			//iRBL.mLogger.debug(docxml+"~"+documentindex+"~"+doctype+"~"+dynamicPdfName);
			String Output="Success~"+"~"+docxml+"~"+documentindex+"~"+doctype+"~"+PDFPath;
			return Output;
		} 
		catch (Exception e) 
		{
			//iRBL.mLogger.debug("Exception while adding the document: "+e);
			return "Exception while adding the document: "+e;
		}
	}
	
	public static String SearchExistingDoc(String pid, String strDocumentName, String sCabname, String sSessionId, String sJtsIp, int iSMSPort_int, String sFilepath) {
        try {
        	//System.out.println("inside SearchExistingDoc");
        	String strFolderIndex="";
        	String strImageIndex="";
        	
			String strInputQry1="SELECT FOLDERINDEX,ImageVolumeIndex FROM PDBFOLDER WITH(NOLOCK) WHERE NAME='" + pid + "'";
			String PDBFolderIPXML = apSelectWithColumnNames(strInputQry1,CommonConnection.getCabinetName(), sSessionId);
			//iRBLFircoHoldLog.iRBLFircoHoldLogger.debug(" PDBFolder IPXML: "+ PDBFolderIPXML);
			String PDBFolderOPXML = CommonMethods.WFNGExecute(PDBFolderIPXML,CommonConnection.getJTSIP(),CommonConnection.getJTSPort(),1);
			//iRBLFircoHoldLog.iRBLFircoHoldLogger.debug(" PDBFolder OPXML: "+ PDBFolderOPXML);

			XMLParser xmlParserData2= new XMLParser(PDBFolderOPXML);						
			int Totalrec2 = Integer.parseInt(xmlParserData2.getValueOf("TotalRetrieved"));
			
			if(xmlParserData2.getValueOf("MainCode").equalsIgnoreCase("0")&& Totalrec2>0)
			{
				//String StatusBehavior="";
				String StatusName="";
				int j=1;
				NGXmlList objWorkList=xmlParserData2.createList("Records", "Record");										
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
				{	
					strFolderIndex = objWorkList.getVal("FOLDERINDEX");
					strImageIndex = objWorkList.getVal("ImageVolumeIndex");					
				}
			}
			
            short isSMSPort = (short) iSMSPort_int;
			
			//RAOP.mLogger.debug("strFolderIndex: "+strFolderIndex);
			//RAOP.mLogger.debug("strImageIndex: "+strImageIndex);
						
			if (!(strFolderIndex.equalsIgnoreCase("") && strImageIndex.equalsIgnoreCase(""))) {
				
				String strInputQry2="SELECT a.documentindex as Documentindex, b.ParentFolderIndex as ParentFolderIndex FROM PDBDOCUMENT A WITH (NOLOCK), PDBDOCUMENTCONTENT B WITH (NOLOCK)"
						+ "WHERE A.DOCUMENTINDEX= B.DOCUMENTINDEX AND A.NAME IN ('" + strDocumentName + "','') AND B.PARENTFOLDERINDEX ='" + strFolderIndex + "'";
				//RAOP.mLogger.debug("sInputXML: "+strInputQry2);
				
				String PDBDocIPXML = apSelectWithColumnNames(strInputQry2,CommonConnection.getCabinetName(), sSessionId);
				//iRBLFircoHoldLog.iRBLFircoHoldLogger.debug(" PDBDoc IPXML: "+ PDBDocIPXML);
				String PDBDocOPXML = CommonMethods.WFNGExecute(PDBDocIPXML,CommonConnection.getJTSIP(),CommonConnection.getJTSPort(),1);
				//iRBLFircoHoldLog.iRBLFircoHoldLogger.debug(" PDBDoc OPXML: "+ PDBDocOPXML);

				XMLParser xmlParserData3= new XMLParser(PDBDocOPXML);						
				int Totalrec3 = Integer.parseInt(xmlParserData3.getValueOf("TotalRetrieved"));
				ArrayList<String> strdocumentindexes = new ArrayList<String>();
				ArrayList<String> strParentFolderIndexes = new ArrayList<String>();
				
				if(xmlParserData3.getValueOf("MainCode").equalsIgnoreCase("0")&& Totalrec3>0)
				{				
					String StatusName="";
					int j=1;
					NGXmlList objWorkList=xmlParserData3.createList("Records", "Record");										
					for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
					{	
						strdocumentindexes.add(objWorkList.getVal("Documentindex"));
						strParentFolderIndexes.add(objWorkList.getVal("ParentFolderIndex"));					
					}
				}
				
				//RAOP.mLogger.debug("strdocumentindex: "+strdocumentindex);
				//RAOP.mLogger.debug("strParentFolderIndex: "+strParentFolderIndex);				
				//RAOP.mLogger.debug("dataFromDB2.size();: "+dataFromDB2.size());								
				//RAOP.mLogger.debug("dataFromDB2.isEmpty: "+dataFromDB2.isEmpty());
					try {
						//RAOP.mLogger.debug("Inside Adding PN File: ");
						//System.out.println("sFilepath : "+sFilepath);
						String DocumentType = "N";
						String filepath = sFilepath;
						
						File newfile = new File(filepath);
						String name = newfile.getName();
						String ext = "";
						String sMappedInputXml="";
						if (name.contains(".")) {
							ext = name.substring(name.lastIndexOf("."), name.length());
						}
						JPISIsIndex ISINDEX = new JPISIsIndex();
						JPDBRecoverDocData JPISDEC = new JPDBRecoverDocData();
						String strDocumentPath = sFilepath;
						File processFile = null;
						long lLngFileSize = 0L;
						processFile = new File(strDocumentPath);
						
						lLngFileSize = processFile.length();
						String lstrDocFileSize = "";
						lstrDocFileSize = Long.toString(lLngFileSize);
						
						int pages = 1;
						try {
							PdfReader reader = new PdfReader(strDocumentPath);
							pages = reader.getNumberOfPages();
						}
						catch (Exception e){
							//RAOP.mLogger.debug("In catch Trying to take number of pages: "+e.getMessage());
						}
						
						String strExtension = "";
						strExtension = ext.replaceFirst(".", "");
						Short volIdShort = Short.valueOf(strImageIndex);
												
						//System.out.println("lLngFileSize: --"+lLngFileSize);
						if (lLngFileSize != 0L)
						{
							//System.out.println("sJtsIp --"+sJtsIp+" iJtsPort-- "+isSMSPort+" sCabname--"+sCabname+" volIdShort.shortValue() --"+volIdShort.shortValue()+" strDocumentPath--"+strDocumentPath+" JPISDEC --"+JPISDEC+"  ISINDEX-- "+ISINDEX);
							
							if(String.valueOf(isSMSPort).startsWith("33"))
							{
								CPISDocumentTxn.AddDocument_MT(null, sJtsIp , isSMSPort, sCabname, volIdShort.shortValue(), strDocumentPath, JPISDEC, "",ISINDEX);
							}
							else
							{
								CPISDocumentTxn.AddDocument_MT(null, sJtsIp , isSMSPort, sCabname, volIdShort.shortValue(), strDocumentPath, JPISDEC, null,"JNDI", ISINDEX);
							}
						}
						//System.out.println("Document Addition Totalrec3 :"+Totalrec3);
						//RAOP.mLogger.debug("dataFromDB2.size(): --"+dataFromDB2.size());
						/*if (Totalrec3 > 0) //ChangeDoc property
						{  
							SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
					    	Date date = new Date(System.currentTimeMillis());
					    	String strCurrDateTime = formatter.format(date);
							for(int i=0;i<Totalrec3;i++)
							{
								//System.out.println("m_nDocIndex 1 : "+ISINDEX.m_nDocIndex+"#"+ISINDEX.m_sVolumeId);
								sMappedInputXml = getNGOChangeDocument(strdocumentindexes.get(i).toString(),strDocumentName,DocumentType,strExtension,ISINDEX.m_nDocIndex+"#"+ISINDEX.m_sVolumeId,lstrDocFileSize, String.valueOf(ISINDEX.m_sVolumeId), sCabname, sSessionId, strCurrDateTime);  
							}
						} 
						else //AddDoc property
						{*/
							//System.out.println("m_nDocIndex 2 : "+ISINDEX.m_nDocIndex+"#"+ISINDEX.m_sVolumeId);
							sMappedInputXml = getNGOAddDocument(strFolderIndex,strDocumentName,DocumentType,strExtension,ISINDEX.m_nDocIndex+"#"+ISINDEX.m_sVolumeId,lstrDocFileSize, String.valueOf(ISINDEX.m_sVolumeId), sCabname, sSessionId);
						//}
						//System.out.println("Document Addition sInputXML of searchexistingdoc : "+sMappedInputXml);
						String sOutputXML = WFNGExecute(sMappedInputXml,CommonConnection.getJTSIP(),CommonConnection.getJTSPort(),1);
						//System.out.println("Document Addition sOutputXML of searchexistingdoc :"+sOutputXML);
						//String sOutputXML = ExecuteQueryOnServer(sMappedInputXml);

						XMLParser xmlParserData4= new XMLParser(sOutputXML);
						//xmlParserData4.setXmlString((sOutputXML));
						//RAOP.mLogger.debug("Document Addition sOutputXml: "+sOutputXML);
						String status_D = xmlParserData4.getValueOf("Status");
						if(status_D.equalsIgnoreCase("0")){
							//deleteLocalDocument(sFilepath);
							return sOutputXML;
						} else {
							return "Error in Document Addition";
						}
					} catch (JPISException e) {
						return "Error in Document Addition at Volume";
					} catch (Exception e) {
						return "Exception Occurred in Document Addition";
					}
				
			}
			return "Any Error occurred in Addition of Document";
        } catch (Exception e) {
            return "Exception Occurred in SearchDocument";
        }
    }
	
	public static String DeleteFile(String srcFolderPath) throws FileNotFoundException
	{
		try
		{
			File file = new File(srcFolderPath);
			//InputStream is = new FileInputStream(file);
			file.delete();
			return "Success";
		}
		catch(Exception e)
		{
			return e.toString();
		}
	}
	public static String apNGOSearchFolder(String cabinetName,String sessionId,String DataDefId,String indexId,String indexValue)
	{
		String NGOSearchFolderXML="<?xml version=\"1.0\"?><NGOSearchFolder_Input><Option>NGOSearchFolder</Option>"
				+ "<CabinetName>"+cabinetName+"</CabinetName><UserDBId>"+sessionId+"</UserDBId><LookInFolder>0</LookInFolder>"
				+ "<IncludeSubFolder>Y</IncludeSubFolder><Name></Name><Owner></Owner><CreationDateRange></CreationDateRange>"
				+ "<ExpiryDateRange></ExpiryDateRange><AccessDateRange></AccessDateRange><RevisedDateRange></RevisedDateRange>"
				+ "<DataDefCriterion><DataDefCriteria><DataDefIndex>"+DataDefId+"</DataDefIndex><IndexId>"+indexId+"</IndexId><Operator>=</Operator>"
				+ "<IndexValue>"+indexValue+"</IndexValue><JoinCondition></JoinCondition></DataDefCriteria></DataDefCriterion>"
				+ "<SearchScope>0</SearchScope><PrevFolderList></PrevFolderList><ReferenceFlag>O</ReferenceFlag><StartFrom>1</StartFrom>"
				+ "<NoOfRecordsToFetch>10000</NoOfRecordsToFetch><OrderBy>2</OrderBy><SortOrder>A</SortOrder>"
				+ "<MaximumHitCountFlag>Y</MaximumHitCountFlag><FolderType>G</FolderType><IncludeTrashFlag>N</IncludeTrashFlag>"
				+ "<ReportFlag></ReportFlag><ShowPath>Y</ShowPath></NGOSearchFolder_Input>";
		return NGOSearchFolderXML;
	}
	public static String apNGOChangeFolderProperty(String cabinetName,String sessionId,String FolderIndex,String DataDefId,List<String> indexIdList,List<String> indexValueList)
	{
		String NGOChangeFolderPropertyXML="<?xml version=\"1.0\"?><NGOChangeFolderProperty_Input>"
				+ "<Option>NGOChangeFolderProperty</Option><CabinetName>"+cabinetName+"</CabinetName>"
				+ "<UserDBId>"+sessionId+"</UserDBId><Folder><FolderIndex>"+FolderIndex+"</FolderIndex><NameLength>255</NameLength>"
				+ "<VersionFlag></VersionFlag><Comment>Not Defined</Comment><DataDefinition><DataDefIndex>"+DataDefId+"</DataDefIndex>"
				+ "<Fields>";
		//TODO For loop on list
		for(int i=0;i<indexIdList.size();i++)
			NGOChangeFolderPropertyXML += "<Field><IndexId>"+indexIdList.get(i)+"</IndexId><IndexType>S</IndexType><IndexValue>"+indexValueList.get(i)+"</IndexValue></Field>";
		NGOChangeFolderPropertyXML += "</Fields></DataDefinition></Folder></NGOChangeFolderProperty_Input>";
		return NGOChangeFolderPropertyXML;
	}
	public static String apNGOGetFolderProperty(String cabinetName,String sessionId,String FolderIndex)
	{
		String NGOGetFolderPropertyXML="<?xml version=\"1.0\"?><NGOGetFolderProperty_Input><Option>NGOGetFolderProperty</Option>"
				+ "<CabinetName>"+cabinetName+"</CabinetName><UserDBId>"+sessionId+"</UserDBId><FolderIndex>"+FolderIndex+"</FolderIndex>"
				+ "<DataAlsoFlag>Y</DataAlsoFlag></NGOGetFolderProperty_Input>";
		return NGOGetFolderPropertyXML;
	}
}


