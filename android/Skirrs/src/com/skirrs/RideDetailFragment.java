package com.skirrs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Ride detail screen. This fragment is either
 * contained in a {@link RideListActivity} in two-pane mode (on tablets) or a
 * {@link RideDetailActivity} on handsets.
 */
public class RideDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Ride.RideItem mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RideDetailFragment() {
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		if ( getArguments().containsKey( ARG_ITEM_ID ) ) {
			
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			System.out.println( "Fetching ride info for id: " + getArguments().getString( ARG_ITEM_ID ) );
			System.out.println( "RIDE_MAP " + Ride.RIDE_MAP );
			mItem = Ride.RIDE_MAP.get(	getArguments().getString( ARG_ITEM_ID ) );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState ) {
		
		View rootView = inflater.inflate( R.layout.fragment_ride_detail,
										  container,
										  false );

		// Show the dummy content as text in a TextView.
		if ( mItem != null ) {
			
			System.out.println( "Setting description for " + mItem.getId() );

			if ( rootView != null ) {
			
				TextView description = ( TextView ) rootView.
													findViewById( R.id.ride_detail_fragment );
				
				description.setText( mItem.getDescription() );
			}
		}

		return rootView;
	}
}
