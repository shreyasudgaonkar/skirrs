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
		
		private String mId;
		private String mFrom;
		private String mTo;
		private String mDateTime;
		private String mPrice;
		private String mSeats;
		private String mUserId;
		
		private String mSrcEstablishment;
		private String mSrcRoute;
		private String mSrcSubLocality;
		private String mSrcLocality;
		private String mSrcAdminArea1;
		private String mSrcAdminArea2;
		
		private String mDestEstablishment;
		private String mDestRoute;
		private String mDestSubLocality;
		private String mDestLocality;
		private String mDestAdminArea1;
		private String mDestAdminArea2;
		
		private boolean mShowMenu;

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
			mShowMenu = false;
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
		
		public void setShowMenu( boolean showMenu )
		{
			mShowMenu = showMenu;
		}
		
		public boolean getShowMenu()
		{
			return mShowMenu;
		}
		
		@Override
		public String toString() {
			
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
		
		public String getmSrcEstablishment() {
			return mSrcEstablishment;
		}

		public void setmSrcEstablishment(String mSrcEstablishment) {
			this.mSrcEstablishment = mSrcEstablishment;
		}

		public String getmSrcRoute() {
			return mSrcRoute;
		}

		public void setmSrcRoute(String mSrcRoute) {
			this.mSrcRoute = mSrcRoute;
		}

		public String getmSrcSubLocality() {
			return mSrcSubLocality;
		}

		public void setmSrcSubLocality(String mSrcSubLocality) {
			this.mSrcSubLocality = mSrcSubLocality;
		}

		public String getmSrcLocality() {
			return mSrcLocality;
		}

		public void setmSrcLocality(String mSrcLocality) {
			this.mSrcLocality = mSrcLocality;
		}

		public String getmSrcAdminArea1() {
			return mSrcAdminArea1;
		}

		public void setmSrcAdminArea1(String mSrcAdminArea1) {
			this.mSrcAdminArea1 = mSrcAdminArea1;
		}

		public String getmSrcAdminArea2() {
			return mSrcAdminArea2;
		}

		public void setmSrcAdminArea2(String mSrcAdminArea2) {
			this.mSrcAdminArea2 = mSrcAdminArea2;
		}

		public String getmDestEstablishment() {
			return mDestEstablishment;
		}

		public void setmDestEstablishment(String mDestEstablishment) {
			this.mDestEstablishment = mDestEstablishment;
		}

		public String getmDestRoute() {
			return mDestRoute;
		}

		public void setmDestRoute(String mDestRoute) {
			this.mDestRoute = mDestRoute;
		}

		public String getmDestSubLocality() {
			return mDestSubLocality;
		}

		public void setmDestSubLocality(String mDestSubLocality) {
			this.mDestSubLocality = mDestSubLocality;
		}

		public String getmDestLocality() {
			return mDestLocality;
		}

		public void setmDestLocality(String mDestLocality) {
			this.mDestLocality = mDestLocality;
		}

		public String getmDestAdminArea1() {
			return mDestAdminArea1;
		}

		public void setmDestAdminArea1(String mDestAdminArea1) {
			this.mDestAdminArea1 = mDestAdminArea1;
		}

		public String getmDestAdminArea2() {
			return mDestAdminArea2;
		}

		public void setmDestAdminArea2(String mDestAdminArea2) {
			this.mDestAdminArea2 = mDestAdminArea2;
		}
		
	}
	
}
