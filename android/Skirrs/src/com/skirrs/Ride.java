package com.skirrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app
 */
public class Ride {

	/**
	 * An array of rides
	 */
	public static ArrayList<RideItem> RIDES = new ArrayList< RideItem >();

	/**
	 * A map of rides, by ID.
	 */
	public static Map< String, RideItem > RIDE_MAP = 
										new HashMap< String, RideItem >();

	public static void addItem( RideItem item ) {
		
		RIDES.add( item );
		RIDE_MAP.put( item.mId, item );
	}
	
	private static int rideID = 0;

	/**
	 * A ride item representing a ride in the DB
	 */
	public static class RideItem {
		
		public String mId;
		public String mRideId;
		public String mFrom;
		public String mTo;
		public String mDateTime;
		public String mPrice;
		public String mSeats;
		public String mUserId;
		
		public String mSrcEstablishment;
		public String mSrcRoute;
		public String mSrcSubLocality;
		public String mSrcLocality;
		public String mSrcAdminArea1;
		public String mSrcAdminArea2;
		
		public String mDestEstablishment;
		public String mDestRoute;
		public String mDestSubLocality;
		public String mDestLocality;
		public String mDestAdminArea1;
		public String mDestAdminArea2;
		
		public boolean mShowMenu;

		public RideItem( String from,
						 String to, 
						 String dateTime,
						 String seats,
						 String price,
						 String userId,
						 String rideId ) 
		{
			mFrom     = from;
			mTo       = to;
			mDateTime = dateTime;
			mSeats    = seats;
			mUserId   = userId;
			mPrice    = price;
			mId       = Integer.toString( rideID++ );
			mRideId   = rideId;
			mShowMenu = false;
		}
		
		public String getDescription()
		{
			return mFrom + " " +
				   mTo + " " +
				   mDateTime + " ";
		}
		
		
		
		public void setRideDetails( String srcEstablishment,
									String srcRoute,
									String srcSubLocality,
									String srcLocality,
									String srcAdminArea1,
									String srcAdminArea2,
									String destEstablishment,
									String destRoute,
									String destSubLocality,
									String destLocality,
									String destAdminArea1,
									String destAdminArea2 )
		{
			mSrcEstablishment = srcEstablishment;
			mSrcRoute         = srcRoute;
			mSrcSubLocality   = srcSubLocality;
			mSrcLocality      = srcLocality;
			mSrcAdminArea1    = srcAdminArea1;
			mSrcAdminArea2    = srcAdminArea2;
			
			mDestEstablishment = destEstablishment;
			mDestRoute         = destRoute;
			mDestSubLocality   = destSubLocality;
			mDestLocality      = destLocality;
			mDestAdminArea1    = destAdminArea1;
			mDestAdminArea2    = destAdminArea2;
			
		}
		
	}
	
}
