/**
 * Aug 5, 2014 8:40:29 AM
 */
package elitech.vietnam.myfashion.database;

import java.util.ArrayList;
import java.util.List;

import elitech.vietnam.myfashion.entities.TradeMark;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Cong
 */
public class DBHandler extends SQLiteOpenHelper {

	private static final int	DB_VERSION				= 1;

	private static final String	DB_NAME					= "shopping";

	private static final String	TABLE_MENUITEM			= "menuitem";
	private static final String	TABLE_MENUITEM_ID		= "id";
	private static final String	TABLE_MENUITEM_NAME		= "name";
	private static final String	TABLE_MENUITEM_IMAGE	= "image";
	private static final String	TABLE_MENUITEM_POS		= "pos";
	private static final String	TABLE_MENUITEM_ICON		= "icon";
	private static final String	TABLE_MENUITEM_GROUP	= "id_group";
	private static final String	TABLE_MENUITEM_NUM		= "num";
	private static final String	CREATE_TABLE_TRADEMARKS	= "CREATE TABLE " + TABLE_MENUITEM + "("
																+ TABLE_MENUITEM_ID + " INTEGER PRIMARY KEY, "
																+ TABLE_MENUITEM_NAME + " TEXT, "
																+ TABLE_MENUITEM_IMAGE + " TEXT, "
																+ TABLE_MENUITEM_ICON + " INTEGER, "
																+ TABLE_MENUITEM_GROUP + " INTEGER, "
																+ TABLE_MENUITEM_NUM + " INTEGER, "
																+ TABLE_MENUITEM_POS + " INTEGER" + ")";
	
	private static final String	TABLE_BESTOFDAY			= "bestofday";

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DBHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_TRADEMARKS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String upgrade = "DROP TABLE IF EXISTS " + TABLE_MENUITEM;
		db.execSQL(upgrade);
		onCreate(db);
	}

	public void saveMenuItem(List<TradeMark> list) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_MENUITEM);
		for (TradeMark item : list) {
			ContentValues values = new ContentValues();
			values.put(TABLE_MENUITEM_ID, item.Id);
			values.put(TABLE_MENUITEM_NAME, item.Name);
			values.put(TABLE_MENUITEM_IMAGE, item.Image);
			values.put(TABLE_MENUITEM_POS, item.Pos);
			values.put(TABLE_MENUITEM_GROUP, item.GroupId);
			values.put(TABLE_MENUITEM_ICON, item.Icon);
			values.put(TABLE_MENUITEM_NUM, item.NumProduct);
			
			db.insert(TABLE_MENUITEM, null, values);
		}
	}
	
	public List<TradeMark> loadMenuItem() {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_MENUITEM;
		Cursor c = db.rawQuery(query, null);
		List<TradeMark> result = new ArrayList<>();
		if (c.moveToFirst()) {
			do {
				TradeMark item = new TradeMark(0, "", 0, 0);
				item.Id = c.getInt(c.getColumnIndex(TABLE_MENUITEM_ID));
				item.Name = c.getString(c.getColumnIndex(TABLE_MENUITEM_NAME));
				item.Image = c.getString(c.getColumnIndex(TABLE_MENUITEM_IMAGE));
				item.Pos = c.getInt(c.getColumnIndex(TABLE_MENUITEM_POS));
				item.GroupId = c.getInt(c.getColumnIndex(TABLE_MENUITEM_GROUP));
				item.Icon = c.getInt(c.getColumnIndex(TABLE_MENUITEM_ICON));
				item.NumProduct = c.getInt(c.getColumnIndex(TABLE_MENUITEM_NUM));
				result.add(item);
			} while (c.moveToNext());
		}
		return result;
	}
}
