package com.skirrs;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SessionManager {
	
	private static SessionManager instance = null;
	
	private SharedPreferences pref;
	private Context           context;
	
	private int PRIVATE_MODE = 0;
	
	/*
	 * The name of the SharedPreferences file
	 */
	private static final String PREF_NAME    = "skirrs";
	
	private static final String IS_LOGGED_IN = "IsLoggedIn";
	
	/*
	 * The keys to be used in SharedPreferences
	 */
	public final String KEY_NAME  = "key_name";
	public final String KEY_EMAIL = "key_email";
	
	
	/**
	 * 
	 * @param context
	 */
	private SessionManager( Context context )
	{
		
		this.context = context;
		pref = this.context.getSharedPreferences( PREF_NAME, PRIVATE_MODE );
		
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static SessionManager getInstance( Context context )
	{
		if ( instance == null ) {
			instance = new SessionManager( context );
		}
		
		return instance;
	}
	
	/**
	 * 
	 */
	public void createLoginSession( String email )
	{
		if ( pref != null ) {
			
			Editor editor = pref.edit();
			
			/*
			 * Set the flag
			 */
			editor.putBoolean( IS_LOGGED_IN, true );
			
			/*
			 * Add user details
			 */
			editor.putString( KEY_EMAIL, email );
			
			editor.commit();
		}
		
	}
	
	
	/**
	 * 
	 * @return
	 */
	public HashMap< String, String > getUserDetails()
	{
		HashMap< String, String > user = new HashMap< String, String >();
		
		if ( user != null && pref != null ) {
			
			/*
			 * Fetch the user details and store it in the hashmap
			 */
			user.put( KEY_NAME, pref.getString( KEY_NAME, null ) );
			user.put( KEY_EMAIL, pref.getString( KEY_EMAIL, null ) );
			
		}
		
		return user;
		
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isLoggedIn()
	{
		boolean retVal = false;
		
		if ( pref != null ) {
			
			System.out.println( "checking isLoggedIn()" );
			retVal = pref.getBoolean( IS_LOGGED_IN, false );
			System.out.println( "retVal: " + retVal );
		}

		return retVal;
	}
	
	
	/**
	 * 
	 */
	public void invalidateLoginSession()
	{
		if ( pref != null ) {
			pref.edit().clear().commit();
		}
	}
	
}
