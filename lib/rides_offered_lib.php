<?php

require_once( $_SESSION['SKIRRS_HOME'] .  'lib/KLogger.php');

function submit_ride( $json_arr )
{
	$response = array();
	$response['success'] = 0;
	
	$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);	
	$log->logInfo( "Inside submit_ride, request: $json_arr" );
	
	$ride_info = json_decode( $json_arr );
	
	/*
	 * Make sure all required parameters are set
	 */
	if ( isset( $ride_info[ 'user_id' ] )  && 
		 isset( $ride_info[ 'source' ] )  &&
		 isset( $ride_info[ 'destination' ] ) && 
		 isset( $ride_info[ 'ltt' ] ) &&
		 isset( $ride_info[ 'lng' ] ) && 
		 isset( $ride_info[ 'price' ] ) &&
		 isset( $ride_info[ 'comments' ] ) ) {
		
		$mysqldate = date( );
		
		// make an entry in users table
		$submit_ride="INSERT INTO `rides_offered`".
							   "( `user_id`, `source`, `destination`, `departure_date_time`, ltt`, `lng`, `price`, `comments`)".
							   "VALUES".
							   "('".$ride_info['user_id']."','"
								  .$ride_info['source']."','"
								  .$ride_info['destination']."','"
								  .$ride_info['destination']."','"
								  .$ride_info['ltt']."','"
								  .$ride_info['lng']."','"
								  .$ride_info['price']."','"
								  .$ride_info['comments']."')";
		
		$log->log( "Query: ", $submit_ride );
		$insert_result = execute($query1);
		
		if( $insert_result == -1 )
		{
			$log->logInfo( "Failed to add the ride offer" );
			$response['success'] = 0;
		} else {
			$response['success'] = 1;
		}
		
	}
		
	return json_encode( $response );	
}

?>