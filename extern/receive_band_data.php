<?php
/*
RECEIVE_BAND_DATA.PHP SERVER SCRIPT DESCRIPTION
This script is responsible to the full synchronization of the user application.
By passing a valid login token, all band data belonging to the current user is being returned.

PARAMETERS:
	t	-	the authentication token
RETURN
	JSON encoded band array of the following structures
	{
		id				: int
		name			: string
		cash_sum		: float
		picture_hash 	: string	[64 digit hexadecimal string]
		members			: ARRAY
		updates			: ARRAY
		events			: ARRAY
		transactions	: ARRAY
		contacts		: ARRAY
	}

	The array structures are described in the code

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

$res = $conn->query("SELECT id, name, picture_hash, cash_sum ".
					"FROM Bands ".
					"INNER JOIN User_Band ON User_Band.band_id = Bands.id ".
					"WHERE User_Band.user_id = " . $uid);
if($res == null){
	$conn->close();
	die('Server script error');
}

// Define band array that we want to return
$bands = array();

while(($band = $res->fetch_assoc()) != null){
	$bands[] = $band;
}

// Fill every band in the band array with its sub-arrays
foreach($bands as &$band){
		// Create members array

		$members = array();
		$res = $conn->query("SELECT id, name, email, picture_hash FROM Users ".
					"INNER JOIN User_Band ON User_Band.user_id = Users.id ".
					"WHERE User_Band.band_id = " . $band['id']);
		// At least the user itself has to be included in the result
		if($res == null){
			$conn->close();
			die('Server script error');
		}
		while(($member = $res->fetch_assoc()) != null){
			$members[] = $member;
		}

		$band['members'] = $members;

		// Create update array
		$updates = array();
		$res = $conn->query("SELECT id, creator_id, title, comments, notification_counter ".
						"FROM Updates WHERE band_id = " . $band['id']);

		while(($update = $res->fetch_assoc()) != null){
			$updates[] = $update;
		}
		$band['updates'] = $updates;

		// Create event array
		$events = array();
		$res = $conn->query("SELECT id, creator_id, title, comments, location, ".
				"starttime, endtime, note, reminder, pay, notification_counter ".
				"FROM Events WHERE band_id = " . $band['id']);

		while(($event = $res->fetch_assoc()) != null){
			$events[] = $event;
		}
		$band['events'] = $events;

		// Create transaction array
		$transactions = array();
		$res = $conn->query("SELECT id, creator_id, event_id, title, comments, ".
				"value, description, notification_counter ".
				"FROM Transactions WHERE band_id = " . $band['id']);

		while(($transaction = $res->fetch_assoc()) != null){
			$transacions[] = $transaction;
		}
		$band['transactions'] = $transactions;

		// Create contact array
		$contacts = array();
		$res = $conn->query("SELECT id, name, email, phone_number, info ".
				"FROM Contacts WHERE band_id = " . $band['id']);

		while(($contact = $res->fetch_assoc()) != null){
			$contacts[] = $contact;
		}
		$band['contacts'] = $contacts;
}


$conn->close();

echo json_encode($bands);
?>
