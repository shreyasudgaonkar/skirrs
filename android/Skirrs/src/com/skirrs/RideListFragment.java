package com.skirrs;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.skirrs.Ride.RideItem;

/**
 * A list fragment representing a list of Rides. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link RideDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class RideListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
	private ListView rideListView;
	
	private TextView searchInfoTextView;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RideListFragment() {
	}
	
	
	@Override
    public View onCreateView( LayoutInflater inflater,
    						  ViewGroup container,
    						  Bundle savedInstanceState ) {
		
        View rootView = inflater.inflate( R.layout.fragment_ride_list,
                						  container,
                						  false );
        
        rideListView = ( ListView ) rootView.findViewById( android.R.id.list );

        searchInfoTextView = 
        		( TextView ) rootView.findViewById( R.id.ride_list_search_info );
        
        /*
		 * Get the source and dest via the intent 
		 * ( SearchRidesActivity provides the values )
		 */
		Intent intent = getActivity().getIntent();
		String source = intent.getExtras().getString( "source" );
		String dest   = intent.getExtras().getString( "dest" );
		
		System.out.println( "In fragment, source: " + source );
        
        searchInfoTextView.setText( source + " to \n" + dest );
        
        return rootView;
    }
	

	@Override
    public void onActivityCreated( Bundle savedInstanceState ) {
		
        super.onActivityCreated( savedInstanceState );

        SkirrsRideListArrayAdapter adapter =
				new SkirrsRideListArrayAdapter( getActivity(),
												android.R.id.list,
												Ride.RIDES );
        rideListView.setAdapter( adapter );
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState ) {
		
		super.onViewCreated( view, savedInstanceState );

		// Restore the previously serialized activated item position.
		if ( savedInstanceState != null
				&& savedInstanceState.containsKey( STATE_ACTIVATED_POSITION ) ) {
			
			setActivatedPosition( savedInstanceState.getInt( 
													STATE_ACTIVATED_POSITION ) );
		}
	}

	@Override
	public void onAttach( Activity activity ) {
		
		super.onAttach( activity );

		// Activities containing this fragment must implement its callbacks.
		if ( !( activity instanceof Callbacks ) ) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = ( Callbacks ) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick( ListView listView,
								 View view,
								 int position,
								 long id ) {
		
		super.onListItemClick( listView, view, position, id );

		ArrayAdapter<RideItem> a = new ArrayAdapter<RideItem>( getActivity(),
															   android.R.id.list );
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		// mCallbacks.onItemSelected( Ride.RIDES.get( position ).getId() );
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		if ( mActivatedPosition != ListView.INVALID_POSITION ) {
			
			// Serialize and persist the activated item position.
			outState.putInt( STATE_ACTIVATED_POSITION, mActivatedPosition );
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick( boolean activateOnItemClick ) {
		
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition( int position ) {
		
		if (position == ListView.INVALID_POSITION) {		
			getListView().setItemChecked( mActivatedPosition, false);
		} else {
			getListView().setItemChecked( position, true );
		}

		mActivatedPosition = position;
	}
	
	
	public class SkirrsRideListArrayAdapter extends ArrayAdapter<RideItem> {

	    Context context;

	    public SkirrsRideListArrayAdapter( Context context,
	    								   int textViewResourceId,
	    								   List<RideItem> objects ) {
	    	
	        super(context, textViewResourceId, objects);
	        this.context = context;
	    }

	    /*private view holder class*/
	    private class ViewHolder {
	    	
	        ImageView mImageView;
	        TextView  mFrom;
	        TextView  mTo;
	        TextView  mPrice;
	        TextView  mSeats;
	        TextView  mDateTime;
	        TextView  mThumbsUp;
	    }

	    @Override
	    public View getView( int position, View convertView, ViewGroup parent ) {
	    	
	        ViewHolder holder = null;
	        RideItem rowItem = getItem( position );

	        Typeface robotoFont = Typeface.createFromAsset( 
	        					getActivity().getAssets(), "Roboto-Regular.ttf"); 
	        
	        LayoutInflater mInflater = ( LayoutInflater ) context
	                .getSystemService( Activity.LAYOUT_INFLATER_SERVICE );
	        
	        if ( convertView == null ) {
	        	
	            convertView = mInflater.inflate( R.layout.ride_list_item, null );
	            holder = new ViewHolder();
	            
	            holder.mFrom   =
	            	( TextView ) convertView.findViewById( R.id.ride_list_from );
	            
	            holder.mFrom.setTypeface( robotoFont );
	            
	            holder.mTo     = 
	            	( TextView ) convertView.findViewById( R.id.ride_list_to );
	            
	            holder.mTo.setTypeface( robotoFont );
	            
	            holder.mPrice  =
	            	( TextView ) convertView.findViewById( R.id.ride_list_price );
	            
	            holder.mPrice.setTypeface( robotoFont );
	            
	            holder.mImageView =
	            	( ImageView ) convertView.findViewById( R.id.ride_list_photo );
	            
	            holder.mSeats  =
	            	( TextView ) convertView.findViewById( R.id.ride_list_seats );
	            
	            holder.mSeats.setTypeface( robotoFont );
	            
	            holder.mDateTime  =
		            	( TextView ) convertView.findViewById( R.id.ride_list_datetime );
	            
	            holder.mDateTime.setTypeface( robotoFont );
	            
	            holder.mThumbsUp = 
	            		( TextView ) convertView.findViewById( R.id.ride_list_thumbsup );
	            
	            convertView.setTag(holder);
	            
	        } else
	            holder = ( ViewHolder ) convertView.getTag();

	        holder.mFrom.setText( rowItem.getFrom() );
	        holder.mTo.setText( rowItem.getTo() );
	        holder.mPrice.setText( rowItem.getPrice() );
	        holder.mSeats.setText( rowItem.getSeats() );        
	        holder.mImageView.setImageResource( R.drawable.user_3 );
	        holder.mDateTime.setText( rowItem.getDateTime() );
	        holder.mThumbsUp.setText( "0" );
	        
	        if ( position % 2 == 1 ) {
	        	//convertView.setBackgroundColor( Color.parseColor( "#E6E6FF" ) );
	        }
	        
	        return convertView;
	    }
	    
	    @Override
	    public void notifyDataSetChanged()
	    {
	    	super.notifyDataSetChanged();
	    }

	}
	
}
