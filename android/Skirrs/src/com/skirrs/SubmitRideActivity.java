package com.skirrs;

import java.text.DecimalFormat;
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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

/*
 * 
 */
public class SubmitRideActivity extends Activity
								implements LatLngClient {

	// JSON Parser object
    JSONParser jParser = new JSONParser();
	      
    private AutoCompleteTextView sourceAutoComplete;
    private AutoCompleteTextView destinationAutoComplete;
    
    private TextView sourceAutoCompleteShowAll;
    private TextView destAutoCompleteShowAll;
    
    private boolean  sourceShowAllOpen = false;
    private boolean  destShowAllOpen = false;

    private TextView priceTextView;
    private TextView commentsTextView;
    private TextView numSeatsTextView;
    
    private String user_id;

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
    public HashMap< String, Double > latLng = null;
    
    /*
     * HashMap to store the formatted address for the source and destination 
     * addresses provided by the user 
     */
    public HashMap< String, String > formattedAddresses = null;
    
    SkirrsProgressDialog submitDialog;
    
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_submit_ride );

	    initialize();
	    
	}

	
	/**
	 * 
	 */
	private void initialize()
	{
		
		latLng             = new HashMap< String, Double >();
		formattedAddresses = new HashMap< String, String >();
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		/*
		 * Get the user id via the intent ( HomeActivity provides it )
		 */
		Intent intent = getIntent();
		user_id = intent.getExtras().getString( "user_id" );
		
		sourceAutoComplete = ( AutoCompleteTextView ) findViewById( R.id.source );
		sourceAutoComplete.setAdapter( new AddressAutoCompleteAdapter( 
								       		this,
								       		R.layout.address_autocomplete_list,
								       		R.id.autocomplete_list_item ) );
		
		/*
		 * Auto complete for the source address field
		 */
		final Drawable map_pin_src = 
						getResources().getDrawable( R.drawable.map_pin_src_3 );
		
		map_pin_src.setBounds( 0, 
							   0,
							   map_pin_src.getIntrinsicWidth(),
							   map_pin_src.getIntrinsicHeight() );
		
		final Drawable remove_text = 
				getResources().getDrawable( R.drawable.remove_text_1 );

		remove_text.setBounds( 0, 
							   0,
							   remove_text.getIntrinsicWidth(),
							   remove_text.getIntrinsicHeight() );
		
		/*
		 * Don't set remove_text drawable during init()
		 * It will be set when text changes using the onTextChangedListener
		 */
		sourceAutoComplete.setCompoundDrawables( map_pin_src, 
												 null, 
												 null,
												 null );
		
		/*
		 * Set an onTouchListener to detect the touch on the drawables
		 */
		sourceAutoComplete.setOnTouchListener( new OnTouchListener() {
			
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		    	
		        if ( sourceAutoComplete.getCompoundDrawables()[0] == null ) {
		            return false;
		        }
		        
		        if ( event.getAction() != MotionEvent.ACTION_UP ) {
		            return false;
		        }
		        if ( event.getX() <
		        		sourceAutoComplete.getLeft() + 
		        		sourceAutoComplete.getPaddingLeft() + 
		        		map_pin_src.getIntrinsicWidth() ) {
	
		        	Intent selectAddress = 
		        				new Intent( getApplicationContext() ,
		        							SearchAddressMapActivity.class );
		        	
		        	selectAddress.putExtra( "requestCode", SOURCE_ADDRESS );
		        	startActivityForResult( selectAddress, SOURCE_ADDRESS );
		        	
		        } else if ( event.getX() >
		        			sourceAutoComplete.getWidth() - 
		        			sourceAutoComplete.getPaddingRight() - 
		        			remove_text.getIntrinsicWidth() ) {
	
		        	sourceAutoComplete.setText( "" );
		        	
		        }
		        
		        return false;
		        
		    }
		    
		});
		
		
		/*
		 * Use a textview to enable compress and expand of the autocompleteview
		 */
		sourceAutoCompleteShowAll = ( TextView ) 
								findViewById( R.id.sourceAutoCompleteShowAll );
		
		sourceAutoCompleteShowAll.setVisibility( View.GONE );
		
		/*
		 * Make the compress/expand textview visible only if there is text
		 * inside the source autocompletetextview
		 */
		sourceAutoComplete.addTextChangedListener( new TextWatcher() {
			
		    @Override
		    public void onTextChanged( CharSequence s,
		    						   int          start,
		    						   int          before,
		    						   int          count ) {
		    	
		    	final Drawable downArrow = 
		    				getResources().getDrawable( R.drawable.down_arrow );
		    	
				downArrow.setBounds( 0,
									 0,
									 downArrow.getIntrinsicWidth(),
									 downArrow.getIntrinsicHeight() );
				
				final Drawable remove_text = 
						getResources().getDrawable( R.drawable.remove_text_1 );

				remove_text.setBounds( 0, 
									   0,
									   remove_text.getIntrinsicWidth(),
									   remove_text.getIntrinsicHeight() );
				
		        if( sourceAutoComplete.getText().toString().equals("") ) {
		        	
		        	/*
		        	 * Empty text. Remove the 'expand' arrow
		        	 */
		        	sourceAutoCompleteShowAll.
		        				setCompoundDrawables( null, null, null, null );
		        	
		        	sourceAutoCompleteShowAll.setVisibility( View.INVISIBLE );
		        	
		        	Drawable[] drawables = sourceAutoComplete.getCompoundDrawables();
		            
		            sourceAutoComplete.setCompoundDrawables( drawables[ 0 ], 
		            										 drawables[ 1 ], 
		            										 null, 
		            										 drawables[ 3 ] );
		            
		        } else {
		    		
		        	/*
		        	 * Show the expand arrow
		        	 */
		            sourceAutoCompleteShowAll.
		            		setCompoundDrawables( null, null, downArrow, null );
		            
		            sourceAutoCompleteShowAll.setVisibility( View.VISIBLE );
		            
		            if ( sourceAutoComplete.getError() != null ) {
		            	sourceAutoComplete.setError( null );
		            }
		            
		            Drawable[] drawables = sourceAutoComplete.getCompoundDrawables();
		            
		            sourceAutoComplete.setCompoundDrawables( drawables[ 0 ], 
		            										 drawables[ 1 ], 
		            										 remove_text, 
		            										 drawables[ 3 ] );
		            
		        }
		        
		    }

		    @Override
		    public void afterTextChanged( Editable arg0 ) {
		    }

		    @Override
		    public void beforeTextChanged( CharSequence s,
		    							   int start,
		    							   int count,
		    							   int after ) {
		    }
		    
		});

		/*
		 * OnClickListener to determine when to expand or compress the textview
		 */
        sourceAutoCompleteShowAll.setOnClickListener( new OnClickListener() {
        	
            @Override
            public void onClick( View v ) {
            	
            	if ( sourceShowAllOpen == false ) {
            	
            		sourceShowAllOpen = true;
            		
	                final Drawable x = 
	                		getResources().getDrawable( R.drawable.up_arrow );
	                
	                x.setBounds( 0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight() );
	                
	                sourceAutoCompleteShowAll.
	                				setCompoundDrawables( null, null, x, null );
	                
	                sourceAutoComplete.setMaxLines( 5 );
	                
            	} else {
            		
            		sourceShowAllOpen = false;
            		final Drawable x = 
            				getResources().getDrawable( R.drawable.down_arrow );
            		
	                x.setBounds( 0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight() );
	                
	                sourceAutoCompleteShowAll.
	                				setCompoundDrawables( null, null, x, null );
	                
	                sourceAutoComplete.setMaxLines( 1 );
            	}

            }
        });
				
		
		/*
		 * Auto complete for the destination address field
		 */
		destinationAutoComplete =
					( AutoCompleteTextView ) findViewById( R.id.destination );
		
		destinationAutoComplete.setAdapter( new AddressAutoCompleteAdapter( 
			       									this,
			       									R.layout.address_autocomplete_list,
			       									R.id.autocomplete_list_item ) );

		final Drawable map_pin_dest = 
				getResources().getDrawable( R.drawable.map_pin_dest_3 );

		map_pin_dest.setBounds( 0, 
							    0,
							    map_pin_dest.getIntrinsicWidth(),
							    map_pin_dest.getIntrinsicHeight() );
		
		destinationAutoComplete.setCompoundDrawables( map_pin_dest, 
													  null, 
													  null,
													  null );
		
		destinationAutoComplete.setOnTouchListener( new OnTouchListener() {
			
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		    	
		        if ( destinationAutoComplete.getCompoundDrawables()[ 0 ] == null ) {
		            return false;
		        }
		        
		        if ( event.getAction() != MotionEvent.ACTION_UP ) {
		            return false;
		        }
		        if ( event.getX() <
		        		destinationAutoComplete.getLeft() + 
		        		destinationAutoComplete.getPaddingLeft() + 
		        		map_pin_dest.getIntrinsicWidth() ) {
		            
		        	/*
		        	 * Start the map activity which will return the address
		        	 * selected by the user
		        	 */
		        	Intent selectAddress = new Intent( getApplicationContext() ,
		        									   SearchAddressMapActivity.class );
		        	
		        	selectAddress.putExtra( "requestCode", DEST_ADDRESS );
		        	startActivityForResult( selectAddress, DEST_ADDRESS );
		        	
		        }  else if ( event.getX() >
		        		destinationAutoComplete.getWidth() - 
		        		destinationAutoComplete.getPaddingRight() - 
    					remove_text.getIntrinsicWidth() ) {

		        			destinationAutoComplete.setText( "" );
    	
		        }
		        return false;
		    }
		});
		
		destAutoCompleteShowAll = ( TextView ) findViewById( R.id.destAutoCompleteShowAll );
		destAutoCompleteShowAll.setVisibility( View.GONE );
		
		/*
		 * Make the compress/expand textview visible only if there is text
		 * inside the dest autocompletetextview
		 */
		destinationAutoComplete.addTextChangedListener( new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	
		    	final Drawable downArrow = 
		    				getResources().getDrawable( R.drawable.down_arrow );
		    	
				downArrow.setBounds( 0,
									 0,
									 downArrow.getIntrinsicWidth(),
									 downArrow.getIntrinsicHeight() );
				
				final Drawable remove_text = 
						getResources().getDrawable( R.drawable.remove_text_1 );

				remove_text.setBounds( 0, 
									   0,
									   remove_text.getIntrinsicWidth(),
									   remove_text.getIntrinsicHeight() );
				
		        if( destinationAutoComplete.getText().toString().equals("") ) {
		        	
		        	/*
		        	 * Empty text. Remove the 'expand' arrow
		        	 */
		        	destAutoCompleteShowAll.setCompoundDrawables( null, null, null, null );
		        	destAutoCompleteShowAll.setVisibility( View.INVISIBLE );
		        	
		        	Drawable[] drawables = destinationAutoComplete.getCompoundDrawables();
		            
		        	destinationAutoComplete.setCompoundDrawables( drawables[ 0 ],
		        												  drawables[ 1 ], 
		        												  null, 
		        												  drawables[ 3 ] );
		            
		        } else {
		    		
		        	/*
		        	 * Show the expand arrow
		        	 */
		        	destAutoCompleteShowAll.setCompoundDrawables( null, null, downArrow, null );
		        	destAutoCompleteShowAll.setVisibility( View.VISIBLE );
		        	
		        	if ( destinationAutoComplete.getError() != null ) {
		        		destinationAutoComplete.setError( null );
		        	}
		        	
		        	Drawable[] drawables = destinationAutoComplete.getCompoundDrawables();
		            
		        	destinationAutoComplete.setCompoundDrawables( drawables[ 0 ],
		        												  drawables[ 1 ], 
		        												  remove_text, 
		        												  drawables[ 3 ] );
		        }
		        
		    }

		    @Override
		    public void afterTextChanged(Editable arg0) {
		    }

		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		    }
		    
		});
		
		/*
		 * 
		 */
		destAutoCompleteShowAll.setOnClickListener( new OnClickListener() {
        	
            @Override
            public void onClick( View v ) {
            	
            	if ( destShowAllOpen == false ) {
            	
            		destShowAllOpen = true;
            		
	                final Drawable x = getResources().getDrawable( R.drawable.up_arrow );
	                x.setBounds( 0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight() );                
	                destAutoCompleteShowAll.setCompoundDrawables( null, null, x, null );	                
	                destinationAutoComplete.setMaxLines( 5 );
	                
            	} else {
            		
            		destShowAllOpen = false;
            		final Drawable x = getResources().getDrawable( R.drawable.down_arrow );
	                x.setBounds( 0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight() );
	                destAutoCompleteShowAll.setCompoundDrawables( null, null, x, null );             
	                destinationAutoComplete.setMaxLines( 1 );
            	}

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
		String desiredFormat = "EE, MMM, dd yyyy";
		
		String date = getDateInDesiredFormat( input, sourceFormat, desiredFormat );
		
		dateView = ( TextView ) findViewById( R.id.date );
		dateView.setHint( date.toString() );
		
		int hour   = c.get( Calendar.HOUR_OF_DAY );
		int minute = c.get( Calendar.MINUTE );
		
		String strHour   = Integer.toString( hour );
		String strMinute = Integer.toString( minute );
		
		if ( strMinute.length() < 2 ) {
			strMinute = "0" + strMinute;
		}
		
		String time = strHour + ":" + strMinute;
	
		timeView = ( TextView ) findViewById( R.id.time );
		timeView.setHint( time.toString() );

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
	public static String getDateInDesiredFormat( String input,
												 String sourceFormat,
												 String desiredFormat )
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
		} else {
			System.out.println( "selectedDate: " + selectedDate );
		}
		
		if ( selectedTime == null || selectedTime.length() < 1 ) {
			timeView.setError( "Required" );
			ok = false;
		} else {
			System.out.println( "selectedTime: " + selectedTime );
		}
		
		if ( priceTextView.getText().toString().length() < 1 ) {
			priceTextView.setError( "Required" );
			ok = false;
		} else {
			System.out.println( "priceTextView: " + priceTextView );
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
	
			GetLatLngTask getLatLngtask = new GetLatLngTask( latLng,
															 formattedAddresses,
															 this  );
			
			submitDialog = SkirrsProgressDialog.show( this,
													  "",
													  "",
													  true );
			
			getLatLngtask.execute( source, destination );
			
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
	public static class TimePickerFragment 
						extends DialogFragment
						implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog( Bundle savedInstanceState ) {
			
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour   = c.get( Calendar.HOUR_OF_DAY );
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
			timeView.setError( null );
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
			int year  = c.get( Calendar.YEAR );
			int month = c.get( Calendar.MONTH );
			int day   = c.get( Calendar.DAY_OF_MONTH );
			
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
			dateView.setError( null );
			
		}
	}



	@Override
	public void postExecuteCallback( boolean success ) {
		
		/*
		 * If we have successfully fetched the formatted address, latitude 
		 * and longitude for the source and destination addresses, then
		 * we are ready to commit the ride to database 
		 */
		if ( success && latLng != null && latLng.size() > 0 ) {
			
			SubmitRideTask submitRideTask = new SubmitRideTask();
			submitRideTask.execute();
			
		}
		
	}

	
	/*
	 * 
	 */
	public class SubmitRideTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground( Void... arg0 ) {
			
			try {
				
				/*
				 * Date and time
				 */
				String sourceFormat  = "dd-MM-yyyy HH:mm:ss";
				String desiredFormat = "yyyy-MM-dd HH:mm:ss";
				String inputDate = getDateInDesiredFormat( selectedDate + " " + 
														   selectedTime +":00", 
														   sourceFormat, 
														   desiredFormat);
				
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
				
				DecimalFormat sixDecimal = new DecimalFormat("#.######");
				
				/*
				 * Source latitude, longitude and formatted address
				 */
				String source = sourceAutoComplete.getText().toString();
				Double srcLat = latLng.get( source + "lat" );
				Double srcLng = latLng.get( source + "lng" );
				String srcFmtAddr = formattedAddresses.get( source );
				System.out.println( "srcFmtAddr: " + srcFmtAddr );
				
				srcLat = Double.valueOf( sixDecimal.format( srcLat ) );
				srcLng = Double.valueOf( sixDecimal.format( srcLng ) );
				
				/*
				 * Destination latitude, longitude and formatted address
				 */
				
				String dest = destinationAutoComplete.getText().toString();
				Double destLat = latLng.get( dest + "lat" );
				Double destLng = latLng.get( dest + "lng" );
				String destFmtAddr = formattedAddresses.get( dest );
				System.out.println( "destFmtAddr: " + destFmtAddr );
				
		        destLat = Double.valueOf( sixDecimal.format( destLat ) );
				destLng = Double.valueOf( sixDecimal.format( destLng ) );
		        
				String srcAdminArea1 =
						formattedAddresses.get( source +
												JSONParser.TAG_ADMIN_AREA_1 );
				
				String srcAdminArea2 =
						formattedAddresses.get( source +
												JSONParser.TAG_ADMIN_AREA_2 );
				
				String srcRoute =
						formattedAddresses.get( source +
												JSONParser.TAG_ROUTE );
				
				String srcSublocality =
						formattedAddresses.get( source +
												JSONParser.TAG_SUB_LOCALITY );
				
				String srcLocality =
						formattedAddresses.get( source +
												JSONParser.TAG_LOCALITY );
				
				String srcEstablishment =
						formattedAddresses.get( source +
												JSONParser.TAG_ESTABLISHMENT );
				
				String destAdminArea1 =
						formattedAddresses.get( dest +
												JSONParser.TAG_ADMIN_AREA_1 );
				
				String destAdminArea2 =
						formattedAddresses.get( dest +
												JSONParser.TAG_ADMIN_AREA_2 );
				
				String destRoute =
						formattedAddresses.get( dest +
												JSONParser.TAG_ROUTE );
				
				String destSublocality =
						formattedAddresses.get( dest +
												JSONParser.TAG_SUB_LOCALITY );
				
				String destLocality =
						formattedAddresses.get( dest +
												JSONParser.TAG_LOCALITY );
				
				String destEstablishment =
						formattedAddresses.get( dest +
												JSONParser.TAG_ESTABLISHMENT );
				
				/*
				 * Create an arraylist of all params
				 */					
				List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
				
				httpParams.add( new BasicNameValuePair( "user_id",
														user_id ) );
				
		        httpParams.add( new BasicNameValuePair( "source",
		        										srcFmtAddr ) );
		        
		        httpParams.add( new BasicNameValuePair( "destination",
		        										destFmtAddr ) );
		        
		        httpParams.add( new BasicNameValuePair( "departure_date_time",
		        										inputDate.toString() ) );
		        
		        httpParams.add( new BasicNameValuePair( "seats_offered",
		        										numSeats ) );
		        
		        httpParams.add( new BasicNameValuePair( "price",
		        										price ) );
		        
		        httpParams.add( new BasicNameValuePair( "pick_up_offered",
		        										"0" ) );
		        
		        httpParams.add( new BasicNameValuePair( "share_contact_info",
		        										"0" ) );
		        
		        httpParams.add( new BasicNameValuePair( "srclat",
		        										srcLat.toString() ) );
		        
		        httpParams.add( new BasicNameValuePair( "srclng",
		        										srcLng.toString() ) );
		        
		        httpParams.add( new BasicNameValuePair( "destlat",
		        										destLat.toString() ) );
		        
		        httpParams.add( new BasicNameValuePair( "destlng",
		        										destLng.toString() ) );
		        
		        httpParams.add( new BasicNameValuePair( "source_administrative_area_level_1",
														srcAdminArea1 ) );
		        
		        httpParams.add( new BasicNameValuePair( "source_administrative_area_level_2",
														srcAdminArea2 ) );
		        
		        httpParams.add( new BasicNameValuePair( "source_route",
														srcRoute ) );
		        
		        httpParams.add( new BasicNameValuePair( "source_sublocality",
														srcSublocality ) );
		        
		        httpParams.add( new BasicNameValuePair( "source_locality",
														srcLocality ) );
		        
		        httpParams.add( new BasicNameValuePair( "source_establishment",
														srcEstablishment ) );

		        httpParams.add( new BasicNameValuePair( "dest_administrative_area_level_1",
														destAdminArea1 ) );

				httpParams.add( new BasicNameValuePair( "dest_administrative_area_level_2",
														destAdminArea2 ) );
				
				httpParams.add( new BasicNameValuePair( "dest_route",
														destRoute ) );
				
				httpParams.add( new BasicNameValuePair( "dest_sublocality",
														destSublocality ) );
				
				httpParams.add( new BasicNameValuePair( "dest_locality",
														destLocality ) );
				
				httpParams.add( new BasicNameValuePair( "dest_establishment",
														destEstablishment ) );
		        
		        httpParams.add( new BasicNameValuePair( "comments",
		        										comments ) );
		        
		        httpParams.add( new BasicNameValuePair( "keyword",
		        										Util.KEYWORD_SUBMIT_RIDE ) );
		        
		    
            	// getting JSON string from URL
            	JSONObject json = jParser.makeHttpRequest( Util.url, 
            										   	   "POST",
            										   	   httpParams );
            	
            	if ( json != null && json.length() > 0 ) {
                    
                	System.out.println( "json: " + json );
                	
	            	int status = json.getInt( JSONParser.TAG_STATUS );
	            	
	            	if ( status == 1 ) {
	            		
	            		System.out.println( "Ride successfully submitted" );
	            		
	            	} else {
	            		
	            		return false;
	            	}
	            	
                } else {
                	System.out.println( "Failed to submit ride" );
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
		
			submitDialog.dismiss();
			
			if ( success ) {
				
				finish();
				
			}
			
		}
		
		
	}
	

	@Override
	public void preExecuteCallback() {
		// TODO Auto-generated method stub
		
	}
	
}
