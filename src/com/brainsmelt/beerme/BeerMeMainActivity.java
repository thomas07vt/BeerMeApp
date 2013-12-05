package com.brainsmelt.beerme;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class BeerMeMainActivity extends Activity {

	//Declare image button variables
	ImageButton iButton0;
	ImageButton iButton1;
	ImageButton iButton2;
	ImageButton iButton3;
	ImageButton iButton4;
	ImageButton iButton5;
	ImageButton iButton6;
	ImageButton iButton7;
	ImageButton iButton8;
	ImageButton iButton9;
	ImageButton iButton10;
	ImageButton iButton11;
	ArrayList<ImageButton> imgButtonList;
	DrinkDataSource datasource;
	
	public static final int RESULT_SETTINGS = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beer_me_main);
		
		 //Create database object
	    datasource = new DrinkDataSource(this);
		
		//Initialize Objects
		iButton0 = (ImageButton) findViewById(R.id.imgButton0);
		iButton1 = (ImageButton) findViewById(R.id.imgButton1);
		iButton2 = (ImageButton) findViewById(R.id.imgButton2);
		iButton3 = (ImageButton) findViewById(R.id.imgButton3);
		iButton4 = (ImageButton) findViewById(R.id.imgButton4);
		iButton5 = (ImageButton) findViewById(R.id.imgButton5);
		iButton6 = (ImageButton) findViewById(R.id.imgButton6);
		iButton7 = (ImageButton) findViewById(R.id.imgButton7);
		iButton8 = (ImageButton) findViewById(R.id.imgButton8);
		iButton9 = (ImageButton) findViewById(R.id.imgButton9);
		iButton10 = (ImageButton) findViewById(R.id.imgButton10);
		iButton11 = (ImageButton) findViewById(R.id.imgButton11);
		imgButtonList = new ArrayList<ImageButton>();
		
		addOnClickListenerToImageButtons();
		//Add ImageButtons to ImageButton List
		creatImageButtonList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.beer_me_main, menu);
		getMenuInflater().inflate(R.menu.beer_me_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.action_Emailsettings:
			Intent intent = new Intent(getBaseContext(),UserSettingsActivity.class);
			startActivity(intent);
			
			break;
		
		//TODO: Finish This :::::::::::::::
		case R.id.action_syncDB:
			//Try to sync DB
			Boolean successSyncBoolean = syncDBfromDrinkMachine();
			
			//If Not Successful
			if(successSyncBoolean == false){
				String toastText = "Could not sync app with the Drink Machine data";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(this, toastText, duration);
				toast.show();
			}
			else {
				//If Successful
				String toastText = "Database successfully synced";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(this, toastText, duration);
				toast.show();
				
				onResume();
			}
			
			
			break;
			
		case R.id.action_UploadDrinkImages:
			//Try to upload Images
			Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
			Boolean success = uploadDrinkIcons();
			
			//If not successful
			if(success == false){
				String toastIconsText = "Icons could not be sent to the drink machine";
				int durationIcon = Toast.LENGTH_SHORT;
				Toast toastIcons = Toast.makeText(this, toastIconsText, durationIcon);
				toastIcons.show();
			}
			else {
				String toastIconsText = "Icons successfully sent to the drink machine";
				int durationIcon = Toast.LENGTH_SHORT;
				Toast toastIcons = Toast.makeText(this, toastIconsText, durationIcon);
				toastIcons.show();
			}
			
			break;
		
		case R.id.action_AdminPanel:
			//Start admin panel intent
			//UserSettingsActivity
			Intent intentAdmin = new Intent(getBaseContext(), ConnectionSettingsActivity.class);
			//Intent intentAdmin = new Intent(getBaseContext(), ConnectionSettingsActivity.class);
			startActivity(intentAdmin);
			
			break;
		}
		
		return true;
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
	    //Open Database to get Drink Icons
	    datasource.open();
	    
	    //Query DB for drink info
	    Map<Long, byte[]> drinkIcons = datasource.getDrinkIcons();
	    
	    if (drinkIcons.isEmpty() == false){
		    //Iterate through HashMap and set DrinkIcons
		    for (Map.Entry<Long, byte[]> entry : drinkIcons.entrySet()) {
		        Long _id = entry.getKey();
		        byte[] drinkIconArray = entry.getValue();
			        if (drinkIconArray != null){
				        Bitmap drinkIconBitmap = BitmapFactory.decodeByteArray(drinkIconArray, 0, drinkIconArray.length);
				        ImageButton tmpImageButton = imgButtonList.get((int)(long)_id);
				        if (drinkIconBitmap != null){
				        	tmpImageButton.setImageBitmap(drinkIconBitmap);
			        }
		        }
		    }
	    }
	}
	
	//This is meant to reduce the heap size significantly
	//See: http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget-android
	@Override
    protected void onDestroy() {
	    super.onDestroy();
	
	    unbindDrawables(findViewById(R.id.BeerMeMainActivityView));
	    System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
        view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
        ((ViewGroup) view).removeAllViews();
        }
    }
	
	private void showDrinkScreen(int i){
		
		Intent intent = new Intent(getBaseContext(), DrinkInfoActivity.class);
		intent.putExtra("ActiveDrink", String.valueOf(i));
		intent.putExtra("ImageURI", String.valueOf(i));
		startActivity(intent);
	}
	
	private Boolean syncDBfromDrinkMachine(){
		
		Connection conn = null;
		String url = "jdbc:mysql://10.0.0.9:3306/";
		String dbName = "drinkmachinecentral";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "Android"; 
		String password = "android";
		try {
			//make connection to MySQL
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,userName,password);
			
			//create query string
	        String getDrinkInfo = "SELECT * From drinkinfo";
	        
	        //execute query and get results
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(getDrinkInfo);
			
			while (results.next()) {
				try{
					//Collect row information
					Long res_id = Long.valueOf(results.getInt("_id"));
					String resDrinkName = results.getString("DrinkName");
					String resCansLeft = results.getString("CansLeft");
					String resCansMax = results.getString("CansMax");
					String resLastDispensed = results.getString("LastDispensed");
					String resLastReplenished = results.getString("LastReplenished");
					byte[] drinkIcon = results.getBytes("DrinkIcon");
					
					//Update SQLite table
					datasource.updateDrinkInfo(res_id, resDrinkName, resCansLeft, resCansMax, resLastDispensed, resLastReplenished, drinkIcon);
				}
				catch (Exception e) {
					continue;
				}
			}

			conn.close();
			results.close();
			
		} catch (Exception e) {
					
			return false;
		}
		
		return true;
	}
	
	private Boolean uploadDrinkIcons(){
	    
	    Connection conn = null;
		String url = "jdbc:mysql://10.0.0.9:3306/";
		String dbName = "drinkmachinecentral";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "Android"; 
		String password = "android";
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,userName,password);

			//Query local SQLite DB for drink info
		    Map<Long, byte[]> drinkIcons = datasource.getDrinkIcons();
		    
		    if (drinkIcons.isEmpty() == false){
			    //Iterate through HashMap and set DrinkIcons
			    for (Map.Entry<Long, byte[]> entry : drinkIcons.entrySet()) {
			        Long _id = entry.getKey();
			        byte[] drinkIconArray = entry.getValue();
			        
			        if (drinkIconArray == null){
			        	continue;
			        }
			        
			        //String iconString = Base64.encodeToString(drinkIconArray, Base64.DEFAULT);
			        String insertTableSQL = "UPDATE drinkinfo "
							+ " SET DrinkIcon = ? WHERE _id = ? ";
			        
					PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
					
					preparedStatement.setBytes(1, drinkIconArray);
					preparedStatement.setLong(2, _id);

					// execute insert SQL statement
					try{
						preparedStatement.executeUpdate();
					}
					catch (Exception e) {
						Toast.makeText(getBaseContext(), " Could Not Update Icon at _id = " + _id.toString(), Toast.LENGTH_LONG).show();
						preparedStatement.close();
						continue;
					}
					preparedStatement.close();
			    }
		    }
		    else {
				return false;
			}

			conn.close();

		} catch (Exception e) {
			
			e.printStackTrace();
			return false;
//			Toast.makeText(getBaseContext(), "Unable to Connect to MySQL server", Toast.LENGTH_LONG).show();
		    
		}
		
		return true;
	}
	
	private void creatImageButtonList() {

		imgButtonList.add(iButton0);
		imgButtonList.add(iButton1);
		imgButtonList.add(iButton2);
		imgButtonList.add(iButton3);
		imgButtonList.add(iButton4);
		imgButtonList.add(iButton5);
		imgButtonList.add(iButton6);
		imgButtonList.add(iButton7);
		imgButtonList.add(iButton8);
		imgButtonList.add(iButton9);
		imgButtonList.add(iButton10);
		imgButtonList.add(iButton11);

	}
	
	private void addOnClickListenerToImageButtons()
	{
		iButton0.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(0);
								
			}
		});
		
		iButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(1);
								
			}
		});
		
		iButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(2);
								
			}
		});
		
		iButton3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(3);
								
			}
		});
		
		iButton4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(4);
								
			}
		});
		
		iButton5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(5);
								
			}
		});
		
		iButton6.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(6);
								
			}
		});
		
		iButton7.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(7);
								
			}
		});
		
		iButton8.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(8);
								
			}
		});
		
		iButton9.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(9);
								
			}
		});
		
		iButton10.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(10);
								
			}
		});
		
		iButton11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				showDrinkScreen(11);
								
			}
		});		
	}
}
