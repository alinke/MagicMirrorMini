package com.diymagicmirror.paidandroid;

//Paid Version

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.view.MenuInflater;
import android.media.MediaPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/*import com.exoplatform.weather.model.WeatherDataModel;
import com.exoplatform.weather.model.WeatherInfo;
import com.exoplatform.weather.model.WeatherPreferences;
import com.exoplatform.weather.model.YahooWeatherHelper;
import com.exoplatform.weather.view.ContextMenuAdapter;
import com.exoplatform.weather.view.ContextMenuItem;*/

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class magicmirror extends IOIOActivity   {
	
	//api key e7fc61dd49597f0cac7871393e8cd971
	//http://samples.openweathermap.org/data/2.5/weather?id=5391997&appid=b1b15e88fa797225412429c1c50c122a1
/*	01d.png  	01n.png  	clear sky
	02d.png  	02n.png  	few clouds
	03d.png  	03n.png  	scattered clouds
	04d.png  	04n.png  	broken clouds
	09d.png  	09n.png  	shower rain
	10d.png  	10n.png  	rain
	11d.png  	11n.png  	thunderstorm
	13d.png  	13n.png  	snow
	50d.png  	50n.png  	mist*/
	
	private TextView proximity_value_;
	private TextView pot_value_;
	private  int proximityPinNumber = 34; //the pin used on IOIO for the alchohol sensor input
	private  int potPinNumber = 35; //the pin used on IOIO for the alchohol sensor input
	private  int weatherSwitchPinNumber = 36; //the pin used on IOIO for the alchohol sensor input
	private  int stockSwitchPinNumber = 37; //the pin used on IOIO for the alchohol sensor input	
	private  int ledPinNumber = 38; //the pin used on IOIO for the alchohol sensor input

	private MediaController mc;
	private VideoView vid;
	
	private CharChangeTimer charchangeTimer; 
	private int i = 0;	
	private int characterChangedFlag = 0;
	private int deviceFound = 0;
	private float potRead = 0;
	private float potReadLast = 0;
	private float potDifference = 0;
	private double proxRead = 0;
	private int proxCounter = 0;
	private int proxMatches = 0;
	private int playingFlag = 0;
	private int idlePlayingFlag = 0;
	private int videoCounter = 0;
	private int weatherVideoCounter = 0;
	private int stockVideoCounter = 0;
	private int brightness;
	private Uri vidPath;
	private Uri vidIdlePath;
	private Uri vidWeatherPathGood;
	private Uri vidWeatherPathOK;
	private Uri vidWeatherPathBad;
	private Uri vidStockPathGood;
	private Uri vidStockPathOK;
	private Uri vidStockPathBad;
	private Uri vidDrink1Path;
	private Uri vidDrink2Path;
	private Uri vidDrink3Path;
	private Uri vidDrink4Path;
	private Uri vidNoInternetPath;
		
	
	private Handler mHandler;
	private static final String TAG = "Magic Mirror";
//	private String character;
	private SharedPreferences prefs;
	private String app_ver;	
	private Resources resources;
	
	private boolean debug;
	private boolean stealth;
	private boolean proximity_sensor;
	private boolean custom_videos;
	private int min_baseline;
	private int max_value;	
	private double stock_goodThreshold;
	private double stock_badThreshold;
	private int character;
	private TextView proximity_label_;
	private TextView pot_label_;
	private TextView textViewResult;
	
	private AssetFileDescriptor ReadyBeepMP3;	
	private AssetFileDescriptor princessCharacterChangeMP3;	
	private AssetFileDescriptor pirateCharacterChangeMP3;	
	private AssetFileDescriptor halloweenCharacterChangeMP3;	
	private AssetFileDescriptor insultCharacterChangeMP3;
	private AssetFileDescriptor customCharacterChangeMP3;
	private MediaPlayer mediaPlayer;
	
	//string text
	///********** Localization String from @string *************
	private String userAcceptanceString; 
	private String userAcceptanceStringTitle;
	private String setupInstructionsString; 
	private String setupInstructionsStringTitle;
	private String notFoundString; 
	private String notFoundStringTitle;
	
	private String instructionsString; 
	private String instructionsStringTitle;
	
	private String batteryLifeString;
	private String batteryLifeStringTitle;
	
	private String blewTooSoonString; 
	private String blewTooSoonStringTitle;
	private String OKText;
	private String AcceptText;
	
	private String level1Result;
	private String level2Result;
	private String level3Result;
	private String level4Result;
	
	private String analyzingText;
	private String justAmomentText;
		
	private String blowForText;
	private String pleaseWaitText;
	private String statusSimulationModeText;
	private String statusResettingText;	
	private String statusInprogressText;
	private String tapTobeginText;
	private String statusReadyText;
	private String statusNotconnectedText;
	private String blowPrompt;
	private String weatherCity;
	
	
	/** Change location */
	private static final int REG_CHANGELOCATION = 1;	
	/** Request get location */
	private static final int REG_CHANGESTOCKS = 5;	
	/** Request get location */
	private static final int REG_GET_WEATHER_START = 100;	
	/** Request get location finish */
	private static final int REG_GET_WEATHER_FINISH = 101;	
	/** Frequency update */
	private static final int ONE_MINUTE = 60*1000;	
	/** Context menu */
	private static final int MENU_CONTEXT_0 = 0;		
	/** Context menu */
	private static final int MENU_CONTEXT_1 = 1;	
	/** Context menu */
	private static final int MENU_CONTEXT_2 = 2;	
	/** Item 1 */
	private static final int SELECT_ITEM_1 = 0;	
	/** Item 2 */
	private static final int SELECT_ITEM_2 = 1;	
	/** Item 3 */
	private static final int SELECT_ITEM_3 = 2;	
	/** Weather infomation */
	/*private WeatherInfo m_WeatherInfo;	
	*//** Weather setting *//*
	private WeatherPreferences m_Preferneces;	
	*//** Model data *//*
	private WeatherDataModel m_DataModel;*/
	/** Icon */
	//private ImageView m_WeatherIcon;
	/** Handle request */
	Handler m_HandleRequest;
	/** Dialog */
	//ProgressDialog m_ProgressDialog;	
	/** Dialog */
	AlertDialog m_Dialog;		
	/** Runable */
	Runnable m_Runnable;	
	/** For adapter of dialog */
	/*private ContextMenuAdapter m_contextAdapter;	*/
	/** Dialog */
	AlertDialog m_Alert;
		/** Temperature */
	private String s_Temperature;		
	private String s_Humimidy;	
	private String s_Visibility;
	private int weatherCode;
	public static String weatherIconCode;
	private String weatherCondition;
	private List<String> lines = new ArrayList<String>();
	//private double stocks[]; //array for the stocks
	private double stockPriceChange = 0;
	private String stocksCSVString;	
    private String[] stockarray;
    private double[] stocks2;
    private BufferedReader bufferReader;
    
    private String idleCustomPath = "/sdcard/Videos/idle_custom.3gp";
    private String noInternetCustomPath = "/sdcard/Videos/no_internet_custom.3gp";
    private String weatherGoodCustomPath = "/sdcard/Videos/weather_good_custom.3gp";
    private String weatherOKCustomPath = "/sdcard/Videos/weather_ok_custom.3gp";
    private String weatherRainCustomPath = "/sdcard/Videos/weather_rain_custom.3gp";    
    private String stockGoodCustomPath = "/sdcard/Videos/stock_up_custom.3gp";
    private String stockNoChangeCustomPath = "/sdcard/Videos/stock_no_change_custom.3gp";
    private String stockBadCustomPath = "/sdcard/Videos/stock_down_custom.3gp";
    private String proximity1CustomPath = "/sdcard/Videos/proximity1_custom.3gp";
    private String proximity2CustomPath = "/sdcard/Videos/proximity2_custom.3gp";

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);  //get rid of title bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  //get rid of notification bar
	          WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //force only portrait mode	
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);	
		SharedPreferences prefs = getSharedPreferences("stocklist", MODE_PRIVATE ); //get the stocks preference, this came from the special stocks data entry screen
		stocksCSVString = prefs.getString("stocks","");
		
        proximity_value_ = (TextView)findViewById(R.id.proximityValue);
		pot_value_ = (TextView)findViewById(R.id.potValue);		
		
		proximity_label_ = (TextView)findViewById(R.id.proximityLabel);
		pot_label_ = (TextView)findViewById(R.id.potLabel);	
		
		textViewResult = (TextView)findViewById(R.id.result);
       
        charchangeTimer = new CharChangeTimer(500,500);
		//screenTimer.start(); 
        
        //**** Alert Box Dialogs, the text comes from strings.xml for locatlization *****
        userAcceptanceString = getResources().getString(R.string.userAcceptanceString);
        userAcceptanceStringTitle = getResources().getString(R.string.userAcceptanceStringTitle);   
        
        setupInstructionsString = getResources().getString(R.string.setupInstructionsString);
        setupInstructionsStringTitle = getResources().getString(R.string.setupInstructionsStringTitle);   
        
        notFoundString = getResources().getString(R.string.notFoundString);
        notFoundStringTitle = getResources().getString(R.string.notFoundStringTitle);   
        
        instructionsString = getResources().getString(R.string.instructionsString);
        instructionsStringTitle = getResources().getString(R.string.instructionsStringTitle);   
        
        batteryLifeString = getResources().getString(R.string.batteryInfoString);
        batteryLifeStringTitle = getResources().getString(R.string.batteryInfoStringTitle);   
        
        blewTooSoonString = getResources().getString(R.string.blewTooSoonString);
        blewTooSoonStringTitle = getResources().getString(R.string.blewTooSoonStringTitle);  
        
        OKText = getResources().getString(R.string.OKText);  
        AcceptText = getResources().getString(R.string.AcceptText);  
        
        analyzingText = getResources().getString(R.string.analyzingText);
        justAmomentText = getResources().getString(R.string.justAmomentText);      
        
        level1Result = getResources().getString(R.string.level1Result);
        level2Result = getResources().getString(R.string.level2Result);
        level3Result = getResources().getString(R.string.level3Result);
        level4Result = getResources().getString(R.string.level4Result);
        
        blowForText = getResources().getString(R.string.blowForText);
        pleaseWaitText = getResources().getString(R.string.pleaseWaitText);
        statusSimulationModeText = getResources().getString(R.string.statusSimulationModeText);
        statusResettingText = getResources().getString(R.string.statusResettingText);
        statusInprogressText = getResources().getString(R.string.statusInprogressText);
        tapTobeginText = getResources().getString(R.string.tapTobeginText);
        statusReadyText = getResources().getString(R.string.statusReadyText);
        statusNotconnectedText = getResources().getString(R.string.statusNotconnectedText);
        blowPrompt = getResources().getString(R.string.blowPrompt);
        
        playingFlag = 0;
        
        //******** preferences code
        resources = this.getResources();
        setPreferences();
        //**********
        
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //disable sleep		 
		
        vid = (VideoView) findViewById(R.id.VideoView);
		mc = new MediaController(this);	
		vid.setOnErrorListener(mOnErrorListener);

		
		Log.v(TAG, "Started"); 
		
		  try
	        {
	            app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
	        }
	        catch (NameNotFoundException e)
	        {
	            Log.v(TAG, e.getMessage());
	        }
		
		// showToast("started");
		 
		 vid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  //this plays after the video has been completed
	            public void onCompletion(MediaPlayer mc) {
	            	
	            	if (stealth == true) {screenOff();} // if we are in stealth mode, turn off the screen
	            	
	            	setVideoPath(); //set it here for the next videos
					
					if (stealth == false) {		
						vid.setVideoURI(vidIdlePath); //change vid to point to the idle videos paths that were already set as opposed to the prox videos or some other sensor path
						vid.start();
						idlePlayingFlag = 1;
					}
					
					playingFlag = 0; //let's get the idle video started before we set the playingflag back to 0
	            }
	            
	        });

        //enableUi(false);
		 
		  //******* Weather Stuff ***************
	        //boolean bResult = initializeData();
	   
	        
	       /* if (bResult == false){
	        	Log.e(TAG,"Init data failed");
	        	 Add notify here and quit app 
	        	finish();
	        	return;
	        }	*/
			        
	        ///********** weather and stock updates functions*********
			
	        new getWeatherCodeTask(
                    "San Francisco",
                    textViewResult).execute();
	        
	     
	        
	        //requestUpdateWeather();  //old yahoo broken api
			stockUpdate();
		 //**************************************
		 
    } //end main function
    
    private OnErrorListener mOnErrorListener = new OnErrorListener() {  //we go here if a video could not be played

        public boolean onError(MediaPlayer mc, int what, int extra) {
            
        	if (character == 4) {  //if we went here, the user was in custom mode but they haven't added any custom files to their sd card
            	        		
        		for (int i=0; i < 5; i++)
        		{
        			showToast("You're in custom mode but haven't added any custom videos to your micro SD card. Either uncheck Custom Videos in Settings and restart or add your custom videos to the SD card path \\Videos and ensure to follow the file naming convention in the Setup Instructions");
        		}
        		
            }  	
            return true;
        }
    };

    
    private void resetVideos() {
  		runOnUiThread(new Runnable() {
  			@Override
  			public void run() {						    	 
  				if (stealth == true) {screenOff();} // if we are in stealth mode, turn off the screen
            	
            	setVideoPath(); //set it here for the next videos
				
				if (stealth == false) {		
					vid.setVideoURI(vidIdlePath); //change vid to point to the idle videos paths that were already set as opposed to the prox videos or some other sensor path
					vid.start();
					idlePlayingFlag = 1;
				}
				
				playingFlag = 0; //let's get the idle video started before we set the playingflag back to 0
  					
  			}
  		});
      }
    
    private void stockUpdate(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {						    	 
				stockPriceChange = 0;
	    		URL textUrl;
				String stringText = null;
				String line = null;
				stockarray = null; 
				stocks2 = null;
				
				if (stocksCSVString != "") { 
				
					try {
					 // String textSource = "http://download.finance.yahoo.com/d/quotes.csv?s=amat,sbux,ge,vz,aapl&f=c1&e=.csv"; //just used this for troubleshooting
					   String textSource = "http://download.finance.yahoo.com/d/quotes.csv?s=" + stocksCSVString + "&f=c1&e=.csv"; //stocks we retrieved from preferences, we should also check if it's null
					   textUrl = new URL(textSource);
				
					   lines.clear();  //we're going to call the stock update multiple times so cannot forget to clear the string array, otherwise we'd be double counting
					   
					//   bufferReader = null;
					   bufferReader = new BufferedReader(new InputStreamReader(textUrl.openStream()));
					   int i = 0;
					   while ((line = bufferReader.readLine()) != null) {
				            lines.add(line);
				        }
					     bufferReader.close();
					     lines.toArray(new String[lines.size()]);
					  } catch (MalformedURLException e) {
						  showToast("Stock updated failed, no Internet connection or invalid stock symbol");
					   e.printStackTrace();
					  
					  } catch (IOException e) {
						  showToast("Stock updated failed, no Internet connection or invalid stock symbol");
					   e.printStackTrace();			
					
					  }	
										
					    stockarray = lines.toArray(new String[lines.size()]); //convert list array to a real array
					    stocks2 = new double[stockarray.length];
					    for(int i=0; i<stockarray.length; i++)
					    {
					       stocks2[i] = Double.parseDouble(stockarray[i]);
					    }
					 
					    for (int j = 0; j < stocks2.length; j++) {				
								stockPriceChange = stockPriceChange + stocks2[j];
						}
					   // showToast("stock array: " + lines.size());
						
						stockPriceChange = (double)(Math.round(stockPriceChange*100))/100; //round the stock sum or loss gain to two decimals
						//showToast("stock sum gain or loss: " + stockPriceChange);
					}	
			}
		});
    }
	
	
	public class CharChangeTimer extends CountDownTimer
	{

		public CharChangeTimer(long startTime, long interval)
			{
				super(startTime, interval);
			}

		@Override
		public void onFinish()
			{
			characterChange();
				
			}

		@Override
		public void onTick(long millisUntilFinished)				{
			//not used
		}
	}

	
	
	private void showNotFound() {	
		AlertDialog.Builder alert=new AlertDialog.Builder(this);
		//alert.setTitle("Not Found").setIcon(R.drawable.icon).setMessage("Please ensure Bluetooth pairing has been completed prior. The Bluetooth pairing code is: 4545.").setNeutralButton("OK", null).show();		
		//alert.setTitle(notFoundStringTitle).setIcon(R.drawable.icon).setMessage(notFoundString).setNeutralButton(OKText, null).show();		
	}
	
	private void characterChange() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				 
				
				if (custom_videos == true) {
				
					if (potRead < 200 && character != 0) {
						 try {
								playprincessCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 0;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
			    	 
			    	 if (potRead > 200 && potRead < 400 && character != 1) {
			    		try {
							playpirateCharacterChangeMP3();
							characterChangedFlag = 1;
							character = 1;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	 }
			    	 
			    	 if (potRead > 400 && potRead < 600 && character != 2) {
			    		 try {
								playhalloweenCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 2;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
			    	 
			    	 if (potRead > 600 && potRead < 800 && character != 3) {
			    		 try {
								playinsultCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 3;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
			    	 
			    	 if (potRead > 800 && character != 4) {
			    		 try {
								playcustomCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 4;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
				}	 
				
				else {  //we're not in custom mode so only 4 characters
					
					if (potRead < 250 && character != 0) {
						 try {
								playprincessCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 0;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
			    	 
			    	 if (potRead > 250 && potRead < 500 && character != 1) {
			    		try {
							playpirateCharacterChangeMP3();
							characterChangedFlag = 1;
							character = 1;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	 }
			    	 
			    	 if (potRead > 500 && potRead < 750 && character != 2) {
			    		 try {
								playhalloweenCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 2;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
			    	 
			    	 if (potRead > 750 && character != 3) {
			    		 try {
								playinsultCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 3;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
					
				}
		    	 
		    	 setVideoPath();
		    	 
		    	 if (stealth == false && characterChangedFlag == 1) {	//then we are playing the idle videos so we need to switch over to the selected character
						vid.pause(); //pause it since it was on the old one and then play again with the new one
						setIdlePath();  //this is a runnable but we don't need since we're already in the main thread
						vid.start();
				 }
		    	 
		    	 characterChangedFlag = 0; //had to add this because the pot value doesn't seem to be so stable and changes quite often, in other words only do the above if the pot value changed enough to charnge the character, not just within the same character
			}
		});
	}	
	
	private void setVideoPath() { //this should be case statement based on the character change, don't need to check the pot again
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				 
				if (custom_videos == true) {
					if (potRead < 200) {
			    		character = 0;
			    		vidIdlePath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idle_princess);
			    		vidNoInternetPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_internet_princess);
	    		 		vidWeatherPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_good_princess); 
	    		 		vidWeatherPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_ok_princess); 
	    		 		vidWeatherPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_rain_princess); 
	    		 		vidStockPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_up_princess); 
	    		 		vidStockPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_no_change_princess); 
	    		 		vidStockPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_down_princess); 
			    		// vidDrink1Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink1_princess); 
			    		// vidDrink2Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink2_princess); 
			    		// vidDrink3Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink3_princess); 
			    		// vidDrink4Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink4_princess); 
			    		 
			    		 if (videoCounter == 1) {
			        		 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity1_princess); //do not add any extension		 
						 }
						 else {
							 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity2_princess); //do not add any extension	
							 videoCounter = 0; //reset it
						 }
			    	 }
			    	 
			    	 if (potRead > 200 && potRead < 400) {
			    		character = 1;
			    		vidIdlePath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idle_pirate);
			    		vidNoInternetPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_internet_pirate);
	    		 		vidWeatherPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_good_pirate); 
	    		 		vidWeatherPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_ok_pirate); 
	    		 		vidWeatherPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_rain_pirate); 
	    		 		vidStockPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_up_pirate); 
	    		 		vidStockPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_no_change_pirate); 
	    		 		vidStockPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_down_pirate); 
			    	//	 vidDrink1Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink1_pirate); 
			    	//	 vidDrink2Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink2_pirate); 
			    	//	 vidDrink3Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink3_pirate); 
			    	//	 vidDrink4Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink4_pirate); 
			    		 
			    		 if (videoCounter == 1) {
			        		 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity1_pirate); //do not add any extension		 
						 }
						 else {
							 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity2_pirate); //do not add any extension	
							 videoCounter = 0; //reset it
						 }
			    	 }
			    	 
			    	 if (potRead > 400 && potRead < 600) {
			    		character = 2;
			    		vidIdlePath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idle_halloween);
			    		vidNoInternetPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_internet_halloween);
	    		 		vidWeatherPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_good_halloween); 
	    		 		vidWeatherPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_ok_halloween); 
	    		 		vidWeatherPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_rain_halloween); 
	    		 		vidStockPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_up_halloween); 		    		 	
	    		 		vidStockPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_no_change_halloween); 
	    		 		vidStockPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_down_halloween); 
			    		 
						 
			    	//	 vidDrink1Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink1_halloween); 
			    	//	 vidDrink2Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink2_halloween); 
			    	//	 vidDrink3Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink3_halloween); 
			    	//	 vidDrink4Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink4_halloween); 
			    		 
			    		 if (videoCounter == 1) {
			        		 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity1_halloween); //do not add any extension		 
						 }
						 else {
							 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity2_halloween); //do not add any extension	
							 videoCounter = 0; //reset it
						 }
			    	 }
			    	 
			    	 if (potRead > 600 && potRead < 800) {
			    		character = 3;
			    		vidIdlePath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idle_insult);
			    		vidNoInternetPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_internet_insult);
	    		 		vidWeatherPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_good_insult); 
	    		 		vidWeatherPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_ok_insult); 
	    		 		vidWeatherPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_rain_insult); 
	    		 		vidStockPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_up_insult); 
	    		 		vidStockPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_no_change_insult); 
	    		 		vidStockPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_down_insult); 
	    		 
			    	//	 vidDrink1Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink1_insult); 
			    	//	 vidDrink2Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink2_insult); 
			    	//	 vidDrink3Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink3_insult); 
			    	//	 vidDrink4Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink4_insult); 		    		 
			    		 
			    		 if (videoCounter == 1) {
			        		 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity1_insult); //do not add any extension		 
						 }
						 else {
							 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity2_insult); //do not add any extension	
							 videoCounter = 0; //reset it
						 }
			    	 }
			    	 
			     	 if (potRead > 800) {  //custom video files read from sd card
			    		 character = 4;
			    		 
			    		 
			    		// Uri myUri = Uri.parse("http://stackoverflow.com") uri example
			    		 
		    		    vidIdlePath = Uri.parse(idleCustomPath);
			    		vidNoInternetPath = Uri.parse(noInternetCustomPath);
	    		 		vidWeatherPathGood = Uri.parse(weatherGoodCustomPath); 
	    		 		vidWeatherPathOK = Uri.parse(weatherOKCustomPath); 
	    		 		vidWeatherPathBad = Uri.parse(weatherRainCustomPath); 
	    		 		vidStockPathGood = Uri.parse(stockGoodCustomPath); 
	    		 		vidStockPathOK = Uri.parse(stockNoChangeCustomPath); 
	    		 		vidStockPathBad = Uri.parse(stockBadCustomPath);  
			    		 
			    		//vidIdlePath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idle_custom);
			    		//vidNoInternetPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_internet_insult);
	    		 		//vidWeatherPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_good_custom); 
	    		 		//vidWeatherPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_ok_custom); 
	    		 		//vidWeatherPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_rain_custom); 
	    		 		//vidStockPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_up_custom); 
	    		 		//vidStockPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_no_change_custom); 
	    		 		//vidStockPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_down_custom); 
			    	//	 vidDrink1Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink1_insult); 
			    	//	 vidDrink2Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink2_insult); 
			    	//	 vidDrink3Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink3_insult); 
			    	//	 vidDrink4Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink4_insult); 		    		 
			    		 
			    		 if (videoCounter == 1) {
			        		 //vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity1_custom); //do not add any extension
			    			 vidPath =  Uri.parse(proximity1CustomPath); 
			    			 
						 }
						 else {
							 //vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity2_custom); //do not add any extension	
							 vidPath =  Uri.parse(proximity2CustomPath); 
							 videoCounter = 0; //reset it
						 }
			    	 } 
				}
				else {  //no custom videos
					
					if (potRead < 250) {
			    		 character = 0;
			    		 
			    		vidIdlePath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idle_princess);
			    		vidNoInternetPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_internet_princess);
	    		 		vidWeatherPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_good_princess); 
	    		 		vidWeatherPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_ok_princess); 
	    		 		vidWeatherPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_rain_princess); 
	    		 		vidStockPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_up_princess); 
	    		 		vidStockPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_no_change_princess); 
	    		 		vidStockPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_down_princess); 
			    		// vidDrink1Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink1_princess); 
			    		// vidDrink2Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink2_princess); 
			    		// vidDrink3Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink3_princess); 
			    		// vidDrink4Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink4_princess); 
			    		 
			    		 if (videoCounter == 1) {
			        		 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity1_princess); //do not add any extension		 
						 }
						 else {
							 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity2_princess); //do not add any extension	
							 videoCounter = 0; //reset it
						 }
			    	 }
			    	 
			    	 if (potRead > 250 && potRead < 500) {
			    		character = 1;
			    		vidIdlePath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idle_pirate);
			    		vidNoInternetPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_internet_pirate);
	    		 		vidWeatherPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_good_pirate); 
	    		 		vidWeatherPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_ok_pirate); 
	    		 		vidWeatherPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_rain_pirate); 
	    		 		vidStockPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_up_pirate); 
	    		 		vidStockPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_no_change_pirate); 
	    		 		vidStockPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_down_pirate); 						 
			    	//	 vidDrink1Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink1_pirate); 
			    	//	 vidDrink2Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink2_pirate); 
			    	//	 vidDrink3Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink3_pirate); 
			    	//	 vidDrink4Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink4_pirate); 
			    		 
			    		 if (videoCounter == 1) {
			        		 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity1_pirate); //do not add any extension		 
						 }
						 else {
							 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity2_pirate); //do not add any extension	
							 videoCounter = 0; //reset it
						 }
			    	 }
			    	 
			    	 if (potRead > 500 && potRead < 750) {
			    		character = 2;
			    		vidIdlePath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idle_halloween);
			    		vidNoInternetPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_internet_halloween);
	    		 		vidWeatherPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_good_halloween); 
	    		 		vidWeatherPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_ok_halloween); 
	    		 		vidWeatherPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_rain_halloween); 
	    		 		vidStockPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_up_halloween); 		    		 	
	    		 		vidStockPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_no_change_halloween); 
	    		 		vidStockPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_down_halloween); 
			    	//	 vidDrink1Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink1_halloween); 
			    	//	 vidDrink2Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink2_halloween); 
			    	//	 vidDrink3Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink3_halloween); 
			    	//	 vidDrink4Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink4_halloween); 
			    		 
			    		 if (videoCounter == 1) {
			        		 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity1_halloween); //do not add any extension		 
						 }
						 else {
							 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity2_halloween); //do not add any extension	
							 videoCounter = 0; //reset it
						 }
			    	 }
			    	 
			    	 if (potRead > 750) {
			    		character = 3;
			    		vidIdlePath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idle_insult);
			    		vidNoInternetPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_internet_insult);
	    		 		vidWeatherPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_good_insult); 
	    		 		vidWeatherPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_ok_insult); 
	    		 		vidWeatherPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.weather_rain_insult); 
	    		 		vidStockPathGood = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_up_insult); 
	    		 		vidStockPathOK = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_no_change_insult); 
	    		 		vidStockPathBad = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stock_down_insult); 
			    	//	 vidDrink1Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink1_insult); 
			    	//	 vidDrink2Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink2_insult); 
			    	//	 vidDrink3Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink3_insult); 
			    	//	 vidDrink4Path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drink4_insult); 		    		 
			    		 
			    		 if (videoCounter == 1) {
			        		 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity1_insult); //do not add any extension		 
						 }
						 else {
							 vidPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.proximity2_insult); //do not add any extension	
							 videoCounter = 0; //reset it
						 }
			    	 }
				}
		    	 
		    	 
			}
		});
	}	
	
	private void setProxPath() { //this should be case statement based on the character change, don't need to check the pot again
		runOnUiThread(new Runnable() {
			@Override
			public void run() {						    	 
		    	 vid.setVideoURI(vidPath);
			}
		});
	}	
	
	private void setIdlePath() { //this should be case statement based on the character change, don't need to check the pot again
		runOnUiThread(new Runnable() {
			@Override
			public void run() {						    	 
		    	 vid.setVideoURI(vidIdlePath);
			}
		});
	}
	
	private void setWeatherPath() { 
		runOnUiThread(new Runnable() {
			@Override
			public void run() {		
				
				if (weatherCondition == "sunny") {
					 vid.setVideoURI(vidWeatherPathGood);
				}
				
				if (weatherCondition == "cold") {
					 vid.setVideoURI(vidWeatherPathOK);
				}
				
				if (weatherCondition == "rain") {
					 vid.setVideoURI(vidWeatherPathBad);
				}
		    	
			}
		});
	}
	
	private void setStockPath() { 
		runOnUiThread(new Runnable() {
			@Override
			public void run() {						    	 
		    	 
				if (stockPriceChange >= stock_goodThreshold) {		
					vid.setVideoURI(vidStockPathGood);
				}
				
				if (stockPriceChange <= stock_badThreshold) {	
					 vid.setVideoURI(vidStockPathBad);					
				}
				
				if ((stockPriceChange < stock_goodThreshold) && (stockPriceChange > stock_badThreshold)) {
					 vid.setVideoURI(vidStockPathOK);
				}
				
				//showToast ("bad threshold " + stock_badThreshold);
				//showToast ("good threshold " + stock_goodThreshold);
				//showToast ("stock price change " + stockPriceChange);
			}
		});
	}
	
	
	class Looper extends BaseIOIOLooper {
		private AnalogInput input_;
		//private PwmOutput led_;
		private DigitalOutput led_;
		private AnalogInput pot_;
		private AnalogInput proximity_;
		
		private DigitalInput weatherSwitch_; 
		private DigitalInput stockSwitch_;
		
		private boolean weatherSwitchValue;
		private boolean stockSwitchValue;

		
		@Override
		public void setup() throws ConnectionLostException {
			try {
				playingFlag = 0;
				pot_ = ioio_.openAnalogInput(potPinNumber);
				pot_.setBuffer(50);
				proximity_ = ioio_.openAnalogInput(proximityPinNumber);
				proximity_.setBuffer(50);
				//led_ = ioio_.openPwmOutput(ledPinNumber, 0);
				led_ = ioio_.openDigitalOutput(ledPinNumber, true);				
				weatherSwitch_ = ioio_.openDigitalInput(weatherSwitchPinNumber, DigitalInput.Spec.Mode.PULL_UP);
				stockSwitch_ = ioio_.openDigitalInput(stockSwitchPinNumber, DigitalInput.Spec.Mode.PULL_UP);
				
				
				//enableUi(true);
								
				hideTroubleshootingScreen();
				
				try {   //play a ready beep letting the user know verbally the IOIO has connected     	
		     		playReadyBeepMP3();
			         } catch (Exception e) {
			           e.printStackTrace();
			      }
				
				try {
					//potRead = pot_.read() * 1000;
					potRead = pot_.readBuffered() *1000;
					potReadLast = potRead;
					Log.v(TAG, "pot read from setup: " + Float.toString(potReadLast));	
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				setPot(Float.toString(potRead));
				//videoCounter++;
				setVideoPath(); //here we set the character and the video paths 
				if (stealth == false) {
					playIdleFirstTimeOnly(); //if stealth is off, then let's play the idle video
				}
			
				
			} catch (ConnectionLostException e) {
				//enableUi(false);
				throw e;
			}
		}
		
		@Override
		public void loop() throws ConnectionLostException {
			try {
				
				//potRead = pot_.read() * 1000;
				potRead = pot_.readBuffered() *1000;
				if (debug == true) { setPot(Float.toString(potRead));}
				potDifference = Math.abs(potRead - potReadLast); //let's check to see if the character pot was changed
				//Log.v(TAG, "pot read: " + Float.toString(potRead));	
				//Log.v(TAG, "pot read last: " + Float.toString(potReadLast));	
				
				if  (potDifference > 100 && playingFlag == 0) { // the difference was high so this means someone turned the knob but don't go here if a sensor video is already playing, play a wrong sound if that was the case, add an else statement to do that				
					//Log.v(TAG, "difference: " + Float.toString(potDifference));	
					potReadLast  = potRead;
					charchangeTimer.start(); //need to have a delay or the character will change multiple times
				}
				
				if (proximity_sensor == true) {		
					//proxRead = proximity_.getVoltage() / .0064;  //at 3.3V, 1 inch = 6.4 mV from the MaxBotix EZ-1 Sensor
					//proxRead = proximity_.getVoltageBuffered() / .0064;
					//proxRead = proximity_.getVoltageBuffered();
					proxRead = 1000 - (proximity_.readBuffered() *1000);
					if (debug == true) { setProx(Double.toString(proxRead));}
					
					
					if (proxRead < min_baseline && playingFlag == 0) {
						
						proxCounter++;
						//led_.setDutyCycle(60);
						//Log.v(TAG, "Proximity Counter: " + proxCounter);	
						if (proxCounter > proxMatches) {	 //proxMatches is set in preferences, default is 10					
							
							playingFlag = 1;	//set this so we don't play two videos at the same time
							proxCounter = 0 ; //reset it for the next time
							screenOn();	
							
							if (vid.isPlaying() == true) { //if we went here, it means the idle video was playing
								vid.pause();  //we also need to reset the video path
							}
							videoCounter++;
							setProxPath();
							vid.start(); //play whatever video the path was pointing to
						}
					}
				}
				
				try {
						weatherSwitchValue = weatherSwitch_.read();
					} catch (InterruptedException e1) {						
						//e1.printStackTrace();
					}
					
					if (weatherSwitchValue == false && playingFlag == 0) {
							//requestUpdateWeather(); //get the latest weather
							
							new getWeatherCodeTask(
				                    "San Francisco",
				                    textViewResult).execute();
							
							playingFlag = 1;
							screenOn();
							
							if (vid.isPlaying() == true) { //if we went here, it means the idle video was playing
								vid.pause();  //we also need to reset the video path
							}
							
							setVideoPath();										
							setWeatherPath(); //set this to one of three based on the yahoo api weather result									
							vid.start();
					}
					
					try {
						stockSwitchValue = stockSwitch_.read();
						if (debug == true) { setPot(Boolean.toString(stockSwitchValue));}
					} catch (InterruptedException e1) {
						
						//e1.printStackTrace();
					}
					
					if (stockSwitchValue == false && playingFlag == 0) {
						//showToast("went to stock");
						playingFlag = 1;
						stockUpdate(); //this was causing problems
									
						if (stocksCSVString != "") { 
								
								//playingFlag = 1;
								screenOn();
								if (vid.isPlaying() == true) { //if we went here, it means the idle video was playing
									vid.pause();  //we also need to reset the video path
								}
								setVideoPath();									
								setStockPath();								
								vid.start();
								
								
						}
						else {
							showToast("You have not entered any stocks yet"); 							
							resetVideos();
							//playingFlag = 0;
						}
						
					}
				
				//try {
				//		weatherSwitch_.waitForValue(false);
					//	if (playingFlag == 0) {
					//		playingFlag = 1;
					//		setWeatherPath();
					//		 vid.start();
					//	}	
						
				//	} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
				//		e1.printStackTrace();
				//	}
					
				//	try {
				//		stockSwitch_.waitForValue(false);
				//		if (playingFlag == 0) {
				//			playingFlag = 1;
				//			setStockPath();
				//			 vid.start();
				//		}	
						//led_.write(true);
				//	} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
				//		e1.printStackTrace();
				//	}
			
				Thread.sleep(10);
			} catch (InterruptedException e) {
				ioio_.disconnect();
			} catch (ConnectionLostException e) {
				//enableUi(false);
				throw e;
			}
		}
	}
	
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//seekBar_.setEnabled(enable);
				//toggleButton_.setEnabled(enable);
			}
		});
	}
	
	private void setProx(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				proximity_value_.setText(str);
			}
		});
	}
	
	private void setPot(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				pot_value_.setText(str);
			}
		});
	}
	
	private void setPotLabel(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				pot_label_.setText(str);
			}
		});
	}
	
	private void setProximityLabel(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				proximity_label_.setText(str);
			}
		});
	}
	
	private void play() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				 vid.start();
			}
		});
	}
	
	private void screenOn() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();  //turn the screen back on
				lp.screenBrightness = brightness / 100.0f;  
				//lp.screenBrightness = 100 / 100.0f;  
				getWindow().setAttributes(lp);
			}
		});
	}
	
	private void screenOff() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();  //turn the screen back on
				lp.screenBrightness = 1 / 100.0f;  
				getWindow().setAttributes(lp);
			}
		});
	}
	
	private void playIdleFirstTimeOnly() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {				
				setIdlePath();  //this is a runnable but we don't need since we're already in the main thread
				vid.start();
			}
		});
	}
	
	private void hideTroubleshootingScreen() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {				
				findViewById(R.id.notConnectedLabel).setVisibility(View.GONE); //we found the board so hide this message
			}
		});
	}
  
    
    private void showToast(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(magicmirror.this, msg, Toast.LENGTH_LONG);
                toast.show();
			}
		});
	}   
    
	private void updatePrefs() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				 if (debug == false) {         	   		
         			findViewById(R.id.proximityValue).setVisibility(View.GONE);
         			findViewById(R.id.potValue).setVisibility(View.GONE);
					//setProximityLabel("Trial Version: Get Full Version to Clear this Message");
         			findViewById(R.id.proximityLabel).setVisibility(View.GONE);
         			findViewById(R.id.potLabel).setVisibility(View.GONE);         			
         		}  
				 else {
					//setProximityLabel("Proximity Sensor:");
					findViewById(R.id.proximityValue).setVisibility(View.VISIBLE);
         			findViewById(R.id.potValue).setVisibility(View.VISIBLE);
         			findViewById(R.id.proximityLabel).setVisibility(View.VISIBLE);
         			findViewById(R.id.potLabel).setVisibility(View.VISIBLE);     
					 
				 }
         	 
         		 if (stealth == true) { //if we are in stealth mode, turn off the screen, this is good for a true mirror effect as the screen will be black and not show through the mirror at all
         		 	screenOff(); 
         		 }
         		 else {
         			screenOn();  
         		 }
			}
		});
	}   
    
   
    
     
    private void playReadyBeepMP3() throws Exception {
    	  
		 ReadyBeepMP3 = getResources().openRawResourceFd(R.raw.connected);
		 
	        if (ReadyBeepMP3 != null) {

	            mediaPlayer = new MediaPlayer();
	            mediaPlayer.setDataSource(ReadyBeepMP3.getFileDescriptor(), ReadyBeepMP3.getStartOffset(), ReadyBeepMP3.getLength());
	            ReadyBeepMP3.close();
	            mediaPlayer.prepare();
	            mediaPlayer.start();
	        }
	    }
    
    private void playprincessCharacterChangeMP3() throws Exception {
  	  
    	princessCharacterChangeMP3 = getResources().openRawResourceFd(R.raw.character_change_princess);
		 
	        if (princessCharacterChangeMP3 != null) {

	            mediaPlayer = new MediaPlayer();
	            mediaPlayer.setDataSource(princessCharacterChangeMP3.getFileDescriptor(), princessCharacterChangeMP3.getStartOffset(), princessCharacterChangeMP3.getLength());
	            princessCharacterChangeMP3.close();
	            mediaPlayer.prepare();
	            mediaPlayer.start();
	        }
	    }
       
    private void playpirateCharacterChangeMP3() throws Exception {
    	  
    	pirateCharacterChangeMP3 = getResources().openRawResourceFd(R.raw.character_change_pirate);
		 
	        if (pirateCharacterChangeMP3 != null) {

	            mediaPlayer = new MediaPlayer();
	            mediaPlayer.setDataSource(pirateCharacterChangeMP3.getFileDescriptor(), pirateCharacterChangeMP3.getStartOffset(), pirateCharacterChangeMP3.getLength());
	            pirateCharacterChangeMP3.close();
	            mediaPlayer.prepare();
	            mediaPlayer.start();
	        }
	    }
    
    private void playhalloweenCharacterChangeMP3() throws Exception {
    	  
    	halloweenCharacterChangeMP3 = getResources().openRawResourceFd(R.raw.character_change_halloween);
		 
	        if (halloweenCharacterChangeMP3 != null) {

	            mediaPlayer = new MediaPlayer();
	            mediaPlayer.setDataSource(halloweenCharacterChangeMP3.getFileDescriptor(), halloweenCharacterChangeMP3.getStartOffset(), halloweenCharacterChangeMP3.getLength());
	            halloweenCharacterChangeMP3.close();
	            mediaPlayer.prepare();
	            mediaPlayer.start();
	        }
	    }
    
    private void playinsultCharacterChangeMP3() throws Exception {
  	  
    	insultCharacterChangeMP3 = getResources().openRawResourceFd(R.raw.character_change_insult);
		 
	        if (insultCharacterChangeMP3 != null) {

	            mediaPlayer = new MediaPlayer();
	            mediaPlayer.setDataSource(insultCharacterChangeMP3.getFileDescriptor(), insultCharacterChangeMP3.getStartOffset(), insultCharacterChangeMP3.getLength());
	            insultCharacterChangeMP3.close();
	            mediaPlayer.prepare();
	            mediaPlayer.start();
	        }
	    }
    
    private void playcustomCharacterChangeMP3() throws Exception {
    	  
    	customCharacterChangeMP3 = getResources().openRawResourceFd(R.raw.character_change_custom);
		 
	        if (insultCharacterChangeMP3 != null) {

	            mediaPlayer = new MediaPlayer();
	            mediaPlayer.setDataSource(customCharacterChangeMP3.getFileDescriptor(), customCharacterChangeMP3.getStartOffset(), customCharacterChangeMP3.getLength());
	            customCharacterChangeMP3.close();
	            mediaPlayer.prepare();
	            mediaPlayer.start();
	        }
	    }
	
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
       
    	 super.onCreateOptionsMenu(menu);
    	
    	vid.pause();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
       
        	
      if (item.getItemId() == R.id.menu_instructions) {
	    	AlertDialog.Builder alert=new AlertDialog.Builder(this);
	      	alert.setTitle(setupInstructionsStringTitle).setIcon(R.drawable.icon).setMessage(setupInstructionsString).setNeutralButton(OKText, null).show();
	   }
	  
	  if (item.getItemId() == R.id.menu_about) {
		  
		    AlertDialog.Builder alert=new AlertDialog.Builder(this);
	      	alert.setTitle(getString(R.string.menu_about_title)).setIcon(R.drawable.icon).setMessage(getString(R.string.menu_about_summary) + "\n\n" + getString(R.string.versionString) + " " + app_ver).setNeutralButton(OKText, null).show();	
	   }
    	
    	if (item.getItemId() == R.id.menu_prefs)
       {
    		
    		Intent intent = new Intent()
           		.setClass(this,
           				com.diymagicmirror.paidandroid.preferences.class);
       
           this.startActivityForResult(intent, 0);
       }
    	
    
   	 /*if (item.getItemId() == R.id.menu_setting) {
  		  
   		selectSetting();
   		 // selectWeatherSetting();	//this is an extra box for setting celsius or farenheight, not needed at the moment
	   }*/
   	 
   	if (item.getItemId() == R.id.menu_stock) {
   		Intent stock_intent = new Intent(getApplicationContext(), com.exoplatform.weather.controller.ActivityStockList.class);
   		//intent.putExtra("weatherCityKey", weatherCity);
   		//startActivityForResult(intent, REG_CHANGELOCATION);  
   		startActivityForResult(stock_intent, REG_CHANGESTOCKS);  
   		//startActivity();  
	   }
              
       return true;
    }
    
  //now let's get data back from the preferences activity below
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) //we'll go into a reset after this
    {
    	
    	super.onActivityResult(reqCode, resCode, data);
    	
    	switch (reqCode){
    	case REG_CHANGELOCATION:
    		//updateDataOfCurrentLocation();
    		//showToast("went here after onactivity weather");
    		break;
    	//case REG_CHANGESTOCKS: //this means the stocks were changed so we need to update the variables
    		//SharedPreferences prefs = getSharedPreferences("stocklist", MODE_PRIVATE ); 
        	//stocksCSVString = prefs.getString("stocks","");
        	//showToast("stock string: " + stocksCSVString);
        //	break;
    		default:
    			//Log.w(TAG,"Not handle request code:"+ reqCode);
    		break;
    	}
    	
    	// if (debug == true) {
    	//	 Toast.makeText(getBaseContext(), "On Activity Result Code: " + reqCode, Toast.LENGTH_LONG).show();
        // }      	
    	
    	setPreferences(); //very important to have this here, after the menu comes back this is called, we'll want to apply the new prefs without having to re-start the app
    	//update stocks
    	
    	vid.start();
    	
    }
    
   private void setPreferences() //here is where we read the shared preferences into variables
    {
     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

  //   character = Integer.valueOf(prefs.getString(   //the selected character 0, 1, 2, 3, 4
    //	        resources.getString(R.string.selected_character),
    //	        resources.getString(R.string.character_default_value)));   
     
       
     proximity_sensor =  prefs.getBoolean("pref_proximity_sensor", true); //show the numeric alcohol value or not
     debug = prefs.getBoolean("pref_debug", false);  //whether or not to show debug text
     stealth = prefs.getBoolean("pref_stealth_mode", false);
     custom_videos = prefs.getBoolean("pref_custom_mode", false);
     
     
     min_baseline = Integer.valueOf(prefs.getString(   
 	        resources.getString(R.string.pref_min_baseline),
 	        resources.getString(R.string.min_baseline)));  
    
     max_value = Integer.valueOf(prefs.getString(   
  	        resources.getString(R.string.pref_max_value),
  	        resources.getString(R.string.max_value)));  
     
     brightness = Integer.valueOf(prefs.getString(   
  	        resources.getString(R.string.pref_screenBrightness),
  	        resources.getString(R.string.screenBrightnessDefault))); 
     
     proxMatches = Integer.valueOf(prefs.getString(   
   	        resources.getString(R.string.pref_prox_matches),
   	        resources.getString(R.string.proximityMatchesDefault))); 
     
     stock_goodThreshold = Double.valueOf(prefs.getString(   
  	        resources.getString(R.string.pref_good_stock_value),
  	        resources.getString(R.string.pref_good_stock_default_value)));  
     
     stock_badThreshold = Double.valueOf(prefs.getString(   
  	        resources.getString(R.string.pref_bad_stock_value),
  	        resources.getString(R.string.pref_bad_stock_default_value)));  
     
    proximityPinNumber = Integer.valueOf(prefs.getString(   
   	        resources.getString(R.string.pref_prox_pinNumber),
   	        resources.getString(R.string.pref_prox_pinNumber_default_value)));  
     
     potPinNumber = Integer.valueOf(prefs.getString(   
   	        resources.getString(R.string.pref_pot_pinNumber),
   	        resources.getString(R.string.pref_pot_pinNumber_default_value)));  
     
     weatherSwitchPinNumber = Integer.valueOf(prefs.getString(   
   	        resources.getString(R.string.pref_weather_pinNumber),
   	        resources.getString(R.string.pref_weather_pinNumber_default_value)));  
     
     stockSwitchPinNumber = Integer.valueOf(prefs.getString(   
   	        resources.getString(R.string.pref_stock_pinNumber),
   	        resources.getString(R.string.pref_stock_pinNumber_default_value)));  
     
     ledPinNumber = Integer.valueOf(prefs.getString(   
   	        resources.getString(R.string.pref_led_pinNumber),
   	        resources.getString(R.string.pref_led_pinNumber_default_value)));  
     
     updatePrefs(); //if the user turned off verbose or stealth, then dynamically update that here
    
 }
   
/*///****** weather functions here *****
   
	private AlertDialog createContextMenuSetting(Context context){
		 Crate menu list 
		AlertDialog dialogMenu = null;
		List<ContextMenuItem> arrMenuItem = null;
		AlertDialog.Builder contextMenu = new AlertDialog.Builder(context);

		 Create menu item of context menu 
		arrMenuItem = _createContextMenuList();
		if (arrMenuItem == null){
			Log.e(TAG,"Can note create dialog item");
			return null;
		}

		this.m_contextAdapter = new ContextMenuAdapter(context,
				0, arrMenuItem);
		contextMenu.setAdapter(m_contextAdapter, new HandleSelectContextMenu());
		contextMenu.setInverseBackgroundForced(true);
		contextMenu.setTitle(R.string.title_context_menu_setting);
		contextMenu.setIcon(R.drawable.ic_context_menu);
		
		dialogMenu = contextMenu.create();
		dialogMenu.setCanceledOnTouchOutside(true);
		
		return dialogMenu;
	}
	
	private List<ContextMenuItem> _createContextMenuList(){
		ArrayList<ContextMenuItem> arrMenuItem = new ArrayList<ContextMenuItem>();

		 Create first menu item base on menu state 
		ContextMenuItem itemContext1 = new ContextMenuItem(
				MENU_CONTEXT_0,
				R.string.context_menu_changeLocation,
				R.drawable.location_ic);

	//	ContextMenuItem itemContext2 = new ContextMenuItem(
		//		MENU_CONTEXT_1,
		//		R.string.context_menu_update_time,
		//		R.drawable.update_time);
		
		ContextMenuItem itemContext2 = new ContextMenuItem(
				MENU_CONTEXT_1,
				R.string.temperature_unit,
				R.drawable.temperature_ic);		
		
		 Add context item to list 
		arrMenuItem.add(itemContext1);
		//arrMenuItem.add(itemContext2);
		arrMenuItem.add(itemContext2);
		return arrMenuItem;
	}		
	
	*//**************************************************************************
	 * Handle select context menu
	 * @author DatNQ
	 *
	 **************************************************************************//*
	private class HandleSelectContextMenu implements 
					android.content.DialogInterface.OnClickListener{
		
		*//*********************************************************
		 * Handle when select context menu item
		 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
		 * @author DatNQ
		 ********************************************************//*
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			switch (which){
			case MENU_CONTEXT_0:
				selectSetting();
				//selectSetting();
				
				break;

		//	case MENU_CONTEXT_1:
				//selectSetting();
				//selectTempFormat();
				//selectTimeIntervalUpdating();
			//	break;
				
			case MENU_CONTEXT_1:
				selectTempFormat();
				
				//selectTempFormat();
				break;
				
				default:
					Log.e(TAG,"Invalid context menu");
					break;
			}
		}
	}	
	
	 *//***************************************************************************
     * Select temperature format
     * @date May 12, 2011
     * @time 11:21:27 PM
     * @author DatNQ
     **************************************************************************//*
    private void selectTempFormat(){
    	final CharSequence[] items = {"Celsius", "Fahrenheit"};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.selectTemperatureUnit);
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	         Check to update setting 
    	    	boolean bIsC = true;
    	    	switch( item ){
    	    	case SELECT_ITEM_1:
    	    		bIsC = true;
    	    		break;
    	    	case SELECT_ITEM_2:
    	    		bIsC = false;
    	    		
    	    		default:
    	    			break;
    	    	}
    	    	
    	    	m_Preferneces.setTempFmt(bIsC);
    	    	m_Alert.dismiss();
    	    	//notifyUpdateTime();
    	    	updateWeatherInfo(m_WeatherInfo);
    	    }
    	});
    	m_Alert = builder.create();  
    	m_Alert.show();
    }    
	
	*//***************************************************************************
	 * Select setting
	 * @date May 12, 2011
	 * @time 11:26:52 PM
	 * @author DatNQ
	 **************************************************************************//*
	private void selectWeatherSetting(){
		m_Dialog = createContextMenuSetting(this);
		if (m_Dialog != null){
			m_Dialog.show();
		}		
	}
   
  
   
   *//***************************************************************************
    * Initialize data
    * @return true if success, false if failed
    * @date May 7, 2011
    * @time 6:48:46 AM
    * @author DatNQ
    **************************************************************************//*
   private boolean initializeData(){
   	 Get application context 
   	Context appContext = this.getApplicationContext();
   	
   	 Get preference instance 
   	m_Preferneces = WeatherPreferences.getInstance(appContext);
   	if (m_Preferneces == null){
   		Log.e(TAG, "Get preference instance failed, check please");
   		return false;
   	}
   	
   	 Get instance of data model 
   	m_DataModel = WeatherDataModel.getInstance();
   	if (m_DataModel == null){
   		Log.e(TAG,"Can not get data model");
   		return false;
   	}

   	initializeHandleRequest();
   	
   	return true;
   }    
   
   *//***************************************************************************
    * Draw weather screen, about if need
    * @date May 7, 2011
    * @time 5:38:13 PM
    * @author DatNQ
    **************************************************************************//*
     
   *//***************************************************************************
    * Change location
    * @date May 9, 2011
    * @time 9:57:52 PM
    * @author DatNQ
    ***************************************************************************//*
   private void updateDataOfCurrentLocation(){
   	requestUpdateWeather();
   }
   
   *//***************************************************************************
    * Update weather information
    * @param weatherInfo
    * @date May 9, 2011
    * @time 3:38:08 AM
    * @author DatNQ
    **************************************************************************//*
   private void updateWeatherInfo(WeatherInfo weatherInfo){
   	if (weatherInfo == null){  //we'll go here if no internet connection
   		Log.e(TAG,"Weather is null");
   		resetVideos(); //make sure and set this back or nothing else will play
   		showToast("Could not retrieve weather, no Internet connection");
   		return;
   	}
   	
   	String strCode = weatherInfo.getCode();
   	int nCode = getImageByCode(strCode);
   	//m_WeatherIcon.setImageResource(nCode);   	
   	
   	//String strForecastHigh = weatherInfo.getForecastHigh();   	
   	String strForecastCode = weatherInfo.getForecastCode();   	
   	//String strForecastHigh2 = weatherInfo.getForecastHigh2();   	
   	//String strForecastCode2 = weatherInfo.getForecastCode2();   	
   	//String strForecastText = weatherInfo.getForecastText();   	
   	//String strForecastText2 = weatherInfo.getForecastText2();   	
   	String strCity = weatherInfo.getCity();
   	String strCountry = weatherInfo.getCountry();
   	//String strDate = weatherInfo.getDate(); 
   	boolean bIsC = m_Preferneces.getTempFmt();
   	
   	weatherCode = Integer.valueOf(strForecastCode.toString());   	
   	getWeatherVideo();
   	//showToast("Weather Condition: " + weatherCondition); //rain, cold, or sunny
   	
   //	String strFmt;
   //	String strTemp = weatherInfo.getTemperature(WeatherInfo.TEMPERATURE_FMT_CELSIUS);
   //	if (bIsC == true){
   //		strFmt = getString(R.string.str_temperature_fmt); 
   //	} else {
   //		strFmt = getString(R.string.str_temperature_fmt_f);
   //		strTemp = WeatherDataModel.convertC2F(strTemp);
  // 	}
   	
   	//String strTemperature = String.format(strFmt, strTemp);
   	
   	//12797534 santa clara woeid
   	
   	//m_TextLocation.setText(weatherInfo.getCity());
   	//m_Temperature.setText(strTemperature);
   	//m_Date.setText(weatherInfo.getDate());
   	
   	//strFmt = getString(R.string.str_humidity_fmt);
   	//String strHumidity = String.format(strFmt, weatherInfo.getHumidity());
   //	s_Humimidy = strHumidity;
   	//m_Humimidy.setText(strHumidity);
   	//strFmt = getString(R.string.str_visi_fmt);
   	//String strVisi = String.format(strFmt, weatherInfo.getVisibility());
   	//m_Visibility.setText(strVisi);
   	
   //	 showToast("Code: " + strCode);
   //	 showToast("Temp: " + strTemp);
   //	 showToast("Temperature: " + strDate);
   	 
   	// showToast("Forecast High: " + strForecastHigh);
  // 	 showToast("Forecast Code: " + strForecastCode);
  // 	 showToast("Forecast Text: " + strForecastText);
   	 
   //	showToast("Forecast High2: " + strForecastHigh2);
   //	showToast("Forecast Code2: " + strForecastCode2);
   //	showToast("Forecast Text2: " + strForecastText2);
   	
   	//showToast("City: " + strCity);
   	//showToast("Country: " + strCountry);  
   	
   //	String both = name + "-" + dest;
    weatherCity = strCity + ", " + strCountry;
  //  showToast(weatherCity);  
   	
   	// String strForecastHigh = weatherInfo.getForecastHigh();
   }
   
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
 
   *//***************************************************************************
	 * Handler request
	 * @date May 10, 2011
	 * @time 8:50:24 PM
	 * @author DatNQ
	 **************************************************************************//*
   private void initializeHandleRequest(){
		m_Runnable = new Runnable(){

			@Override
			public void run() {
				requestUpdateWeather();
			}			
		};
		
		
	     Setting up handler for ProgressBar 
		m_HandleRequest = new Handler(){
			@Override
			public void handleMessage(Message message) {
				int nRequest = message.what;
				
				switch(nRequest){
				case REG_GET_WEATHER_START:
			    	String strWOEID = m_Preferneces.getLocation();
			    	if (strWOEID == null){
			    		Log.e(TAG,"Can not get WOEID");
			    		 showToast("Could not retrive weather, please check Internet connection");
			    		 resetVideos();
			    		//m_ProgressDialog.dismiss();
			    		displayNotifyCation(R.string.strFetchFailed);
			    		return;
			    	} else {
				    	 Get weather information 
				        m_WeatherInfo = m_DataModel.getWeatherData(strWOEID);			
			    	}
					
					Message msgRegSearch = new Message();
					msgRegSearch.what = REG_GET_WEATHER_FINISH;
					sendMessage(msgRegSearch);
					break;
					
				case REG_GET_WEATHER_FINISH:
			    	if (m_WeatherInfo != null){
			    		updateWeatherInfo(m_WeatherInfo);
			    		//notifyUpdateTime();
			    	}
			    	else {
			    		 showToast("Could not retrive weather, please check Internet connection");
			    		 resetVideos();
			    	}
					//m_ProgressDialog.dismiss();
					//m_HandleRequest.postDelayed(m_Runnable, (ONE_MINUTE*m_Preferneces.getTimeUpdate()));
					break;
					 
					 default:
						 Log.e(TAG,"Can not handle this message");
						 showToast("Could not retrive weather, please check Internet connection");
			    		 resetVideos();
					break;
				}
			}
       };		
	}  
   
      
   
   *//***************************************************************************
    * Select setting
    * @date May 7, 2011
    * @time 8:57:55 PM
    * @author DatNQ
    **************************************************************************//*
   	
	 private void requestUpdateWeather(){
	    	Message msgFetchData = new Message();
	    	msgFetchData.what = REG_GET_WEATHER_START;
	    	m_HandleRequest.sendMessage(msgFetchData);    	
	 }
	 
	 private void displayNotifyCation(int nResID){
			Toast.makeText(getApplicationContext(), getString(nResID),
					Toast.LENGTH_LONG).show();    	
	    }
		
	 *//***************************************************************************
	     * Select setting
	     * @date May 7, 2011
	     * @time 8:57:55 PM
	     * @author DatNQ
	     **************************************************************************//*
    private void selectSetting(){
		//Intent intent = new Intent(magicmirror.this,com.exoplatform.weather.controller.ActivityScreenLocation.class);
    	
    	Intent intent = new Intent(getApplicationContext(), com.exoplatform.weather.controller.ActivityScreenLocation.class);
		intent.putExtra("weatherCityKey", weatherCity);
		startActivityForResult(intent, REG_CHANGELOCATION);  
		//startActivity(intent);
		 
	//	Intent i= new Intent(getApplicationContext(), NewActivity.class);
//		i.putExtra("new_variable_name","value");
	//	startActivity(i);
    }
    
    private void getWeatherVideo() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (weatherCode)  //sets the video playlists based on the mode
				{
					case 0: //tornado							
						weatherCondition = "rain";				
						break;
					case 1: //tropical storm					
						weatherCondition = "rain";	
						break;
					case 2: //hurricane					
						weatherCondition = "rain";	
						break;
					case 3: //severe thunderstorms					
						weatherCondition = "rain";	
						break;
					case 4: //thunderstorms					
						weatherCondition = "rain";	
						break;
					case 5: //mixed rain and snow
						weatherCondition = "rain";	
						break;
					case 6: //mised rain and sleet
						weatherCondition = "rain";	
						break;
					case 7: //mised snow and sleet
						weatherCondition = "rain";	
						break;
					case 8: //freezing drizzle
						weatherCondition = "rain";	
						break;
					case 9: //drizzle
						weatherCondition = "rain";	
						break;
					case 10: //freezing rain
						weatherCondition = "rain";	
						break;
					case 11: //showers
						weatherCondition = "rain";	
						break;	
					case 12: //showers
						weatherCondition = "rain";	
						break;	
					case 13: //snow flurries
						weatherCondition = "rain";	
						break;	
					case 14: //light snow showers
						weatherCondition = "rain";	
						break;	
					case 15: //blowing snow
						weatherCondition = "rain";	
						break;
					case 16: //snow
						weatherCondition = "rain";	
						break;
					case 17: //hail
						weatherCondition = "rain";	
						break;
					case 18: //sleet
						weatherCondition = "rain";	
						break;
					case 19: //dust
						weatherCondition = "rain";	
						break;
					case 20: //foggy
						weatherCondition = "rain";	
						break;
					case 21: //haze
						weatherCondition = "rain";	
						break;
					case 22: //smoky
						weatherCondition = "rain";	
						break;
					case 23: //blustery
						weatherCondition = "rain";	
						break;
					case 24: //windy
						weatherCondition = "cold";	
						break;
					case 25: //cold
						weatherCondition = "cold";	
						break;
					case 26: //cloudy
						weatherCondition = "cold";	
						break;
					case 27: //mostly cloudy (day)
						weatherCondition = "cold";	
						break;
					case 28: //mostly cloudy (night)
						weatherCondition = "cold";	
						break;
					case 29: //partly cloudy (night)
						weatherCondition = "cold";	
						break;
					case 30: //partly cloudy (day)
						weatherCondition = "cold";	
						break;
					case 31: //clear (night)
						weatherCondition = "sunny";	
						break;
					case 32: //sunny
						weatherCondition = "sunny";	
						break;
					case 33: //fair (night)
						weatherCondition = "sunny";	
						break;
					case 34: //fair (day)
						weatherCondition = "sunny";	
						break;
					case 35: //mixed rain and hail
						weatherCondition = "rain";	
						break;
					case 36: //hot
						weatherCondition = "sunny";	
						break;
					case 37: //isoldated thunderstorms
						weatherCondition = "rain";	
						break;
					case 38: //scattered thunderstorms
						weatherCondition = "rain";	
						break;
					case 39: //scattered thunderstorms
						weatherCondition = "rain";	
						break;
					case 40: //scattered showers
						weatherCondition = "rain";	
						break;
					case 41: //heavy snow
						weatherCondition = "rain";	
						break;	
					case 42: //scattered snow showers
						weatherCondition = "rain";	
						break;	
					case 43: //heavy snow
						weatherCondition = "rain";	
						break;	
					case 44: //partly cloudy
						weatherCondition = "cold";	
						break;
					case 45: //thundershowers
						weatherCondition = "rain";	
						break;
					case 46: //snow showers
						weatherCondition = "rain";	
						break;
					case 47: //isoldated thundershowers
						weatherCondition = "rain";	
						break;	
					case 3200: //not available
						weatherCondition = "cold";	
						break;						
					default:
						showToast("Could not obtain weather, please check Internet connection");
				}
			}
		});
    }*/
   
   @Override
   public void onDestroy() {
       super.onDestroy();
       vid.stopPlayback();      
       charchangeTimer.cancel();
   }
   
   @Override
   protected void onStop()
{
   // Stop play
   super.onStop();
   vid.stopPlayback();
   
}
   
   @Override
   protected void onPause() {
       super.onPause();
       vid.pause();
   }
   
   protected void onResume() { //this runs after the control is returned to this main activie, ie, after the set stock or set weather things are fun
	   super.onResume();
	   SharedPreferences prefs = getSharedPreferences("stocklist", MODE_PRIVATE ); 
	   stocksCSVString = prefs.getString("stocks","");
	   
	   new getWeatherCodeTask(
               "San Francisco",
               textViewResult).execute();
	  
	   //requestUpdateWeather();
	  // showToast("stock string: " + stocksCSVString);
  
   }
   
   public class getWeatherCodeTask extends AsyncTask<Void, Void, String> {
	    
	    String cityName;
	    TextView tvResult;

	    String dummyAppid = "e7fc61dd49597f0cac7871393e8cd971";
	    String queryWeather = "http://api.openweathermap.org/data/2.5/weather?q=";
	    String queryDummyKey = "&appid=" + dummyAppid;

	    getWeatherCodeTask(String cityName, TextView tvResult){
	        this.cityName = cityName;
	        this.tvResult = tvResult;
	    }

	    @Override
	    protected String doInBackground(Void... params) {
	        String result = "";
	        String queryReturn;

	        String query = null;
	        try {
	            query = queryWeather + URLEncoder.encode(cityName, "UTF-8") + queryDummyKey;
	            queryReturn = sendQuery(query);
	            result += ParseJSON(queryReturn);
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	            queryReturn = e.getMessage();
	        } catch (IOException e) {
	            e.printStackTrace();
	            queryReturn = e.getMessage();
	        }


	        final String finalQueryReturn = query + "\n\n" + queryReturn;
	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                //textViewInfo.setText(finalQueryReturn);
	                //textViewInfo.setText(finalQueryReturn);
	            }
	        });


	        return result;
	    }

	    private void runOnUiThread(Runnable runnable) {
			// TODO Auto-generated method stub
			
		}

		@Override
	    //protected void onPostExecute(String s, String weatherIcon) {
		protected void onPostExecute(String s) {
	       tvResult.setText(s);
	       weatherIconCode = s;
	       
	       if (s.equals("01d") || s.equals("01n")) {
	    	   weatherCondition = "sunny";	
	       }
	       else if (s.equals("02d") || s.equals("02n")) {
	    	   weatherCondition = "sunny";	
	       }
	       else if (s.equals("02d") || s.equals("02n")) {
	    	   weatherCondition = "sunny";	
	       }
	       else if (s.equals("03d") || s.equals("03n")) {
	    	   weatherCondition = "cold";	
	       }
	       else if (s.equals("04d") || s.equals("04n")) {
	    	   weatherCondition = "cold";	
	       }
	       else if (s.equals("09d") || s.equals("09n")) {
	    	   weatherCondition = "rain";	
	       }
	       else if (s.equals("10d") || s.equals("10n")) {
	    	   weatherCondition = "rain";	
	       }
	       else if (s.equals("11d") || s.equals("11n")) {
	    	   weatherCondition = "rain";	
	       }
	       else if (s.equals("13d") || s.equals("13n")) {
	    	   weatherCondition = "cold";	
	       }
	       else if (s.equals("50d") || s.equals("50n")) {
	    	   weatherCondition = "rain";	
	       }
	       else {
	    	   weatherCondition = "sunny";
	       }
	       
	       //showToast(weatherCondition);
	     
	       
	     /*  *	01d.png  	01n.png  	clear sky
	   	02d.png  	02n.png  	few clouds
	   	03d.png  	03n.png  	scattered clouds
	   	04d.png  	04n.png  	broken clouds
	   	09d.png  	09n.png  	shower rain
	   	10d.png  	10n.png  	rain
	   	11d.png  	11n.png  	thunderstorm
	   	13d.png  	13n.png  	snow
	   	50d.png  	50n.png  	mist*/
	       
	    
	    }

	    private String sendQuery(String query) throws IOException {
	        String result = "";

	        URL searchURL = new URL(query);

	        HttpURLConnection httpURLConnection = (HttpURLConnection)searchURL.openConnection();
	        if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
	            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
	            BufferedReader bufferedReader = new BufferedReader(
	                    inputStreamReader,
	                    8192);

	            String line = null;
	            while((line = bufferedReader.readLine()) != null){
	                result += line;
	            }

	            bufferedReader.close();
	        }

	        return result;
	    }

	    private String ParseJSON(String json){
	        String jsonResult = "";

	        try {
	            JSONObject JsonObject = new JSONObject(json);
	            String cod = jsonHelperGetString(JsonObject, "cod");

	            if(cod != null){
	                if(cod.equals("200")){

	                   /* jsonResult += jsonHelperGetString(JsonObject, "name") + "\n";
	                    JSONObject sys = jsonHelperGetJSONObject(JsonObject, "sys");
	                    if(sys != null){
	                        jsonResult += jsonHelperGetString(sys, "country") + "\n";
	                    }
	                    jsonResult += "\n";

	                    JSONObject coord = jsonHelperGetJSONObject(JsonObject, "coord");
	                    if(coord != null){
	                        String lon = jsonHelperGetString(coord, "lon");
	                        String lat = jsonHelperGetString(coord, "lat");
	                        jsonResult += "lon: " + lon + "\n";
	                        jsonResult += "lat: " + lat + "\n";
	                    }
	                    jsonResult += "\n";*/
	                    
	                	  JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
	                      if(weather != null){
	                          for(int i=0; i<weather.length(); i++){
	                              JSONObject thisWeather = weather.getJSONObject(i);
	                              jsonResult += jsonHelperGetString(thisWeather, "icon");
	                             // weatherIcon_ = jsonHelperGetString(thisWeather, "icon");
	                             
	                          }
	                      }
	                	
	                	/*JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
	                    if(weather != null){
	                        for(int i=0; i<weather.length(); i++){
	                            JSONObject thisWeather = weather.getJSONObject(i);
	                            jsonResult += jsonHelperGetString(thisWeather, "icon") + "\n";
	                            jsonResult += "\n";
	                            weatherIcon = jsonHelperGetString(thisWeather, "icon");
	                           
	                        }
	                    }*/

	                }else if(cod.equals("404")){
	                    String message = jsonHelperGetString(JsonObject, "message");
	                    jsonResult += "cod 404: " + message;
	                }
	            }else{
	                jsonResult += "cod == null\n";
	            }

	        } catch (JSONException e) {
	            e.printStackTrace();
	            jsonResult += e.getMessage();
	        }

	        return jsonResult;
	    }

	    private String jsonHelperGetString(JSONObject obj, String k){
	        String v = null;
	        try {
	            v = obj.getString(k);
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }

	        return v;
	    }

	    private JSONObject jsonHelperGetJSONObject(JSONObject obj, String k){
	        JSONObject o = null;

	        try {
	            o = obj.getJSONObject(k);
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }

	        return o;
	    }

	    private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k){
	        JSONArray a = null;

	        try {
	            a = obj.getJSONArray(k);
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }

	        return a;
	    }
	}
   
    
}