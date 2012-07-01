/******************************************************************************
 * Class       : WeatherDataModel.java		        		         		  *
 * Data model for weather                                                     *
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

import org.w3c.dom.Document;

import com.exoplatform.weather.controller.ActivityScreenLocation;

import android.util.Log;

/*******************************************************************************
 * Data model for weather
 * @author DatNQ
 *
 *******************************************************************************/
public class WeatherDataModel {
	/** For debugging */
	private static final String TAG = "WeatherDataModel";
	
	/** URL for Yahoo API */
	private static final String URL_YAHOO_API_WEATHER = "http://weather.yahooapis.com/forecastrss?w=%s&u=c";
	
	/** Request type */
	private static final int REG_GET_WEATHER = 1;
	
	/** Connect helper for connection */
	private HttpConnectHelper m_ConnectHelper = null;
	
	/** Data model instance */
	private static WeatherDataModel m_Instance = null;
	
	/***************************************************************************
	 * Private constructor
	 * @date May 9, 2011
	 * @time 1:58:17 AM
	 * @author DatNQ
	 **************************************************************************/
	private WeatherDataModel(){
		/* Create for connect helper */
		m_ConnectHelper = new HttpConnectHelper();
	}
	
	/***************************************************************************
	 * Get instance of WeatherDataModel
	 * @return intance of WeatherDataModel
	 * @date May 9, 2011
	 * @time 1:59:58 AM
	 * @author DatNQ
	 **************************************************************************/
	public static synchronized WeatherDataModel getInstance(){
		if (m_Instance == null){
			m_Instance = new WeatherDataModel();
		}
		
		return m_Instance;
	}
	
	/***************************************************************************
	 * Get weather data by location
	 * @param strQuerry
	 * @return
	 * @date May 9, 2011
	 * @time 1:24:19 AM
	 * @author DatNQ
	 **************************************************************************/
	public WeatherInfo getWeatherData(String strQuerry){
		/* Verify input parameter */
		if (strQuerry == null){
			Log.e(TAG,"Input is invalid");
			return null;
		}
		
		/* Currently use Yahoo API */
		WeatherInfo weatherInfo = getYahooWeatherInfo(strQuerry);
		return weatherInfo;
	}
	
	/***************************************************************************
	 * Get yahoo weather
	 * @param strLocation
	 * @return
	 * @date May 9, 2011
	 * @time 2:44:49 AM
	 * @author DatNQ
	 **************************************************************************/
	private WeatherInfo getYahooWeatherInfo(String strWOEID){
		if (strWOEID == null){
			Log.e(TAG,"Invalid location");
			return null;
		}

		/* Create request URL */
		String strRegURL = createURL(REG_GET_WEATHER, strWOEID);
		if (strRegURL == null){
			Log.e(TAG,"Reg URL error");
			return null;
		}

		WeatherInfo yahooWeather = null;
		try {
			Document doc = m_ConnectHelper.getDocumentFromURL(strRegURL);
			if (doc != null){
				yahooWeather = YahooWeatherHelper.parserYahooWeatherInfo(doc);
			}
	
		} catch (Exception e) {
			Log.e(TAG, "XML Pasing error:" + e);
		}    
		
		return yahooWeather;
	}
	
	/***************************************************************************
	 * Get WOEID code
	 * @param strKeyword location
	 * @return
	 * @date May 9, 2011
	 * @time 1:21:35 AM
	 * @author DatNQ
	 **************************************************************************/
	public String getWOEIDByLocation(String strLocation){
		String strWOEID = null;
		String strQuerryWOEID = ActivityScreenLocation.createQuerryGetWoeid(strLocation);
		if (strQuerryWOEID == null){
			Log.e(TAG,"Can not create WOEID");
			return null;
		}
		
		try {
			Document doc = m_ConnectHelper.getDocumentFromURL(strQuerryWOEID);
			if (doc != null){
				strWOEID = YahooLocationHelper.parserWOEIDData(doc);
			}
	
		} catch (Exception e) {
			Log.e(TAG, "XML Pasing error:" + e);
			return null;
		}  		
		
		return strWOEID;
	}

	/***************************************************************************
	 * Create request URL
	 * @param nRequestType Kind of request
	 * @param strData Data info
	 * @return
	 * @date May 9, 2011
	 * @time 1:30:25 AM
	 * @author DatNQ
	 **************************************************************************/
	private String createURL(int nRequestType, String strData){
		if (strData == null){
			Log.e(TAG, "Invalid input data");
			return null;
		}
		
		String strRegURL = null;
		switch (nRequestType){
		case REG_GET_WEATHER:
			strRegURL = String.format(URL_YAHOO_API_WEATHER, strData);
			break;
			
			default:
				Log.e(TAG, "Not support this request:"+nRequestType);
				return null;
		}
		
		return strRegURL;
	}
	
	/***************************************************************************
	 * Convert from C to F
	 * @param strC
	 * @return
	 * @date May 12, 2011
	 * @time 10:47:29 PM
	 * @author DatNQ
	 **************************************************************************/
	public static String convertC2F(String strC){
		if (strC == null){
			return "";
		}
		
		int nC = Integer.parseInt(strC);
		int nF = (nC*9)/5 + 32;
		return Integer.toString(nF);
	}
	   	
}
