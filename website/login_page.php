<!DOCTYPE html> 
<html>
<head>
<title>Skirrs - Login Page</title></head>
<body>
<?php
session_start();
$capital = 67;
print("Variable capital is $capital<br>");
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
				alert(email_address);
				alert(password);
				document.submit();
				return true;			
			}
		}
	</script>
	
	<form name='login_form' action='../lib/login_lib.php' method='POST'> 
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
	
</body>
</html>