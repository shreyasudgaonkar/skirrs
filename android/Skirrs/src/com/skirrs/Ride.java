package com.skirrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	public static List< RideItem > RIDES = new ArrayList< RideItem >();

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
		
		private String mId;
		private String mFrom;
		private String mTo;
		private String mDateTime;
		private String mPrice;
		private String mSeats;
		private String mUserId;

		public RideItem( String from,
						 String to, 
						 String dateTime,
						 String seats,
						 String price,
						 String userId ) 
		{
			mFrom     = from;
			mTo       = to;
			mDateTime = dateTime;
			mSeats    = seats;
			mUserId   = userId;
			mPrice    = price;
			mId       = Integer.toString( rideID++ );
		}
		
		public String getDescription()
		{
			return mFrom + " " +
				   mTo + " " +
				   mDateTime + " ";
		}
		
		public String getFrom()
		{
			return mFrom;
		}
		
		public String getTo()
		{
			return mTo;
		}
		
		public String getDateTime()
		{
			return mDateTime;
		}
		
		public String getPrice()
		{
			return mPrice;
		}
		
		public String getSeats()
		{
			return mSeats;
		}
		
		public String getId()
		{
			return mId;
		}
		
		public String getUserId()
		{
			return mUserId;
		}
		
		@Override
		public String toString() {
			
			return mFrom + " " +
				   mTo + " " +
				   mDateTime + " ";
		}
		
	}
	
}
