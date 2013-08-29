package com.skirrs;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private TextView welcomeMessage;
	
	/*
     * Managing sessions and user sign-ins
     */
	SessionManager sessionManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		sessionManager = SessionManager.getInstance( getApplicationContext() );
		
		/*
		 * If the user has never signed-in, then show the sign-in screen
		 */
		if ( sessionManager.isLoggedIn() == false ) {
			
			Intent signInIntent = new Intent( getApplicationContext(),
											  SignInActivity.class );
			startActivity( signInIntent );
			
		}
		
		welcomeMessage = ( TextView ) findViewById( R.id.welcome_message );
		
		HashMap<String, String> user = sessionManager.getUserDetails();
		
		welcomeMessage.setText( "Welcome, " + user.get( sessionManager.KEY_EMAIL ) );
		
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
	    	return true;
	    	
	    case R.id.action_search:
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
		
		/*
		 * Finish this activity so that user cannot press the back key
		 * and return to this screen
		 */
		finish();
	}

}
