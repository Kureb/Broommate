<?php

  if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $google_id = $_POST['google_id'];


    $sql = "UPDATE user
            SET `group_id` = ''
            WHERE `google_id` = '$google_id'";


    require_once('dbConnect.php');

    if (mysqli_query($con, $sql)) {
      echo 'User deleted from group successfully';
    } else {
      echo 'Could not delete user from group';
    }


    mysqli_close($con);

  }

 ?>
