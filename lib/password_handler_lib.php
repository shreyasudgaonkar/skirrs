<?php

require('PasswordHash.php');
/*
  This function will take the input password during registration and find its hash using PasswordHash.
  Store the hash in password field of users table
*/
function encrypt_password($password)
{
	$hasher = new PasswordHash(8, false);
	
	$hash = $hasher->HashPassword($password);
	if (strlen($hash) < 20) 
		return -1;
	else
		return $hash;
	
}

/*
  This function will be called when a user gives email_address and password to login
  Password will be matched against its stored hash in users table using PasswordHash
*/
function verify_password($email_address, $password)
{
	require('user_lib.php');
        $stored_password = get_encrypted_password_from_email($email_address);
	$hasher = new PasswordHash(8, false);
	$match = $hasher->CheckPassword($password, $stored_password);
	return $match;
}
?>

		