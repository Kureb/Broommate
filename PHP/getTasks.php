<?php

  require_once('dbConnect.php');

//  $id_group = $_POST['id_group'];

  $sql = "SELECT *
          FROM tasks";
          //WHERE id_group = '$id_group'";

  $r = mysqli_query($con, $sql);

  $result = array();

  while ($row = mysqli_fetch_array($r)) {
    array_push($result, array(
      "id" => $row['id'],
      "name" => $row['name'],
      "owner" => $row['owner'],
      "worker" => $row['worker'],
      "priority" => $row['priority'],
      "date_start" => $row['date_start'],
      "date_end" => $row['date_end'],
      "state" => $row['state'] //TODO, DOING, DONE, DELETED
    ));
  }

  echo json_encode(array('result' => $result));

  mysqli_close($con);

?>
