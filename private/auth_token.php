<?php
/*
 * function that generates a random hexadezimal string (the token)
 * of a given length (the parameter)
 */
function token_generate($length) {
	// The str object that is being returned from the function
	$tokenstr = '';

	// While the random string is not long enough
	while(strlen($tokenstr) < $length){
		// Generate random number -> convert to hexadezimal
		$randstr = sprintf('%x', mt_rand());

		// Find out how much of the new random string must be
		// appended to the result string to reach final length
		$rest = $length - strlen($tokenstr);
		if($rest > strlen($randstr)){	// Append full string
			$tokenstr .= $randstr;
		}
		else{							// Append a substring
			$tokenstr .= substr($randstr, -$rest);
		}
	}

	return $tokenstr;
}

function token_add_new($dbconn, $token, $uid){
	if($dbconn == null){
		return;
	}

	if(strlen($token) > 100){
		echo 'Token too long!';
		return;
	}

	$dbconn->query('INSERT INTO LoginTokens(token, user_id) VALUES ("' .
									$token . '", ' . $uid . ')');

	if($dbconn->error){
		echo "Error in Login-Token database! " . $dbconn->error;
	}
}


/*!
	IMPORTANT NOTE: This function is only secure when there is no possible
	way to call this function manually or valid user_id.
	Exploit possibility here: Attacker gets to call this method with a valid
	user_id and his own authentication token. Every script call is then fully
	authenticated for that specific user.
!*/
function token_update($dbconn, $token, $uid){
	if($dbconn == null){
		// May only happen if the calling script is badly written
		return false;
	}

	if(strlen($token) > 100){
		//echo 'Token too long!';
		return false;
	}

	$stmt = $dbconn->prepare('UPDATE LoginTokens SET token = ? WHERE user_id = ?;');
	if(!$stmt || !$stmt->bind_param('si', $token, $uid)){
		//echo "Binding error";
		return false;
	}
	$stmt->execute();
	return true;
}

/*
*
*/
function token_verify($dbconn, $token){
	if($dbconn == null || $dbconn->connect_error){
		return null;
	}

	if(strlen($token) > 100){
		// echo 'Token too long!';
		return null;
	}
	if(strlen($token) < 40){
		// echo 'Token not long enough!';
		return null;
	}

	$stmt = $dbconn->prepare('SELECT user_id FROM LoginTokens WHERE token = ?;');
	$stmt->bind_param('s', $token);
	$stmt->execute();
	if($stmt->error){
		// echo "Executing error";
		die('Executing error');
	}
	$stmt->bind_result($uid);
	$stmt->fetch();
	if($uid == 0){
		return null;
	}
	else{
		return $uid;
	}
}

?>
