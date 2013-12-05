package com.brainsmelt.beerme;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserSettingsActivity extends Activity {


	Button btnSaveSettings;
	EditText txtTO;
	EditText txtSubject;
	EditText txtBody;
	Long _ID;
	EmailDataSrouce datasource;
	
	//Email Parameters
	String eMAIL_TO;
	String eMAIL_Subject; //Generated based on contextual data
	String eMAIL_Body; //Generated based on contextual data
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.user_settings_view);
		
		//Create database object
	    datasource = new EmailDataSrouce(this);
	    //Called in onResume() so its not needed here
	    //datasource.open();
	    
	    //initialize activity items
	    btnSaveSettings = (Button) findViewById(R.id.btnSaveEmailSettings);
	    txtTO = (EditText) findViewById(R.id.txtTO);
	    txtSubject = (EditText) findViewById(R.id.txtSubject);
	    txtBody = (EditText) findViewById(R.id.txtBody);    
	    _ID = (long) 0;
	    
	    //Set maxHeight and maxWidth for TextViews so they do not resize
	    txtTO.setMaxWidth(txtTO.getWidth());
	    txtTO.setMaxHeight(txtTO.getHeight());
	    txtSubject.setMaxWidth(txtSubject.getWidth());
	    txtSubject.setMaxHeight(txtSubject.getHeight());
	    txtBody.setMaxWidth(txtBody.getWidth());
	    txtBody.setMaxHeight(txtBody.getHeight());
	    
	    //Set OnClickListener to buttons
	    setOnClickListeners();
		
		
	}
	
	@Override
	protected void onResume() {
	    datasource.open();
	    super.onResume();
	    _ID = (long) 0;
	    
	    loadEmailSettings();
	}

	@Override
	protected void onPause() {
	    datasource.close();
	    super.onPause();
	    System.gc();
	}

	private void setOnClickListeners() {
		// TODO Auto-generated method stub
		//Add listener to ImgButton
		btnSaveSettings.setOnClickListener(new OnClickListener() {
			  
			@Override
            public void onClick(View arg0) {
				//Get variables
				eMAIL_TO = txtTO.getText().toString();	
				eMAIL_Subject = txtSubject.getText().toString();
				eMAIL_Body = txtBody.getText().toString();
                //Update EmailDB
				datasource.updateEMailInfo(eMAIL_TO, eMAIL_Subject, eMAIL_Body);
				
				savedSettings();
            }
        });
		
	}
	
	private void savedSettings(){
		
		String toastText = "Email Settings Saved";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, toastText, duration);
		toast.show();
		
	}
	
	private void loadEmailSettings() {
		
		//Query Database for Email Settings
		try{
			EmailRow emailInfo = datasource.getEmailInfo(_ID);
			//Set Email Settings
			txtTO.setText(emailInfo.getTO());
			txtSubject.setText(emailInfo.getSUBJECT());
			txtBody.setText(emailInfo.getBODY());
		}
		catch (Exception e) {
			//This will happen if there are no email settings
			e.printStackTrace();
			datasource.updateEMailInfo("email@gmail.com", "Urgent! We need more Drinks!", "Hello, We need more berverages. Thank You");
		}
		
	}

}
