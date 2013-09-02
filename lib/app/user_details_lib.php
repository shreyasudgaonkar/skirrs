<?php

/*
 * This file contains the wrapper functions which will be used by the Android app.
 */
define('ROOT', '/Applications/MAMP/htdocs/skirrs/lib/');

require_once( ROOT . "KLogger.php" );
require_once( ROOT . 'user_lib.php' );

$log = new KLogger('/Users/Shreyas/Desktop', KLogger::INFO);
$log->logInfo( "In user_details_lib.php" );

if ( isset ( $_POST[ 'email_address' ] ) ) {
	
	$response = array();
	$response[ 'success' ] = 0;
	
	$log->logInfo( "Getting user details ..." );
	
	$result = get_user_details_from_email( $_POST[ 'email_address' ] );
	
	if ( $result == -1 ) {
		
		echo json_encode( $response );
	
	} else {
		
		$response[ 'success' ] = "1";
		$response[ 'first_name' ] = $result[ 'first_name' ];
		$response[ 'last_name' ] = $result[ 'last_name' ];
		$response[ 'phone_number' ] = $result[ 'phone_number' ];
		$response[ 'user_id' ] = $result[ 'user_id' ];
		
		echo json_encode( $response );
	}
	
} else {
	$log->log( "email_address not set in \$_POST" );
}

?>