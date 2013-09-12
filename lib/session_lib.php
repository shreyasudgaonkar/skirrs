<?php

require_once( $_SESSION['SKIRRS_HOME'] . 'lib/KLogger.php');
require_once( $_SESSION['SKIRRS_HOME'] . 'lib/mysql_dblib.php');
require_once( $_SESSION['SKIRRS_HOME'] . 'lib/user_lib.php');

function set_user_info_in_session($email_addr)
{
	$log = new KLogger($_SESSION['LOG_DIR'] , KLogger::INFO);
	$log->logInfo( "Storing user info in the current session, email_address: $email_addr" );

	$user_info_json = get_user_details_from_email( $email_addr );
	$user_info = json_decode($user_info_json, true);
	$log->logInfo( "Got user info as ". serialize($user_info));
	foreach ($user_info as $key => $value) {
		$_SESSION[$key] = $value;
 	}
 	$log->logInfo("Session variables: ". serialize($_SESSION));

}

