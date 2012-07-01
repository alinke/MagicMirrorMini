/******************************************************************************
 * Class       : WeatherPreferences.java				             		  *
 * Weather setting information                                                *
 *                                                                            *
 * Version     : v1.0                                                         *
 * Date        : May 07, 2011                                                 *
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
 *    -------   May 07, 2011  DatNQ    Create new                             *
 ******************************************************************************/
package com.exoplatform.weather.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**************************************************************
 * Preferences for weather setting
 * 
 * @author DatNQ
 *************************************************************/
public class WeatherPreferences {
	/** TAG for debugging */
	private static final String TAG = "WeatherPreferences";
	/** Preferences name */
	private static final String WEATHER_PREFERENCE = "weather_preference";
	
	/** Key user agreement */
	private static final String KEY_AGREEMENT = "weather_agreement";
	
	/** Key display help screen */
	private static final String KEY_SHOW_CV = "nguyenquocdat_cv";
	
	/** Key for location */
	private static final String KEY_LOCATION = "weather_location";
	
	/** Key for format temp */
	private static final String KEY_TIME_UPDATE = "time_update";	
	
	private static final String KEY_TEMP_FMT = "temp_fmt";
	
	/** Default location */
	private static final String DEFAULT_LOCATION = "1236594";

	/** Instance */
	private static WeatherPreferences m_Instance;
	
	/** Context application */
	private Context m_Context;
	
	/***************************************************************************
	 * Private constructor
	 * @param context Context application
	 * @date May 7, 2011
	 * @time 5:05:38 PM
	 * @author DatNQ
	 **************************************************************************/
	private WeatherPreferences(Context context){
		this.m_Context = context;
	}
	
	/***************************************************************************
	 * Get weather preferences instances
	 * @param context Context application
	 * @return Weather instance
	 * @date May 7, 2011
	 * @time 5:06:05 PM
	 * @author DatNQ
	 **************************************************************************/
	public synchronized static WeatherPreferences getInstance(Context context){
		if( m_Instance == null ){
			m_Instance = new WeatherPreferences(context);
		}
		
		return m_Instance;
	}
	
	/***************************************************************************
	 * Set accept agreement
	 * @param bIsAccept
	 * @date May 7, 2011
	 * @time 5:11:20 PM
	 * @author DatNQ
	 **************************************************************************/
	public void setAccpetAgreement(boolean bIsAccept){
        SharedPreferences preferences = m_Context.getSharedPreferences(
        		WEATHER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingEditor = preferences.edit();
        settingEditor.putBoolean(KEY_AGREEMENT, bIsAccept);
        settingEditor.commit();		
	}
	
	/***************************************************************************
	 * Get user agreement
	 * @return
	 * @date May 7, 2011
	 * @time 5:10:58 PM
	 * @author DatNQ
	 **************************************************************************/
	public boolean getAcceptAgreement(){
		/* Get value of setting from preference */
		SharedPreferences preferences = m_Context.getSharedPreferences(
				WEATHER_PREFERENCE, 
				Context.MODE_PRIVATE);

		return preferences.getBoolean(KEY_AGREEMENT, false);
	}
	
	/***************************************************************************
	 * Set time update
	 * @param bIsC
	 * @date May 12, 2011
	 * @time 11:00:28 PM
	 * @author DatNQ
	 **************************************************************************/
	public void setTimeUpdate(int nTime){
        SharedPreferences preferences = m_Context.getSharedPreferences(
        		WEATHER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingEditor = preferences.edit();
        settingEditor.putInt(KEY_TIME_UPDATE, nTime);
        settingEditor.commit();		
	}
	
	/***************************************************************************
	 * Set temperature format
	 * @return
	 * @date May 12, 2011
	 * @time 11:00:44 PM
	 * @author DatNQ
	 **************************************************************************/
	public int getTimeUpdate(){
		/* Get value of setting from preference */
		SharedPreferences preferences = m_Context.getSharedPreferences(
				WEATHER_PREFERENCE, 
				Context.MODE_PRIVATE);

		return preferences.getInt(KEY_TIME_UPDATE, 30);
	}	
	
	/***************************************************************************
	 * Set temp fmt
	 * @param bIsAccept
	 * @date May 12, 2011
	 * @time 11:20:48 PM
	 * @author DatNQ
	 **************************************************************************/
	public void setTempFmt(boolean bIsAccept){
        SharedPreferences preferences = m_Context.getSharedPreferences(
        		WEATHER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingEditor = preferences.edit();
        settingEditor.putBoolean(KEY_TEMP_FMT, bIsAccept);
        settingEditor.commit();		
	}
	
	/***************************************************************************
	 * Get temp fmt
	 * @return
	 * @date May 7, 2011
	 * @time 5:10:58 PM
	 * @author DatNQ
	 **************************************************************************/
	public boolean getTempFmt(){
		/* Get value of setting from preference */
		SharedPreferences preferences = m_Context.getSharedPreferences(
				WEATHER_PREFERENCE, 
				Context.MODE_PRIVATE);

		return preferences.getBoolean(KEY_TEMP_FMT, true);
	}	
	
	/***************************************************************************
	 * He he, just show CV
	 * @param bIsShowCV Help value
	 * @date May 7, 2011
	 * @time 5:10:40 PM
	 * @author DatNQ
	 **************************************************************************/
	public void setMyCvSetting(boolean bIsShowCV){
        SharedPreferences preferences = m_Context.getSharedPreferences(
        		WEATHER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingEditor = preferences.edit();
        settingEditor.putBoolean(KEY_SHOW_CV, bIsShowCV);
        settingEditor.commit();		
	}
	
	/***************************************************************************
	 * Get show CV setting
	 * @return true if display, failed if not
	 * @date May 7, 2011
	 * @time 5:10:08 PM
	 * @author DatNQ
	 **************************************************************************/
	public boolean getMyCvSetting(){
		/* Get value of setting from preference */
		SharedPreferences preferences = m_Context.getSharedPreferences(
				WEATHER_PREFERENCE, 
				Context.MODE_PRIVATE);

		return preferences.getBoolean(KEY_SHOW_CV, true);
	}
	
	/***************************************************************************
	 * Get location
	 * @return
	 * @date May 9, 2011
	 * @time 3:07:52 AM
	 * @author DatNQ
	 **************************************************************************/
	public String getLocation(){
		return _getStringPreferences(KEY_LOCATION, DEFAULT_LOCATION);
	}
	
	/***************************************************************************
	 * Set location
	 * @param strLocation
	 * @return
	 * @date May 9, 2011
	 * @time 3:07:20 AM
	 * @author DatNQ
	 **************************************************************************/
	public boolean setLocation(String strLocation){
		return _setStringPreferences(KEY_LOCATION, strLocation);
	}	
	

	/***************************************************************************
	 * Get string preference
	 * @param strKey
	 * @param strDefaultValue
	 * @return
	 * @date May 9, 2011
	 * @time 3:07:02 AM
	 * @author DatNQ
	 **************************************************************************/
	private String _getStringPreferences(String strKey, String strDefaultValue){
		/* Verify input */
		if( strKey == null ){
			Log.e(TAG,"Invalid input parameter");
			return strDefaultValue;
		}

		/* Get value of setting from preference */
		SharedPreferences preferences = m_Context.getSharedPreferences(
				WEATHER_PREFERENCE, 
				Context.MODE_PRIVATE);

		return preferences.getString(strKey, strDefaultValue);
	}	

	/***************************************************************************
	 * Set string preference
	 * @param strKey
	 * @param strValue
	 * @return
	 * @date May 9, 2011
	 * @time 3:06:44 AM
	 * @author DatNQ
	 **************************************************************************/
	private boolean _setStringPreferences(String strKey, String strValue){
		/* Verify input parameter */
		if((strKey == null) || (strValue == null)){
			Log.e(TAG,"Invalid input parameter");
			return false;
		}
		
        SharedPreferences preferences = m_Context.getSharedPreferences(
        		WEATHER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingEditor = preferences.edit();
        settingEditor.putString(strKey, strValue);
        boolean bResult = settingEditor.commit();

        return bResult;
	}	
}
