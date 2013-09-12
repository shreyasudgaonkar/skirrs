<?php

if (!isset($_SESSION))
	session_start();

include( $_SESSION['SKIRRS_HOME'] .  'lib/KLogger.php');
include( $_SESSION['SKIRRS_HOME'] . 'lib/mysql_dblib.php');

/* This file is responsible for searching for ride information in the db */
function search_ride( $json_arr )
{
	$response = array();
	$response['status'] = 0; # FAILURE
	
	$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);	
	$log->logInfo( "Inside search_ride, request: $json_arr" );
	
	$ride_info = json_decode( $json_arr, true );
	$ride_info_str = serialize($ride_info);

	$log->logInfo( "ride_info: $ride_info_str" );


	// collect all input params for searching
	
	
	// get data out of the db based on the params specified

	// return the response
	return json_encode( $response );	
}

?>
