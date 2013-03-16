<?php

error_reporting(E_ALL);

$password=trim($_POST[password]);
$debtor=trim($_POST[debtor]);
$creditor=trim($_POST[creditor]);
$amount=trim($_POST[amount]);
$description=trim($_POST[description]);

if (sha1($password) == "XXXXXXXXXXXXXXX") {

	$host = "localhost";
	$username = "user";
	$error1 = "error1";
	$error1 = "error2";

	$db = mysql_connect($host, $username, $password) or die ($error1);

	$dbnaam = "debtdb";

	if(!(@mysql_select_db($dbnaam, $db) or die ($error1))) {
		print $error1;
		exit;
	}

	$stmt="INSERT INTO Debts(amount, description, creditorid, debtorid) VALUES($amount, '$description', (SELECT id FROM User WHERE name='$creditor'), (SELECT id FROM User WHERE name='$debtor'))";

	mysql_query($stmt);

	mysql_close();
}
?>
