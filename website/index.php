<?php
	session_start();
	require('session_constants.php');
?>

<!DOCTYPE html> 
<html>
<head>
<title>Skirrs - Main Page</title></head>
<body>
	<script type='text/javascript'>
	</script>
	    
	    <div>
	    	<span stype="float:left"><h3>Welcome to Skirrs ... This is the home page</h3></span>
	    	<span stype="float:left"><a href="ride_offer_page.php">Offer a ride?</a></span>
	    	<?php
	    		if(isset($_SESSION['user_id']))
	    		{
	    			echo '<span style="float:right">Welcome '. ucwords($_SESSION['first_name']) .' | <a href="logout_page.php">Log Out</a></span>';
	    		}
	    		else
	    		{
	    			$_SESSION['page_after_login'] = 'index.php';
	    			echo '<span style="float:right"><a href="login_page.php">Sign In</a> | <a href="registration_page.php">Register</a></span>';
	    		}
	    	?>
	    </div>

	
</body>
</html>