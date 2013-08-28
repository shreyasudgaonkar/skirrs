
<?php
session_start();
print('I am her');
if(isset($_POST['email_address'])) {
  print($_POST['email_address']);
}
print($_SESSION['views']);
?>