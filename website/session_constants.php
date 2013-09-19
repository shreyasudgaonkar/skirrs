<?php

if (! isset($_SESSION['SKIRRS_HOME'])) {
	$current_os = strtoupper(substr(php_uname('s'), 0, 6));
	if ($current_os === 'WINDOW') {
		$_SESSION['SKIRRS_HOME'] = 'C:/wamp/www/skirrs/';
	}
	elseif($current_os === 'DARWIN') {
		$_SESSION['SKIRRS_HOME'] = '/Applications/MAMP/htdocs/skirrs/';
	}
	else {
		$_SESSION['SKIRRS_HOME'] = '/var/www/html/skirrs/';
	}
}

if (! isset($_SESSION['LOG_DIR'])) {
	$_SESSION['LOG_DIR'] = $_SESSION['SKIRRS_HOME'] . 'logs/';
}

if (! isset($_SESSION['DISTANCE_THRESHOLD'])) {
	$_SESSION['DISTANCE_THRESHOLD'] = 20;
}


?>
