<?php
/*
RECEIVE_USER_DATA.PHP SERVER SCRIPT DESCRIPTION
Users data and band data is kept separated to reduce network traffic.
Band updates would happen more likely than user information changes.

PARAMETERS:
	t	-	the authentication token
RETURN
	JSON encoded user object
	{
		id				: int
		name			: string
		email			: string
		picture_hash 	: string
	}

@MYBAND PROJECT
*/

$token = $_POST["t"];

require '../private/dbaccess.php';
require '../private/auth_token.php';

$conn = new mysqli($server, $dbuser, $dbpw, $dbname);

$uid = token_verify($conn, $token);
if($uid == false){
	die('Wrong token!');
}
elseif ($uid == null) {
	die('Token error!');
}

$res = $conn->query("SELECT id, name, email, picture_hash FROM Users WHERE id = " . $uid);
if($res == null){
	$conn->close();
	die('Server error induced by wrong token');
}
$user = $res->fetch_assoc();

if($user == null){
	die('User error!');
}

header("Content-type: application/json");
echo json_encode($user);
?>
