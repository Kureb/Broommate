<?php

if($_SERVER['REQUEST_METHOD'] = 'POST') {

  $posX = $_POST['posX'];
  $posY = $_POST['posY'];
  $group_key = $_POST['key'];

  $sql = "UPDATE home
          SET posX = '$posX',
              posY = '$posY'
          WHERE group_id = '$group_key'";



  require_once('dbConnect.php');

  if(mysqli_query($con, $sql)) {
    echo 'Home location updated successfully';
  } else {
    echo 'Could not update home location';
  }

  mysqli_close($con);

}

 ?>
