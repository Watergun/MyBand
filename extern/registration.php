<?php
/*
REGISTRATION.PHP SERVER SCRIPT DESCRIPTION
By calling this script, a new user is registered in the database. A new login token
is therefore generated and returned to the app.

PARAMETERS:
	name	-	the user name (f.e.: "Bob")
	email	-	the user email. This is an important key that must be saved, because
				the email represents the login name.
	pw		-	the user password (only saved as hash)

RETURN:
	'Server script error':
		Something went wrong in the script
	'Email alredy registered':
		That email is already registered in the database
	'Success\n[TOKEN]':
		A successful registration was made. The generated login token is Appended

*/
$name = $_POST['name'];
$email = $_POST['email'];
$password = $_POST['pw'];
$instruments = '';

require '../private/dbaccess.php';
require '../private/auth_token.php';

$conn = new mysqli($server, $dbuser, $dbpw, $dbname);

if($conn->connect_error) {
	die('Database connection error');
}

$sql = 'SELECT id FROM Users WHERE email=?';
$stmt = $conn->prepare($sql);

if(!$stmt->bind_param('s', $email)){
	$conn->close();
	die('Server script error');
}
if(!$stmt->execute()){
	$conn->close();
	die('Server script error');
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
	die('Server script error');
}
if(!$stmt->execute()){
	$conn->close();
	die('Server script error');
}

// Since auto_increment is turned on, we have to find out the newly assigned ID
$stmt = $conn->prepare('SELECT id FROM Users WHERE email = ?;');
$stmt->bind_param('s', $email);
$stmt->execute();
$stmt->bind_result($uid);
$stmt->fetch();
$stmt->close();

// Return success and
// Generate a new login token for the user (and store it in the database)
$token = token_generate(50);
token_add_new($conn, $token, $uid);
echo 'Success-' . $token;

$conn->close();
?>
