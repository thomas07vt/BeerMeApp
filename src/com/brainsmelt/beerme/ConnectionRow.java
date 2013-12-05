package com.brainsmelt.beerme;

public class ConnectionRow {
	
	private String TABLE_CONN = "ConnectionSettings ";
	private long _ID = 0;
	private String USERNAME = "Android";
	private String PASSWORD = "android";
	private String IP = "10.0.0.9";
	private String PORT = "3306";

	
	public long getId() {
	  return _ID;
	}
	
	public void setId(long id) {
	  this._ID = id;
	}
	
	public String getUsername(){
		return USERNAME;
	}
	
	public void setUsername(String USERNAME){
		this.USERNAME = USERNAME;
	}
	
	public String getPassword() {
	  return PASSWORD;
	}
	
	public void setPassword (String PASSWORD) {
	  this.PASSWORD = PASSWORD;
	}
	
	public String getIP() {
	  return IP;
	}
		
	public void setIP(String IP) {
	  this.IP = IP;
	}
	
	public String getPort() {
		  return PORT;
	}
		
	public void setPort(String PORT) {
	  this.PORT = PORT;
	}

}
