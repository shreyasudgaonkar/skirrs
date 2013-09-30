package com.skirrs;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	private boolean fbRegister = false;
	
	private String mEmailAddress;
	private String mFullName;
	private String mFirstName;
	private String mLastName;
	
	JSONParser jParser;
	
	SkirrsProgressDialog progressDialog;
	
	/*
     * Managing sessions and user sign-ins
     */
	SessionManager sessionManager;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_register );
		
		sessionManager = SessionManager.getInstance( getApplicationContext() );
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		/*
		 * First check is to see if this is a FB based registration
		 */
		if ( extras != null ) {
			fbRegister = extras.getBoolean( "FB_REGISTER" );
		}
		
		if ( fbRegister ) {
			
			/*
			 * Facebook based registration
			 * Get emailaddress and full name from Intent
			 */
			
			progressDialog = SkirrsProgressDialog.show( this,
														"",
														"",
														true );

			mEmailAddress = extras.getString( "EMAIL_ADDRESS" );
			mFullName     = extras.getString( "FULL_NAME" );
			
			String[] parts = mFullName.split( "\\s+" );
			
			if ( parts != null && parts.length > 0 ) {
				mFirstName = parts[ 0 ];
				mLastName  = parts[ 1 ];
			} else {
				mFirstName = mFullName;
				mLastName = "";
			}
			
			RegisterUserTask registerUserTask = new RegisterUserTask();
			registerUserTask.execute();
			
		} else {
			
			/*
			 * Not facebook based
			 * Show the register form
			 */
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.register, menu );
		return true;
		
	}

	
	public class RegisterUserTask extends AsyncTask< Void, Void, Boolean > {

		@Override
		protected Boolean doInBackground( Void... arg0 ) {
			
			// Building Parameters
            List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
            
            httpParams.add( new BasicNameValuePair( "email_address",
            										 mEmailAddress ) );
            
            httpParams.add( new BasicNameValuePair( "full_name",
            										mFullName ) );
            
            httpParams.add( new BasicNameValuePair( "keyword",
            										Util.KEYWORD_REGISTER_FB_USER ) );
            
            try {
            	
            	// getting JSON string from URL
            	JSONObject json = jParser.makeHttpRequest( Util.url, 
            										   	   "POST",
            										   	   httpParams);
            	
            	if ( json != null && json.length() > 0 ) {
                    
	            	int status = json.getInt( JSONParser.TAG_STATUS );
	            	
	            	if ( status == 1 ) {
	            		
	            		return true;
	            		
	            	} else {
	            		
	            		return false;
	            	}
	            	
                } else {
                	System.out.println( "Failed to get register the user" );
                	return false;
                }
            	
            } catch ( JSONException j ) {
            	System.out.println( "JSONException: " + j );
            	return false;
            }

		}
		
		
		@Override
		protected void onPostExecute( final Boolean success ) {
		
			progressDialog.dismiss();
			
			if ( success ) {

				/* 
				 * Add user data to SessionManager
				 */				
				if ( sessionManager != null ) {
					sessionManager.createLoginSession( mEmailAddress );
				}
				
				/*
				 * Launch home screen activity here
				 */
				Intent homeActIntent =
						new Intent( getApplicationContext(),
									HomeActivity.class );
				
				homeActIntent.putExtra( "EMAIL_ADDRESS", mEmailAddress );
				
				startActivity( homeActIntent );
				finish();
				
			} else {
				
				/*
				 * XXX
				 * What to do here?
				 */
			}
			
		}
		
		
	}
	
}
