package com.skirrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.skirrs.Util.GetGCMRegistrationId;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private TextView welcomeMessage;
	private TextView upcomingRidesMessage;
	private TextView errorMessage;
	
	/*
     * Managing sessions and user sign-ins
     */
	SessionManager sessionManager;
	
	User user;
	
	// JSON Parser object
    JSONParser jParser = new JSONParser();
    
    private UserDetailsTask userDetailsTask;
    
    SkirrsProgressDialog progressDialog;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_home );

		progressDialog = SkirrsProgressDialog.show( this,
													"",
													"",
													true );
		
		sessionManager = SessionManager.getInstance( getApplicationContext() );

		/*
		 * If the user has never signed-in, then show the sign-in screen
		 */
		if ( sessionManager.isLoggedIn() == false ) {
			
			Intent signInIntent = new Intent( getApplicationContext(),
											  SignInActivity.class );
			startActivity( signInIntent );
			
			finish();
			
		} else {
					
			GetGCMRegistrationId getGCMRegistrationId = new GetGCMRegistrationId();
	    	getGCMRegistrationId.execute( getBaseContext() );
			
			HashMap<String, String> userDetails = sessionManager.getUserDetails();
			
			/*
			 * Fetch user profile and display a welcome message.
			 * Also display any upcoming rides that the user might have
			 */
			
			user = new User();
			
			user.mEmail = userDetails.get( sessionManager.KEY_EMAIL );

        	welcomeMessage = ( TextView ) findViewById( R.id.welcome_message );
        	upcomingRidesMessage = ( TextView ) findViewById( R.id.upcoming_rides );
        	errorMessage = ( TextView ) findViewById( R.id.error_message );
        	
			userDetailsTask = new UserDetailsTask();
			userDetailsTask.execute( (Void) null );
			
		}
		
	}
	
	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.home, menu );
		return true;
	}
	
	
	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		
	    switch ( item.getItemId() ) {
	    
	    case R.id.action_logout:
	        logoutUser();
	        return true;
	        
	    case R.id.action_edit_profile:
	    	return true;
	    	
	    case R.id.action_post_ride:
	    	
	    	/*
			 * Open the submit ride activity
			 */
			Intent submitRideIntent = new Intent( getApplicationContext(),
					  							  SubmitRideActivity.class );
			
			submitRideIntent.putExtra( "email_address", user.mEmail );
			submitRideIntent.putExtra( "first_name",    user.mFirstName );
			submitRideIntent.putExtra( "last_name",     user.mLastName );
			submitRideIntent.putExtra( "phone_number",  user.mPhoneNumber );
			submitRideIntent.putExtra( "user_id",       user.mUserId );
			
			submitRideIntent.putExtra( "user", user );
			
			startActivity( submitRideIntent );
			
	    	return true;
	    	
	    case R.id.action_search:
	    	
	    	/*
			 * Open the submit ride activity
			 */
			Intent searchRideIntent = new Intent( getApplicationContext(),
					  							  SearchRidesActivity.class );
			
			searchRideIntent.putExtra( "user_id", user.mUserId );
			searchRideIntent.putExtra( "user", user );
			
			startActivity( searchRideIntent );
			
	    	return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	
	/**
	 * 
	 * @param v
	 */
	public void logoutUser()
	{
		/*
		 * Invalidate the current session
		 */
		sessionManager.invalidateLoginSession();
		
		/*
		 * Redirect the user to the sign-in screen
		 */
		Intent signInIntent = new Intent( getApplicationContext(),
				  						  SignInActivity.class );
		startActivity( signInIntent );
		
		finish();
	}
	
	
	public class UserDetailsTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground( Void... arg0 ) {
			
			// Building Parameters
            List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
            
            httpParams.add( new BasicNameValuePair( "email_address",
            										 user.mEmail ) );
            
            httpParams.add( new BasicNameValuePair( "keyword",
            										Util.KEYWORD_USER_DETAILS ) );
            
            try {
            	
            	// getting JSON string from URL
            	JSONObject json = jParser.makeHttpRequest( Util.url, 
            										   	   "POST",
            										   	   httpParams);
            	
            	if ( json != null && json.length() > 0 ) {
                    
	            	int status = json.getInt( JSONParser.TAG_STATUS );
	            	
	            	if ( status == 1 ) {
	            		
	            		user.mFirstName   = json.getString( JSONParser.TAG_FIRST_NAME );
	            		user.mLastName    = json.getString( JSONParser.TAG_LAST_NAME );
	            		user.mPhoneNumber = json.getString( JSONParser.TAG_PHONE_NUM );
	            		user.mUserId      = json.getString( JSONParser.TAG_USER_ID );
	            		
	            	} else {
	            		
	            		return false;
	            	}
	            	
                } else {
                	System.out.println( "Failed to get user details" );
                	return false;
                }
            	
            } catch ( JSONException j ) {
            	System.out.println( "JSONException: " + j );
            	return false;
            }
            
            return true;
		}
		
		
		@Override
		protected void onPostExecute( final Boolean success ) {
		
			progressDialog.dismiss();
			
			if ( success ) {

    			welcomeMessage.setText( "Welcome, " + user.mFirstName );
    			upcomingRidesMessage.setText( "You have no upcoming rides" );
    			
    			errorMessage.setVisibility( TextView.INVISIBLE );
				
			} else {
				/*
        		 * Unable to get user details
        		 * Set an error message
        		 */
        		errorMessage.setText( "Error. Unable to fetch user details" );
        		errorMessage.setVisibility( TextView.VISIBLE );
        		
        		welcomeMessage.setVisibility( TextView.INVISIBLE );
        		upcomingRidesMessage.setVisibility( TextView.INVISIBLE );
			}
			
		}
		
		
	}

}
