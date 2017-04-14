<?php
/*
LOG_USER.PHP SERVER SCRIPT DESCRIPTION
If the user logs into an existing account, the application has to receive
a fresh valid token.
A different occasion is the expiration of an existing token.

PARAMETERS:
	email	- The user's email address
	pw		- The user's account password hash [SHA256]
RETURN: [All possible return strings]
	'Connect error'  or
	'Server script error' or
	'User database corrupt':
		All of the 3 message above indicate, that there is a little or big fault
		in this script (or the underlying server). Reperation is absolutely essential.

	'Unknown email' 	:
		No such email in the database -> New login
	'Incorrect password'	:
		Wrong password 	-> New login
 	'Success-[TOKEN]'	:
		Valid email pw combination, Appended token must be saved

@MYBAND PROJECT
*/

$email = $_POST["email"];
$pw	= $_POST["pw"];

//echo "EMAIL: " . $email;
//exit(1);

require '../private/dbaccess.php';
require '../private/auth_token.php';

$conn = new mysqli($server, $dbuser, $dbpw, $dbname);

if($conn->connect_error){
	die('Connect Error!');
}

$sql = 'SELECT id, name, password FROM Users WHERE email = ?';
$stmt = $conn->prepare($sql);

if(!$stmt->bind_param('s', $email)){
	$conn->close();
	die('Server script error');
}

if(!$stmt->execute()){
	$conn->close();
	die('Server script error');
}

$stmt->bind_result($uid, $email_db, $pw_db);

# Return the login status
if(!$stmt->fetch()){
	$conn->close();
	die('Unknown email');
}

$stmt->close();

if($uid == 0){
	$conn->close();
	die('User database corrupt');
}

# Password comparison
if(strcmp($pw_db, $pw) != 0){
	$conn->close();
	die('Incorrect password');
}
else{
	$token = token_generate(50);
	if(token_update($conn, $token, $uid)){
		echo 'Success-' . $token;
	}
	else {
		echo 'Server script error';
	}
}

$conn->close();
?>
