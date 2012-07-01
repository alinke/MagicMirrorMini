/******************************************************************************
 * Class       : HttpConnectHelper.java		        		         		  *
 * Main Weather activity, in this demo apps i use API from yahoo, you can     *
 * use other weather web service which you prefer                             *
 *                                                                            *
 * Version     : v1.0                                                         *
 * Date        : May 09, 2011                                                 *
 * Copyright (c)-2011 DatNQ some right reserved                               *
 * You can distribute, modify or what ever you want but WITHOUT ANY WARRANTY  *
 * Be honest by keep credit of this file                                      *
 *                                                                            *
 * If you have any concern, feel free to contact with me via email, i will    *
 * check email in free time                                                   * 
 * Email: nguyendatnq@gmail.com                                               *
 * ---------------------------------------------------------------------------*
 * Modification Logs:                                                         *
 *   KEYCHANGE  DATE          AUTHOR   DESCRIPTION                            *
 * ---------------------------------------------------------------------------*
 *    -------   May 09, 2011  DatNQ    Create new                             *
 ******************************************************************************/
package com.exoplatform.weather.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.util.Log;

/*******************************************************************************
 * Connect to server helper
 * @author DatNQ
 *
 ******************************************************************************/
public class HttpConnectHelper {
	/** TAG for debugging */
	private static final String TAG = "HttpConnectHelper";
	
	/** HTTP Connection */
	private HttpURLConnection httpConnection;

	/***************************************************************************
	 * Connect server
	 * @param strURL URL
	 * @throws IOException
	 * @date May 9, 2011
	 * @time 1:38:18 AM
	 * @author DatNQ
	 **************************************************************************/
	private void requestConnectServer(String strURL) throws IOException {	
		httpConnection = (HttpURLConnection)new URL(strURL).openConnection();
		httpConnection.connect();
		
		if(httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
			Log.e(TAG,"Something wrong with connection");
			httpConnection.disconnect();
			throw new IOException("Error in connection: "+ httpConnection.getResponseCode());
		}
	}
	
	/***************************************************************************
	 * Close connection
	 * @date May 9, 2011
	 * @time 1:39:33 AM
	 * @author DatNQ
	 **************************************************************************/
	private void requestDisconnect(){
		if(httpConnection != null){
			httpConnection.disconnect();
		}
	}
	
	/***************************************************************************
	 * Get Document XML
	 * @param strURL URL string
	 * @return
	 * @throws IOException
	 * @date May 9, 2011
	 * @time 1:40:54 AM
	 * @author DatNQ
	 **************************************************************************/
	public Document getDocumentFromURL(String strURL) throws IOException {
		/* Verify URL */
		if (strURL == null){
			Log.e(TAG,"Invalid input URL");
			return null;
		}
		
		/* Connect to server */
		requestConnectServer(strURL);

		/* Get data from server */
		String strDocContent = getDataFromConnection();
		
		/* Close connection */
		requestDisconnect();
		
		if (strDocContent == null){
			Log.e(TAG,"Can not get xml content");
			return null;
		}
		
		int strContentSize = strDocContent.length();
		StringBuffer strBuff = new StringBuffer();
		strBuff.setLength(strContentSize+1);
		strBuff.append(strDocContent);
		ByteArrayInputStream is = new ByteArrayInputStream(strDocContent.getBytes());

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document docData = null;
		
		try {
			db = dbf.newDocumentBuilder();
			docData = db.parse(is);
		} catch (Exception e) {
			Log.e(TAG,"Parser data error");
			return null;
		}
		
		return docData;
	}
	
	/***************************************************************************
	 * Get xml document
	 * @return string xml
	 * @throws IOException
	 * @date May 9, 2011
	 * @time 1:43:28 AM
	 * @author DatNQ
	 **************************************************************************/
	private String getDataFromConnection() throws IOException {
		if (httpConnection == null){
			Log.e(TAG,"connection is null");
			return null;
		}
		
		String strValue = null;
		InputStream inputStream = httpConnection.getInputStream();
		if (inputStream == null){
			Log.e(TAG, "Get input tream error");
			return null;
		}
		
		StringBuffer strBuf = new StringBuffer();		
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream));
		String strLine = "";
		
		while ((strLine = buffReader.readLine()) != null){
			strBuf.append(strLine+"\n");
			strValue +=strLine+"\n";
		}
		
		/* Release resource to system */
		buffReader.close();
		inputStream.close();
		
		return strBuf.toString();
	}
}
/*******************************************************************************
 * END OF FILE
 ******************************************************************************/
