package com.brainsmelt.beerme;

public class EmailRow {
	
	private String TABLE_DRINKS = "Email";
	private long _ID = 0;
	private String TO = "email@gmail.com";
	private String SUBJECT = "Urgent! We need more Drinks!";
	private String BODY = "Hello, we need more drinks please.";

	private byte[] DRINKICON;
	
	public long getId() {
	  return _ID;
	}
	
	public void setId(long id) {
	  this._ID = id;
	}
	
	public String getTO(){
		return TO;
	}
	
	public void setTO(String TO){
		this.TO = TO;
	}
	
	public String getSUBJECT() {
	  return SUBJECT;
	}
	
	public void setSUBJECT (String SUBJECT) {
	  this.SUBJECT = SUBJECT;
	}
	
	public String getBODY() {
	  return BODY;
	}
		
	public void setBODY(String BODY) {
	  this.BODY = BODY;
	}

}
