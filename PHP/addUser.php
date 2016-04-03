<?php

  if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $name         =   $_POST['name'];
    //$surname      =   $_POST['surname'];
    //$picture      =   $_POST['picture'];
    $facebook_id  =   $_POST['facebook_id'];

    $sql = "INSERT INTO user (name, facebook_id)
            VALUES ('$name', '$facebook_id')";

    require_once('dbConnect.php');

    if (mysqli_query($con, $sql)) {
      echo 'User added successfully';
    } else {
      echo 'Could not add user';
    }

    mysqli_close($con);

  }
 ?>
