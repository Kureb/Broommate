<?php

  if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $posX = $_POST['posX'];
    $posY = $_POST['posY'];
    $facebook_id = $_POST['facebook_id'];
    date_default_timezone_set('Europe/Riga');
    $last_update = date ("Y-m-d H:i:s");

    $sql = "UPDATE user
            SET posX = '$posX',
                posY = '$posY',
                last_update = '$last_update'
            WHERE facebook_id = $facebook_id";


    require_once('dbConnect.php');

    if (mysqli_query($con, $sql)) {
      echo 'Location updated successfully';
    } else {
      echo 'Could not update location';
    }


    mysqli_close($con);

  }

 ?>
