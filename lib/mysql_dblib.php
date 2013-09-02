<?php

define('ROOT', '/Applications/MAMP/htdocs/skirrs/lib/');

/*
 * This function will be used to establish a connection with database
 */
function connect() 
{
	require_once ROOT . '/db_config.php';
	require_once ROOT . '/KLogger.php';
	$log = new KLogger('/Users/Shreyas/Desktop', KLogger::INFO);
	
	$con=mysqli_connect( DB_SERVER , DB_USER, DB_PASSWORD, DB_DATABASE );
	
	// Check connection
	if (mysqli_connect_errno($con))
	{
		 $log->logError( "Failed to connect to MYSQL :" . mysqli_connect_error() );
	      echo "Failed to connect to MySQL: " . mysqli_connect_error(); 
	}	
	return $con;
}


/*
 * This function will be typically used to insert/update/alter/delete queries 
 */
function execute($query)
{
	$con=connect();
	if (!$con) {
		return -1;
	}
	if (!mysqli_query($con, $query))
	{
		$log->logError( "Failed to execute query :" .mysqli_error($con) );
		echo 'Error: '.mysqli_error($con);
		return -1;
	}
	close($con);
	return 1;

}


/* This finction will be used for selection rows, i.e., fetching data from the tables
 * Takes the sql query and format, wrt which results will be sent
 * Single Data - Select user_id from users where email_address='xyz@xyz.com'
 * Single row - Select user_id, first_name, last_name from users where email_address='xyz@xyz.com'
 * Multiple Rows - Select * from users
 */
function fetch($query, $format)
{
	$con=connect();		
	if (!$con) {
		return -1;
	}
	$result = mysqli_query($con, $query);	
	if($format=='single_data')
	{
		$row=mysqli_fetch_row($result);	
		$return_val = $row[0];		
	}
	elseif($format=='single_row'){ 
		$row=mysqli_fetch_array($result);
		$return_val = $row;
	}
	elseif($format=='multiple_rows')
	{
		$rows = Array();
		while($row = mysqli_fetch_assoc($result))
			array_push($rows,$row);
		$return_val = $rows;
	}
	else
	{
		echo 'Invalid format';
		$return_val = false;	
	}
	close($con);
	return $return_val;
}


/*
 * This function will be used to close the connection with database
 */
function close($con)
{
	mysqli_close($con);
}


?>

		
