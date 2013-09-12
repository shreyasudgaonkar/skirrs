<?php
	session_start();
	require('session_constants.php');
?>

<!DOCTYPE html> 
<html>
<head>
<title>Skirrs - Logout Page</title></head>
<body>

<?php
	session_destroy();
	echo '<p>Thank you for visiting us. <br>You have successfully logged out.<br><br></p>';
	echo '<p><a href="login_page.php">Click Here</a> to login again</p>';
?>
</body>
</html>	