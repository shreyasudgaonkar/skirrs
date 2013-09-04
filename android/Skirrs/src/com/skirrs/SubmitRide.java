package com.skirrs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.TimePicker;

public class SubmitRide extends Activity {

	// JSON Parser object
    JSONParser jParser = new JSONParser();
	  
    private ArrayList<String> autoCompleteList = null;
    
    private AutoCompleteTextView sourceAutoComplete;
    private AutoCompleteTextView destinationAutoComplete;

    private TextView priceTextView;
    private TextView commentsTextView;
    
    private String user_id;
    
    /*
     * URL to get the latitude and longitude for a given address
     */
    public static final String url_geocode = 
    				"http://maps.googleapis.com/maps/api/geocode/";
    
    /*
     * URL to get autocomplete suggestions for a given (partial) address
     */
	public static final String url_address_autocomplete = 
					"https://maps.googleapis.com/maps/api/place/autocomplete/";
	
	public static final String url_submit_ride = 
					"http://192.168.1.64/skirrs/lib/app/submit_ride_lib.php";
	
	private static String selectedDate;
	private static String selectedTime;
	
    public static TextView dateView;
    public static TextView timeView;
    
    /*
     * HashMaps to store the latitudes and longitudes of source and 
     * destination addresses
     * Keys are obtained by concatenating source with lat ot lng and
     * destination with lat or lng
     * sourcelat, sourcelng, destlat, destlng
     */
    public HashMap< String, Double > latLng;
    
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_submit_ride );
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		
		/*
		 * Get the user id via the intent ( HomeActivity provides it )
		 */
		user_id = intent.getExtras().getString( "user_id" );
		
		sourceAutoComplete = ( AutoCompleteTextView ) findViewById( R.id.source );
		sourceAutoComplete.setAdapter( new AddressAutoCompleteAdapter( 
								       this, R.layout.address_autocomplete_list ) );
		
		/*
		 * Auto complete for the source address field
		 */
		sourceAutoComplete.setOnTouchListener(
				new RightDrawableOnTouchListener( sourceAutoComplete ) {
			        @Override
			        public boolean onDrawableTouch( final MotionEvent event ) {
			        	sourceAutoComplete.setText("");
			        	return false;
			        }
				}
		);
		
		/*
		 * Auto complete for the destination address field
		 */
		destinationAutoComplete = ( AutoCompleteTextView ) findViewById( R.id.destination );
		destinationAutoComplete.setAdapter( new AddressAutoCompleteAdapter( 
			       							this, R.layout.address_autocomplete_list ) );
		
		destinationAutoComplete.setOnTouchListener(
				
				new RightDrawableOnTouchListener( destinationAutoComplete ) {
					
			        @Override
			        public boolean onDrawableTouch( final MotionEvent event ) {
			        	
			        	destinationAutoComplete.setText( "" );
			        	return false;
			        }
				}
		);
		
		dateView = ( TextView ) findViewById( R.id.date );
		timeView = ( TextView ) findViewById( R.id.time );
		
		priceTextView    = ( TextView ) findViewById( R.id.price );
		commentsTextView = ( TextView ) findViewById( R.id.comments );
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit_ride, menu);
		return true;
		
	}

	
	/**
	 * Finish this activity and return to the previous activity
	 * @param v
	 */
	public void cancelActivity( View v )
	{
		finish();
	}
	
	
	/*
	 * First step towards posting the ride
	 */
	public void getSrcDestLatLng( View v )
	{
		
		String source      = sourceAutoComplete.getText().toString();
		GetLatLngTask getLatLngtask = new GetLatLngTask();
		getLatLngtask.execute( source );
		
		String destination = destinationAutoComplete.getText().toString();
		GetLatLngTask getDestLatLngtask = new GetLatLngTask();
		getDestLatLngtask.execute( destination );
			
	}
	
	/**
	 * 
	 * @param v
	 */
	public void submitRide( View v )
	{
		try {
			
			System.out.println( "selectedDate: " + selectedDate );
			SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.UK );
			Date inputDate = fmt.parse( selectedDate + " " + selectedTime );
			System.out.println( "inputDate: " + inputDate );
			
			String price = priceTextView.getText().toString();
			System.out.println( "price: " + price );
			
			String comments = commentsTextView.getText().toString();
			System.out.println( "comments: " + comments );
		
			/*
			 * Create an arraylist of all params
			 */
			/*List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
			
	        httpParams.add( new BasicNameValuePair( "source", source ) );
	        httpParams.add( new BasicNameValuePair( "destination", destination ) );
	        httpParams.add( new BasicNameValuePair( "user_id", user_id ) );
	        httpParams.add( new BasicNameValuePair( "price", price ) );
	        httpParams.add( new BasicNameValuePair( "datetime", inputDate.toString() ) );
	        httpParams.add( new BasicNameValuePair( "comments", comments ) );
	        */
		} catch ( ParseException p ) {
			
			System.out.println( " Exception when parsing date " + p );
			
		}
		
	}
	
	
	/**
	 * Represents an asynchronous login task used to authenticate
	 * the user.
	 */
	public class GetLatLngTask extends AsyncTask< String, Void, Boolean > {

		@Override
		protected Boolean doInBackground( String... input ) {
			
			String address = input [ 0 ];

			System.out.println( "address: " + address );
			
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
	        			
	        			JSONObject location = json.getJSONArray("results").
	        									   getJSONObject(0).
	        									   getJSONObject("geometry").
	        									   getJSONObject("location");
	        			
	        			double lat = location.getDouble("lat");
	        			double lng = location.getDouble("lng");
	        			
	        			System.out.println( "lat: " + lat );
	        			System.out.println( "lng: " + lng );
	        			
	        			latLng.put( address + "lat", lat );
	        			latLng.put( address + "lng", lng );
	        			
	        		} else {
	        			throw new SkirrsException( ErrorId.SKIRRS_JSON_SUCCESS_EXCEPTION );
	        		}
	        		
	        	} else {
	        		
	        		throw new SkirrsException( ErrorId.SKIRRS_JSON_COMM_EXCEPTION );
	        	}
	        	
			} catch ( JSONException j ) {
				
				System.out.println( "Exception when fetching lat, lng for " + address );
				return false;
				
			} catch ( SkirrsException s ) {
				
				System.out.println( "Exception when fetching lat, lng for " + 
									address +
									"ErrorCode: " + s.getErrorCode() );
				return false;
			}
			
			
			return true;
		}
		
		@Override
		protected void onPostExecute( final Boolean success ) {
			
			if ( success ) {
				
			}
			
		}
		
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
        httpParams.add( new BasicNameValuePair( "components", "country:us" ) );
        
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
	 * @param v
	 */
	public void showTimePickerDialog(View v) {
		
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show( getFragmentManager(), "timePicker" );
	    
	}
	
	
	/**
	 * 
	 * @param v
	 */
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show( getFragmentManager(), "datePicker" );
	}
	
	/**
	 * 
	 * @author Shreyas
	 *
	 */
	public abstract class RightDrawableOnTouchListener implements OnTouchListener {
		
	    Drawable drawable;
	    private int fuzz = 10;

	    /**
	     * @param keyword
	     */
	    public RightDrawableOnTouchListener(TextView view) {
	        super();
	        final Drawable[] drawables = view.getCompoundDrawables();
	        if ( drawables != null && drawables.length == 4 )
	            this.drawable = drawables[2];
	    }

	    
	    @Override
	    public boolean onTouch( final View v, final MotionEvent event ) {
	    	
	        if ( event.getAction() == MotionEvent.ACTION_DOWN && drawable != null ) {
	        	
	            final int x = (int) event.getX();
	            final int y = (int) event.getY();
	            final Rect bounds = drawable.getBounds();
	            
	            if ( x >= ( v.getRight() - bounds.width() - fuzz ) && 
	            	 x <= ( v.getRight() - v.getPaddingRight() + fuzz ) && 
	            	 y >= ( v.getPaddingTop() - fuzz) && 
	            	 y <= ( v.getHeight() - v.getPaddingBottom() ) + fuzz ) {
	            	
	                return onDrawableTouch(event);
	            }
	        }
	        return false;
	    }

	    public abstract boolean onDrawableTouch(final MotionEvent event);

	}
	
	
	/**
	 * @brief This adapter is used for autocompleting the source and destination
	 *  text views with suggestions from Google Places Autocomplete API
	 *  The class overrides the default behavior and adds the suggestions to 
	 *  the results list (does not filter them in anyway)
	 * @author Shreyas
	 *
	 */
	private class AddressAutoCompleteAdapter extends ArrayAdapter<String> 
											 implements Filterable {
		
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
	
	
	/**
	 * 
	 * @author Shreyas
	 *
	 */
	public static class TimePickerFragment 
						extends DialogFragment
						implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog( Bundle savedInstanceState ) {
			
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get( Calendar.HOUR_OF_DAY );
		int minute = c.get( Calendar.MINUTE );
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog( getActivity(),
									 this,
									 hour,
									 minute,
									 DateFormat.is24HourFormat( getActivity() ) );
		}
		
		/**
		 * @brief This method is invoked when the user selects a time in the 
		 * timepicker
		 */
		public void onTimeSet( TimePicker view, int hourOfDay, int minute ) {
		
			String time, strHourOfDay, strMinute;
			
			strHourOfDay = Integer.toString( hourOfDay );
			strMinute    = Integer.toString( minute );
			
			if ( strMinute.length() < 2 ) {
				time = strHourOfDay + ":0" + strMinute;
			} else {
				time = strHourOfDay + ":" + strMinute;
			}
			selectedTime = time;
			timeView.setText( time );
		}
		
	}
	
	
	/**
	 * 
	 * @author Shreyas
	 *
	 */
	public static class DatePickerFragment 
						extends DialogFragment
						implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog( getActivity(), this, year, month, day );
		}
		
		public void onDateSet( DatePicker view, int year, int month, int day) {

			month = month + 1;
			
			selectedDate = Integer.toString( day ) + "-" + 
						   Integer.toString( month ) + "-" + 
						   Integer.toString( year );
			
			String dayOfTheWeek = "";
			GregorianCalendar calendar = new GregorianCalendar( year, month - 1, day );
			int i = calendar.get( Calendar.DAY_OF_WEEK );

			    if(i == 2) {
			        dayOfTheWeek = " Mon";           
			    } else if (i==3){
			        dayOfTheWeek = " Tue";
			    } else if (i==4){
			        dayOfTheWeek = " Wed";
			    } else if (i==5){
			        dayOfTheWeek = " Thu";
			    } else if (i==6){
			        dayOfTheWeek = " Fri";
			    } else if (i==7){
			        dayOfTheWeek = " Sat";
			    } else if (i==1){
			        dayOfTheWeek = " Sun";
			    }
			
			String date = dayOfTheWeek + ", " +
						  Integer.toString( day ) + "-" +
						  Integer.toString( month ) + "-" +
						  Integer.toString( year );
			
			dateView.setText( date );
			
		}
	}
	
}
