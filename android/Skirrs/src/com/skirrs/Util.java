package com.skirrs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

	/*
	 * The single point of entry for android apps to make php calls
	 */
    public static String url = 
    			"http://ec2-54-221-233-104.compute-1.amazonaws.com/" +
        		"skirrs/website/app_wrapper.php";
    
    /*
     * The keyword(s) that will be used by app_wrapper.php to determine
     * which php library to include and which php function to call
     */
    public static final String KEYWORD_SIGN_IN       = "sign_in";
    public static final String KEYWORD_REGISTER      = "register";
    public static final String KEYWORD_USER_DETAILS  = "user_details";
    public static final String KEYWORD_EDIT_PROFILE  = "edit_profile";
    public static final String KEYWORD_SUBMIT_RIDE   = "submit_ride";
    public static final String KEYWORD_SEARCH_RIDES  = "search_rides";
    public static final String KEYWORD_VIEW_MESSAGES = "view_messages";
    
    
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
	
}
