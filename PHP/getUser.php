<?php

  require_once('dbConnect.php');

  $group_id = $_GET['id_group'];

  //echo $facebook_id;

  $sql = "SELECT * FROM user
          WHERE group_id = '$group_id'";


    $sql ="SELECT u.id as id,
                  u.facebook_id as facebook_id,
                  u.name as name,
                  u.group_id as group_id,
                  u.posX as posX,
                  u.posY as posY,
                  u.last_update as last_update,
                  u.google_id as google_id,
        		      h.posX as posX_home,
                  h.posY as posY_home,
                  g.name as group_name
        from user u
        join home h
        join `group` g
        where u.group_id = '$group_id'
          and h.group_id = '$group_id'
          and g.key = '$group_id'";


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
   "last_update" => $row['last_update'],
   "google_id" => $row['google_id'],
   "posX_home" => $row['posX_home'],
   "posY_home" => $row['posY_home'],
   "group_name" => $row['group_name']
 ));
 }

  echo json_encode(array('result' => $result));

  mysqli_close($con);

 ?>
