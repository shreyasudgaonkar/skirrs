<?php
session_start();
require('session_constants.php');
require_once($_SESSION['SKIRRS_HOME'] . 'lib/KLogger.php');
$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);

$log->logInfo( "Inside app_wrapper.php" );

/*
	map_keyword[] is kind of a triplet mapping system
	Given the key, it will be used to get the lib file to be included 
	and the relevant function to be called can be determined.

	For any new keyword, include an internal array in $map_keyword in following format:

    $map_keyword = array("<keyword>" => array(
    											"lib" => "<lib_name>",
    											"func" => "<func_to_call>"
    					    				 )
    					)
*/
$map_keyword = array(
						"sign_in" => array( "lib"  => "verify_lib.php",
								    "func" => "verify_login_credentials" ),

						"submit_ride" => array( "lib"  => "rides_offered_lib.php",
									"func" => "submit_ride" ),
						
						"user_details" => array( "lib"  => "user_lib.php",
									 "func" => "get_user_details_from_email" ),

						"register" => array( "lib"  => "user_lib.php",
								     "func" => "register_user" ),
								       
						"search_rides" => array( "lib"  => "search_ride_lib.php",
									 "func" => "search_rides" ),

						"fb_register" => array( "lib"  => "user_lib.php",
									 "func" => "register_fb_user" ),
					);


/*
	If received a POST request, find the keyword and finds its matching library and function using $map_keyword
	Then, run that function. Send appropriate messages and status incase of failures/errors.
*/
$response = array();
$response['status'] = 0; # FAILURE

if(!empty($_POST))
{
	$log->logInfo("Received a POST request, params: $_POST");

	# If keyword is not passed in POST params or an incorrect keyword is POSTED because of which mapping to lib/func failed.
	if( (!isset($_POST['keyword'])) || (! array_key_exists($_POST['keyword'], $map_keyword)) ) 
	{
		$response['message'] = 'No keyword supplied or mapping of keyword failed. Please check the keyword supplied';
		$response_json = json_encode($response);
		$log->logInfo("Response: $response_json");
		echo $response_json;
	}
	else
	{
		$keyword = $_POST['keyword'];

		$log->logInfo( "For keyword: $keyword");

		$lib_name = $map_keyword[$keyword]['lib'];
		$log->logInfo( "lib_name: $lib_name");
		
		$func_to_call = $map_keyword[$keyword]['func'];
		$log->logInfo( "func_to_call: $func_to_call");

		$lib_path = $_SESSION['SKIRRS_HOME'] . 'lib/'. $lib_name;		
		$log->logInfo( "lib_path: $lib_path");

		require_once($lib_path);
		$params_json = json_encode($_POST);

		$log->logInfo( "Invoking $func_to_call" );

		$response_json = call_user_func($func_to_call, $params_json);
		$log->logInfo("Response: $response_json");
		echo $response_json;
	}
}
else # Not a POST request
{
	$response['message'] = "ERROR: Not a POST request";
	$response_json = json_encode($response);
	$log->logInfo("Response: $response");
	echo $response_json;
}

?>
