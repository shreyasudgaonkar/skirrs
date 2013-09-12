<?php

include( $_SESSION['SKIRRS_HOME'] .  'lib/KLogger.php');
include( $_SESSION['SKIRRS_HOME'] . 'lib/mysql_dblib.php');

/* This file is responsible for searching for ride information in the db */
function search_ride( $json_arr )
{
	$response = array();
	$response['status'] = 0; # FAILURE
	
	$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);	
	$log->logInfo( "Inside search_ride, request: $json_arr" );
	
	$ride_info = json_decode( $json_arr, true );
	$ride_info_str = serialize($ride_info);

	$log->logInfo( "ride_info: $ride_info_str" );


	// collect all input params for searching
	$srcLat 		= $ride_info['srcLat'];
	$srcLng 		= $ride_info['srcLng'];
	$dstLat 		= $ride_info['dstLat'];
	$dstLng 		= $ride_info['dstLng'];

	if (!isset($srcLat) || !isset($srcLng) || !isset($dstLat) || !isset($dstLng)) {
		$log->logError("Source and / or is not set correctly.");
		return json_encode($response);
	}

	$date 			= $ride_info['date'];
	$time 			= $ride_info['time'];

	$datetime = null;
	if (isset($date))
		$datetime 	= $date;
	if (isset($time))
		$datetime .= " " . $time; // something like 2012-04-04 12:14:43:453 

	$seatsOffered 	= $ride_info['seats_offered'];
	$price			= $ride_info['price'];
	$pickUpOffered	= $ride_info['pick_up_offered'];

	// get data out of the db based on the params specified except for lat long params
	$sqlStr = "Select * from `rides_offered` where ";
	if (isset($datetime))
		$sqlStr .= " `departure_date_time` > '".$date."'";
	if (isset($seatsOffered))
		$sqlStr .= " AND `seats_offered` >= $seatsOffered";
	if (isset($price))
		$sqlStr .= " AND `price` <= $price";
	if (isset($pickUpOffered))
		$sqlStr .= " AND `pick_up_offered` = $pickUpOffered";

	$res = fetch($sqlStr,'multiple_rows');
	if($res == false) {
		$log->logError( "No ride details found with params: $ride_info_str");
		return json_encode($response);
	}

	$finalRows = array();
	// iterate over results and keep relevant results around
	foreach($res as $row) {
		// if the source and destination are within 3 miles from the entries in the db show them in the ui
		if ((getDistance($row['srcLat'], $get['srcLng'], $srcLat, $srcLng, "K") < $_SESSION['DISTANCE_THRESHOLD'])
			&& (getDistance($row['dstLat'], $get['dstLng'], $dstLat, $dstLng, "K") < $_SESSION['DISTANCE_THRESHOLD'])) {
			array_push($finalRows, $row);
		}
	}

	// fill response
	if (count($finalRows) > 0) {
		$response['results'] = $finalRows;
		$response['status'] = 1; # Success
 	}

	// return the response
	return json_encode( $response );	
}


/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::                                                                         :*/
/*::  This routine calculates the distance between two points (given the     :*/
/*::  latitude/longitude of those points). It is being used to calculate     :*/
/*::  the distance between two locations using GeoDataSource(TM) Products    :*/
/*::                     													 :*/
/*::  Definitions:                                                           :*/
/*::    South latitudes are negative, east longitudes are positive           :*/
/*::                                                                         :*/
/*::  Passed to function:                                                    :*/
/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
/*::    unit = the unit you desire for results                               :*/
/*::           where: 'M' is statute miles                                   :*/
/*::                  'K' is kilometers (default)                            :*/
/*::                  'N' is nautical miles                                  :*/
/*::  Worldwide cities and other features databases with latitude longitude  :*/
/*::  are available at http://www.geodatasource.com                          :*/
/*::                                                                         :*/
/*::  For enquiries, please contact sales@geodatasource.com                  :*/
/*::                                                                         :*/
/*::  Official Web site: http://www.geodatasource.com                        :*/
/*::                                                                         :*/
/*::         GeoDataSource.com (C) All Rights Reserved 2013		   		     :*/
/*::                                                                         :*/
/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
function getDistance($lat1, $lon1, $lat2, $lon2, $unit) {

  $theta = $lon1 - $lon2;
  $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
  $dist = acos($dist);
  $dist = rad2deg($dist);
  $miles = $dist * 60 * 1.1515;
  $unit = strtoupper($unit);

  if ($unit == "K") {
    return ($miles * 1.609344);
  } else if ($unit == "N") {
      return ($miles * 0.8684);
    } else {
        return $miles;
      }
}

?>
