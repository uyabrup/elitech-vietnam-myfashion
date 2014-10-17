<?php

class MemoTracker {
	private $conn;
	function __construct() {
		$this->conn = new mysqli('1.234.1.96', 'cam', '5678', 'shopping');

        if (mysqli_connect_errno() || !$this->conn->set_charset("utf8")) {
            echo "Failed to connect to MySQL" . mysqli_connect_error();
        }
	}
	
	public function isValidApiKey($api_key) {
		return $api_key == "89a7f77b5a13635f5d6707d694c22a71";
	}
	
	private function generateApiKey() {
		return md5 ( uniqid ( rand (), true ) );
	}
	
	public function addTrack($idaccount, $email, $ip, $date, $version) {
		$str = "INSERT INTO `track` (`id_account`, `email`, `ip`, `launch_time`, `app_version`)
				VALUES				(?, ?, ?, ?, ?);";
				
		$stmt = $this->conn->prepare($str);
		$stmt->bind_param ( "issss", $idaccount, $email, $ip, $date, $version );
		$stmt->execute ();
		$res = $stmt->affected_rows;
		$stmt->close();
		return $res;
	}
}

?>
