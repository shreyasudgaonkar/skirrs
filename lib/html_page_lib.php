<?php

require_once( $_SESSION['SKIRRS_HOME'] .  'lib/KLogger.php');

/* This file is responsible for page related functions like reload, redirect, refresh, etc. purely for WEBSITE related pages*/
function page_reload_without_post_data($html_page_url, $display_msg)
{
	$_SESSION['reload'] = true;
    $_SESSION['reload_msg'] = $display_msg;
    $page = $html_page_url;
	header('Location: '.$page, true, 303);
	exit;
}