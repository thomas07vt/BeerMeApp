package com.brainsmelt.beerme;

public class DrinkRow {

	//private String TABLE_DRINKS = "Drinks ";
	private long _ID = 0;
	private String DrinkName= "DrinkName";
	private String CANSLEFT = "CansLeft";
	private String CANSMAX = "CansMax";
	private String LASTDISPENSED = "LastDispensed";
	private String LASTREPLENISHED = "LastReplenished";
	private byte[] DRINKICON;
	
	public long getId() {
	  return _ID;
	}
	
	public void setId(long id) {
	  this._ID = id;
	}
	
	public String getDrinkName(){
		return DrinkName;
	}
	
	public void setDrinkName(String DrinkName){
		this.DrinkName = DrinkName;
	}
	
	public String getCansLeft() {
	  return CANSLEFT;
	}
	
	public void setCansLeft(String CANSLEFT) {
	  this.CANSLEFT = CANSLEFT;
	}
	
	public String getCansMax() {
	  return CANSMAX;
	}
		
	public void setCansMax(String CANSMAX) {
	  this.CANSMAX = CANSMAX;
	}
	
	public String getLastDispensed() {
		return LASTDISPENSED;
	}
	
	public void setLastDispensed(String LASTDISPENSED){
		this.LASTDISPENSED = LASTDISPENSED;
	}
	
	public String getLastRefilled(){
		return LASTREPLENISHED;
	}
	
	public void setLastRefilled(String LASTREPLENISHED){
		this.LASTREPLENISHED = LASTREPLENISHED;
	}
	
	public byte[] getDrinkIcon(){
		return DRINKICON;
	}
	
	public void setDrinkIcon(byte[] DRINKICON){
		this.DRINKICON = DRINKICON;
	}
	
	
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
	  return DrinkName;
	}
}
