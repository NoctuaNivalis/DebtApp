<?php
$password=trim($_REQUEST['password']);
if($password == "XXXXXXXXX"){
	mysql_connect("localhost","user","XXXXXXXXX");
	mysql_select_db("debtdb");
	$sql=mysql_query($_REQUEST['stmt']);
	while($row=mysql_fetch_assoc($sql))
		$output[]=$row;
	print(json_encode($output));
	mysql_close();
}
?>
