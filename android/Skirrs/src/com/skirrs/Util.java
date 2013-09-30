package com.skirrs;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.os.AsyncTask;

public class Util {

	/*
	 * The single point of entry for android apps to make php calls
	 */
    public static String url = 
    			"http://ec2-54-221-233-104.compute-1.amazonaws.com/" +
        		"skirrs/website/app_wrapper.php";
    
    
    public static final String GCM_SENDER_ID = "1039856074910";
    
    public static String gcmRegistrationId = null;
    
    /*
     * The keyword(s) that will be used by app_wrapper.php to determine
     * which php library to include and which php function to call
     */
    public static final String KEYWORD_SIGN_IN          = "sign_in";
    public static final String KEYWORD_REGISTER_USER    = "register";
    public static final String KEYWORD_REGISTER_FB_USER = "fb_register";
    public static final String KEYWORD_USER_DETAILS     = "user_details";
    public static final String KEYWORD_EDIT_PROFILE     = "edit_profile";
    public static final String KEYWORD_SUBMIT_RIDE      = "submit_ride";
    public static final String KEYWORD_SEARCH_RIDES     = "search_rides";
    public static final String KEYWORD_VIEW_MESSAGES    = "view_messages";
    
    public static SkirrsProgressDialog progressDialog;
    
    /**
	 * 
	 * @param input
	 * @param sourceFormat
	 * @param desiredFormat
	 * @return
	 */
	public static String getDateInDesiredFormat( String input,
												 String sourceFormat,
												 String desiredFormat )
	{
		String date = "";
		
		try {
			
			SimpleDateFormat source  = new SimpleDateFormat( sourceFormat,
															 Locale.UK );
			
			SimpleDateFormat desired = new SimpleDateFormat( desiredFormat,
															 Locale.UK );
			
			Date dt = source.parse( input );
			
			date = desired.format( dt );
			
		} catch ( ParseException p ) {
			
			System.out.println( "Exception " + p + " when parsing date" );
		}
		
		return date;
	}
	
	/*
	 * This background task is used to get the registration ID from the GCM 
	 * servers using the project ID (or GCM_SENDER_ID)
	 */
	public static class GetGCMRegistrationId extends AsyncTask< Context, Void, Boolean > {

		@Override
		protected Boolean doInBackground( Context... arg0 ) {
			
        	GoogleCloudMessaging gcm =
        				GoogleCloudMessaging.getInstance( arg0[ 0 ] );
        	
        	try {
        		
        		Util.gcmRegistrationId = gcm.register( Util.GCM_SENDER_ID );
        		System.out.println( "registrationId: " + Util.gcmRegistrationId );
        		
        	} catch ( IOException i ) {
        		
        		System.out.println( "Exception when getting registrationId: " +
        							i.getMessage() );
        		
        	}

        	return true;
        	
		}
		
	}
	
	
	/*
	 * This background task is used to get the registration ID from the GCM 
	 * servers using the project ID (or GCM_SENDER_ID)
	 */
	public static class SetGCMRegistrationIdinDb 
						extends AsyncTask< Void, Void, Boolean > {

		@Override
		protected Boolean doInBackground( Void... arg0 ) {

        	return true;
        	
		}
		
	}
	
}
