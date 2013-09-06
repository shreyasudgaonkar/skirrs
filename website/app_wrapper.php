<?php
session_start();
require('session_constants.php');
require_once($_SESSION['SKIRRS_HOME'] . 'lib/KLogger.php');
$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);

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
						"sign_in" => array(
											"lib"  => "verify_lib.php",
											"func" => "verify_login_credentials"
										  ),
						"submit_ride" => array(
									     		"lib"  => "rides_offered_lib.php",
										    	"func" => "submit_ride"
										      )
						/* TODO:
						"user_details" => array(
									        	  "lib"  => "user_lib.php",
										    	  "func" => "******"
										       ),
						"register" => array(
											"lib"  => "user_lib.php",
											"func" => "register_user"
										   ),										       
						*/
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
		$lib_name = $map_keyword[$keyword]['lib'];
		$func_to_call = $map_keyword[$keyword]['func'];

		$lib_path = $_SESSION['SKIRRS_HOME'] . 'lib/'. $lib_name;		
		require_once($lib_path);
		$params_json = json_encode($_POST);
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