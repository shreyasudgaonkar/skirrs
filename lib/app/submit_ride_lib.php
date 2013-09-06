<?php

define('ROOT', '/Applications/MAMP/htdocs/skirrs/lib/');

require_once( ROOT .  'KLogger.php');
require_once( ROOT . "rides_offered_lib.php");

$log = new KLogger('/Users/Shreyas/Desktop', KLogger::INFO);

$log->logInfo( "Inside submit_ride_lib.php" );

$request = json_encode( $_POST );

$response = submit_ride( $request );

$log->logInfo( "Response: ", $response );

echo $response;

?>