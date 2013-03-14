<?php
$password=trim($_POST[password]);
$debtor=trim($_POST[debtor]);
$creditor=trim($_POST[creditor]);
$amount=trim($_POST[amount]);
$description=trim($_POST[description]);
if($password == "XXXXXXXXX"){
	mysql_connect("localhost","user","XXXXXXXXXXX");
	mysql_select_db("debtdb");
	$sql=mysql_query('INSERT INTO Debts(amount, description, creditorid, debtorid) VALUES($amount , $description , (SELECT id FROM User WHERE name=$creditor) , (SELECT id FROM User WHERE=$debtor))');
	while($row=mysql_fetch_assoc($sql))
	$output[]=$row;
	print(json_encode($output));
	mysql_close();
}
?>
