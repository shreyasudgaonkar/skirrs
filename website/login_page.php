<!DOCTYPE html> 
<html>
<head>
<title>Skirrs - Login Page</title></head>
<body>

<?php
	session_start();
    require('session_constants.php');
	
	require($_SESSION['SKIRRS_HOME'] . 'lib/verify_lib.php');
	$login_success = 0;
	if(isset($_POST['email_address']) && isset($_POST['password']))
	{
		// Convert the $_POST array to json and register the user
		$json_arr = json_encode($_POST);
		$result = verify_login_credentials($json_arr);
		
		$response = json_decode ( $result, true );
	
		if( $response[ 'status' ] == 1 )
		{
			echo 'Login Successful';
			$login_success = 1;
			print_r($_SESSION);
			# redirect to home page
		}
		else
		{
			echo 'Incorrect email_address or password. Please try again... <br>'; 
		}
	}
	
	if($login_success == 0) 
	{
	
?>
	<script type='text/javascript'>
			
		function verify_submit(formname) {
		        var email_address = document.getElementById('email_address').value;
			var password = document.getElementById('password').value;
			if (!email_address.length) {
				alert('Enter email_address');
				return false;
			} else if(!password.length) {
			        alert('Enter password');
				return false;
			} else {	
				document.submit();
				return true;			
			}
		}
	</script>
	
	<form name='login_form' action="<?php $_PHP_SELF ?>" method='POST'> 
	        <table>
			<tr>
				<td>Email Address: </td>
				<td><input id='email_address' type='text' name='email_address' maxlength='254'></td>
			</tr>
			<tr>
				<td>Password: </td>
				<td><input id='password' type='password' name='password' maxlength='72'><br></td>
			</tr>
			<tr><td><input type='submit' onclick="return verify_submit('login_form');"></td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td><a href='forgot_password_form.php'>Forgot Password?</a></td></tr>
			<tr><td><a href='registration_page.php'>Not a member? Register Here</a></td></tr>
			
		</table>
	</form>
	
	<?php
	}
	?>

</body>
</html>
