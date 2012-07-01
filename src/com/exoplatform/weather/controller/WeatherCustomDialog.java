/******************************************************************************
 * Class       : WeatherCustomDialog.java				             		  *
 * For custom dialog for display CV                                           *
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
package com.exoplatform.weather.controller;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.CompoundButton.OnCheckedChangeListener;

//import com.exoplatform.weather.R;
import com.diymagicmirror.paidandroid.R;
import com.exoplatform.weather.model.WeatherPreferences;

/******************************************************************************
 * Custom dialog setting
 * Now i just use only for show my CV
 * 
 * @author DatNQ
 ******************************************************************************/
public class WeatherCustomDialog extends Dialog implements OnClickListener {
	/** TAG for debugging */
	private static final String TAG = "WeatherCustomDialog";
	
	/** Custom dialog size width */
	private static final int CUSTOM_DIALOG_SIZE_WIDTH = 300;
	
	/** Custom dialog size height */
	private static final int CUSTOM_DIALOG_SIZE_HEIGHT = 433;
	
	/** Check icon */
	private CheckBox m_Checkbox;
	
	/** Button dialog */
	private Button m_Button;
	
	/** Context application */
	private Context m_Context;
	
	/** Preference setting */
	private WeatherPreferences m_Setting;
	
	/** Current setting */
	private boolean m_IsShowCustom;
	
	/***************************************************************************
	 * Default constructor
	 * @param context
	 * @date May 7, 2011
	 * @time 5:55:13 PM
	 * @author DatNQ
	 **************************************************************************/
	public WeatherCustomDialog(Context context){
	    super(context);
	    m_Context = context;
	    
	    /* Initialize data */
	    boolean bResult = initializeData();
	    if (bResult == false){
	    	Log.e(TAG,"Initialize data failed");
	    	return;
	    }	    
	    
	    /* Initialize view */
	    bResult = initializeView();
	    if (bResult == false){
	    	Log.e(TAG,"Initialize view failed");
	    	return;
	    }
	    
	    /* Request draw view of dialog */
	    drawDialogCustom();
	}
  
	/***************************************************************************
	 * Draw screen
	 * @date May 7, 2011
	 * @time 5:55:33 PM
	 * @author DatNQ
	 **************************************************************************/
	private void drawDialogCustom(){
	    drawTitle();
	    drawDialogContent();
	}
	
	/***************************************************************************
	 * Init data
	 * @return true if success, failed if unlucky :)
	 * @date May 7, 2011
	 * @time 5:56:12 PM
	 * @author DatNQ
	 **************************************************************************/
	private boolean initializeData(){
		m_Setting = WeatherPreferences.getInstance(m_Context);
		if (m_Setting == null){
			Log.e(TAG,"Can not get instance of setting");
			return false;
		}
		
		/* Get show CV setting */
		m_IsShowCustom = m_Setting.getMyCvSetting();		
		return true;
	}
	
	/***************************************************************************
	 * Initialize view of custom dialog
	 * @return true if success
	 * @date May 7, 2011
	 * @time 5:56:50 PM
	 * @author DatNQ
	 **************************************************************************/
	private boolean initializeView(){
		boolean bResult = getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		if (bResult == false){
			Log.e(TAG,"Can not request feature");
			return false;
		}
		
	    setContentView(R.layout.customdialog_layout);
	    m_Checkbox = (CheckBox)findViewById(R.id.checkbox);
	    m_Button = (Button)findViewById(R.id.btn_done);
	    if ((m_Checkbox == null) || (m_Button == null)){
	    	Log.e(TAG,"Can not get view references");
	    	return false;
	    }
	    
	    return true;
	}
	
	/***************************************************************************
	 * Draw title
	 * @date May 7, 2011
	 * @time 5:57:21 PM
	 * @author DatNQ
	 **************************************************************************/
	private void drawTitle(){
		setTitle(R.string.str_author);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, 
				R.drawable.custom_ic);
	}
	
	/***************************************************************************
	 * Draw body of dialog
	 * @date May 7, 2011
	 * @time 5:57:35 PM
	 * @author DatNQ
	 **************************************************************************/
	private void drawDialogContent(){
	    ScrollView localScrollView = (ScrollView)findViewById(R.id.scroll_view);
	    ViewGroup.LayoutParams localLayoutParams = localScrollView.getLayoutParams();

	    localLayoutParams.height = CUSTOM_DIALOG_SIZE_HEIGHT;
	    localLayoutParams.width = CUSTOM_DIALOG_SIZE_WIDTH;
	    localScrollView.setLayoutParams(localLayoutParams);
	    
	    /* Set check state */
	    if (m_Checkbox != null){
	    	m_Checkbox.setChecked(m_IsShowCustom);
	    	m_Checkbox.setOnCheckedChangeListener(new HandleCustomDialogView());
	    }
	    
	    if (m_Button != null){
	    	m_Button.setOnClickListener(this);
	    }

	}
	
	/***************************************************************************
	 * Handle action
	 * @author DatNQ
	 *
	 **************************************************************************/
	private class HandleCustomDialogView implements OnCheckedChangeListener{

		/***********************************************************************
		 * Handle state check
		 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
		 * @date May 7, 2011
		 * @time 5:58:19 PM
		 * @author DatNQ
		 **********************************************************************/
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			m_Setting.setMyCvSetting(isChecked);
		}
		
	}

	/***************************************************************************
	 * Handle abort from user
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * @date May 7, 2011
	 * @time 5:58:44 PM
	 * @author DatNQ
	 ***************************************************************************/
	@Override
	public void onClick(View v) {
		this.dismiss();
	}
}