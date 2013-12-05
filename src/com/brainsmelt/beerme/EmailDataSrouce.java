package com.brainsmelt.beerme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class EmailDataSrouce {
	
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { 
			MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_TO,
			MySQLiteHelper.COLUMN_SUBJECT,
			MySQLiteHelper.COLUMN_BODY
	    };
	
	public EmailDataSrouce(Context context) {
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
		public void updateEMailInfo(String TO, String SUBJECT, String BODY) {
		  //We use the values to update row in SQLite table
		  ContentValues values = new ContentValues();
		  values.put(MySQLiteHelper.COLUMN_ID, Long.valueOf(0));
		  values.put(MySQLiteHelper.COLUMN_TO, TO);
		  values.put(MySQLiteHelper.COLUMN_SUBJECT, SUBJECT);
		  values.put(MySQLiteHelper.COLUMN_BODY, BODY);

		  //Update (or insert) drink information
		  try{
			  database.insertWithOnConflict(MySQLiteHelper.TABLE_EMAIL, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		  }
		  catch (Exception e) {
			  // TODO: handle exception
			  e.printStackTrace();
		  }
		}
		
		public EmailRow getEmailInfo(Long _id) {
			Cursor cursor = database.query(MySQLiteHelper.TABLE_EMAIL,
					allColumns, MySQLiteHelper.COLUMN_ID + " = " + _id, null,
				      null, null, null);
			cursor.moveToFirst();
			EmailRow newEmailRow = cursorToDrinkRow(cursor);
			cursor.close();
			return newEmailRow;
	    }

		private EmailRow cursorToDrinkRow(Cursor cursor) {
			EmailRow emailRow = new EmailRow();
			emailRow.setId(cursor.getLong(0));
			emailRow.setTO(cursor.getString(1));
			emailRow.setSUBJECT(cursor.getString(2));
			emailRow.setBODY(cursor.getString(3));
		    return emailRow;
		}

}
