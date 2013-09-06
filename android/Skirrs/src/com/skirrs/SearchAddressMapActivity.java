package com.skirrs;

import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchAddressMapActivity extends Activity
									  implements LatLngClient {

	GoogleMap mGoogleMap;
	
	String input;

	/*
	 * Token will be handed over to the background task
	 * When it calls the callback function, the token will be compared.
	 * If the tokens don't match, then the callback is discarded since another
	 * request went through for the background task while waiting for the 
	 * previous one
	 */
	int token = 0;
	
	HashMap< String, Double > latLng = null;
	HashMap< String, String > formattedAddresses = null;
	
	private int requestCode;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_search_address_map );
		
		MapFragment mapFragment = ( MapFragment ) getFragmentManager().findFragmentById( R.id.map );
        mGoogleMap = mapFragment.getMap();
        
        /*
         * requestCode is used for returning the result of the activity
         */
        requestCode = getIntent().getExtras().getInt( "requestCode" );
        
        /*
         * Initialize the HashMaps
         */
        latLng             = new HashMap< String, Double >();
	   	formattedAddresses = new HashMap< String, String>();
        
        /*
         * Get the action bar and enable custom view
         */
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled( true );
        actionBar.setDisplayShowCustomEnabled( true );

        /*
         * Add the autocompletetextview to the action bar as a custom view
         */
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService( Context.LAYOUT_INFLATER_SERVICE) ;
        View v = inflator.inflate( R.layout.search_address_map_layout, null);

        actionBar.setCustomView( v );
        
        /*
         * Get the autocompletetextview and set the adapter
         */
        final AutoCompleteTextView searchAddrMapAuto = ( AutoCompleteTextView ) 
        					findViewById( R.id.searchAddressMapAutoComplete );
        
        searchAddrMapAuto.setAdapter( new AddressAutoCompleteAdapter( 
			       				this, R.layout.address_autocomplete_list ) );
        
        /*
         * Add an onItemClickListener so that the map can be updated when the
         * user selects an address from the autocomplete list
         */
        searchAddrMapAuto.setOnItemClickListener( new OnItemClickListener() {

            @Override
            public void onItemClick( AdapterView<?> parent, 
            						 View arg1,
            						 int pos,
            						 long id ) {
            	
            	 String input = searchAddrMapAuto.getText().toString();
            	 System.out.println( "Selected: " + input );
            	 hideSoftKeyboard();
                 getLatLng( input ); 
            }
        });

	}
	
	
	/*
	 * 
	 */
	public void returnResult( View v )
	{
		Intent output = new Intent();
		output.putExtra( "requestCode", requestCode );
		
		if ( formattedAddresses != null && 
				formattedAddresses.containsKey( input ) ) {
			
			output.putExtra( "address", formattedAddresses.get( input ) );
			setResult( RESULT_OK, output );
			
		} else {
			
			setResult( RESULT_CANCELED, output );
			
		}
		
		finish();
	}
	
	
	/*
	 * Hide the soft keyboard when not needed
	 */
	public void hideSoftKeyboard()
	{
		InputMethodManager inputManager = ( InputMethodManager ) 
				getApplicationContext().getSystemService( Context.INPUT_METHOD_SERVICE );
		
  		inputManager.hideSoftInputFromWindow( this.getCurrentFocus().getWindowToken(),      
      		    							  InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	
	/**
	 * 
	 * @param input
	 */
	public void getLatLng( String input )
	{
		/*
         * Get the lat, lng and formatted address for the selected entry
         */
		
		System.out.println( "Invoking GetLatLngTask with input: " + input );
		
		this.input = input;
	   	GetLatLngTask getlatLng = new GetLatLngTask( latLng,
	   												 formattedAddresses,
	   												 this );
	   	
	   	getlatLng.execute( input );
	}
	

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		return true;
	}


	@Override
	public void postExecuteCallback( boolean success ) {
		
		System.out.println( "in postExecuteCallback" );
		
		if ( success == false || latLng == null || latLng.size() < 1 ) {
			return;
		}
		
		double inputLat = latLng.get( this.input + GetLatLngTask.LAT_SUFFIX );
		double inputLng = latLng.get( this.input + GetLatLngTask.LNG_SUFFIX );
		
		final LatLng INPUT = new LatLng( inputLat, inputLng );
		
		System.out.println( "Placing marker at " + INPUT.toString() );
		
		CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target( INPUT )      // Sets the center of the map to Mountain View
	    .zoom( 14 )             // Sets the zoom
	    
	    .build();            // Creates a CameraPosition from the builder
		
		mGoogleMap.animateCamera( 
					CameraUpdateFactory.newCameraPosition( cameraPosition ) );
		
		mGoogleMap.addMarker( new MarkerOptions()
							        .position( INPUT )
							        .title( "Select this location: " + input ) );
		
	}


	@Override
	public void preExecuteCallback() {
		
		
	}

}
