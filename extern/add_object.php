<?php
$auth_name	= $_POST['email'];
$auth_pw	= $_POST['pw'];
$obj		= $_POST['obj'];
$group		= $_POST['groupId'];

require '../dbaccess.php';

$conn = new mysqli($server, $dbuser, $dbpw, $dbname);
if($conn->connect_error){
	die('Connect error');
}

$sql = 'SELECT id, password FROM Users WHERE email = ?';
$stmt = $conn->prepare($sql);
$stmt->bind_param('s', $auth_name);
$stmt->execute();
$stmt->bind_result($uid, $upw);
if(!$stmt->fetch() || $auth_pw != $upw){
	$conn->close();
	die('Wrong credentials');
}
$stmt->close();

switch($obj){
	case 'band':
		if(!array_key_exists('bName', $_POST)){
			$conn->close();
			die('Not enough arguments passed for target object');
		}
		$band_name = $_POST['bName'];
		if($band_name == ''){
			$conn->close();
			die('Parameter must be non-empty');
		}
		$sql = 'INSERT INTO Bands(name, cash_sum) VALUES (?, 0.0);';
		$stmt = $conn->prepare($sql);
		$stmt->bind_param(band_name);
		$stmt->execute();

		break;
	case 'update':
		break;
	case 'event':
		break;
	case 'transaction':
		break;
	case 'song':
		break;
	default:
		echo 'Unknown object requested!';
}


?>
