
package com.exoplatform.weather.controller;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

//import com.exoplatform.weather.R;
import com.diymagicmirror.paidandroid.R;
import com.exoplatform.weather.model.WeatherDataModel;
import com.exoplatform.weather.model.WeatherInfo;
import com.exoplatform.weather.model.WeatherPreferences;
import com.exoplatform.weather.model.YahooWeatherHelper;

public class WidgetWeather extends AppWidgetProvider {
	/** For debug */
	private static final String TAG = "WidgetWeather";
	
	/** Update weather */
	public static final String UPDATE_WEATHER = "com.exeplatform.datnq.UPDATE_WEATHER";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	/***************************************************************************
	 * Register pending intent
	 * @param context
	 * @param remoteViews
	 * @date May 12, 2011
	 * @time 7:48:45 AM
	 * @author DatNQ
	 **************************************************************************/
	private void registerPendindIntent(Context context, RemoteViews remoteViews){
		Intent settingIntent = new Intent(context, ActivityWeatherSetting.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, settingIntent, 0);

		/* Set pending intent */
		remoteViews.setOnClickPendingIntent(R.id.weather_widget, pendingIntent);		
	}


	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);

		/* Verify input parameter */
		if ((context == null) || (intent == null)){
			Log.e(TAG, "Invalid input parameter");
			return;
		}
				
		/* Get intent data */
		String intentAction = intent.getAction();
		if (intentAction == null){
			Log.e(TAG,"No Action");
			return;
		}
		
		/* Handler special action */
		if (intentAction.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) 
				|| intentAction.equals(UPDATE_WEATHER)){
			/* When receive request update view failed */
			updateWeatherView(context);
		}
	}


	private void updateWeatherView(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.weather_widget_layout);
		
    	String strWOEID = WeatherPreferences.getInstance(context).getLocation();
    	if (strWOEID == null){
    		Log.e(TAG,"Can not get WOEID");
    		return;
    	}
    	
	    /* Get weather information */
	    WeatherInfo weatherInfo = WeatherDataModel.getInstance().getWeatherData(strWOEID);
	    if (weatherInfo == null){
	    	Log.w(TAG,"Can not get weather info");
	    	return;
	    }
	    
    	boolean bIsC = WeatherPreferences.getInstance(context).getTempFmt();
    	
    	String strFmt;
    	String strTemp = weatherInfo.getTemperature(WeatherInfo.TEMPERATURE_FMT_CELSIUS);
    	if (bIsC == true){
    		strFmt = context.getString(R.string.str_temperature_fmt); 
    	} else {
    		strFmt = context.getString(R.string.str_temperature_fmt_f);
    		strTemp = WeatherDataModel.convertC2F(strTemp);
    	}

    	String strTemperature = String.format(strFmt, strTemp);	
    	remoteViews.setTextViewText(R.id.tempe, strTemperature);
    	
    	String strCode = weatherInfo.getCode();
    	int nCode = getImageByCode(strCode);    	
	        
		remoteViews.setImageViewResource(R.id.img_ic, nCode);
		
		/* Set pending intent */
		registerPendindIntent(context, remoteViews);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName component = new ComponentName(context, WidgetWeather.class);
		int[] arrWidgetID = appWidgetManager.getAppWidgetIds(component);		
		appWidgetManager.updateAppWidget(arrWidgetID, remoteViews);
	}
	
    /***************************************************************************
     * Get weather icon
     * @param nCode
     * @return
     * @date May 9, 2011
     * @time 4:42:52 AM
     * @author DatNQ
     **************************************************************************/
    private int getImageByCode(String strCode){
    	int nImageCode = R.drawable.a0;
    	
    	if (strCode == null){
    		Log.e(TAG,"Code is null");
    		return nImageCode;
    	}
    	
    	int nCode = Integer.parseInt(strCode);
    	
    	int nNumber= YahooWeatherHelper.m_ImageArr.length;
    	for (int i=0; i < nNumber; i++){
    		if (nCode == YahooWeatherHelper.m_ImageArr[i][1]){
    			return YahooWeatherHelper.m_ImageArr[i][0];
    		}
    	}
    	return nImageCode;
    }	
}
/******************************************************************************
 * END OF FILE
 ******************************************************************************/