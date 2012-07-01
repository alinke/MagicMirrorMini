package com.exoplatform.weather.view;

import java.util.List;

//import com.exoplatform.weather.R;
import com.diymagicmirror.paidandroid.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//import com.exoplatform.weather.R;

/**************************************************************
 * Adapter for context menu setting adapter
 * 
 * @author DatNQ
 *************************************************************/
public class ContextMenuAdapter extends ArrayAdapter<ContextMenuItem> {
	/** For debugging */
	private static final String TAG = "ContextMenuAdapter";

	/** LayoutInflater to apply view for List */
	private final LayoutInflater inflater;

	/** Context of application */
	private Context m_context = null;

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
	public ContextMenuAdapter(Context context, int MessagewResourceId,
			List<ContextMenuItem> objects) {
		super(context, MessagewResourceId, objects);

		this.m_context = context;
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
		ContextMenuItem item = (ContextMenuItem) getItem(position);
		Resources res = m_context.getResources();
		View view = convertView;

		if (item != null) {
			view = inflater.inflate(R.layout.context_menu_layout, null);
			if (view == null){
				Log.e(TAG, "WTF, why view is null?");
			}
			
			/* Set item ID */
			view.setId(item.getItemID());

			TextView menuLabel = (TextView) view.findViewById(R.id.itemLabel);

			if (menuLabel != null) {
				Theme th = m_context.getTheme();
				TypedValue tv = new TypedValue();

				if (th.resolveAttribute(
						android.R.attr.textAppearanceLargeInverse, tv, true)) {
						menuLabel.setTextAppearance(m_context, tv.resourceId);
				}
				menuLabel.setTag(item);
				menuLabel.setText(item.getItemName());

				Drawable menuIcon = null;
				int nResID = item.getItemImage();
				if ( nResID != ContextMenuItem.INVALID_IMG_RES) {
					menuIcon = res.getDrawable(item.getItemImage());
				}
				menuLabel.setCompoundDrawablesWithIntrinsicBounds(menuIcon, null,
						null, null);
			}
		}

		return view;
	}
}
/******************************************************************************
 * END OF FILE
 ******************************************************************************/