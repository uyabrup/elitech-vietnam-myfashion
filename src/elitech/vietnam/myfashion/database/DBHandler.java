/**
 * Aug 5, 2014 8:40:29 AM
 */
package elitech.vietnam.myfashion.database;

import java.util.ArrayList;
import java.util.List;

import elitech.vietnam.myfashion.entities.City;
import elitech.vietnam.myfashion.entities.District;
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
	
	private static final String TABLE_CONFIG			= "config";
	private static final String TABLE_CONFIG_ID			= "id";
	private static final String TABLE_CONFIG_KEY		= "key";
	private static final String TABLE_CONFIG_VALUE		= "value";
	private static final String	CREATE_TABLE_CONFIG		= "CREATE TABLE " + TABLE_CONFIG + "("
				+ TABLE_CONFIG_ID + " INTEGER PRIMARY KEY, "
				+ TABLE_CONFIG_KEY + " TEXT, "
				+ TABLE_CONFIG_VALUE + " TEXT)";
	private static final String DROP_TABLE_CONFIG		= "DROP TABLE IF EXISTS " + TABLE_CONFIG;;

	private static final String	TABLE_MENUITEM			= "menuitem";
	private static final String	TABLE_MENUITEM_ID		= "id";
	private static final String	TABLE_MENUITEM_NAME		= "name";
	private static final String	TABLE_MENUITEM_IMAGE	= "image";
	private static final String	TABLE_MENUITEM_POS		= "pos";
	private static final String	TABLE_MENUITEM_ICON		= "icon";
	private static final String	TABLE_MENUITEM_GROUP	= "id_group";
	private static final String	TABLE_MENUITEM_NUM		= "num";
	private static final String	CREATE_TABLE_MENUITEM	= "CREATE TABLE " + TABLE_MENUITEM + "("
																+ TABLE_MENUITEM_ID + " INTEGER PRIMARY KEY, "
																+ TABLE_MENUITEM_NAME + " TEXT, "
																+ TABLE_MENUITEM_IMAGE + " TEXT, "
																+ TABLE_MENUITEM_ICON + " INTEGER, "
																+ TABLE_MENUITEM_GROUP + " INTEGER, "
																+ TABLE_MENUITEM_NUM + " INTEGER, "
																+ TABLE_MENUITEM_POS + " INTEGER" + ")";
	private static final String DROP_TABLE_MENUITEM 	= "DROP TABLE IF EXISTS " + TABLE_MENUITEM;

	private static final String	TABLE_CITY				= "city";
	private static final String	TABLE_CITY_ID			= "id";
	private static final String	TABLE_CITY_NAME			= "name";
	private static final String	TABLE_CITY_CODE			= "image";
	private static final String	TABLE_CITY_POS			= "pos";
	private static final String	CREATE_TABLE_CITY		= "CREATE TABLE " + TABLE_CITY + "("
			+ TABLE_CITY_ID + " INTEGER PRIMARY KEY, "
			+ TABLE_CITY_NAME + " TEXT, "
			+ TABLE_CITY_CODE + " TEXT, "
			+ TABLE_CITY_POS + " INTEGER)";
	private static final String DROP_TABLE_CITY 		= "DROP TABLE IF EXISTS " + TABLE_CITY;
	
	private static final String	TABLE_DISTRICT			= "district";
	private static final String	TABLE_DISTRICT_ID		= "id";
	private static final String	TABLE_DISTRICT_NAME		= "name";
	private static final String	TABLE_DISTRICT_IDCITY	= "id_city";
	private static final String	TABLE_DISTRICT_POS		= "pos";
	private static final String	TABLE_DISTRICT_SHIP		= "ship";
	private static final String	CREATE_TABLE_DISTRICT	= "CREATE TABLE " + TABLE_DISTRICT + "("
			+ TABLE_DISTRICT_ID + " INTEGER PRIMARY KEY, "
			+ TABLE_DISTRICT_NAME + " TEXT, "
			+ TABLE_DISTRICT_IDCITY + " INTEGER, "
			+ TABLE_DISTRICT_POS + " INTEGER, "
			+ TABLE_DISTRICT_SHIP + " DOUBLE)";
	private static final String DROP_TABLE_DISTRICT 	= "DROP TABLE IF EXISTS " + TABLE_DISTRICT;

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
		db.execSQL(CREATE_TABLE_MENUITEM);
		db.execSQL(CREATE_TABLE_CITY);
		db.execSQL(CREATE_TABLE_DISTRICT);
		db.execSQL(CREATE_TABLE_CONFIG);
		
		ContentValues values = new ContentValues();
		values.put(TABLE_CONFIG_ID, "0");
		values.put(TABLE_CONFIG_KEY, "first_launch");
		values.put(TABLE_CONFIG_VALUE, "true");
		db.insert(TABLE_CONFIG, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE_MENUITEM);
		db.execSQL(DROP_TABLE_CITY);
		db.execSQL(DROP_TABLE_DISTRICT);
		db.execSQL(DROP_TABLE_CONFIG);
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
		db.close();
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
		db.close();
		return result;
	}
	
	public void saveCities(List<City> cities) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_CITY);
		for (City item : cities) {
			ContentValues values = new ContentValues();
			values.put(TABLE_CITY_ID, item.Id);
			values.put(TABLE_CITY_NAME, item.Name);
			values.put(TABLE_CITY_CODE, item.Code);
			values.put(TABLE_CITY_POS, item.Order);
			
			db.insert(TABLE_CITY, null, values);
		}
		db.close();
	}
	
	public List<City> loadCities() {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_CITY;
		Cursor c = db.rawQuery(query, null);
		List<City> result = new ArrayList<>();
		if (c.moveToFirst()) {
			do {
				City item = new City();
				item.Id = c.getInt(c.getColumnIndex(TABLE_CITY_ID));
				item.Name = c.getString(c.getColumnIndex(TABLE_CITY_NAME));
				item.Code = c.getString(c.getColumnIndex(TABLE_CITY_CODE));
				item.Order = c.getInt(c.getColumnIndex(TABLE_CITY_POS));
				result.add(item);
			} while (c.moveToNext());
		}
		db.close();
		return result;
	}
	
	public void saveDistricts(List<District> district) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_DISTRICT);
		for (District item : district) {
			ContentValues values = new ContentValues();
			values.put(TABLE_DISTRICT_ID, item.Id);
			values.put(TABLE_DISTRICT_NAME, item.Name);
			values.put(TABLE_DISTRICT_IDCITY, item.Publish);
			values.put(TABLE_DISTRICT_POS, item.Order);
			values.put(TABLE_DISTRICT_SHIP, item.Ship);
			
			db.insert(TABLE_DISTRICT, null, values);
		}
		db.close();
	}
	
	public List<District> loadDistricts() {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_DISTRICT;
		Cursor c = db.rawQuery(query, null);
		List<District> result = new ArrayList<>();
		if (c.moveToFirst()) {
			do {
				District item = new District();
				item.Id = c.getInt(c.getColumnIndex(TABLE_DISTRICT_ID));
				item.Name = c.getString(c.getColumnIndex(TABLE_DISTRICT_NAME));
				item.Publish = c.getInt(c.getColumnIndex(TABLE_DISTRICT_IDCITY));
				item.Order = c.getInt(c.getColumnIndex(TABLE_DISTRICT_POS));
				item.Ship = c.getInt(c.getColumnIndex(TABLE_DISTRICT_SHIP));
				result.add(item);
			} while (c.moveToNext());
		}
		db.close();
		return result;
	}
	
	public boolean isFirstLaunch() {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT " + TABLE_CONFIG_VALUE + " FROM " + TABLE_CONFIG + " WHERE " + TABLE_CONFIG_KEY + "='first_launch'";
		Cursor c = db.rawQuery(query, null);
		boolean result = true;
		if (c.moveToFirst()) {
			result = Boolean.valueOf(c.getString(c.getColumnIndex(TABLE_CONFIG_VALUE)));
		}
		db.close();
		return result;
	}
	
	public void updateFirstLaunch(boolean value) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TABLE_CONFIG_VALUE, value + "");
		db.update(TABLE_CONFIG, values, TABLE_CONFIG_KEY + " = ?", new String[] {"first_launch"});
		db.close();
	}
}