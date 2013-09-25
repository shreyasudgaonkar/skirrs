package com.skirrs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	
    @Override
    public void onReceive( Context context, Intent intent ) {
    	
		String regId = intent.getExtras().getString( "registration_id" );
		
		if( regId != null && !regId.equals("") ) {
			
			Util.gcmRegistrationId = regId;
			System.out.println( "Util.gcmRegistrationId: " + 
	    		  								Util.gcmRegistrationId );
		}
		
	}
    
}