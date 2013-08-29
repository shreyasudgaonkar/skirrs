<?php

/*
 * The following code will invoke the function verify_login_credentials() in verify_lib.php 
 * and then returns the response as a JSON object.
 * The android (or iOS) app will invoke this php file. It will pass the username and password
 * as POST parameters.
 * HTML code directly invokes verify_login_credentials()
 * This file is an indirection used only by apps (android or iOS)
 */
if( isset( $_POST[ 'email_address' ] ) && isset( $_POST[ 'password' ] ) ) {

	require( '../verify_lib.php' );

	$request = json_encode( $_POST );
	echo verify_login_credentials( $request );
}

?>
