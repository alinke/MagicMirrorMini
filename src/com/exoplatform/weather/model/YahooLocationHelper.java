/******************************************************************************
 * Class       : YahooLocationHelper.java						   			  *
 * Main Yahoo Weather activity                                                *
 *                                                                            *
 * Version     : v1.0                                                         *
 * Date        : May 06, 2011                                                 *
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
 *    -------   May 06, 2011  DatNQ    Create new                             *
 ******************************************************************************/
package com.exoplatform.weather.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.util.Log;

/*******************************************************************************
 * This class for provider list country support
 * For performance issue it is better to prepare WOEID by country and city
 * 
 * Alternative for dynamic solution is use yahoo API for get country then 
 * get city then get WOEID
 * http://developer.yahoo.com/geo/geoplanet/
 * Ref: http://where.yahooapis.com/v1/countries?appid=[yourappidhere]
 * @author DatNQ
 *
 ******************************************************************************/
public class YahooLocationHelper {
	
	/** WOID */
	private static final String PARAM_WOEID = "woeid";
	
	/** Currently not support this country */
	public static final int WOEID_NOTSUPPORT = -1;
	
	/** TAG for debugging */
	private static final String TAG = "YahooCountrySupport";
	
	/** Instance of yahoo location support*/
	private static YahooLocationHelper m_Instance;
	
	/***************************************************************************
	 * Private constructor
	 * @date May 6, 2011
	 * @time 11:48:29 PM
	 * @author DatNQ
	 **************************************************************************/
	private YahooLocationHelper(){
		/* Do nothing */
	}
	
	/***************************************************************************
	 * Get instance country support
	 * @date May 7, 2011
	 * @time 12:08:25 AM
	 * @author DatNQ
	 **************************************************************************/
	public synchronized YahooLocationHelper getInstance(){
		if (m_Instance == null){
			m_Instance = new YahooLocationHelper();
		}
		
		return m_Instance;
	}
	
	/***************************************************************************
	 * Get WOEID by country
	 * @param strContry Country name
	 * @return
	 * @date May 6, 2011
	 * @time 11:49:08 PM
	 * 
	 * Ref: http://where.yahooapis.com/v1/places.q('Barrie CA')?appid=[yourappidhere]
	 * @author DatNQ
	 **************************************************************************/
	public int getWOEIDByLocation(String strLocation){
		/* Verify input */
		if (strLocation == null){
			Log.e(TAG,"Invalid input parameter");
			return WOEID_NOTSUPPORT;
		}

		int nWOEID = WOEID_NOTSUPPORT;
		/* T.B.D */
		
		return nWOEID;	
	}
	
	/***************************************************************************
	 * Get WOEID
	 * @param docWeather
	 * @return
	 * @date May 9, 2011
	 * @time 8:14:13 AM
	 * @author DatNQ
	 **************************************************************************/
	public static String parserWOEIDData(Document docWeather){
		if (docWeather == null){
			Log.e(TAG,"Invalid doc weatehr");
			return null;
		}
		
		String strWOEID = null;
		try {
			Element root = docWeather.getDocumentElement();
			root.normalize();
	
			strWOEID = root.getElementsByTagName(PARAM_WOEID).item(0).getFirstChild().getNodeValue();
			
		} catch (Exception e){
			Log.e(TAG,"Something wroing with parser data");
			return null;
		}

		return strWOEID;
	}	


}
