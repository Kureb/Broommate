<?php

  require_once('dbConnect.php');

  $group_id = $_GET['id'];

  $sql = "SELECT * FROM user
          WHERE group_key = $group_id";

  $r = mysqli_query($con, $sql);

  $result = array();

  while ($row = mysqli_fetch_array($r)) {
    array_push($result, array(
      "id" => $row['id'],
      "facebook_id" => $row['facebook_id'],
      "group_id" => $row['group_id'],
      "name" => $row['name'],
      "posX" => $row['posX'],
      "posY" => $row['posY'],
      "last_update" => $row['last_update']
    ));
  }

  echo json_encode(array('result' => $result));

  mysqli_close($con);

 ?>
