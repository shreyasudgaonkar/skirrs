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
//TODO: Replace $hash with $email_address, and get $hash from skirrs.users table using email_address
function verify_password($password, $hash)
{
	$hasher = new PasswordHash(8, false);
	$check = $hasher->CheckPassword($password, $hash);
	if ($check)
		return 1;
	else
		return -1;
}
?>

		