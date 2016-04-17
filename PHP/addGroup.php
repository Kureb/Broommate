<?php

if($_SERVER['REQUEST_METHOD'] == 'POST') {

  $key  = $_POST['key'];
  $name = $_POST['name'];




  //$sql = "INSERT INTO group (key, name)
  //        VALUES ('$key', '$name')";

  $sql = "INSERT INTO `group` ( `key`, `name`)
          VALUES ('$key', '$name')";


  //echo $sql;

  require_once('dbConnect.php');

  if (mysqli_query($con, $sql)) {
    echo 'Group added successfully';
  } else {
    echo 'Could not add group';
  }

  mysqli_close($con);

}

 ?>
