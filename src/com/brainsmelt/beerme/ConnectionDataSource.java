package com.brainsmelt.beerme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ConnectionDataSource {
	 
	// Database fields
		private SQLiteDatabase database;
		private MySQLiteHelper dbHelper;
		private String[] allColumns = { 
				MySQLiteHelper.COLUMN_ID_CONN,
				MySQLiteHelper.COLUMN_USERNAME,
				MySQLiteHelper.COLUMN_PASSWORD,
				MySQLiteHelper.COLUMN_IP,
				MySQLiteHelper.COLUMN_PORT
		    };
		
		public ConnectionDataSource(Context context) {
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
		public void updateConnectionInfo(String UserName, String Password, String IP, String Port) {
		  //We use the values to update row in SQLite table
		  ContentValues values = new ContentValues();
		  values.put(MySQLiteHelper.COLUMN_ID_CONN, (Long.valueOf(0)));
		  values.put(MySQLiteHelper.COLUMN_USERNAME, UserName);
		  values.put(MySQLiteHelper.COLUMN_PASSWORD, Password);
		  values.put(MySQLiteHelper.COLUMN_IP, IP);
		  values.put(MySQLiteHelper.COLUMN_PORT, Port);

		  //Update (or insert) drink information
		  try{
			  database.insertWithOnConflict(MySQLiteHelper.TABLE_CONNECTION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		  }
		  catch (Exception e) {
			  // TODO: handle exception
			  e.printStackTrace();
		  }
		}
		
		public ConnectionRow getConnectionInfo(Long _id) {
			Cursor cursor = database.query(MySQLiteHelper.TABLE_CONNECTION,
					allColumns, MySQLiteHelper.COLUMN_ID + " = " + _id, null,
				      null, null, null);
			cursor.moveToFirst();
			ConnectionRow newConnRow = cursorToDrinkRow(cursor);
			cursor.close();
			return newConnRow;
	    }

		private ConnectionRow cursorToDrinkRow(Cursor cursor) {
			ConnectionRow connRow = new ConnectionRow();
			connRow.setId(cursor.getLong(0));
			connRow.setUsername(cursor.getString(1));
			connRow.setPassword(cursor.getString(2));
			connRow.setIP(cursor.getString(3));
			connRow.setPort(cursor.getString(4));
		    return connRow;
		}

}
