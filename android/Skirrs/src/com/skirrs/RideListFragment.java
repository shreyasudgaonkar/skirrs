package com.skirrs;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	
	private User user;
	
	JSONParser jParser = new JSONParser();

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
		public void onItemSelected( String id ) {
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

        SkirrsRideListArrayAdapter adapter =
				new SkirrsRideListArrayAdapter( getActivity(),
												android.R.id.list,
												Ride.RIDES );
        rideListView.setAdapter( adapter );
        
        searchInfoTextView = 
        		( TextView ) rootView.findViewById( R.id.ride_list_search_info );
        
        /*
		 * Get the source and dest via the intent 
		 * ( SearchRidesActivity provides the values )
		 */
		Intent intent = getActivity().getIntent();
		String source = intent.getExtras().getString( "source" );
		String dest   = intent.getExtras().getString( "dest" );

		user = ( User ) intent.getExtras().get( "user" );
		
        searchInfoTextView.setText( source + " to \n" + dest );
        
        return rootView;
    }
	

	@Override
    public void onActivityCreated( Bundle savedInstanceState ) {
		
        super.onActivityCreated( savedInstanceState );

        if ( Util.progressDialog != null &&
        	 Util.progressDialog.isShowing() ) {
        	
        	Util.progressDialog.dismiss();
        	
        }
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

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		// mCallbacks.onItemSelected( Ride.RIDES.get( position ).getId() );
		
		
		for ( int i = 0; i < listView.getCount(); i++ ) {
			
			RideItem rideItem = ( RideItem ) listView.getItemAtPosition( i );
			
			if ( i == position ) {
				
				if ( rideItem.mShowMenu ) {
					
					rideItem.mShowMenu = false;	
					
				} else {
					
					rideItem.mShowMenu = true;
					
				}
				
			} else {
				
				rideItem.mShowMenu = false;
			}
			
		}
		
		( ( BaseAdapter ) listView.getAdapter() ).notifyDataSetChanged();
		
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		
		super.onSaveInstanceState( outState );
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
			getListView().setItemChecked( mActivatedPosition, false );
		} else {
			getListView().setItemChecked( position, true );
		}

		mActivatedPosition = position;
	}
	
	
	public class SkirrsRideListArrayAdapter extends BaseAdapter {

	    Context mContext;
	    
	    ArrayList< RideItem >  mItems;
	    private boolean[]      mRowStatuses;
	    private LayoutInflater mInflater;

	    public SkirrsRideListArrayAdapter( Context context,
	    								   int textViewResourceId,
	    								   ArrayList< RideItem > objects ) {
	    	mContext  = context;
	        mInflater = LayoutInflater.from( context );
	        
	        mItems = objects;
	        
	        int count = objects.size();
			mRowStatuses = new boolean[ count ];
			for ( int i = 0; i < count; i++ ) {
				mRowStatuses[ i ] = false;
			}
	    }
	    
	    @Override
		public int getCount() {
			return mItems.size();
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

	        if ( convertView == null ) {
	        	
	            convertView = mInflater.inflate( R.layout.ride_list_item, null );
	            holder = new ViewHolder();
	            
	            holder.mFrom   =
	            	( TextView ) convertView.findViewById( R.id.ride_list_from );
	            
	            holder.mTo     = 
	            	( TextView ) convertView.findViewById( R.id.ride_list_to );

	            holder.mPrice  =
	            	( TextView ) convertView.findViewById( R.id.ride_list_price );
	            
	            holder.mImageView =
	            	( ImageView ) convertView.findViewById( R.id.ride_list_photo );
	            
	            holder.mSeats  =
	            	( TextView ) convertView.findViewById( R.id.ride_list_seats );

	            holder.mDateTime  =
		            ( TextView ) convertView.findViewById( R.id.ride_list_datetime );
	            
	            holder.mThumbsUp = 
	            	( TextView ) convertView.findViewById( R.id.ride_list_thumbsup );
	            
	            convertView.setTag( holder );
	            
	        } else
	            holder = ( ViewHolder ) convertView.getTag();

	       
	        String source = "";
	        
	        /*
	         * Establishment name
	         */    
	        if ( rowItem.mSrcEstablishment != null && 
	        	 rowItem.mSrcEstablishment.length() > 1 ) {

	        	source = rowItem.mSrcEstablishment + ", ";
	        	
	        } 
	        
	        /*
	         * Street/road
	         */
	        if ( rowItem.mSrcRoute != null && 
	        	 rowItem.mSrcRoute.length() > 1 ) {
	        	
	        	source = source + rowItem.mSrcRoute + ", ";
	        }
	        
	        
	        /*
	         * Sublocality
	         * Show sublocality only if road is not present
	         */
	        if ( rowItem.mSrcSubLocality != null && 
	        	 rowItem.mSrcSubLocality.equals( "" ) == false &&
	        	 ( rowItem.mSrcRoute == null || 
	        	   rowItem.mSrcRoute.length() <= 1 ) ) {
	        	
	        	source = source + rowItem.mSrcSubLocality + ", ";
	        	
	        }
	        				    
	        /*
	         * Locality
	         */
	        source = source + rowItem.mSrcLocality;
	        
	        String dest = "";
	        
	        /*
	         * Establishment name
	         */
	         if ( rowItem.mDestEstablishment != null && 
	        	 rowItem.mDestEstablishment.length() > 1 ) {
	        	
	        	dest = rowItem.mDestEstablishment + ", ";
	        	
	        }

	        /*
	         * Street/road
	         */
	        if ( rowItem.mDestRoute != null && 
	        	 rowItem.mDestRoute.length() > 1 ) {
	        	
	        	dest = dest + rowItem.mDestRoute + ", ";
	        	
	        }
	        			  
	        
	        /*
	         * Sublocality
	         * Show sublocality only if road is not present
	         */
	        if ( rowItem.mDestSubLocality != null && 
	        	 rowItem.mDestSubLocality.equals( "" ) == false &&
	        	 ( rowItem.mDestRoute == null ||
	        	   rowItem.mDestRoute.length() <= 1 ) ) {
	        	
	        	dest = dest + rowItem.mDestSubLocality + ", ";
	        	
	        }
	        			  
	        
	        /*
	         * Locality
	         */
	        dest = dest + rowItem.mDestLocality;
	        
	        holder.mFrom.setText( source );
	        holder.mTo.setText( dest );
	        holder.mPrice.setText( rowItem.mPrice );
	        holder.mSeats.setText( rowItem.mSeats );
	        holder.mDateTime.setText( rowItem.mDateTime );
	        holder.mThumbsUp.setText( "0" );
	        
	        /*
	         * Show/Hide the menu under the lisitem based on the boolean value
	         */
	        if ( rowItem.mShowMenu ) {
	        	
	        	( ( LinearLayout ) convertView.
	        					findViewById( R.id.ride_list_menu ) ).
	        							setVisibility( View.VISIBLE );
	        	
	        	TextView requestSeat =
	        			( TextView ) convertView.
	        						findViewById( R.id.ride_list_request_seat );
	        	
	        	/*
	        	 * Set the position tag so that the item can be referenced
	        	 * when it gets clicked
	        	 */
	        	requestSeat.setTag( Integer.valueOf( position ) );
	        	
	        	requestSeat.setOnClickListener( new OnClickListener() {
	        		 
					@Override
					public void onClick( View v ) {

						Util.progressDialog = SkirrsProgressDialog.show( 
												v.getContext(),
												"",
												"",
												true );
						
						int position = ( Integer ) v.getTag();
						
						System.out.println( "Seat request for position: " + position );
						
						RideItem rideItem = getItem( position );
						
						RequestSeatTask requestSeatTask = new RequestSeatTask();
						requestSeatTask.execute( rideItem );
						
					}
				});
	        	
	        	TextView details =
	        			( TextView ) convertView.
	        						findViewById( R.id.ride_list_view_details );
	        	
	        	details.setTag( Integer.valueOf( position ) );
	        	
	        	details.setOnClickListener( new OnClickListener() {
	        		 
					@Override
					public void onClick( View v ) {
	
					}
				});
	        	
		        convertView.setBackgroundColor( Color.LTGRAY );
	        	
	        	
	        } else {
	        	( ( LinearLayout ) convertView.
    					findViewById( R.id.ride_list_menu ) ).
    							setVisibility( View.GONE );

		        convertView.setBackgroundColor( Color.TRANSPARENT );
		        
	        }
	        
	        return convertView;
	    }
	    
	    @Override
	    public void notifyDataSetChanged()
	    {
	    	super.notifyDataSetChanged();
	    }

	    @Override
		public RideItem getItem( int position ) {
			return mItems.get( position );
		}
 
		@Override
		public long getItemId( int position ) {
			return position;
		}

	}
	
	
	public class RequestSeatTask extends AsyncTask< RideItem, Void, Boolean > {

		@Override
		protected Boolean doInBackground( RideItem... arg0 ) {
			
			RideItem rideItem = arg0[ 0 ];
			
            List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
            
            httpParams.add( new BasicNameValuePair( "keyword",
            										Util.KEYWORD_REQUEST_SEAT ) );
            
            httpParams.add( new BasicNameValuePair( "requester_user_id",
													user.mUserId ) );
            
            httpParams.add( new BasicNameValuePair( "requestee_user_id",
													rideItem.mUserId ) );
            
            httpParams.add( new BasicNameValuePair( "ride_id",
													rideItem.mRideId ) );
            
            try {
            	
            	JSONObject json = jParser.makeHttpRequest( Util.url, 
            										   	   "POST",
            										   	   httpParams);
            	
            	if ( json != null && json.length() > 0 ) {
                    
	            	int status = json.getInt( JSONParser.TAG_STATUS );
	            	
	            	if ( status == 1 ) {
	            		
	            		/*
	            		 * Seat successfully requested
	            		 */
	            		return true;
	            		
	            	} else {
	            		
	            		/*
	            		 * Seat request failed
	            		 */
	            		
	            		return false;
	            	}
	            	
                } else {
                	System.out.println( "Failed to request seat" );
                	return false;
                }
            	
            } catch ( JSONException j ) {
            	System.out.println( "JSONException: " + j );
            	return false;
            }

		}
		
		
		@Override
		protected void onPostExecute( final Boolean success ) {
		
			Util.progressDialog.dismiss();
			
			if ( success ) {

    			
				
			} else {

			}
			
		}

	}
	
}
