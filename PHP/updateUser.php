<?php

if($_SERVER['REQUEST_METHOD'] = 'POST') {

  $facebook_id = $_POST['facebook_id'];
  $google_id = $_POST['google_id'];
  $name = $_POST['name'];

  $sql = "UPDATE user
          SET google_id = '$google_id'
          WHERE facebook_id = '$facebook_id'";



  require_once('dbConnect.php');

  if(mysqli_query($con, $sql)) {
    echo 'User updated successfully';
  } else {
    echo 'Could not update user';
  }

  mysqli_close($con);

}

 ?>
