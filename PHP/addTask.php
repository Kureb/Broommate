<?php

  if($_SERVER['REQUEST_METHOD'] == 'POST') {

    // Getting values
    //if (isset($_POST['name']))
    $name = $_POST['name'];
    //$owner = $_POST['owner'];
    $priority = $_POST['priority'];
    //$id_group = $_POST['id_group'];
    $state = $_POST['state'];

    $sql = "INSERT INTO tasks (name, priority, state)
            VALUES ('$name', '$priority', '$state')"; //add time through android or php ?


    require_once('dbConnect.php');

    if (mysqli_query($con, $sql)) {
      echo 'Task added successfully';
    } else {
      echo 'Could not add task';
    }

    mysqli_close($con);
  }
 ?>
