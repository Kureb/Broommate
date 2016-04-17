<?php

if($_SERVER['REQUEST_METHOD'] == 'POST') {

  $group_id     = $_POST['group_id'];
  $facebook_id  = $_POST['facebook_id'];

  $sql = "UPDATE user
          SET group_id = '$group_id'
          WHERE facebook_id = $facebook_id";

  require_once('dbConnect.php');

  if (mysqli_query($con, $sql)) {
    echo 'User added to group successfully';
  } else {
    echo 'Could not add user to group';
  }


  mysqli_close($con);

}

 ?>
