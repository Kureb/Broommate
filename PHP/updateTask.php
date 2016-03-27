<?php
  if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id = $_POST['id'];
    $name = $_POST['name'];
    $owner = $_POST['owner'];
    $worker = $_POST['worker'];
    $priority = $_POST['priority'];
    $date_start = $_POST['date_start']:
    $date_end = $_POST['date_end'];
    $state = $_POST['state'];

    require_once('dbConnect.php');

    $sql = "UPDATE tasks
            SET name = '$name',
                owner = '$owner',
                worker = '$worker',
                priority = '$priority',
                date_start = '$date_start',
                date_end = '$date_end',
                state = '$state'
            WHERE id = $id";


    if (mysqli_query($con, $sql)) {
      echo 'Task updated successfully';
    } else {
      echo 'Could not update task';
    }

    mysqli_close($con);
  }

 ?>
