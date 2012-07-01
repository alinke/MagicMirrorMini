
package com.exoplatform.weather.view;

//import com.exoplatform.weather.R;
import com.diymagicmirror.paidandroid.R;



/**************************************************************
 * Context menu item of menu setting
 * 
 * @author DatNQ
 *************************************************************/
public class ContextMenuItem {
	
	/** Invalid id of menu item */
	public static final int INVALID_ID = -1;
	
	/** Invalid menu item name */
	public static final int INVALID_NAME = R.string.defalt_menu_name;
	
	/** Invalid image resource id */
	public static final int INVALID_IMG_RES = R.drawable.default_menu_item;
	
	
	/** Item id of context menu */
	private int m_ItemID;
	
	/** Name of menu item */
	private int m_NameID;
	
	/** Image icon of menu item */
	private int m_nImageID;
	
	/***************************************************************************
	 * Default constructor of context menu item
	 * 9:50:20 AM
	 * @author DatNQ
	 **************************************************************************/
	public ContextMenuItem(){
		this(INVALID_ID, INVALID_NAME, INVALID_IMG_RES);
	}

	/**********************************************************
	 * Constructor of context menu item
	 * 
	 * @param strItemName
	 * 		Name of menu item
	 * @param imgID
	 * 		ID of image icon for menu item
	 * @author DatNQ
	 **********************************************************/		
	public ContextMenuItem(int nID, int nNameID, int imgID){
		this.m_ItemID = nID;
		this.m_NameID = nNameID;
		this.m_nImageID = imgID;
	}
	
	/***************************************************************************
	 * Get name of setting menu item
	 * @param strName Item name
	 * 9:42:04 AM
	 * @author DatNQ
	 **************************************************************************/
	public void setItemName(int nameID){
		this.m_NameID = nameID;
	}
	
	/***************************************************************************
	 * Get name of setting menu item
	 * @return Name of menu item
	 * 9:42:39 AM
	 * @author DatNQ
	 **************************************************************************/
	public int getItemName(){
		return this.m_NameID;
	}
	
	/***************************************************************************
	 * Set image of menu setting item
	 * @param nResID Resource id of image menu item
	 * 9:43:53 AM
	 * @author DatNQ
	 **************************************************************************/
	public void setItemImage(int nResID){
		this.m_nImageID = nResID;
	}
	
	/***************************************************************************
	 * Get image of menu setting item
	 * @return Resource id of context menu item
	 * 9:43:53 AM
	 * @author DatNQ
	 **************************************************************************/
	public int getItemImage(){
		return this.m_nImageID;
	}
	
	/***************************************************************************
	 * SetID of item menu
	 * @param nID
	 * 9:48:26 AM
	 * @author DatNQ
	 **************************************************************************/
	public void setItemID(int nID){
		this.m_ItemID = nID;
	}
	
	/***************************************************************************
	 * Get Item ID
	 * @return ID of item
	 * 9:49:10 AM
	 * @author DatNQ
	 **************************************************************************/
	public int getItemID(){
		return this.m_ItemID;
	}
}
/******************************************************************************
 * END OF FILE
 ******************************************************************************/