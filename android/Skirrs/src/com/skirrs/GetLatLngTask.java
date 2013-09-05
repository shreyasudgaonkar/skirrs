package com.skirrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;


/**
 * 
 * @author Shreyas
 *
 */
public class GetLatLngTask extends AsyncTask< String, Void, Boolean > {

	/*
	 * Any class which uses GetLatLngTask must implement LatLngClient interface
	 * It will be used to make the callback in onPreExecute() and onPostExecute()
	 */
	public LatLngClient latLngClient;
	
	/*
     * HashMap to store the latitudes and longitudes of source and 
     * destination addresses
     * Keys are obtained by concatenating source with lat or lng and
     * destination with lat or lng
     * sourcelat, sourcelng, destlat, destlng
     */
    public HashMap< String, Double > latLng = null;
    
    /*
     * HashMap to store the formatted address for the source and destination 
     * addresses provided by the user 
     */
    public HashMap< String, String > formattedAddresses = null;
    
    /*
     * URL to get the latitude and longitude for a given address
     */
    public static final String url_geocode = 
    				"http://maps.googleapis.com/maps/api/geocode/";
    
    JSONParser jParser = new JSONParser();
	
    
    public static String LAT_SUFFIX = "lat";
    public static String LNG_SUFFIX = "lng";
    
    
    /*
     * Constructor
     */
    public GetLatLngTask( HashMap< String, Double > latLngMap,
    					  HashMap< String, String > formattedAddressMap,
    					  LatLngClient client ) {
    	
    	latLng             = latLngMap;
    	formattedAddresses = formattedAddressMap;
    	latLngClient       = client;
    }
    
    
	/**
	 * 
	 */
	@Override
	protected void onPreExecute() {
		
		latLngClient.preExecuteCallback();
		
	}
	
	
	/**
	 * 
	 */
	@Override
	protected Boolean doInBackground( String... input ) {
		
		if ( latLng == null || formattedAddresses == null )
		{
			return false;
		}
		
		int inputLen = input.length;

		for ( int i = 0; i < inputLen; i++ ) {
		
			String address = input [ i ];

			String output = "json";
			
			String url = url_geocode + output;
			
			List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
	        httpParams.add( new BasicNameValuePair( "address", address ) );
	        httpParams.add( new BasicNameValuePair( "sensor", "false" ) );
			
			try {
				
				// getting JSON string from URL
	        	JSONObject json = jParser.makeHttpRequest( url, 
	        										   	   "GET",
	        										   	   httpParams );

	        	if ( json != null && json.length() > 0 ) {
	        		
	        		String status = json.getString( JSONParser.TAG_STATUS );
	        		
	        		if ( status.equals( "OK" ) ) {
	        			
	        			JSONObject location = json.getJSONArray( JSONParser.TAG_RESULTS ).
	        									   getJSONObject( 0 ).
	        									   getJSONObject( JSONParser.TAG_GEOMETRY ).
	        									   getJSONObject( JSONParser.TAG_LOCATION );
	        			
	        			double lat = location.getDouble("lat");
	        			double lng = location.getDouble("lng");
	        			
	        			System.out.println( "lat: " + lat );
	        			System.out.println( "lng: " + lng );
	        			
	        			latLng.put( address + LAT_SUFFIX, lat );
	        			latLng.put( address + LNG_SUFFIX, lng );
	        			
	        			String formattedAddress = ( String ) 
	        					json.getJSONArray( JSONParser.TAG_RESULTS ).
									 getJSONObject( 0 ).
									 get( JSONParser.TAG_FORMATTED_ADDRESS );
													   
	        			
	        			formattedAddresses.put( address, formattedAddress.toString() );
	        			
	        			System.out.println( "formattedAddress: " + formattedAddress.toString() );
	        			
	        		} else {
	        			throw new SkirrsException( ErrorId.SKIRRS_JSON_SUCCESS_EXCEPTION );
	        		}
	        		
	        	} else {
	        		
	        		throw new SkirrsException( ErrorId.SKIRRS_JSON_COMM_EXCEPTION );
	        	}
	        	
			} catch ( JSONException j ) {
				
				System.out.println( "Exception " + j + " when fetching lat, lng for " + address );
				return false;
				
			} catch ( SkirrsException s ) {
				
				System.out.println( "Exception when fetching lat, lng for " + 
									address +
									"ErrorCode: " + s.getErrorCode() );
				return false;
			}
			
		}
		
		return true;
	}
	
	
	@Override
	protected void onPostExecute( final Boolean success ) {
		
		latLngClient.postExecuteCallback( success );
		
	}
	
}
