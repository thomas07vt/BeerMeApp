package com.brainsmelt.beerme;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectionSettingsActivity extends Activity{
	
	//Define activity class variables
	Button btnSaveCONN;
	EditText txtUsername;
	EditText txtPassword;
	EditText txtIP;
	EditText txtPORT;
	Long _ID;
	ConnectionDataSource datasource;
	
	//Email Parameters
	String cONN_Username;
	String cONN_Password; //Generated based on contextual data
	String cONN_IP;
	String cONN_Port;//Generated based on contextual data
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.admin_panel);
		
		//Set activity variables equal to the items on the UI
		btnSaveCONN = (Button) findViewById(R.id.btnSaveCONNSettings);
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtIP = (EditText) findViewById(R.id.txtIP);
		txtPORT = (EditText) findViewById(R.id.txtPort);
		_ID = (long) 0;
		
		//Create database object
	    datasource = new ConnectionDataSource(this);
		
		//Set maxHeight and maxWidth for TextViews so they do not resize
		txtUsername.setMaxWidth(txtUsername.getWidth());
		txtUsername.setMaxHeight(txtUsername.getHeight());
		txtPassword.setMaxWidth(txtPassword.getWidth());
		txtPassword.setMaxHeight(txtPassword.getHeight());
		txtIP.setMaxWidth(txtIP.getWidth());
		txtIP.setMaxHeight(txtIP.getHeight());
		txtPORT.setMaxWidth(txtPORT.getWidth());
		txtPORT.setMaxHeight(txtPORT.getHeight());
	    
	    //Set OnClickListener to buttons
	    setOnClickListeners();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	    datasource.open();
	    _ID = (long) 0;
	    
	    loadConnectionSettings();
	}

	@Override
	protected void onPause() {
		super.onPause();
	    datasource.close();
	    System.gc();
	}


	private void setOnClickListeners() {
		// TODO Auto-generated method stub
		
		//Add listener to ImgButton
		btnSaveCONN.setOnClickListener(new OnClickListener() {
			  
			@Override
            public void onClick(View arg0) {
				//Get variables
				cONN_Username = txtUsername.getText().toString();
				cONN_Password = txtPassword.getText().toString();
				cONN_IP = txtIP.getText().toString();
				cONN_Port = txtPORT.getText().toString();
				

                //Update EmailDB
				datasource.updateConnectionInfo(cONN_Username, cONN_Password, cONN_IP, cONN_Port);
				
				savedSettings();
            }
        });
		
	}
	
	private void savedSettings(){
		
		String toastText = "Connection Settings Saved";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, toastText, duration);
		toast.show();
		
	}
	
	private void loadConnectionSettings(){
		try{
			
			ConnectionRow connRow = datasource.getConnectionInfo(_ID);
			txtUsername.setText(connRow.getUsername());
			txtPassword.setText(connRow.getPassword());
			txtIP.setText(connRow.getIP());
			txtPORT.setText(connRow.getPort());
		
		}
		catch (Exception e) {
			// TODO: handle exception
			
		}
	}

}
