<?php
  if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    $id = $_POST['id'];
    $state = $_POST['state'];

    require_once('dbConnect.php');
    

    $sql = "UPDATE tasks
            SET state = '$state'
            WHERE id = $id";


    if (mysqli_query($con, $sql)) {
      echo 'Task updated successfully';
    } else {
      echo 'Could not update task';
    }

    mysqli_close($con);
  }

 ?>
