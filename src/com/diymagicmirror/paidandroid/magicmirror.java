package com.diymagicmirror.paidandroid;


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
import android.view.ViewConfiguration;
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
import java.lang.reflect.Field;
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



@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class magicmirror extends IOIOActivity   {
	
	private TextView proximity_value_;
	private TextView pot_value_;
	private  int proximityPinNumber = 34; //the pin used on IOIO for the alchohol sensor input
	private  int potPinNumber = 35; //the pin used on IOIO for the alchohol sensor input
	private  int weatherSwitchPinNumber = 4; //the pin used on IOIO for the alchohol sensor input
	private  int stockSwitchPinNumber = 31; //the pin used on IOIO for the alchohol sensor input	
	private  int ledPinNumber = 1; //the pin used on IOIO for the alchohol sensor input

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
	private double stock_goodThreshold;
	private double stock_badThreshold;
	private int character;
	private TextView proximity_label_;
	private TextView pot_label_;
	private TextView textViewResult;
	private TextView weatherButtonValue_;
	private TextView stockButtonValue_;
	
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
	
	
	private static final int REG_CHANGESTOCKS = 5;	
	
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
    
    //private String WeatherZipCountry = "95050,US";
    private String weatherZip_ = "95050";
    private String weatherCity_ = "";
    private String weatherCountry_ = "US";
    private String openWeatherMapAPIKey_ = "";
    private Boolean characterChangePot_ = false;
    private Boolean weatherSwtichNoPullUp_ = false;
    private Boolean stockSwitchNoPullUp_ = false;
    private int proxLowerRange_;
    private int proxUpperRange_;
    
    private String weatherIcon_;
    
    private Boolean weatherSwitchTrigger = false;  //we use this for the touch sensor and make true if touch sensor is enabled
    private Boolean stockSwitchTrigger = false;
    
    private Boolean weatherPinEnable_;
    private Boolean stockPinEnable_;
    private Boolean maxBotixSensor_;
    private Boolean demoMode_;
    

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);  //get rid of title bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    	
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //force only portrait mode	
		
		//getOverflowMenu();  //not needed right now but potentially in future http://stackoverflow.com/questions/19746314/menu-button-doesnt-show-on-nexus-7
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);	
		SharedPreferences prefs = getSharedPreferences("stocklist", MODE_PRIVATE ); //get the stocks preference, this came from the special stocks data entry screen
		stocksCSVString = prefs.getString("stocks","");
		
        proximity_value_ = (TextView)findViewById(R.id.proximityValue);
		pot_value_ = (TextView)findViewById(R.id.potValue);		
		
		proximity_label_ = (TextView)findViewById(R.id.proximityLabel);
		pot_label_ = (TextView)findViewById(R.id.potLabel);	
		
		weatherButtonValue_ = (TextView)findViewById(R.id.weatherButtonValue);	
		stockButtonValue_ = (TextView)findViewById(R.id.stockButtonValue);
		
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
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  //get rid of notification bar
  	          WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
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
		 
		 ///********** weather and stock updates functions*********
	     
		 if (demoMode_ != true) {
		 
			 if (weatherPinEnable_ == true) {
				 new getWeatherCodeTask(weatherZip_,weatherCity_,weatherCountry_, textViewResult,openWeatherMapAPIKey_).execute();
			 }
			 
			 stockUpdate();
		 }
		 
		 //**************************************
		 
		 //added this for users to do a video test without the board
		 potRead = 150;
		 setVideoPath(); //here we set the character and the video paths 
		 if (stealth == false) {
				playIdleFirstTimeOnly(); //if stealth is off, then let's play the idle video
		 }
		 
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
				
					if (potRead < 160 && character != 0) {
						 try {
								playprincessCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 0;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
			    	 
			    	 if (potRead > 160 && potRead < 320 && character != 1) {
			    		try {
							playpirateCharacterChangeMP3();
							characterChangedFlag = 1;
							character = 1;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	 }
			    	 
			    	 if (potRead > 320 && potRead < 480 && character != 2) {
			    		 try {
								playhalloweenCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 2;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
			    	 
			    	 if (potRead > 480 && potRead < 640 && character != 3) {
			    		 try {
								playinsultCharacterChangeMP3();
								characterChangedFlag = 1;
								character = 3;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	 }
			    	 
			    	 if (potRead > 640 && character != 4) {
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
					if (potRead < 160) {
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
			    	 
			    	 if (potRead > 160 && potRead < 320) {
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
			    	 
			    	 if (potRead > 320 && potRead < 480) {
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
			    	 
			    	 if (potRead > 480 && potRead < 640) {
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
			    	 
			     	 if (potRead > 640) {  //custom video files read from sd card
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
				led_ = ioio_.openDigitalOutput(ledPinNumber, false);	
				
				if  (weatherSwtichNoPullUp_ == true) {
					weatherSwitch_ = ioio_.openDigitalInput(weatherSwitchPinNumber, DigitalInput.Spec.Mode.PULL_UP);
					weatherSwitchTrigger = false;  //we use this for the touch sensor and make true if touch sensor is enabled
				   
				    
				}
				else {
					weatherSwitch_ = ioio_.openDigitalInput(weatherSwitchPinNumber, DigitalInput.Spec.Mode.PULL_DOWN);
					weatherSwitchTrigger = true;  
				}
				
				if  (stockSwitchNoPullUp_ == true) {
					stockSwitch_ = ioio_.openDigitalInput(stockSwitchPinNumber, DigitalInput.Spec.Mode.PULL_UP);
					stockSwitchTrigger = false;
				}
				else {
					stockSwitch_ = ioio_.openDigitalInput(stockSwitchPinNumber, DigitalInput.Spec.Mode.PULL_DOWN);  //this is for grove switches
					stockSwitchTrigger = true;
				}
				
				
				//enableUi(true);
								
				hideTroubleshootingScreen();
				
				try {   //play a ready beep letting the user know verbally the IOIO has connected     	
		     		playReadyBeepMP3();
			         } catch (Exception e) {
			           e.printStackTrace();
			      }
				
				if (characterChangePot_ == true) {
				
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
				
				}
				
				
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
				
				potRead = pot_.readBuffered() *1000;
				int potReadInt = (int) potRead;
				if (debug == true && characterChangePot_ == true) { setPot(Integer.toString(potReadInt));}
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
					
					if (maxBotixSensor_ == true) {
						//proxRead = (proximity_.readBuffered() *1000);
						proxRead = proximity_.getVoltageBuffered() / .0064;
					} 
					else {                                                        //using IR sensor
						proxRead = 1000 - (proximity_.readBuffered() *1000);
					}
					
					int proxReadInt = (int) proxRead;
					
					if (debug == true) { setProx(Integer.toString(proxReadInt));}
					
					if ((proxRead > proxLowerRange_) && (proxRead < proxUpperRange_) && playingFlag == 0) {
						
						proxCounter++;
						//led_.setDutyCycle(60);
						//Log.v(TAG, "Proximity Counter: " + proxCounter);	
						if (proxCounter > proxMatches) {	 //proxMatches is set in preferences, default is 10					
							
							led_.write(true);
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
					else {
						led_.write(false);
					}
					
				}
				
				try {
						weatherSwitchValue = weatherSwitch_.read();
						
					if (debug == true && weatherPinEnable_ == true) { setWeatherValue(Boolean.toString(weatherSwitchValue));}  //let's print the weather switch value if in debug mode
						
					} catch (InterruptedException e1) {						
						//e1.printStackTrace();
					}
				
				  
				if (weatherPinEnable_ == true && weatherSwitchValue == weatherSwitchTrigger && playingFlag == 0) {	
					 
					playingFlag = 1;	
						
					if (demoMode_ == true) {
						
						screenOn();
						
						if (vid.isPlaying() == true) { //if we went here, it means the idle video was playing
							vid.pause();  //we also need to reset the video path
						}
						
						setVideoPath();
						//vid.setVideoURI(vidWeatherPathGood);	
						weatherCondition = "sunny";
						setWeatherPath();
						vid.start();
					}
					
					else {
					
						new getWeatherCodeTask(weatherZip_,weatherCity_,weatherCountry_, textViewResult, openWeatherMapAPIKey_).execute();
							
							//to do should we move this into async task?
						  
						   
							screenOn();
							
							if (vid.isPlaying() == true) { //if we went here, it means the idle video was playing
								vid.pause();  //we also need to reset the video path
							}
							
							setVideoPath();										
							setWeatherPath(); //set this to one of three based on the yahoo api weather result									
							vid.start();
					}
				}
					
					try {
						stockSwitchValue = stockSwitch_.read();
						if (debug == true && stockPinEnable_ == true) { setStockValue(Boolean.toString(stockSwitchValue));}
					} catch (InterruptedException e1) {
						
						//e1.printStackTrace();
					}
					
					if (stockPinEnable_ == true && stockSwitchValue == stockSwitchTrigger && playingFlag == 0) {
						
						playingFlag = 1;
						
						if (demoMode_ == true) {
							
							screenOn();
							if (vid.isPlaying() == true) { //if we went here, it means the idle video was playing
								vid.pause();  //we also need to reset the video path
							}	
							setVideoPath();
							stockPriceChange = 9999999;   //making this a high number so it will always return the good stock video
							setStockPath();		
							vid.start();
						}
						
						else {
						
							stockUpdate(); //TO DO need to fix if user entered a bad ticker
										
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
	
	private void setWeatherValue(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				weatherButtonValue_.setText(str);
			}
		});
	}
	
	private void setStockValue(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				stockButtonValue_.setText(str);
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
         			findViewById(R.id.WeatherButtonLabel).setVisibility(View.GONE);
         			findViewById(R.id.weatherButtonValue).setVisibility(View.GONE);
         			findViewById(R.id.stockButtonLabel).setVisibility(View.GONE);
         			findViewById(R.id.stockButtonValue).setVisibility(View.GONE);
         			findViewById(R.id.WeatherForecastLabel).setVisibility(View.GONE);
         			findViewById(R.id.result).setVisibility(View.GONE);
         			findViewById(R.id.instructions).setVisibility(View.GONE);
         			
         			
         			
         		}  
				 else {
					//setProximityLabel("Proximity Sensor:");
					findViewById(R.id.proximityValue).setVisibility(View.VISIBLE);
         			findViewById(R.id.potValue).setVisibility(View.VISIBLE);
         			findViewById(R.id.proximityLabel).setVisibility(View.VISIBLE);
         			findViewById(R.id.potLabel).setVisibility(View.VISIBLE);  
         			findViewById(R.id.result).setVisibility(View.VISIBLE);
         			findViewById(R.id.WeatherButtonLabel).setVisibility(View.VISIBLE);
         			findViewById(R.id.weatherButtonValue).setVisibility(View.VISIBLE);
         			findViewById(R.id.stockButtonLabel).setVisibility(View.VISIBLE);
         			findViewById(R.id.stockButtonValue).setVisibility(View.VISIBLE);
         			findViewById(R.id.WeatherForecastLabel).setVisibility(View.VISIBLE);
         			findViewById(R.id.result).setVisibility(View.VISIBLE);
         			findViewById(R.id.instructions).setVisibility(View.VISIBLE);
					 
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
    	
    	/*switch (reqCode){
    	//case REG_CHANGELOCATION:
    		//updateDataOfCurrentLocation();
    	
    		//break;
    	//case REG_CHANGESTOCKS: //this means the stocks were changed so we need to update the variables
    		//SharedPreferences prefs = getSharedPreferences("stocklist", MODE_PRIVATE ); 
        	//stocksCSVString = prefs.getString("stocks","");
        	
        //	break;
    		default:
    			//Log.w(TAG,"Not handle request code:"+ reqCode);
    		break;
    	}*/
    	
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
     
     debug = prefs.getBoolean("pref_debug", true);  //whether or not to show debug text
 
     proximity_sensor =  prefs.getBoolean("pref_proximity_sensor", false); //show the numeric alcohol value or not
     maxBotixSensor_ = prefs.getBoolean("pref_maxBotixSensor", false);
     demoMode_ = prefs.getBoolean("pref_demoMode", false);
    	
     stealth = prefs.getBoolean("pref_stealth_mode", false);
     custom_videos = prefs.getBoolean("pref_custom_mode", false);
     
     weatherZip_ = prefs.getString(   
   	        resources.getString(R.string.pref_weatherZip),
   	        resources.getString(R.string.pref_weatherZipDefault)); 
     
     weatherCity_ = prefs.getString(   
    	        resources.getString(R.string.pref_weatherCity),
    	        resources.getString(R.string.weatherCityDefaultSummary)); 
     
     openWeatherMapAPIKey_ = prefs.getString(   
    	        resources.getString(R.string.pref_openWeatherMapAPI),
       	        resources.getString(R.string.openWeatherMapAPIDefault)); 
     
     weatherCountry_ = prefs.getString(   
    	        resources.getString(R.string.pref_weatherCountry),
       	        resources.getString(R.string.weatherCountryDefault));
     
     characterChangePot_ = prefs.getBoolean("pref_pot_sensor", false);  
    		    
     weatherPinEnable_ = prefs.getBoolean("pref_weatherPinEnable", false);	
     stockPinEnable_ = prefs.getBoolean("pref_stockPinEnable", false);		
     
     weatherSwtichNoPullUp_ = prefs.getBoolean("pref_weatherPinTouch", false);
     stockSwitchNoPullUp_ = prefs.getBoolean("pref_stockPinTouch", false);
     
     proxLowerRange_ = Integer.valueOf(prefs.getString(   
 	        resources.getString(R.string.pref_proxLowerRange),
 	        resources.getString(R.string.proxLowerRangeDefault)));  
     
     proxUpperRange_ = Integer.valueOf(prefs.getString(   
   	        resources.getString(R.string.pref_proxUpperRange),
   	        resources.getString(R.string.proxUpperRangeDefault))); 
     
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
	   
	   if (demoMode_ != true) {
	   
		   if (weatherPinEnable_ == true) {
			   new getWeatherCodeTask(weatherZip_,weatherCity_,weatherCountry_, textViewResult,openWeatherMapAPIKey_).execute();
		   }
		   
	   }
	  
	   
	  // showToast("stock string: " + stocksCSVString);
  
   }
   
   public class getWeatherCodeTask extends AsyncTask<Void, Void, String> {
	    
	    String zip;
	    String city;
	    String country;
	    TextView tvResult;
	    String openWeatherMapAPIKey_;

	    String OpenWeatherMapAPIKeyDefault = "e7fc61dd49597f0cac7871393e8cd971";
	    
	    String queryWeatherValue_;
	    
	    String queryWeather_;
	    String queryWeatherZip = "http://api.openweathermap.org/data/2.5/weather?zip=";  //if the city is blank, we'll do a zip, country code query
	    String queryWeatherCity = "http://api.openweathermap.org/data/2.5/weather?q=";   // if the city was specified which means international, then we'll do a city, country query
	    String queryDummyKey = "&appid=" + OpenWeatherMapAPIKeyDefault;

	    getWeatherCodeTask(String zip, String city, String country, TextView tvResult, String openWeatherMapAPIKey_){
	        this.zip = zip;
	        this.city = city;
	        this.country = country;
	        this.tvResult = tvResult;
	        this.openWeatherMapAPIKey_ = openWeatherMapAPIKey_;
	    }

	    @Override
	    protected String doInBackground(Void... params) {
	        String result = "";
	        String queryReturn;

	        String query = null;
	        
	        
	        if (openWeatherMapAPIKey_.equals("")) {  
	        	showToast("Please register for a free Open Weather Map API Key at http://openweathermap.com/api and then enter this key in the Settings of this app");
	        }
	        else {
	        	queryDummyKey = "&appid=" + openWeatherMapAPIKey_;
	        }
	        
	                   
	        if (!city.equals("")) {        //if the city is non blank, then it was specified so we'll go international with this and not zip
	        	queryWeather_ = queryWeatherCity;
	        	queryWeatherValue_ = city + "," + country;
	        }
	        else {
	        	queryWeather_ = queryWeatherZip;
	        	queryWeatherValue_ = zip;
	        }
	         
	        try {
	            query = queryWeather_ + URLEncoder.encode(queryWeatherValue_, "UTF-8") + queryDummyKey;
	            //showToast(query);
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
			
		   if (debug) {	
			   tvResult.setText(s);  //show on the screen if debug
		   }
		   
		   if (weatherIconCode == null || weatherIconCode.equals("99999")) {  //had to add this because if the user entered the wrong API key, this will crash
			   weatherIconCode = "99999";
			   showToast("Weather call failed, please check your API key in Settings in this app");
		   }
	       
	       if (weatherIconCode.equals("01d") || weatherIconCode.equals("01n")) {
	    	   weatherCondition = "sunny";	
	       }
	       else if (weatherIconCode.equals("02d") || weatherIconCode.equals("02n")) {
	    	   weatherCondition = "sunny";	
	       }
	       else if (weatherIconCode.equals("02d") || weatherIconCode.equals("02n")) {
	    	   weatherCondition = "sunny";	
	       }
	       else if (weatherIconCode.equals("03d") || weatherIconCode.equals("03n")) {
	    	   weatherCondition = "cold";	
	       }
	       else if (weatherIconCode.equals("04d") || weatherIconCode.equals("04n")) {
	    	   weatherCondition = "cold";	
	       }
	       else if (weatherIconCode.equals("09d") || weatherIconCode.equals("09n")) {
	    	   weatherCondition = "rain";	
	       }
	       else if (weatherIconCode.equals("10d") || weatherIconCode.equals("10n")) {
	    	   weatherCondition = "rain";	
	       }
	       else if (weatherIconCode.equals("11d") || weatherIconCode.equals("11n")) {
	    	   weatherCondition = "rain";	
	       }
	       else if (weatherIconCode.equals("13d") || weatherIconCode.equals("13n")) {
	    	   weatherCondition = "cold";	
	       }
	       else if (weatherIconCode.equals("50d") || weatherIconCode.equals("50n")) {
	    	   weatherCondition = "rain";	
	       }
	       else {
	    	   weatherCondition = "cold";
	       }
	       
	  /*     if (s.equals("01d") || s.equals("01n")) {
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
	       }*/
	       
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

	                    jsonResult += jsonHelperGetString(JsonObject, "name") + "\n";
	                    JSONObject sys = jsonHelperGetJSONObject(JsonObject, "sys");
	                    if(sys != null){
	                        jsonResult += jsonHelperGetString(sys, "country") + "\n";
	                    }
	                    //jsonResult += "\n";

	                   /* JSONObject coord = jsonHelperGetJSONObject(JsonObject, "coord");
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
	                              jsonResult += jsonHelperGetString(thisWeather, "description");
	                              jsonResult += "\n";
	                              jsonResult += "weather code: ";
	                              jsonResult += jsonHelperGetString(thisWeather, "icon");
	                              weatherIconCode = jsonHelperGetString(thisWeather, "icon");
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

	                }
	                
	                else if(cod.equals("404")){
	                    String message = jsonHelperGetString(JsonObject, "message");
	                    jsonResult += "cod 404: " + message;
	                }
	                else if(cod.equals("401")){
	                    String message = jsonHelperGetString(JsonObject, "message");
	                    jsonResult += "cod 401: " + message;
	                    showToast("Invalid Weather API Key");
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
   
   private void getOverflowMenu() {  //not used

	     try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
   
    
}