<?php
/*
LOG_USER.PHP SERVER SCRIPT DESCRIPTION
If the user application recognozes a locally saved token, it may
verify the token's validity.
Therefore it must send it to this script, which in return answers with a
response message.

PARAMETERS:
   t - The token (A 50 digit long hexadecimal key)
RETURN:
    'Connect error':
        Invalid database logon credentials. Immediately repair.
    'Valid token':
        This token is valid and can be used to authenticate database accesses.
    'Invalid token':
        This token is unknown to the database.
    'Expired token':  [NOT IMPLEMENTED YET]
        This token is known by the database, but is marked as expired. The app
        then has to renew it by calling the LOG_USER.PHP script.

@MYBAND PROJECT
*/

$token = $_POST["t"];

require '../private/dbaccess.php';
require '../private/auth_token.php';

$conn = new mysqli($server, $dbuser, $dbpw, $dbname);
if($conn->connect_error){
    die('Connect error');
}

$uid = token_verify($conn, $token);

if($uid == null){
    echo "Invalid token";
}
//elseif ($uid == null) {
//    echo "Token error";
//}
else {
    echo "Valid token";
}

$conn->close();
?>
