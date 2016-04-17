<?php

  if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $name       =   $_POST['name'];
    $priority   =   $_POST['priority'];
    $state      =   "TODO";
    $owner      =   $_POST['owner'];
    $date_start =   date ("Y-m-d H:i:s");
    $key        =   $_POST['key'];

    $sql = "INSERT INTO tasks (name, priority, state, owner, date_start, group_key)
            VALUES ('$name', '$priority', '$state', '$owner', '$date_start', '$key')";


    require_once('dbConnect.php');

    if (mysqli_query($con, $sql)) {
      echo 'Task added successfully';
    } else {
      echo 'Could not add task';
    }

    mysqli_close($con);
  }
 ?>
