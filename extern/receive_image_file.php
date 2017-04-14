<?php
/*
RECEIVE_IMAGE_FILE.PHP SERVER SCRIPT DESCRIPTION
When the user application misses an image file, or recognizes a newer image
version via hash value comparison, it may request the full image file from
the server (using this script).

PARAMETERS:
    type    -   The type of image requested
                Can either be
                    'user' : A user image
                    'band' : A band image
    id      -   The specific object id (depending on the type parameter)

RETURN:
    [base64-encoded image data]

@MYBAND PROJECT
*/

$type = $_POST['type'];
$id = $_POST['id'];

require '../private/dbaccess.php';
require '../private/auth_token.php';

$conn = new mysqli($server, $dbuser, $dbpw, $dbname);
if($conn->connect_error){
    die('Database connection could not be established');
}


?>
