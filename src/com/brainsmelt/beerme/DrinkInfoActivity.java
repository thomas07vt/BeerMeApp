package com.brainsmelt.beerme;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.sql.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DrinkInfoActivity extends Activity {

	Button btnDispense;
	Button btnEmailReminder;
	Button btnSave;
	//TextView ID_textView;
	Spinner activeDrinkSpinner;
	EditText txtDrinkName;
	EditText txtCansLeft;
	EditText txtCansMax;
	EditText txtLastDispensed;
	EditText txtLastRefilled;
	ImageButton imgActiveDrink;
	Long _ID;
	DrinkDataSource datasource;
	String selectedImagePath;
	
	//Email Parameters
	String eMAIL_TO;
	String eMAIL_Subject;
	String eMAIL_Body;
	
	//Connection parameters
	String conUsername;
	String conPasword;
	String conIP;
	String conPORT;
	ConnectionDataSource conDatasource;
	
	//Set Request Codes to handle onActivityResult()
	public static final int IMAGE_REQUEST = 1;
	public static final int EMAIL_REQUEST = 2;
	
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.drink_info_layout);
	    
	    //Create database object
	    datasource = new DrinkDataSource(this);
	    //Called in onResume() so its not needed here
	    //datasource.open();
	    
	    //initialize activity items
	    btnDispense = (Button) findViewById(R.id.btnDispense);
	    btnEmailReminder = (Button) findViewById(R.id.btnSaveEmailSettings);
	    btnSave = (Button) findViewById(R.id.btnSave);
	    activeDrinkSpinner = (Spinner)findViewById(R.id.spnDrinkInfo);
	    //ID_textView = (TextView) findViewById(R.id.txtIntentID);
	    txtDrinkName = (EditText) findViewById(R.id.txtDrinkName);
	    txtCansLeft = (EditText) findViewById(R.id.txtCansLeft);
	    txtCansMax = (EditText) findViewById(R.id.txtCansMax);
	    txtLastDispensed = (EditText) findViewById(R.id.txtLastDispensed);
	    txtLastRefilled = (EditText) findViewById(R.id.txtLastRefilled);
	    imgActiveDrink = (ImageButton) findViewById(R.id.imgActiveDrinkImage);
	    
	    
	    //Get ID that is passed from the main activity
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        String value = extras.getString("ActiveDrink");
	        _ID = Long.valueOf(value);
	        activeDrinkSpinner.setSelection(Integer.valueOf(value));
	        //activeDrinkSpinner.seta
	    }
	    
	    //Set OnClickListener to buttons
	    setOnClickListeners();
	    
	    //onResume() gets called after onCreate(), so populateDrinkData() gets called there
	    //Checkout:: http://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
//	    _ID = Long.parseLong((String) ID_textView.getText());
//	    populateDrinkData(_ID);
	    
	}
	
	//This gets called after a picture was selected
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.gc();
	    if (resultCode == RESULT_OK) {
	    	
	    	//Request code 1 = image selector
		    if (requestCode == IMAGE_REQUEST) {
		    	
		        Uri selectedImageUri = data.getData();
		        imgActiveDrink.setImageDrawable(null);
		        System.gc();
		        //I need to get thumbnail version of the image:
		        try {
			        Bitmap bitThumbnailBitmap = getThumbnail(selectedImageUri, this);
			        //Make the thumbnail a square
			        Bitmap croppedBitMapThumBitmap = cropBitmap(bitThumbnailBitmap);
			        imgActiveDrink.setImageBitmap(croppedBitMapThumBitmap);
			        
			        //convert bitmap to string to store in SQLite table
			        byte[] bitMapArray = getBitmapAsByteArray(croppedBitMapThumBitmap);
			        
			        //Update SQLite table with byte[] array
			        updateDrinkImage(_ID, bitMapArray);
			        
		        } catch (Exception e) {
					// TODO: handle exception
		        	e.printStackTrace();
				}
	        }
		    else if (requestCode == EMAIL_REQUEST){
		    	//Do something?
		    }
		    
	    }
	}
	
	
	@Override
	protected void onResume() {
	    super.onResume(); 
	    datasource.open();
	    
	    //Query DB for drink info
	    //_ID = Long.parseLong((String) ID_textView.getText());
	    populateDrinkData(_ID);
	}

	@Override
	protected void onPause() {
	    datasource.close();
	    super.onPause();
	    System.gc();
	}
	
	//This is meant to reduce the heap size significantly
	//See: http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget-android
	@Override
    protected void onDestroy() {
	    super.onDestroy();
	
	    unbindDrawables(findViewById(R.id.DrinkInfoActivityView));
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
        try {
        	((ViewGroup) view).removeAllViews();
		} catch (Exception e) {
			// TODO: handle exception
		}
        }
    }
    
    private void updateDrinkImage(Long _id, byte[] imgArray){
    	if (datasource.isOpen() == false) {
    		datasource.open();
    	}
    	
    	DrinkRow activeDrinkRow = datasource.getDrinkInfo(_id);
    	datasource.updateDrinkInfo(_id, activeDrinkRow.getDrinkName(), activeDrinkRow.getCansLeft(), 
    			activeDrinkRow.getCansMax(), activeDrinkRow.getLastDispensed(), activeDrinkRow.getLastRefilled(), imgArray);
    }
	 
	private void populateDrinkData(Long _id) {
		
		// TODO Auto-generated method stub
		try{
			//This part will throw and exception if there is no SQLite row with this _ID
			DrinkRow activeDrinkRow = datasource.getDrinkInfo(_id);
			//set activity item data
			txtDrinkName.setText(activeDrinkRow.getDrinkName());
			txtCansLeft.setText(activeDrinkRow.getCansLeft());
			txtCansMax.setText(activeDrinkRow.getCansMax());
			txtLastDispensed.setText(activeDrinkRow.getLastDispensed());
			txtLastRefilled.setText(activeDrinkRow.getLastRefilled());
			if (activeDrinkRow.getDrinkIcon() != null){
				//Convert byte[] array back to bitmap and set ImageButton accordingly
				Bitmap bitMapImage = getBitMapFromArray(activeDrinkRow.getDrinkIcon());
				imgActiveDrink.setImageBitmap(bitMapImage);
			}
			else {
				imgActiveDrink.setImageResource(R.drawable.taptoupdate);
			}
		}
		//Exception will occur if there is no row in the DB for this drink. 
		//This should only occur once per Drink Icon
		catch (Exception e) {
			// Insert row in SQLitetable
			byte[] DrinkIco = null;
			datasource.updateDrinkInfo(_id, "Drink", "12", "12", "Never", "Never", DrinkIco);

			//set activity item data
			txtDrinkName.setText("Drink");
			txtCansLeft.setText("12");
			txtCansMax.setText("12");
			txtLastDispensed.setText("Never");
			txtLastRefilled.setText("Never");
		}
	}
	
	public Bitmap getBitMapFromArray(byte[] bitmapArray){
        return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	} 
	
	public static Bitmap getThumbnail(Uri uri, Activity THIS) throws FileNotFoundException, IOException{
		
		int THUMBNAIL_SIZE = 256;
		InputStream input = THIS.getContentResolver().openInputStream(uri);

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither=true;//optional
		onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
			return null;

		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

		double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither=true;//optional
		bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
		input = THIS.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}
	
	//This is supposed to make the image compression work faster
	private static int getPowerOfTwoForSampleRatio(double ratio){
		int k = Integer.highestOneBit((int)Math.floor(ratio));
		if(k==0) return 1;
		else return k;
	}
	
	//Crop Bitmap into a square
	public Bitmap cropBitmap(Bitmap sBmap){
		
		Bitmap dBmap;
		if (sBmap.getWidth() >= sBmap.getHeight()){

			dBmap = Bitmap.createBitmap(sBmap, sBmap.getWidth()/2 - sBmap.getHeight()/2, 0, sBmap.getHeight(), sBmap.getHeight());
		}
		else{

			dBmap = Bitmap.createBitmap(sBmap, 0, sBmap.getHeight()/2 - sBmap.getWidth()/2, sBmap.getWidth(), sBmap.getWidth());
		}
		
		return dBmap;
	}
	
	//Convert Bitmap to byte[] array
	public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    //I don't think I need to compress the Bitmap... so use quality of 100
	    bitmap.compress(CompressFormat.PNG, 100, outputStream);       
	    return outputStream.toByteArray();
	}
	
	public void sendEmailReminder(){
		//Query Email DB for info
		//Get Drink Info for email body:
		//
		eMAIL_Body = "Drink Name:   " + txtDrinkName.getText() + System.getProperty("line.separator")
				+    "Cans Left:        " + txtCansLeft.getText() + System.getProperty("line.separator")
				+    "Last Refilled:   " + txtLastRefilled.getText() + System.getProperty("line.separator") + System.getProperty("line.separator");
		/*
		 * Drink Name
		 * Cans Left
		 * Last Refilled
		 * System.getProperty("line.separator")
		 */
		EmailDataSrouce datasourceEmail = new EmailDataSrouce(this);
		datasourceEmail.open();
		try{
			EmailRow emailInfo = datasourceEmail.getEmailInfo((long) 0);
			eMAIL_TO = emailInfo.getTO();
			eMAIL_Subject = emailInfo.getSUBJECT();
			eMAIL_Body = eMAIL_Body + emailInfo.getBODY();
		}
		catch(Exception e){
			eMAIL_TO = "email@gmail.com";
			eMAIL_Subject = "BeerMe: We Need More Drinks!";
		}
		
		datasourceEmail.close();
		
		//http://stackoverflow.com/questions/2197741/how-to-send-email-from-my-android-application
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{eMAIL_TO});
		i.putExtra(Intent.EXTRA_SUBJECT, eMAIL_Subject);
		i.putExtra(Intent.EXTRA_TEXT   , eMAIL_Body);
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void dispenseCan(Long _id) {
		
		//We need to check to see if there are any more cans to dispense
		
		//Get Connection info
		conDatasource = new ConnectionDataSource(this);
		conDatasource.open();
		
		ConnectionRow conRow = conDatasource.getConnectionInfo(_id);
		
		
		//Create socket client and send dispense command with ID
	    String host = conRow.getIP();
	    conDatasource.close();
	    
	    //MySQL is port 3306 but the DMServer is listening on port 3307
	    int port = 3307;

	    StringBuffer instr = new StringBuffer();
	    //System.out.println("SocketClient initialized");
	    
	    try {
	    	
	        //Establish a socket connection
	        Socket connection = new Socket();
	        connection.connect(new InetSocketAddress(host, port), 5000);
	        
	                
	        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

            //Instantiate an OutputStreamWriter object with the optional character encoding.
            OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
            
            String process = "Dispense: " + _id.toString() +  (char) 13;

            //Write across the socket connection and flush the buffer
            osw.write(process);
            osw.flush();
            
            //Read response from Server
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");

            //Read the socket's InputStream and append to a StringBuffer
            int counter = 0;
            int c;
            while ( (c = isr.read()) != 13 && counter < 1000){
              instr.append( (char) c);
              counter +=1 ;
            }
            
            //Close the socket connection.
            connection.close();
            Toast.makeText(getBaseContext(), instr, Toast.LENGTH_SHORT).show();
            
            updateDrinkinfo(_id);
        }
	    catch (IOException f) {
	    	Toast.makeText(getBaseContext(), f.toString(), Toast.LENGTH_SHORT).show();
	    }
	    catch (Exception g) {
	    	Toast.makeText(getBaseContext(), g.toString(), Toast.LENGTH_SHORT).show();
	    }
            
	}
	
	private void updateDrinkinfo(Long _Id) {
		// TODO Auto-generated method stub
		if (datasource.isOpen() == false){
			datasource.open();
		}
		
		//Get previous drink info
		DrinkRow prevInfo = datasource.getDrinkInfo(_Id);
		
		//Get current Date
		Format dFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar nowDate = Calendar.getInstance();
		String date = dFormat.format(nowDate.getTime());
		
		//Get cansLeft
		Integer cLeft = Integer.valueOf(prevInfo.getCansLeft());
		cLeft = cLeft - 1;
		
		datasource.updateDrinkInfo(_Id, prevInfo.getDrinkName(), cLeft.toString(), prevInfo.getCansMax(), date, prevInfo.getLastRefilled(), prevInfo.getDrinkIcon());
		
		populateDrinkData(_Id);
	}

	private void SaveDrinkInfo() {
		// TODO Auto-generated method stub
		
		String dName = String.valueOf(txtDrinkName.getText());
		String dCansLeft = String.valueOf(txtCansLeft.getText());
		String dCansMax = String.valueOf(txtCansMax.getText());
		String dLastDisp = String.valueOf(txtLastDispensed.getText());
		String dLastRefill = String.valueOf(txtLastRefilled.getText());
		
		DrinkRow drinkRowinfo = datasource.getDrinkInfo(_ID);
		byte[] bitmapIMG = drinkRowinfo.getDrinkIcon();
		
		datasource.updateDrinkInfo(_ID, dName, dCansLeft, dCansMax, dLastDisp, dLastRefill, bitmapIMG);
		
		DrinkRow drinkRowForCDB = datasource.getDrinkInfo(_ID);
		Boolean successBoolean = updateCentralDB(drinkRowForCDB);
		
		if(successBoolean == true){
			Toast.makeText(this, "Drink Information Successfully Saved", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, "Drink Information could not be sent to Drink Machine. Please try again later.", Toast.LENGTH_SHORT).show();
		}
		
		
		
	}
	
	private Boolean updateCentralDB(DrinkRow dr){
		
		if(conDatasource.isOpen() == false){
			conDatasource.open();
		}
		
		ConnectionRow conRow = conDatasource.getConnectionInfo(_ID);
		conIP = conRow.getIP();
		conPORT = conRow.getPassword();
		conUsername = conRow.getUsername();
		conPasword = conRow.getPassword();
		
		conDatasource.close();
		
		Connection conn = null;
		String url = "jdbc:mysql://" + conIP.trim() + ":" + conPORT.trim() +"/";
		String dbName = "drinkmachinecentral";
		String driver = "com.mysql.jdbc.Driver";

		try {
			//make connection to MySQL
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,conUsername,conPasword);
			
			//create query string
			  
			String insertTableSQL = "UPDATE drinkinfo "
					+ " SET DrinkIcon = ?, DrinkName = ?, CansLeft = ?, CansMax = ?, LastDispensed = ?, LastReplenished = ? WHERE _id = ? ";
	        
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			
			preparedStatement.setBytes(1, dr.getDrinkIcon());
			preparedStatement.setString(2, dr.getDrinkName());
			preparedStatement.setString(3, dr.getCansLeft());
			preparedStatement.setString(4, dr.getCansMax());
			preparedStatement.setString(5, dr.getLastDispensed());
			preparedStatement.setString(6, dr.getLastRefilled());
			preparedStatement.setLong(7, dr.getId());

			// execute insert SQL statement
			try{
				preparedStatement.executeUpdate();
				preparedStatement.close();
				conn.close();
				return true;
			}
			catch (Exception e) {
				conn.close();
				return false;
			}
			
		} catch (Exception e) {
					
			return false;
		}
		
	}
	
	public void setOnClickListeners(){
		
		//Add listener to ImgButton
		imgActiveDrink.setOnClickListener(new OnClickListener() {
			  
			@Override
            public void onClick(View arg0) {
                //Select a picture
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), IMAGE_REQUEST);
            }
        });
		
		//Add listener to Email Button
		btnEmailReminder.setOnClickListener(new OnClickListener() {
			  
			@Override
            public void onClick(View arg0) {
				
				//Send Email Reminder:
				sendEmailReminder();
            }
        });
		
		btnDispense.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dispenseCan(_ID);
			}

		});
		
		activeDrinkSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				_ID = Long.valueOf(activeDrinkSpinner.getSelectedItemPosition());
				onResume();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			SaveDrinkInfo();
				
			}

		});
		
	}

}
