<?php

/*
Received a json of format: {'address': 'Chembur, Mumbai, India'}
Calculates latitude and longitude of the address
Returns a json of format: {'status': 1, 'lat': '123.123456', 'lng': '123.123456', 'msg': 'Msg only incase of failure'}
status is 1 for success and 0 for false
*/
function get_lat_lng_from_address($address_json)
{

	$results = array();
	try 
	{
		$address_arr = json_decode($address_json, true);
		$request_url = "http://maps.googleapis.com/maps/api/geocode/json?address=".str_replace(" ", "+", $address_arr['address'])."&sensor=false";
		$response = file_get_contents($request_url);
	    $response = json_decode($response, true);

	    if($response['status'] == 'OK')
	    {
	    	$source_location = $response['results'][0]['geometry']['location'];
	    	$results['lat'] = number_format((float)$source_location['lat'], 6, '.', '');
	    	$results['lng'] = number_format((float)$source_location['lng'], 6, '.', '');
	    	$results['status'] = 1;
	    }
	    else
	    {
	    	throw new Exception('Google Maps API returned status: '.$response['status']);	    	
	    }
	}
	catch (Exception $e)
	{
		$results['msg'] = 'Caught exception: '.  $e->getMessage(). "<br>";
		$results['status'] = 0;
	}
    return json_encode($results);
}

?>
