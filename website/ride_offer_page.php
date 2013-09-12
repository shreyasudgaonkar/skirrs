<?php
session_start();
require('session_constants.php');
require($_SESSION['SKIRRS_HOME'] . 'lib/location_lib.php');
require($_SESSION['SKIRRS_HOME'] . 'lib/rides_offered_lib.php');
require($_SESSION['SKIRRS_HOME'] . 'lib/html_page_lib.php');

if(!empty($_POST))
{

	# Get source lattitude and longitude
	echo '<br><br>';
	$_POST['departure_date_time'] = $_POST['date'] . ' ' . $_POST['time'];
	$src_arr = array("address" => $_POST['source'],);
	$src_request_json = json_encode($src_arr);
	$src_response = json_decode(get_lat_lng_from_address($src_request_json), true);

	if($src_response['status'] == 1)
	{
		$_POST['srclat'] = $src_response['lat'];
		$_POST['srclng'] = $src_response['lng'];
		#print_r($_POST);
	}

	# Get destination lattitude and longitude
	$dest_arr = array("address" => $_POST['destination'],);
	$dest_request_json = json_encode($dest_arr);
	$dest_response = json_decode(get_lat_lng_from_address($dest_request_json), true);

	if($dest_response['status'] == 1)
	{
		$_POST['destlat'] = $dest_response['lat'];
		$_POST['destlng'] = $dest_response['lng'];
		#print_r($_POST);
	}

    # Reload the same form if source/destination not found.
    $src_not_found = $dest_not_found = $page_reload = false;

	if( !isset($_POST['srclat']) || !isset($_POST['srclng']) )  
	{
		$display_msg = 'Could not determine accurate source address';
        $src_not_found = true;
        $page_reload = true;
	}	
	if ( !isset($_POST['destlat']) || !isset($_POST['destlng']) )   
	{
		$display_msg = 'Could not determine accurate destination address';
        $dest_not_found = true;
        $page_reload = true;
	}

	if($dest_not_found === true && $src_not_found === true)
	{
		$display_msg = 'Could not determine accurate source and destination address .. Please try again';

	}

	if($page_reload === true)
	{
		$reload_page_url = $_SERVER['HTTP_REFERER'];
		page_reload_without_post_data($reload_page_url, $display_msg);
	}

    # If source and destination found successfully,
    # Convert the $_POST array to json and pass it to submit_ride() in rides_offered_lib.php
    $submit_resp_json = submit_ride( json_encode($_POST) );
    $submit_resp = json_decode($submit_resp_json, true);
    if($submit_resp['status'] === 1)
    {
    	echo 'Ride submitted successfully.';
        # TODO: redirect to home page or provide a link to home page
    }
    else {  
    		# If submit ride fails (for some reason), reload the page
    		$reload_page_url = $_SERVER['HTTP_REFERER'];
    		$display_msg = 'Ride not submitted successfully .. please try again ... ';
    	    page_reload_without_post_data($reload_page_url, $display_msg);
    	 }
}


else {

?>
<DOCTYPE html> 
<html>
<head>
	<title>Skirrs - Offer a Ride Page</title>
    <!-- TODO: put actual map
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <style>
      html, body, #map-canvas {
        margin: 0;
        padding: 0;
        height: 50%;
        width: 75%;
      }
    </style>-->
</head>


<body>
    <?php
    	if(isset ($_SESSION['reload']) && ($_SESSION['reload'] === true)  && (isset($_SESSION['reload_msg']))) 
       	{
    		echo '<b>'. $_SESSION['reload_msg'] . '</b><br>'; 
    		$_SESSION['reload'] = false;
    		$_SESSION['reload_msg'] = '';
    	} 
    ?>
    		 

    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&libraries=places"></script>
    <script>
	var map;
	function initialize() {

		var options = {
  		componentRestrictions: {country: "in"} //Country set to india 
 		};
  		/* TODO: show map on a lightbox onclick of a button
  		var mapOptions = {
    		zoom: 8,
    		center: new google.maps.LatLng(-34.397, 150.644),
    		mapTypeId: google.maps.MapTypeId.ROADMAP
  		};
  		map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);*/
  		var input_src = (document.getElementById('source'));
  		var autocomplete_src = new google.maps.places.Autocomplete(input_src, options);

  		var input_dest = (document.getElementById('destination'));
  		var autocomplete_dest = new google.maps.places.Autocomplete(input_dest, options);
	}

	google.maps.event.addDomListener(window, 'load', initialize);
    </script>

	<form name="submit_ride_form" action="<?php $_PHP_SELF?>" method="POST">
		<table border="0">
			<tr>
				<td>User ID: </td>
				<td><input type='text' name='user_id'></td>
			</tr>
			<tr>
				<td>From: </td>
				<td><input type='text' id='source' name='source' size="50" maxlength="128"></td>
			</tr>
			<tr>
				<td>To: </td>
				<td><input type='text' id='destination' name='destination' size="50" maxlength="128"></td>
			</tr>
			<tr>
				<td>Date: </td>
				<td><input type='date' name='date'></td>
			</tr>
			<tr>
				<td>Time: </td>
				<td><input type='time' name='time' step='1'></td>
			</tr>
			<tr>
				<td>Seats offered: </td>
				<td>
					<select name='seats_offered'>
						<option value='1' selected="selected">1</option>
						<option value='2'>2</option>
						<option value='3'>3</option>
						<option value='4'>4</option>
						<option value='5'>5</option>
						<option value='6'>6</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>Price: </td>
				<td><input type='text' name='price'></td>
			</tr>
			<tr>
				<td>Pick up offered? </td>
				<td>
					<input type='radio' name='pick_up_offered' value='1' checked>Yes
					<input type='radio' name='pick_up_offered' value='0'>No
				</td>
			</tr>
			<tr>
				<td>Share Contact Info? </td>
				<td>
					<input type='radio' name='share_contact_info' value='1' checked>Yes
					<input type='radio' name='share_contact_info' value='0'>No
				</td>
			</tr>
			<tr>
				<td>Comments: </td>
				<!--<td><input type='text' name='comments'></td>-->
				<td><textarea name="comments" cols="40" rows="4"></textarea></td>
			</tr>
			<tr>
				<td> &nbsp; </td>
				<td><input type='submit' /></td>
			</tr>																																			
		</table>
	</form>


	<!--<input type="text" id="searchTextField" size="50">
	<div id="map-canvas"></div>-->

</body>
</html>
<?php } ?>


