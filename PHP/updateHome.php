<?php

if($_SERVER['REQUEST_METHOD'] = 'POST') {

  $posX = $_POST['posX'];
  $posY = $_POST['posY'];
  $id = 1;

  $sql = "UPDATE home
          SET posX = '$posX',
              posY = '$posY'
          WHERE id = $id";



  require_once('dbConnect.php');

  if(mysqli_query($con, $sql)) {
    echo 'Home location updated successfully';
  } else {
    echo 'Could not update home location';
  }

  mysqli_close($con);

}

 ?>
