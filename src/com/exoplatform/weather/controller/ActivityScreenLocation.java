  
package com.exoplatform.weather.controller;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.exoplatform.weather.R;
import com.diymagicmirror.paidandroid.R;
import com.exoplatform.weather.model.WeatherDataModel;
import com.exoplatform.weather.model.WeatherPreferences;


public class ActivityScreenLocation extends Activity implements OnClickListener {
	/** For debugging */
	private static final String TAG = "ActivityScreenLocation";

	/** Querry to get location */
	private static final String GET_LOCATION_WOEID = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.places%20where%20text%3D%22";	
	
	/** Location error*/
	private static final int LOCATION_ERROR = -1;
	
	/** Locattion OK */
	private static final int LOCATION_OK = 0;
	
	/** NO WOEID */
	private static final int LOCATION_NOWOEID = 1;
	
	/** Get data failed */
	private static final int LOCATION_GET_FAILED = 2;
	
	/** Request get location */
	private static final int REG_GET_LOCATION_SATRT = 100;
	
	/** Request get location finish */
	private static final int REG_GET_LOCATION_FINISH = 101;
	
	/** Request searching */
	private static final int REG_GET_LOCATION_SEARCHING = 102;
	
	/** Search button */
	private Button m_btnSearch;
	
	/** Input country */
	private EditText m_Country;
	
	/** Input city */
	//private EditText m_City;
	
	/** Data model */
	private WeatherDataModel m_DataModel;
	
	/** Preference */
	private WeatherPreferences m_Preference;
	
	/** Dialog */
	ProgressDialog m_Dialog;	
	
	/** Handle request */
	Handler m_HandleRequest;
	
	/** Request result */
	private int m_RequestResult = LOCATION_ERROR;
	private String weatherCityString;
	private TextView weatherCitytextView_;
	

	/*********************************************************
	 * Call when first create activity
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * @author DatNQ
	 *********************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null) {
			weatherCityString = extras.getString("weatherCityKey");
		}
		
		setContentView(R.layout.activity_setting_location);		
		weatherCitytextView_ = (TextView)findViewById(R.id.weatherCityTextView);
		setWeatherCity(weatherCityString);
		
		boolean bResult = false;
		bResult = initializeData();
		if (bResult == true) {
			initializeView();
		}

		if (bResult == false) {
			Log.e(TAG,"onCreate Error");
		}
		
		/* Draw screen */
		drawScreen();		
		
	}
	
	private void setWeatherCity(final String str) {
		runOnUiThread(new Runnable() {
			public void run() {
				weatherCitytextView_.setText(str);
			}
		});
	}

	/*********************************************************
	 * Initialize view element
	 * 
	 * @return true: initialize data success false: initialize data false
	 * @author DatNQ
	 ********************************************************/
	private boolean initializeView() {
		boolean bResult = true;

		m_btnSearch = (Button) findViewById(R.id.btnSearch);
		m_Country = (EditText) findViewById(R.id.inputCountry);
		//m_City = (EditText) findViewById(R.id.inputCity);

		if ( (m_btnSearch == null) || (m_Country == null)){
			Log.e(TAG,"initialize view error");
			bResult = false;
		}
		
		/* Regist handle click */
		m_btnSearch.setOnClickListener(this);

		/* Regist handle */
		initializeHandleRequest();
		
		return bResult;
	}
	
	/***************************************************************************
	 * Handler request
	 * @date May 10, 2011
	 * @time 8:50:24 PM
	 * @author DatNQ
	 **************************************************************************/
	private void initializeHandleRequest(){
	    /* Setting up handler for ProgressBar */
		m_HandleRequest = new Handler(){
			@Override
			public void handleMessage(Message message) {
				int nRequest = message.what;
				
				switch(nRequest){
				case REG_GET_LOCATION_SATRT:
					String strMsg = getString(R.string.strOnSearching);	
					m_Dialog = ProgressDialog.show(ActivityScreenLocation.this, "", strMsg, true);
					
					Message msgRegSearch = new Message();
					msgRegSearch.what = REG_GET_LOCATION_SEARCHING;
					sendMessage(msgRegSearch);
					break;
					
				case REG_GET_LOCATION_SEARCHING:
					m_RequestResult = getDataByLocatition();
					Message msgFinish = new Message();
					msgFinish.what = REG_GET_LOCATION_FINISH;
					sendMessage(msgFinish);					
					break;
					
				case REG_GET_LOCATION_FINISH:
					m_Dialog.dismiss();
					getLocationFinish(m_RequestResult);
					 break;
					 
					 default:
						 Log.e(TAG,"Can not handle this message");
						 break;
				}
			}
        };		
	}

	/***************************************************************************
	 * Initilize data
	 * @return
	 * @date May 10, 2011
	 * @time 8:50:09 PM
	 * @author DatNQ
	 **************************************************************************/
	private boolean initializeData() {
		boolean bResult = true;
		
		m_DataModel = WeatherDataModel.getInstance();
		if (m_DataModel == null){
			Log.e(TAG,"Init data failed");
			return false;
		}
		
		m_Preference = WeatherPreferences.getInstance(getApplicationContext());
		if (m_Preference == null){
			Log.e(TAG,"Can not get preference");
			return false;
		}

		return bResult;
	}

	/***************************************************************************
	 * Draw screen
	 * @date May 10, 2011
	 * @time 8:49:53 PM
	 * @author DatNQ
	 **************************************************************************/
	private void drawScreen() {
		drawTitle();

	}

	/***************************************************************************
	 * Draw title
	 * @date May 10, 2011
	 * @time 8:49:36 PM
	 * @author DatNQ
	 **************************************************************************/
	private void drawTitle() {
	    setTitle(R.string.strSettingLocationTitle);
	}
	
	/***************************************************************************
	 * Get data by locatin
	 * @date May 9, 2011
	 * @time 7:54:47 AM
	 * @author DatNQ
	 **************************************************************************/
	private int getDataByLocatition(){
		String strLocationQuerry = createQueryByCityCountry();
		if (strLocationQuerry == null){
			Log.e(TAG,"Querry invalid");
			return LOCATION_ERROR;
		}
		
		String strWOEID = m_DataModel.getWOEIDByLocation(strLocationQuerry);
		if (strWOEID != null){
			m_Preference.setLocation(strWOEID);
			return LOCATION_OK;
		}
		
		return LOCATION_ERROR;
	}
	
	/***************************************************************************
	 * Create query by city and country
	 * 
	 * @return Query String
	 * @date May 9, 2011
	 * @time 8:02:29 AM
	 * @author DatNQ
	 **************************************************************************/
	private String createQueryByCityCountry(){
		StringBuffer strQuerryBuf = new StringBuffer();
		
		strQuerryBuf.append(m_Country.getText().toString().trim());

		String strQuerryWOEID =  strQuerryBuf.toString().trim();
		if (strQuerryWOEID != null){
			strQuerryWOEID = strQuerryWOEID.replace(" ", "%20");
		}
		
		return strQuerryWOEID;
	}
	
	/***************************************************************************
	 * Create query to get WOEID
	 * @param strQuerry Location
	 * @return
	 * @date May 9, 2011
	 * @time 9:28:07 PM
	 * @author DatNQ
	 **************************************************************************/
	public static String createQuerryGetWoeid(String strQuerry){
		if (strQuerry == null){
			return null;
		}
		
		StringBuffer strQuerryBuf = new StringBuffer(GET_LOCATION_WOEID);
		strQuerryBuf.append(strQuerry);
		strQuerryBuf.append("%22&format=xml");

		return strQuerryBuf.toString();				
	}


	/***************************************************************************
	 * When button press
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * @author DatNQ
	 **************************************************************************/
	public void onClick(View arg0) {
		/* Get woeid */
		getWoeIDByLocation();
	}
	
	/***************************************************************************
	 * Get woeid by location
	 * @date May 10, 2011
	 * @time 8:52:04 PM
	 * @author DatNQ
	 ***************************************************************************/
	private void getWoeIDByLocation(){
		Message regSearchLocation = new Message();
		regSearchLocation.what = REG_GET_LOCATION_SATRT;
		m_HandleRequest.sendMessage(regSearchLocation);
	}
	
	/***************************************************************************
	 * Get location finish
	 * @date May 10, 2011
	 * @time 8:54:23 PM
	 * @author DatNQ
	 **************************************************************************/
	private void getLocationFinish(int nGetResult){
		Intent changeResult = new Intent();
		switch (nGetResult){
		case LOCATION_OK:
			setResult(RESULT_OK, changeResult);
			break;
			
		case LOCATION_ERROR:
		case LOCATION_GET_FAILED:
		case LOCATION_NOWOEID:
			setResult(RESULT_CANCELED, changeResult);
			default:
		}

		finish();		
	}
}