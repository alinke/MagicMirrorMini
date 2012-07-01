  
package com.exoplatform.weather.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.diymagicmirror.paidandroid.R;


public class ActivityStockList extends ListActivity   {
	
	
	private String stocks;
	private String stocksCSVString;
	private TextView stockEntryTextView_;
	private ListView stockListView_;
	private Editor mEditor;
	private String stockArray[];
	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> stock_list=new ArrayList<String>();

	//DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
	ArrayAdapter<String> adapter;

	/*********************************************************
	 * Call when first create activity
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * @author DatNQ
	 *********************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences prefs = getSharedPreferences("stocklist", MODE_PRIVATE );
		mEditor = prefs.edit();		
		stocks = prefs.getString("stocks","");
		
		setContentView(R.layout.stock_list_layout);		
		stockEntryTextView_ = (TextView)findViewById(R.id.oneStock);
		stockListView_ = (ListView) findViewById(android.R.id.list);
		
		adapter=new ArrayAdapter<String>(this,
			    android.R.layout.simple_list_item_1,
			    stock_list);
			setListAdapter(adapter);	  
		
		//stocks is a csv string, we need to parse it into the listview		
		List<String> stockItemsListArray = Arrays.asList(stocks.split(","));
		
		for(String item: stockItemsListArray){ //now let's parse the array and put each item in the list
			stock_list.add(item);	
		}
		
		//stock_list.add(stocks);	

		stockListView_.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			//@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
			        return onLongListItemClick(v,pos,id);
			}
			
			protected boolean onLongListItemClick(View v, final int pos, long id) {
			        final String str=stockListView_.getItemAtPosition(pos).toString();
			        Log.i("ListView", "onLongListItemClick stirng=" + str);
			        AlertDialog.Builder builder = new  
			                AlertDialog.Builder(ActivityStockList.this);
			            builder.setMessage("Remove Stock?")
			            .setCancelable(false)
			            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			                       public void onClick(DialogInterface dialog, int id) {
			                    	   stock_list.remove(pos);
			                    	   adapter.notifyDataSetChanged();
			                    	   saveStocks(); //we removed a stock so let's be sure and save also
			                       }
			                   })
			                   .setNegativeButton("No", new DialogInterface.OnClickListener() {
			                       public void onClick(DialogInterface dialog, int id) {
			                            dialog.cancel();
			                       }
			                   });
			            AlertDialog alert = builder.create();
			            alert.show();
			            return true;
			        }

			    });
		
	} //end main routine
	
	
	public void addItems(View v) {
		
		if (stockEntryTextView_.getText().toString().equals("")) { //let's make sure it's not blank
			showToast("Stock ticker cannot be blank");			
		}
		else {
			stock_list.add(stockEntryTextView_.getText().toString());
			stockEntryTextView_.setText("");
			adapter.notifyDataSetChanged();
		}	
		
		saveStocks(); //as we add each item, we do a save also
	}
	
	
	public void saveStocks() {

		StringBuilder sb = new StringBuilder();
		for (String s : stock_list)
		{
		    
		   if(sb.length() > 0){
		        sb.append(',');
		    }
		    sb.append(s);
		}

		stocksCSVString = sb.toString();
		//showToast(stocksCSVString);
		mEditor.putString("stocks", stocksCSVString);
		mEditor.commit();
	}
	
	 private void showToast(final String msg) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(ActivityStockList.this, msg, Toast.LENGTH_LONG);
	                toast.show();
				}
			});
		}   
}