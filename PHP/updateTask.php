<?php
  if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    $id = $_POST['id'];
    $state = $_POST['state'];


    $sql = "UPDATE tasks
            SET state = '$state'";

    if ($state = "DONE"){
      $date_end = date ("Y-m-d H:i:s");
      $sql .= ", date_end = '$date_end'";
    }


    if (isset($_POST['worker']) && !empty($_POST['worker'])) {
      $worker = $_POST['worker'];
      $sql .= ", worker = '$worker'";
    }

    $sql .= "WHERE id = $id";



    require_once('dbConnect.php');




    if (mysqli_query($con, $sql)) {
      echo 'Task updated successfully';
    } else {
      echo 'Could not update task';
    }

    mysqli_close($con);
  }

 ?>
