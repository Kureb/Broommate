<?php

  require_once('dbConnect.php');

  $key_group = $_GET['id_group'];

  $sql = "SELECT T.id, T.name, T.owner, T.worker, T.priority, T.date_start, T.date_end, T.state, U.name as owner_name, W.name as worker_name, U.google_id as google_id
          FROM tasks T left join user U on U.facebook_id = T.owner left join user W on W.facebook_id = T.worker
          WHERE T.state <> 'DELETED'
          AND T.group_key = '$key_group'";

  $r = mysqli_query($con, $sql);

  //printf("Error: %s\n", mysqli_error($con));

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
      "worker_name" => $row['worker_name'],
      "google_id" => $row['google_id']
    ));
  }

  echo json_encode(array('result' => $result));

  mysqli_close($con);

?>
