<?php

define('ROOT', '/Applications/MAMP/htdocs/skirrs/lib/');

require_once( ROOT .  'password_handler_lib.php');
require_once( ROOT .  'KLogger.php');
require_once( ROOT .  'user_lib.php');


/*
 * This function will verify the user's login credentials with the ones that are stored in database
 * Note: input is a json struct with email_address and password keys.
 */
function verify_login_credentials($json_arr)
{	
	
	$log = new KLogger('/Users/Shreyas/Desktop', KLogger::INFO);
	
	$log->logInfo( "Inside verify_login_credentials, request: $json_arr" );
	
	$response = array();
	$response[ 'success' ] = 0;
	
	$login_info_a = json_decode($json_arr, true);
	
	$log->logInfo( "login_info:", $login_info_a );
	
	$email_addr = $login_info_a[ 'email_address' ];
	
	$log->logInfo( "email_addr: $email_addr" );
	
	$email_addr_verified = verify_email_address( $email_addr );
	
	$log->logInfo( "email_addr_verified: $email_addr_verified" );
	
	if( $email_addr_verified == true )
	{
		$log->logInfo( "email verified" );
		
		$stored_password = get_encrypted_password_from_email($login_info_a['email_address']);
		
		$log->logInfo( "stored pass: ", $stored_password );
		
		$match = verify_password($login_info_a['password'], $stored_password);
		if($match)
		{
			$response[ 'success' ] = 1;
			$log->logInfo( "Password verified! Sign-in successful" );
		} else {
			$log->logInfo( "Password incorrect! Sign-in unsuccessful" );
		}
	} else {
		$log->logInfo( "Email address not found! Sign-in unsuccessful" );
	} 
	
	$log->logInfo( "Done in verify_credentials" );
	
	return json_encode( $response );
}


/*
  * This function will verify email_address given on login page
  */
function verify_email_address($email_address)
{
	$log = new KLogger('/Users/Shreyas/Desktop', KLogger::INFO);
	
	$log->logInfo( "email_address: $email_address" );
	
	$retval = get_userid_from_email($email_address);

	if($retval != -1)
	{
		$log->logInfo( "Email address verification successful" );
		return TRUE;
	}
	$log->logInfo( "Email address verification failed" );
	return FALSE;
}

?>

		
