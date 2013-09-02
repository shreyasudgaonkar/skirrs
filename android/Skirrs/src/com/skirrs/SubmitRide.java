package com.skirrs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

public class SubmitRide extends Activity {

	public static final String url_address_autocomplete = 
					"https://maps.googleapis.com/maps/api/place/autocomplete/";
	
	// JSON Parser object
    JSONParser jParser = new JSONParser();
	  
    private ArrayList<String> autoCompleteList = null;
    
    private AutoCompleteTextView sourceAutoComplete;
    private AutoCompleteTextView destinationAutoComplete;
    
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_submit_ride );
		
		sourceAutoComplete = ( AutoCompleteTextView ) findViewById( R.id.source );
		sourceAutoComplete.setAdapter( new AddressAutoCompleteAdapter( 
								       this, R.layout.address_autocomplete_list_item ) );
		
		destinationAutoComplete = ( AutoCompleteTextView ) findViewById( R.id.destination );
		destinationAutoComplete.setAdapter( new AddressAutoCompleteAdapter( 
			       							this, R.layout.address_autocomplete_list_item ) );
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit_ride, menu);
		return true;
		
	}

	
	/**
	 * 
	 * @param v
	 */
	public void cancelActivity( View v )
	{
		finish();
	}
	
	
	public ArrayList<String> addressAutoComplete( String data )
	{
		String key = "AIzaSyBieMMbVaNUstfNEIMtpJJ0CvDrj-RtzIE";
		String input = "";
		
		try {
            input = URLEncoder.encode( data, "utf-8" );
        } catch ( UnsupportedEncodingException e1 ) {
            e1.printStackTrace();
            return null;
        }
		
		// place type to be searched
        String types = "geocode";

        // Sensor enabled
        String sensor = "false";

        // Output format
        String output = "json";
		
        String url = url_address_autocomplete + output;
        
     // Building Parameters
        List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        httpParams.add( new BasicNameValuePair( "input", input ) );
        httpParams.add( new BasicNameValuePair( "types", types ) );
        httpParams.add( new BasicNameValuePair( "sensor", sensor ) );
        httpParams.add( new BasicNameValuePair( "key", key ) );
        
        try {
            
        	// getting JSON string from URL
        	JSONObject json = jParser.makeHttpRequest( url, 
        										   	   "GET",
        										   	   httpParams);

        	if ( json != null && json.length() > 0 ) {

            	String status = json.getString( JSONParser.TAG_STATUS );
            	
            	if ( status.equals( "OK" ) ) {
            		
            		// Create a JSON object hierarchy from the results
                    JSONObject jsonObj = new JSONObject( json.toString() );
                    JSONArray predsJsonArray = jsonObj.getJSONArray( "predictions" );
                    
                    // Extract the Place descriptions from the results
                    autoCompleteList = new ArrayList<String>( predsJsonArray.length() );
                    for (int i = 0; i < predsJsonArray.length(); i++) {
                    	autoCompleteList.add( predsJsonArray.
                        					getJSONObject(i).
                        						getString( "description" ) );
                        
                    }
            		
            	} else {
            		
            		return null;
            	}
            	
            } else {
            	System.out.println( "Failed to get user details" );
            	return null;
            }
        	
        } catch ( JSONException j ) {
        	System.out.println( "JSONException: " + j );
        	return null;
        }
        
		return autoCompleteList;
		
	}
	
	
	/**
	 * 
	 * @author Shreyas
	 *
	 */
	private class AutoCompleteAddressTask extends AsyncTask<String, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground( String... place ) {

			String key = "AIzaSyBieMMbVaNUstfNEIMtpJJ0CvDrj-RtzIE";
			String input = "";
			
			try {
                input = URLEncoder.encode( place[ 0 ], "utf-8" );
            } catch ( UnsupportedEncodingException e1 ) {
                e1.printStackTrace();
                return false;
            }
			
			// place type to be searched
            String types = "geocode";
 
            // Sensor enabled
            String sensor = "false";
 
            // Output format
            String output = "json";
			
            String url = url_address_autocomplete + output;
            
         // Building Parameters
            List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
            httpParams.add( new BasicNameValuePair( "input", input ) );
            httpParams.add( new BasicNameValuePair( "types", types ) );
            httpParams.add( new BasicNameValuePair( "sensor", sensor ) );
            httpParams.add( new BasicNameValuePair( "key", key ) );
            
            try {
                
            	// getting JSON string from URL
            	JSONObject json = jParser.makeHttpRequest( url, 
            										   	   "GET",
            										   	   httpParams);

            	System.out.println( "json: " + json );
            	
            	if ( json != null && json.length() > 0 ) {

	            	String status = json.getString( JSONParser.TAG_STATUS );
	            	
	            	if ( status.equals( "OK" ) ) {
	            		
	            		// Create a JSON object hierarchy from the results
	                    JSONObject jsonObj = new JSONObject( json.toString() );
	                    JSONArray predsJsonArray = jsonObj.getJSONArray( "predictions" );

	                    // Extract the Place descriptions from the results
	                    autoCompleteList = new ArrayList<String>( predsJsonArray.length() );
	                    for (int i = 0; i < predsJsonArray.length(); i++) {
	                    	autoCompleteList.add( predsJsonArray.
	                        					getJSONObject(i).
	                        						getString( "description" ) );
	                        
	                    }
	            		
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
			
			if ( success ) {
				System.out.println( autoCompleteList.toString() );			
			}
			
		}
		
	}
	
	
	/**
	 * 
	 * @author Shreyas
	 *
	 */
	private class AddressAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
		
	    public AddressAutoCompleteAdapter( Context context, 
	    								   int textViewResourceId ) {
	    	
	    	
	    	
	        super( context, textViewResourceId );
	    }
	    
	    @Override
	    public int getCount() {
	        return autoCompleteList.size();
	    }

	    @Override
	    public String getItem(int index) {
	        return autoCompleteList.get( index );
	    }

	    @Override
	    public Filter getFilter() {
	    	
	        Filter filter = new Filter() {
	            @Override
	            protected FilterResults performFiltering( CharSequence constraint ) {
	                FilterResults filterResults = new FilterResults();
	                
	                if ( constraint != null ) {
	               
	                    addressAutoComplete( constraint.toString() );
	                	
	            		if ( autoCompleteList != null && autoCompleteList.size() > 0 ) {
		                    // Assign the data to the FilterResults
		                    filterResults.values = autoCompleteList;
		                    filterResults.count = autoCompleteList.size();
	            		}
	                }
	                
	                return filterResults;
	            }

	            @Override
	            protected void publishResults( CharSequence constraint, 
	            							   FilterResults results ) {
	                if ( results != null && results.count > 0 ) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	        return filter;
	    }
	}
	
}
