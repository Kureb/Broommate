<?php

  require_once('dbConnect.php');

  $facebook_id = $_GET['id_facebook'];

  //echo $facebook_id;

  $sql = "SELECT * FROM user
          WHERE facebook_id = '$facebook_id'";

  $r = mysqli_query($con, $sql);

//printf("Error: %s\n", mysqli_error($con));


 $result = array();

 while ($row = mysqli_fetch_array($r)) {
 array_push($result,array(
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
