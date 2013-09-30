<?php

require_once( $_SESSION['SKIRRS_HOME'] . 'lib/mysql_dblib.php');
require_once( $_SESSION['SKIRRS_HOME'] . 'lib/user_lib.php');
require_once( $_SESSION['SKIRRS_HOME'] . 'lib/KLogger.php');

/* 
This function will register the user in skirrs database
1 - Make an entry in users table
2 - Make an entry in user_profiles table
3 - Make an entry in user_addresses table
NOTE: This function receives json encoded array
*/
function register_user($json_arr)
{
	require($_SESSION['SKIRRS_HOME'] . 'lib/password_handler_lib.php');
	$user_info = json_decode($json_arr, true);

	
	// Get the hashed password
	$user_info['password'] = encrypt_password($user_info['password']);
	if ($user_info['password'] == -1)
	{
		return -1;
	}	

		
	// make an entry in users table
	$query1 = "INSERT INTO `users`(`email_address`, `user_id`, `first_name`, `last_name`, `password`, `phone_number`) "
		  ." VALUES ('"
		  .$user_info['email_address']."', DEFAULT,'"
		  .$user_info['first_name']."', '"
		  .$user_info['last_name']."', '"
		  .$user_info['password']."', '"
		  .$user_info['phone_number']."')";
	$insert_result = execute($query1);	
	if($insert_result == -1)
	{
		echo "Insertion in users failed";
		return -1;
	}

	
	// Get the userid that is required to make an entry in skirrs.user_profiles 
	$user_id = get_userid_from_email($user_info['email_address']);
	if (!$user_id)
		return -1;
	
	
	// Get the profile_id for the user
	$query2 = "SELECT `profile_type_id` FROM `profile_types` WHERE `profile_name`='".$user_info['profile_name']."'";
	$fetch_result = fetch($query2, 'single_data');
	if($fetch_result == false)
	{
		echo "Unable to determine profile_type_id for given user's profile";
		clear_user_record_by_userid($user_id);
		return -1;
	}
	$profile_type_id = $fetch_result;	
	
	
	// Make an entry in user_profiles table
	$query3= "INSERT INTO `user_profiles`(`user_id`, `dob`, `sex`, `profile_type_id`, `picture_id`, `rating`, `rides_offered`, `avg_reponse_time`, `registration_date`) "
                ." VALUES ('"
		.$user_id."', '"
		.$user_info['dob']."', '"
		.$user_info['sex']."', "
		.$profile_type_id.", "
		."NULL, "
		."5, "
		."0, "
		."NULL, '"
		.date("Y-m-d")."')";
	$insert_result = execute($query3);	
	if($insert_result == -1)
	{
		echo "Insertion in user_profiles failed";
		clear_user_record_by_userid($user_id);
		return -1;
	}
		
		
	//make an entry in user_addresses table
	$query4 = "INSERT INTO `user_addresses` (`user_id`, `street_address`, `city`, `state`, `zip`, `country`) "
	          ."VALUES ("
		  .$user_id.", '"
		  .$user_info['street_address']."', '"
		  .$user_info['city']."', '"
		  .$user_info['state']."', '"
		  .$user_info['zip']."', '"
		  .$user_info['country']."')";
	$insert_result = execute($query4);	
	if($insert_result == -1)
	{
		echo "User registered but entry in user_addresses failed";
		clear_user_record_by_userid($user_id);
		return -1;
	}
	return 1;	
}


/* Given user_id, this function will delete a user's record from all the tables in database.
   NOTE: Delete in order of dependencies
     - Delete from user_addresses
     - Delete from user_profiles
     - Delete from users
*/
function clear_user_record_by_userid($user_id)
{
	$query = "DELETE FROM `user_addresses` WHERE `user_id`=".$user_id;
	$result = execute($query);	
	if($result == -1)
	{
		echo "Record could not be deleted from user_addresses";
		return -1;
	}
	echo "Deleted from user_addresses";
	
	$query = "DELETE FROM `user_profiles` WHERE `user_id`=".$user_id;
	$result = execute($query);	
	if($result == -1)
	{
		echo "Record could not be deleted from user_profiles";
		return -1;
	}
	echo "Deleted from user_profiles";
	
	$query = "DELETE FROM `users` WHERE `user_id`=".$user_id;
	$result = execute($query);	
	if($result == -1)
	{
		echo "User record could not be deleted";
		return -1;
	}
	echo "User record deleted";
	return 1;	
}


/* Given email_address, this function will delete user's record from all the tables in database
	- Gets user_id from email_address
	- Calls clear_user_record_by_userid
*/
function clear_user_record_by_email($email_address) 
{
	$result = -1;
	$user_id = get_userid_from_email($email_address);
	if($user_id)
	{
		$result = clear_user_record_by_userid($user_id);
	}
	return $result;	
}

function get_userid_from_email($email_address)
{

	$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);
	$log->logInfo( "Inside get_userid_from_email" );
	
	$query = "SELECT `user_id` FROM `users` WHERE `email_address`='".$email_address."'";
	$result = fetch($query, 'single_data');
	if($result == false)
	{
		return -1;
	}
	return $result;
}


function get_encrypted_password_from_email($email_address)
{
	
	$query = "SELECT `password` FROM `users` WHERE `email_address`='".$email_address."'";
	$result = fetch($query, 'single_data');
	if($result == false)
	{
		echo "Getting encrypted_password failed";
		return -1;
	}
	return $result;
}


function get_user_details_from_email( $email_address )
{
	$response = array();
	$response[ 'status'] = 0;

	if ( isset ( $_POST[ 'email_address' ] ) ) {

		$email_address = $_POST[ 'email_address' ];

		$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);
	
		$log->logInfo( "In get_user_details_from_email: $email_address" );
	
		$query = "SELECT `user_id`, `first_name`, `last_name`, `phone_number` FROM `users` WHERE `email_address`='".$email_address."'";
		$result = fetch($query, 'single_row');

		if($result == false)
		{
			$log->logError( "Unable to get user details" );

		} else {

			$response['status'] = 1;
			$response[ 'first_name' ]    = $result[ 'first_name' ];
            $response[ 'last_name' ]     = $result[ 'last_name' ];
            $response[ 'phone_number' ]  = $result[ 'phone_number' ];
            $response[ 'user_id' ]       = $result[ 'user_id' ];
            $response[ 'email_address' ] = $email_address;
		}
	}
	
	return json_encode( $response );
}


/*
 This function will register the user when the user has logged-in using facebook
NOTE: This function receives json encoded array
*/
function register_fb_user($json_arr)
{
	require($_SESSION['SKIRRS_HOME'] . 'lib/password_handler_lib.php');
	$user_info = json_decode($json_arr, true);

	// make an entry in users table
	$query1 = "INSERT INTO `users`(`email_address`, `user_id`, `first_name`, `last_name`) "
			." VALUES ('"
			.$user_info['email_address']."', DEFAULT,'"
					.$user_info['first_name']."', '"
							.$user_info['last_name'] .")";
	
	$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);
	
	$log->logInfo( "In register_fb_user: $query1" );
	
	$register_result = execute($query1);
	if($register_result == -1)
	{
		echo "Failed to register the user";
		return -1;
	}

}

?>

		
