<?php


/*
This function will verify the user's login credentials and the ones that are stored in database
Note: input is a json struct with email_address and password keys.
*/
function verify_login_credentials($json_arr)
{	
	require('password_handler_lib.php');
	$login_info = json_decode($json_arr, true);
	if(verify_email_address($login_info['email_address']))
	{	
		$stored_password = get_encrypted_password_from_email($login_info['email_address']);
		$match = verify_password($login_info['password'], $stored_password);
		if($match)
		{
			return TRUE;
		}
	}	
	return FALSE;
}


/*
This function will verify email_address given on login page
*/
function verify_email_address($email_address)
{
	require('user_lib.php');
	$result = get_userid_from_email($email_address);
	if($result != -1)
	{
		return TRUE;
	}
	return FALSE;
}
?>

		