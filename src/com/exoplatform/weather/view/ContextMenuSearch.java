package com.exoplatform.weather.view;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

//import com.exoplatform.weather.R;
import com.diymagicmirror.paidandroid.R;

/**************************************************************
 * Adapter for context menu setting adapter
 * 
 * @author DatNQ
 *************************************************************/
public class ContextMenuSearch extends ArrayAdapter<ContextMenuItem> {
	/** For debugging */
	private static final String TAG = "ContextMenuAdapter";

	/** LayoutInflater to apply view for List */
	private final LayoutInflater inflater;


	/**********************************************************
	 * Constructor of ContextMenuSettingAdapter
	 * 
	 * @param context
	 *            Context application
	 * @param MessagewResourceId
	 * @param objects
	 *            List Item
	 * @author DatNQ
	 **********************************************************/	
	public ContextMenuSearch(Context context, int MessagewResourceId,
			List<ContextMenuItem> objects) {
		super(context, MessagewResourceId, objects);

		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/*********************************************************
	 * Inflate view of context menu item
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 * @author DatNQ
	 *********************************************************/
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;

		view = inflater.inflate(R.layout.activity_setting_location, null);
		if (view == null){
			Log.e(TAG, "WTF, why view is null?");
		}
			

		return view;
	}
}
/******************************************************************************
 * END OF FILE
 ******************************************************************************/