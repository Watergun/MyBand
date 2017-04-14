<?php
$name = $_POST['name'];
$email = $_POST['email'];
$password = $_POST['pw'];
$instruments = '';

require '../dbaccess.php';
require '../auth_token.php';

$conn = new mysqli($server, $dbuser, $dbpw, $dbname);

if($conn->connect_error) {
	die('Connect Error (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error);
}

$sql = 'SELECT id FROM Users WHERE email=?';
$stmt = $conn->prepare($sql);

if(!$stmt->bind_param('s', $email)){
	$conn->close();
	die('1: Binding parameters failed!');
}
if(!$stmt->execute()){
	$conn->close();
	die('1: Executing sql query failed! ('.$stmt->errno.') '.$stmt->error);
}
$stmt->bind_result($id);
if($stmt->fetch()){
	$conn->close();
	die('Email already registered');
}

$stmt->close();

$sql = 'INSERT INTO Users(name, email, password, instruments) VALUES (?, ?, ?, ?)';
$stmt = $conn->prepare($sql);

if(!$stmt->bind_param('ssss', $name, $email, $password, $instruments)){
	$conn->close();
	die('2: Binding parameters failed!');
}
if(!$stmt->execute()){
	$conn->close();
	die('2: Executing sql query failed! (' . $stmt->errno . ') ' . $stmt->error);
}

$stmt = $conn->prepare('SELECT id FROM Users WHERE email = ?;');
$stmt->bind_param('s', $email);
$stmt->execute();
$stmt->bind_result($uid);
$stmt->fetch();
$stmt->close();

// Return success
echo 'Success\n';

// Generate a new login token for the user and put it in the list
$token = token_generate(50);
token_add_new($conn, $token, $uid);
echo 'Login token: ' . $token;

$conn->close();
?>
