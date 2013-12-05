package com.brainsmelt.beerme;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  //Drink Table variables
  public static final String TABLE_DRINKS = "Drinks ";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_DRINKNAME= "DrinkName";
  public static final String COLUMN_CANSLEFT = "CansLeft";
  public static final String COLUMN_CANSMAX = "CansMax";
  public static final String COLUMN_LASTDISPENSED = "LastDispensed";
  public static final String COLUMN_LASTREPLENISHED = "LastReplenished";
  public static final String COLUMN_DRINKICON = "DrinkIcon";
  
  //Email Table Variables
  public static final String TABLE_EMAIL = "Email ";
  public static final String COLUMN_ID_E = "_id";
  public static final String COLUMN_TO = "EMAILTO";
  public static final String COLUMN_SUBJECT = "Subject";
  public static final String COLUMN_BODY = "Body";
  
  //Connection Settings Table Variables
  public static final String TABLE_CONNECTION = "ConnectionSettings ";
  public static final String COLUMN_ID_CONN = "_id";
  public static final String COLUMN_USERNAME = "Username";
  public static final String COLUMN_PASSWORD = "Password";
  public static final String COLUMN_IP = "IP";
  public static final String COLUMN_PORT = "Port";
  
  //Database Information
  private static final String DATABASE_NAME = "CokeMachine.db";
  private static final int DATABASE_VERSION = 1;

  // Database creation (DRINKS TABLE) SQLite statement
  private static final String DATABASE_CREATE = "create table "
      + TABLE_DRINKS + "(" + COLUMN_ID + " integer primary key autoincrement, " 
	  + COLUMN_DRINKNAME + " text not null, "
      + COLUMN_CANSLEFT + " text not null, "
      + COLUMN_CANSMAX + " text not null, "
      + COLUMN_LASTDISPENSED + " text not null, "
      + COLUMN_LASTREPLENISHED + " text not null, " 
  	  + COLUMN_DRINKICON + " BLOB);"; 
  
  //Database creation (EMAIL TABLE) SQLite statement
  private static final String DATABASE_CREATE_EMAIL = "create table "
	      + TABLE_EMAIL + "(" + COLUMN_ID_E + " integer primary key autoincrement, " 
		  + COLUMN_TO + " text not null, "
	      + COLUMN_SUBJECT + " text not null, "
	      + COLUMN_BODY + " text not null);"; 
  
//Database creation (CONNECTIONS TABLE) SQLite statement
  private static final String DATABASE_CREATE_CONN = "create table "
	      + TABLE_CONNECTION + "(" + COLUMN_ID_CONN + " integer primary key autoincrement, " 
		  + COLUMN_USERNAME + " text not null, "
	      + COLUMN_PASSWORD + " text not null, "
	      + COLUMN_IP + " text not null, "
	      + COLUMN_PORT + " text not null);"; 

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);	 
    database.execSQL(DATABASE_CREATE_EMAIL);
    database.execSQL(DATABASE_CREATE_CONN);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRINKS);
    onCreate(db);
  }

} 
