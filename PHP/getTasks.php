<?php

  require_once('dbConnect.php');

//  $id_group = $_POST['id_group'];

  $sql = "SELECT T.id, T.name, T.owner, T.worker, T.priority, T.date_start, T.date_end, T.state, T.deleted, U.name as owner_name
  FROM tasks T, user U
  WHERE T.owner = U.facebook_id";

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
      "state" => $row['state'],
      "deleted" => $row['deleted'],
      "owner_name" => $row['owner_name']
    ));
  }

  echo json_encode(array('result' => $result));

  mysqli_close($con);

?>
