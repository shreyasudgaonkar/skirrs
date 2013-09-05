package com.skirrs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
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
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class SubmitRideActivity extends Activity {

	// JSON Parser object
    JSONParser jParser = new JSONParser();
	      
    private AutoCompleteTextView sourceAutoComplete;
    private AutoCompleteTextView destinationAutoComplete;

    private TextView priceTextView;
    private TextView commentsTextView;
    private TextView numSeatsTextView;
    
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
    
    public static final int SOURCE_ADDRESS = 1;
    public static final int DEST_ADDRESS   = 2;
    
    /*
     * HashMap to store the latitudes and longitudes of source and 
     * destination addresses
     * Keys are obtained by concatenating source with lat or lng and
     * destination with lat or lng
     * sourcelat, sourcelng, destlat, destlng
     */
    public HashMap< String, Double > latLng;
    
    /*
     * HashMap to store the formatted address for the source and destination 
     * addresses provided by the user 
     */
    public HashMap< String, String > formattedAddresses;
    
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
		final Drawable x = getResources().getDrawable( R.drawable.map_pin_1 );
		x.setBounds( 0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight() );
		
		sourceAutoComplete.setCompoundDrawables( null, 
												 null, 
												 x,
												 null );
		
		sourceAutoComplete.setOnTouchListener( new OnTouchListener() {
			
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		    	
		        if ( sourceAutoComplete.getCompoundDrawables()[2] == null ) {
		            return false;
		        }
		        
		        if ( event.getAction() != MotionEvent.ACTION_UP ) {
		            return false;
		        }
		        if ( event.getX() > 
		        		sourceAutoComplete.getWidth() - 
		        		sourceAutoComplete.getPaddingRight() - 
		        		x.getIntrinsicWidth() ) {
		            
		        	System.out.println( "Launching map activity" );
		        	
		        	Intent selectAddress = new Intent( getApplicationContext() ,
		        									   SearchAddressMapActivity.class );
		        	selectAddress.putExtra( "requestCode", SOURCE_ADDRESS );
		        	startActivityForResult( selectAddress, SOURCE_ADDRESS );
		        	
		        }
		        return false;
		    }
		});
				
		
		/*
		 * Auto complete for the destination address field
		 */
		destinationAutoComplete = ( AutoCompleteTextView ) findViewById( R.id.destination );
		destinationAutoComplete.setAdapter( new AddressAutoCompleteAdapter( 
			       							this, R.layout.address_autocomplete_list ) );
		
		destinationAutoComplete.setOnTouchListener( new OnTouchListener() {
			
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		    	
		        if ( destinationAutoComplete.getCompoundDrawables()[2] == null ) {
		            return false;
		        }
		        
		        if ( event.getAction() != MotionEvent.ACTION_UP ) {
		            return false;
		        }
		        if ( event.getX() > 
		        		destinationAutoComplete.getWidth() - 
		        		destinationAutoComplete.getPaddingRight() - 
		        		x.getIntrinsicWidth() ) {
		            
		        	Intent selectAddress = new Intent( getApplicationContext() ,
		        									   SearchAddressMapActivity.class );
		        	selectAddress.putExtra( "requestCode", DEST_ADDRESS );
		        	startActivityForResult( selectAddress, DEST_ADDRESS );
		        	
		        }
		        return false;
		    }
		});
	
		/*
		 * Use the calendar to set current date and time
		 */
		final Calendar c = Calendar.getInstance();
		
		int day   = c.get( Calendar.DAY_OF_MONTH );
		int month = c.get( Calendar.MONTH ) + 1;
		int year  = c.get( Calendar.YEAR );
		
		String input = day + "-" + month + "-" + year;
		String sourceFormat  = "dd-MM-yyyy";
		String desiredFormat = "EE MMM, dd yyyy";
		
		String date = getDateInDesiredFormat( input, sourceFormat, desiredFormat );
		
		dateView = ( TextView ) findViewById( R.id.date );
		dateView.setText( date.toString() );
		
		int hour   = c.get( Calendar.HOUR_OF_DAY );
		int minute = c.get( Calendar.MINUTE );
		
		String strHour   = Integer.toString( hour );
		String strMinute = Integer.toString( minute );
		
		if ( strMinute.length() < 2 ) {
			strMinute = "0" + strMinute;
		}
		
		String time = strHour + ":" + strMinute;
	
		timeView = ( TextView ) findViewById( R.id.time );
		timeView.setText( time.toString() );

		numSeatsTextView = ( TextView ) findViewById( R.id.numseatsavailable );
		
		priceTextView    = ( TextView ) findViewById( R.id.price );
		commentsTextView = ( TextView ) findViewById( R.id.comments );
		
	}

	
	@Override
	protected void onActivityResult( int requestCode, 
									 int resultCode, 
									 Intent data ) {
		
		if ( resultCode == RESULT_OK ){
			
			if ( requestCode == SOURCE_ADDRESS ) {
				
				String address = data.getExtras().getString( "address" );
				sourceAutoComplete.setText( address );
				
			} else if ( requestCode == DEST_ADDRESS ) {
				
				String address = data.getExtras().getString( "address" );
				destinationAutoComplete.setText( address );
			}
			
		}
	    
	}
	
	/**
	 * 
	 * @param input
	 * @param sourceFormat
	 * @param desiredFormat
	 * @return
	 */
	public static String getDateInDesiredFormat( String input, String sourceFormat, String desiredFormat )
	{
		String date = "";
		
		try {
			
			SimpleDateFormat source  = new SimpleDateFormat( sourceFormat, Locale.UK );
			SimpleDateFormat desired = new SimpleDateFormat( desiredFormat, Locale.UK );
			
			Date dt = source.parse( input );
			
			date = desired.format( dt );
			
		} catch ( ParseException p ) {
			
			System.out.println( "Exception " + p + " when parsing date" );
		}
		
		return date;
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
	public void verifyUserInput( View v )
	{
		boolean ok = true;
		
		if ( selectedDate == null || selectedDate.length() < 1 ) {
			dateView.setError( "Required" );
			ok = false;
		}
		
		if ( selectedTime == null || selectedTime.length() < 1 ) {
			timeView.setError( "Required" );
			ok = false;
		}
		
		if ( priceTextView.getText().toString().length() < 1 ) {
			priceTextView.setError( "Required" );
			ok = false;
		}
		
		if ( commentsTextView.getText().toString().length() < 1 ) {
			commentsTextView.setError( "Required" );
			ok = false;
		}
		
		if ( numSeatsTextView.getText().toString().length() < 1 ) {
			numSeatsTextView.setError( "Required" );
			ok = false;
		}
		
		if ( sourceAutoComplete.getText().toString().length() < 1 ) {
			sourceAutoComplete.setError( "Required" );
			ok = false;
		}
		
		if ( destinationAutoComplete.getText().toString().length() < 1 ) {
			destinationAutoComplete.setError( "Required" );
			ok = false;
		}
		
		if ( ok ) {
			
			String source      = sourceAutoComplete.getText().toString();
			String destination = destinationAutoComplete.getText().toString();
	
			GetLatLngTask getLatLngtask = new GetLatLngTask();
			getLatLngtask.execute( source, destination );
			
		}
			
	}
	
	
	/**
	 * This task is used to get the formatted address, latitude and longitude 
	 * for the given address(es)
	 */
	public class GetLatLngTask extends AsyncTask< String, Void, Boolean > {

		
		/**
		 * 
		 */
		@Override
		protected void onPreExecute() {
			
			/*
			 * Show a dialog to indicate progress
			 */
		}
		
		
		/**
		 * 
		 */
		@Override
		protected Boolean doInBackground( String... input ) {
			
			int inputLen = input.length;

			latLng             = new HashMap<String, Double>();
			formattedAddresses = new HashMap< String, String >();
			
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
		        			
		        			latLng.put( address + "lat", lat );
		        			latLng.put( address + "lng", lng );
		        			
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
			
			/*
			 * If we have successfully fetched the formatted address, latitude 
			 * and longitude for the source and destination addresses, then
			 * we are ready to commit the ride to database 
			 */
			if ( success ) {
				
				try {
					
					/*
					 * Date and time
					 */
					SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.UK );
					Date inputDate = fmt.parse( selectedDate + " " + selectedTime );
					System.out.println( "inputDate: " + inputDate );
					
					/*
					 * Price per seat
					 */
					String price = priceTextView.getText().toString();
					System.out.println( "price: " + price );
					
					/*
					 * Number of seats
					 */
					String numSeats = numSeatsTextView.getText().toString();
					System.out.println( "numSeats: " + numSeats );
					
					/*
					 * Comments
					 */
					String comments = commentsTextView.getText().toString();
					System.out.println( "comments: " + comments );
					
					/*
					 * Source latitude, longitude and formatted address
					 */
					String source = sourceAutoComplete.getText().toString();
					Double srcLat = latLng.get( source + "lat" );
					Double srcLng = latLng.get( source + "lng" );
					String srcFmtAddr = formattedAddresses.get( source );
					
					/*
					 * Destination latitude, longitude and formatted address
					 */
					String dest = destinationAutoComplete.getText().toString();
					Double destLat = latLng.get( dest + "lat" );
					Double destLng = latLng.get( dest + "lng" );
					String destFmtAddr = formattedAddresses.get( dest );
					
					/*
					 * Create an arraylist of all params
					 */					
					List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
					
			        httpParams.add( new BasicNameValuePair( "source",      srcFmtAddr ) );
			        httpParams.add( new BasicNameValuePair( "destination", destFmtAddr ) );
			        httpParams.add( new BasicNameValuePair( "user_id",     user_id ) );
			        httpParams.add( new BasicNameValuePair( "price",       price ) );
			        httpParams.add( new BasicNameValuePair( "datetime",    inputDate.toString() ) );
			        httpParams.add( new BasicNameValuePair( "comments",    comments ) );
			        httpParams.add( new BasicNameValuePair( "num_seats",   numSeats ) );
			        httpParams.add( new BasicNameValuePair( "srcLat",      srcLat.toString() ) );
			        httpParams.add( new BasicNameValuePair( "srcLng",      srcLng.toString() ) );
			        httpParams.add( new BasicNameValuePair( "destLat",     destLat.toString() ) );
			        httpParams.add( new BasicNameValuePair( "destLng",     destLng.toString() ) );
					
				} catch ( ParseException p ) {
					
					System.out.println( " Exception when parsing date " + p );
					
				}
				
			}
			
			/*
			 * Close the progress dialog
			 */
			
		}
		
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

	    public abstract boolean onDrawableTouch( final MotionEvent event );

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

			String date = "";
			
			month = month + 1;
			
			selectedDate = Integer.toString( day ) + "-" + 
						   Integer.toString( month ) + "-" + 
						   Integer.toString( year );
			
			String sourceFormat  = "dd-MM-yyyy";
			String desiredFormat = "EE, MMM dd, yyyy";
			
			date = getDateInDesiredFormat( selectedDate, sourceFormat, desiredFormat );
			System.out.println( "date: " + date );
		
			dateView.setText( date );
			
		}
	}
	
}
