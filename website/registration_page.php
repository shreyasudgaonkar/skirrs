<!DOCTYPE html> 
<html>
<head>
<title>Skirrs - Registration Page</title></head>
<body>

<?php
session_start();
require('../lib/user_lib.php');
?>
	<script type='text/javascript'>
	</script>

	<?php
	$registration_success = 0;
	if(isset($_POST['registration_request']) && $_POST['registration_request'] == 0 ) { 
	
		// Convert the $_POST array to json and register the user
		$json_arr = json_encode($_POST);		
		$result = register_user($json_arr);
		
		if ($result == 1) 
		{
			$registration_success = 1;
		?>
		<p> Registration completed</p>	
		<?php	
		}
		else
		{?>
		<p> Registration failed. Please try again...</p>
		<?php }} ?>

	
	<?php 
	if(! isset($_POST['registration_request']) || $registration_success == 0 ) { 
	?>
	
		<form action="<?php $_PHP_SELF ?>" method="POST">
		<table border="0">
			<tr>
				<td>First Name: </td>
				<td><input type='text' name='first_name'></td>
			</tr>
			<tr>
				<td>Last Name: </td>
				<td><input type='text' name='last_name'></td>
			</tr>
			<tr>
				<td>Email Address: </td>
				<td><input type='text' name='email_address'></td>
			</tr>
			<tr>
				<td>Password: </td>
				<td><input type='password' name='password'></td>
			</tr>
			<tr>
				<td>Re-type Password: </td>
				<td><input type='password' name='re_password'></td>
			</tr>									
			<tr>
				<td>Date of Birth: </td>
				<td><input type='date' name='dob'></td>
			</tr>
			<tr>
				<td>Sex:</td>
				<td>
					<input type='radio' name='sex' value='M'>Male
					<input type='radio' name='sex' value='F'>Female
				</td>
			</tr>								
			<tr>
				<td>Street Address: </td>
				<td><input type='text' name='street_address'></td>
			</tr>
			<tr>
				<td>City: </td>
				<td><input type='text' name='city'></td>
			</tr>
			<tr>
				<td>State: </td>
				<td><input type='text' name='state'></td>
			</tr>
			<tr>
				<td>Zip: </td>
				<td><input type='text' name='zip'></td>
			</tr>
			<tr>
				<td>Country: </td>
				<td><input type='text' name='country'></td>
			</tr>
			<tr>
				<td>Phone: </td>
				<td><input type='tel' name='phone_number'></td>
			</tr>	
			<tr>
				<td>Can you drive? </td>
				<td>
					<input type='radio' name='profile_name' value='driver'>Yes
					<input type='radio' name='profile_name' value='passenger'>No
				</td>
			</tr>
			<tr>
				<td> &nbsp; </td>
				<td><input type='submit' /></td>
			</tr>												
		</table>
		<input type='hidden' name='registration_request' value='0'>
		</form>
	<?php } ?>

	
</body>
</html>