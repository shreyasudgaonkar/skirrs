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

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.Filter;


public class AddressAutoCompleteAdapter extends ArrayAdapter<String> 
										implements Filterable {

	/*
     * URL to get autocomplete suggestions for a given (partial) address
     */
	public static final String url_address_autocomplete = 
					"https://maps.googleapis.com/maps/api/place/autocomplete/";
	
	// JSON Parser object
    JSONParser jParser = new JSONParser();
	  
    private ArrayList<String> autoCompleteList = null;
	
	public AddressAutoCompleteAdapter( Context context, int textViewResourceId ) {
		super(context, textViewResourceId);
	}

	@Override
	public int getCount() {
		return autoCompleteList.size();
	}

	@Override
	public String getItem(int index) {
		return autoCompleteList.get(index);
	}

	@Override
	public Filter getFilter() {

		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();

				if (constraint != null) {

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
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		
		return filter;
	}
	
	/**
	 * @brief This function fetches the autocomplete suggestions from the 
	 * Google Places Autocomplete API
	 * 
	 * @param data The input for the suggestions
	 * @return An Arraylist containing the suggestions
	 */
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
        httpParams.add( new BasicNameValuePair( "components", "country:in" ) );
        
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

}
