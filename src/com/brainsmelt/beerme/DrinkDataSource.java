package com.brainsmelt.beerme;

import java.util.HashMap;
import java.util.Map;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DrinkDataSource {
	
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { 
		MySQLiteHelper.COLUMN_ID,
	    MySQLiteHelper.COLUMN_DRINKNAME,
	    MySQLiteHelper.COLUMN_CANSLEFT,
	    MySQLiteHelper.COLUMN_CANSMAX,
	    MySQLiteHelper.COLUMN_LASTDISPENSED,
	    MySQLiteHelper.COLUMN_LASTREPLENISHED,
	    MySQLiteHelper.COLUMN_DRINKICON
	    };
	
	public DrinkDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
	  database = dbHelper.getWritableDatabase();
	}
	
	public boolean isOpen() throws SQLException {
		  return database.isOpen();
	}

	public void close() {
	  dbHelper.close();
	}
	
	//This actually updates the SQLite table
	public void updateDrinkInfo(Long _ID, String DrinkName, String CansLeft,
			String CansMax, String LastDispensed, String LastReplenished, byte[] DrinkIcon) {
	  //We use the values to update row in SQLite table
	  ContentValues values = new ContentValues();
	  values.put(MySQLiteHelper.COLUMN_ID, _ID);
	  values.put(MySQLiteHelper.COLUMN_DRINKNAME, DrinkName);
	  values.put(MySQLiteHelper.COLUMN_CANSLEFT, CansLeft);
	  values.put(MySQLiteHelper.COLUMN_CANSMAX, CansMax);
	  values.put(MySQLiteHelper.COLUMN_LASTDISPENSED, LastDispensed);
	  values.put(MySQLiteHelper.COLUMN_LASTREPLENISHED, LastReplenished);
	  
	  if (DrinkIcon != null){
		  values.put(MySQLiteHelper.COLUMN_DRINKICON, DrinkIcon);
	  }

	  //Update (or insert) drink information
	  try{
		  database.insertWithOnConflict(MySQLiteHelper.TABLE_DRINKS,null, values, SQLiteDatabase.CONFLICT_REPLACE);
	  }
	  catch (Exception e) {
		  // TODO: handle exception
		  e.printStackTrace();
	  }
	}
	
	public DrinkRow getDrinkInfo(Long _id) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_DRINKS,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + _id, null,
			      null, null, null);
		cursor.moveToFirst();
		DrinkRow newDrinkRow = cursorToDrinkRow(cursor);
		cursor.close();
		return newDrinkRow;
    }

	private DrinkRow cursorToDrinkRow(Cursor cursor) {
	    DrinkRow drinkRow = new DrinkRow();
	    drinkRow.setId(cursor.getLong(0));
	    drinkRow.setDrinkName(cursor.getString(1));
	    drinkRow.setCansLeft(cursor.getString(2));
	    drinkRow.setCansMax(cursor.getString(3));
	    drinkRow.setLastDispensed(cursor.getString(4));
	    drinkRow.setLastRefilled(cursor.getString(5));
	    drinkRow.setDrinkIcon(cursor.getBlob(6));
	    return drinkRow;
	}
	
	public Map<Long, byte[]> getDrinkIcons(){
		Map<Long,byte[]> drinkIconMap = new HashMap<Long, byte[]>();
		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_DRINKS,
					allColumns, MySQLiteHelper.COLUMN_ID + " > " + -1, null,
				      null, null, null);
			cursor.moveToFirst();
			while (cursor.isAfterLast() == false){
				DrinkRow newDrinkRow = cursorToDrinkRow(cursor);
				drinkIconMap.put(newDrinkRow.getId(), newDrinkRow.getDrinkIcon());
				cursor.moveToNext();
			}
			
			cursor.close();	
		}
		catch(Exception e){}
		
		return drinkIconMap;
	}
}
