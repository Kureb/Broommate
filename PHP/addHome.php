<?php

// Maybe we could add home when we create group
  if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $posX         =   $_POST['posX'];
    $posY         =   $_POST['posY'];

    $sql = "INSERT INTO home (posX, posY)
            VALUES ('$posX', '$posY')";

    require_once('dbConnect.php');

    if (mysqli_query($con, $sql)) {
      echo 'Home added successfully';
    } else {
      echo 'Could not add home';
    }

    mysqli_close($con);

  }
 ?>
