<?php
session_start();
require('session_constants.php');
require($_SESSION['SKIRRS_HOME'] . 'lib/location_lib.php');
require($_SESSION['SKIRRS_HOME'] . 'lib/rides_offered_lib.php');
require($_SESSION['SKIRRS_HOME'] . 'lib/html_page_lib.php');

if(!empty($_POST))
{

}

else {

?>
<DOCTYPE html> 
<html>
<head>
	<title>Skirrs - Search for a Ride Page</title>
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
	<form name="search_ride_form" action="<?php $_PHP_SELF?>" method="POST">
		<table border="0">
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
				<td> &nbsp; </td>
				<td><input type='submit' /></td>
			</tr>																																			
		</table>
	</form>
</body>
</html>
<?php } ?>


