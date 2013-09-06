<?php

require_once( $_SESSION['SKIRRS_HOME'] .  'lib/KLogger.php');
require_once( $_SESSION['SKIRRS_HOME'] . 'lib/mysql_dblib.php');


/* This file is responsible for inserting ride information in the db */
function submit_ride( $json_arr )
{
	$response = array();
	$response['status'] = 0; # FAILURE
	
	$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);	
	$log->logInfo( "Inside submit_ride, request: $json_arr" );
	
	$ride_info = json_decode( $json_arr, true );

	$log->logInfo( "ride_info: $ride_info" );

	/*
	 * Make sure all required parameters are set
	 */
	if ( isset( $ride_info[ 'user_id' ] )  &&
		 isset( $ride_info[ 'source' ] )  &&
		 isset( $ride_info[ 'destination' ] ) &&
		 isset( $ride_info[ 'departure_date_time' ] ) &&
		 isset( $ride_info[ 'seats_offered' ] ) &&
		 isset( $ride_info[ 'price' ] ) &&
		 isset( $ride_info[ 'srclat' ] ) &&
		 isset( $ride_info[ 'srclng' ] ) &&
		 isset( $ride_info[ 'destlat' ] ) &&
		 isset( $ride_info[ 'destlng' ] ) && 
		 isset( $ride_info[ 'comments' ] ) ) {
		
		$mysqldate = date( );
		
		// make an entry in rides_offered table
		$submit_ride = "INSERT INTO `rides_offered` ".
			       "( `ride_id`, `user_id`, `source`, `destination`, `departure_date_time`, `seats_offered`,`price`,".
			       " `pick_up_offered`, `share_contact_info`, `srclat`, `srclng`, `destlat`, `destlng`,  `comments`)".
			       " VALUES ".
					"( DEFAULT, "
					   .$ride_info['user_id'].", '"
					   .$ride_info['source']."', '"
					   .$ride_info['destination']."', '"
					   .$ride_info['departure_date_time']."', "
					   .$ride_info['seats_offered'].", "
					   .$ride_info['price'].", "
					   .$ride_info['pick_up_offered'].", "
					   .$ride_info['share_contact_info'].", "
					   .$ride_info['srclat'].", "
					   .$ride_info['srclng'].", "
					   .$ride_info['destlat'].", "
					   .$ride_info['destlng'].", '"
					   .$ride_info['comments']."')";
		
		$log->logInfo( "Query: ". $submit_ride );
		$insert_result = execute( $submit_ride );
		
		if( $insert_result == -1 )
		{
			$log->logInfo( "Failed to add the ride offer" );
			$response['status'] = 0; # Failure
		} else {
			$log->logInfo( "Ride info successfully added" );
			$response['status'] = 1; # Success
		}
	} else {

		$log->lofInfo( "Required parameters not set" );

	}
		
	return json_encode( $response );	
}

?>
