<?php
session_start();
require('session_constants.php');
require_once($_SESSION['SKIRRS_HOME'] . 'lib/KLogger.php');
$log = new KLogger($_SESSION['LOG_DIR'], KLogger::INFO);

$library_name = $_SESSION['SKIRRS_HOME'] . 'lib/verify_lib.php';
require($library_name);
$func_to_call = 'verify_login_credentials';

$params = array();
$params['email_address'] = 'miteshmehta@gmail.com';
$params['password'] = 'abcd';
$params_json = json_encode($params);
$response_json = call_user_func($func_to_call, $params_json);
$log->logInfo("Response: $response_json");
echo $response_json;

?>