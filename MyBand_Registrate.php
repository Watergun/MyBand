<?php
$server = "localhost";
$name = $_POST["name"];
$email = $_POST["email"];
$password = $_POST["password"];

$conn = new mysqli("localhost", "u712340127_root", "Aa123456789", "u712340127_mb");

if($conn->connect_error) {
	exit("Verbindungsfehler:".$conn->connect_error());
}

$sql = "INSERT INTO Users (name, email, password) VALUES (?, ?, ?)";
$statement = $conn->prepare($sql);
$statement->bind_param('ssi', $name, $email, $password);
$statement->execute();

$res = $statement->get_result();

if($row = $res->fetch_object()) {
	echo 0;
}

$conn->close();
?>