<?php

  if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $name       =   $_POST['name'];
    $priority   =   $_POST['priority'];
    $state      =   "TODO";
    $owner      =   $_POST['owner'];
    $date_start =   date ("Y-m-d H:i:s");

    $sql = "INSERT INTO tasks (name, priority, state, owner, date_start)
            VALUES ('$name', '$priority', '$state', '$owner', '$date_start')";


    require_once('dbConnect.php');

    if (mysqli_query($con, $sql)) {
      echo 'Task added successfully';
    } else {
      echo 'Could not add task';
    }

    mysqli_close($con);
  }
 ?>
