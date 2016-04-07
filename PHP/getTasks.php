<?php

  require_once('dbConnect.php');

//  $id_group = $_POST['id_group'];

  $sql = "SELECT T.id, T.name, T.owner, T.worker, T.priority, T.date_start, T.date_end, T.state, U.name as owner_name, W.name as worker_name
          FROM tasks T left join user U on U.facebook_id = T.owner left join user W on W.facebook_id = T.worker
          WHERE T.state <> 'DELETED'";

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
      "owner_name" => $row['owner_name'],
      "worker_name" => $row['worker_name']
    ));
  }

  echo json_encode(array('result' => $result));

  mysqli_close($con);

?>
