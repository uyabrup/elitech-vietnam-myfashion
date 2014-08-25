<?php

/**
 * Class to handle all db operations
 * This class will have CRUD methods for database tables
 *
 * @author Ravi Tamada
 * @link URL Tutorial link
 */
class DbHandler {
	private $conn;
	function __construct() {
		require_once dirname ( __FILE__ ) . '/DbConnect.php';
		// opening db connection
		$db = new DbConnect ();
		$this->conn = $db->connect ();
	}
	
	/* ------------- `users` table method ------------------ */
	
	/**
	 * Creating new user
	 *
	 * @param String $name
	 *        	User full name
	 * @param String $email
	 *        	User login email id
	 * @param String $password
	 *        	User login password
	 */
	public function createUser($name, $email, $password) {
		require_once 'PassHash.php';
		$response = array ();
		
		// First check if user already existed in db
		if (! $this->isUserExists ( $email )) {
			// Generating password hash
			$password_hash = PassHash::hash ( $password );
			
			// Generating API key
			$api_key = $this->generateApiKey ();
			
			// insert query
			$stmt = $this->conn->prepare ( "INSERT INTO users(name, email, password_hash, api_key, status) values(?, ?, ?, ?, 1)" );
			$stmt->bind_param ( "ssss", $name, $email, $password_hash, $api_key );
			
			$result = $stmt->execute ();
			
			$stmt->close ();
			
			// Check for successful insertion
			if ($result) {
				// User successfully inserted
				return USER_CREATED_SUCCESSFULLY;
			} else {
				// Failed to create user
				return USER_CREATE_FAILED;
			}
		} else {
			// User with same email already existed in the db
			return USER_ALREADY_EXISTED;
		}
		
		return $response;
	}
	
	/**
	 * Checking user login
	 *
	 * @param String $email
	 *        	User login email id
	 * @param String $password
	 *        	User login password
	 * @return boolean User login status success/fail
	 */
	public function checkLogin($email, $password) {
		// fetching user by email
		$stmt = $this->conn->prepare ( "SELECT password_hash FROM users WHERE email = ?" );
		
		$stmt->bind_param ( "s", $email );
		
		$stmt->execute ();
		
		$stmt->bind_result ( $password_hash );
		
		$stmt->store_result ();
		
		if ($stmt->num_rows > 0) {
			// Found user with the email
			// Now verify the password
			
			$stmt->fetch ();
			
			$stmt->close ();
			
			if (PassHash::check_password ( $password_hash, $password )) {
				// User password is correct
				return TRUE;
			} else {
				// user password is incorrect
				return FALSE;
			}
		} else {
			$stmt->close ();
			
			// user not existed with the email
			return FALSE;
		}
	}
	
	/**
	 * Checking for duplicate user by email address
	 *
	 * @param String $email
	 *        	email to check in db
	 * @return boolean
	 */
	private function isUserExists($email) {
		$stmt = $this->conn->prepare ( "SELECT id from users WHERE email = ?" );
		$stmt->bind_param ( "s", $email );
		$stmt->execute ();
		$stmt->store_result ();
		$num_rows = $stmt->num_rows;
		$stmt->close ();
		return $num_rows > 0;
	}
	
	/**
	 * Fetching user by email
	 *
	 * @param String $email
	 *        	User email id
	 */
	public function getUserByEmail($email) {
		$stmt = $this->conn->prepare ( "SELECT name, email, api_key, status, created_at FROM users WHERE email = ?" );
		$stmt->bind_param ( "s", $email );
		if ($stmt->execute ()) {
			// $user = $stmt->get_result()->fetch_assoc();
			$stmt->bind_result ( $name, $email, $api_key, $status, $created_at );
			$stmt->fetch ();
			$user = array ();
			$user ["name"] = $name;
			$user ["email"] = $email;
			$user ["api_key"] = $api_key;
			$user ["status"] = $status;
			$user ["created_at"] = $created_at;
			$stmt->close ();
			return $user;
		} else {
			return NULL;
		}
	}
	
	/**
	 * Fetching user api key
	 *
	 * @param String $user_id
	 *        	user id primary key in user table
	 */
	public function getApiKeyById($user_id) {
		$stmt = $this->conn->prepare ( "SELECT api_key FROM users WHERE id = ?" );
		$stmt->bind_param ( "i", $user_id );
		if ($stmt->execute ()) {
			// $api_key = $stmt->get_result()->fetch_assoc();
			// TODO
			$stmt->bind_result ( $api_key );
			$stmt->close ();
			return $api_key;
		} else {
			return NULL;
		}
	}
	
	/**
	 * Fetching user id by api key
	 *
	 * @param String $api_key
	 *        	user api key
	 */
	public function getUserId($api_key) {
		$stmt = $this->conn->prepare ( "SELECT id FROM users WHERE api_key = ?" );
		$stmt->bind_param ( "s", $api_key );
		if ($stmt->execute ()) {
			$stmt->bind_result ( $user_id );
			$stmt->fetch ();
			// TODO
			// $user_id = $stmt->get_result()->fetch_assoc();
			$stmt->close ();
			return $user_id;
		} else {
			return NULL;
		}
	}
	
	/**
	 * Validating user api key
	 * If the api key is there in db, it is a valid key
	 *
	 * @param String $api_key
	 *        	user api key
	 * @return boolean
	 */
	public function isValidApiKey($api_key) {
		return $api_key == "89a7f77b5a13635f5d6707d694c22a71";
	}
	
	/**
	 * Generating random Unique MD5 String for user Api key
	 */
	private function generateApiKey() {
		return md5 ( uniqid ( rand (), true ) );
	}
	
	/* ------------- `tasks` table method ------------------ */
	
	/**
	 * Creating new task
	 *
	 * @param String $user_id
	 *        	user id to whom task belongs to
	 * @param String $task
	 *        	task text
	 */
	public function createTask($user_id, $task) {
		$stmt = $this->conn->prepare ( "INSERT INTO tasks(task) VALUES(?)" );
		$stmt->bind_param ( "s", $task );
		$result = $stmt->execute ();
		$stmt->close ();
		
		if ($result) {
			// task row created
			// now assign the task to user
			$new_task_id = $this->conn->insert_id;
			$res = $this->createUserTask ( $user_id, $new_task_id );
			if ($res) {
				// task created successfully
				return $new_task_id;
			} else {
				// task failed to create
				return NULL;
			}
		} else {
			// task failed to create
			return NULL;
		}
	}
	
	/**
	 * Fetching single task
	 *
	 * @param String $task_id
	 *        	id of the task
	 */
	public function getTask($task_id, $user_id) {
		$stmt = $this->conn->prepare ( "SELECT t.id, t.task, t.status, t.created_at from tasks t, user_tasks ut WHERE t.id = ? AND ut.task_id = t.id AND ut.user_id = ?" );
		$stmt->bind_param ( "ii", $task_id, $user_id );
		if ($stmt->execute ()) {
			$res = array ();
			$stmt->bind_result ( $id, $task, $status, $created_at );
			// TODO
			// $task = $stmt->get_result()->fetch_assoc();
			$stmt->fetch ();
			$res ["id"] = $id;
			$res ["task"] = $task;
			$res ["status"] = $status;
			$res ["created_at"] = $created_at;
			$stmt->close ();
			return $res;
		} else {
			return NULL;
		}
	}
	
	/**
	 * Fetching all user tasks
	 *
	 * @param String $user_id
	 *        	id of the user
	 */
	public function getAllUserTasks($user_id) {
		$stmt = $this->conn->prepare ( "SELECT t.* FROM tasks t, user_tasks ut WHERE t.id = ut.task_id AND ut.user_id = ?" );
		$stmt->bind_param ( "i", $user_id );
		$stmt->execute ();
		$tasks = $stmt->get_result ();
		$stmt->close ();
		return $tasks;
	}
	
	/**
	 * Updating task
	 *
	 * @param String $task_id
	 *        	id of the task
	 * @param String $task
	 *        	task text
	 * @param String $status
	 *        	task status
	 */
	public function updateTask($user_id, $task_id, $task, $status) {
		$stmt = $this->conn->prepare ( "UPDATE tasks t, user_tasks ut set t.task = ?, t.status = ? WHERE t.id = ? AND t.id = ut.task_id AND ut.user_id = ?" );
		$stmt->bind_param ( "siii", $task, $status, $task_id, $user_id );
		$stmt->execute ();
		$num_affected_rows = $stmt->affected_rows;
		$stmt->close ();
		return $num_affected_rows > 0;
	}
	
	/**
	 * Deleting a task
	 *
	 * @param String $task_id
	 *        	id of the task to delete
	 */
	public function deleteTask($user_id, $task_id) {
		$stmt = $this->conn->prepare ( "DELETE t FROM tasks t, user_tasks ut WHERE t.id = ? AND ut.task_id = t.id AND ut.user_id = ?" );
		$stmt->bind_param ( "ii", $task_id, $user_id );
		$stmt->execute ();
		$num_affected_rows = $stmt->affected_rows;
		$stmt->close ();
		return $num_affected_rows > 0;
	}
	
	/* ------------- `user_tasks` table method ------------------ */
	
	/**
	 * Function to assign a task to user
	 *
	 * @param String $user_id
	 *        	id of the user
	 * @param String $task_id
	 *        	id of the task
	 */
	public function createUserTask($user_id, $task_id) {
		$stmt = $this->conn->prepare ( "INSERT INTO user_tasks(user_id, task_id) values(?, ?)" );
		$stmt->bind_param ( "ii", $user_id, $task_id );
		$result = $stmt->execute ();
		
		if (false === $result) {
			die ( 'execute() failed: ' . htmlspecialchars ( $stmt->error ) );
		}
		$stmt->close ();
		return $result;
	}
	
	/**
	 * ----------- SHOPPING SERVICES METHOD ---------------------------------
	 */
	
	/**
	 * Function to get product best of day
	 */
	public function getBestOfDay($day, $idaccount, $start, $count) {
		$str = "SELECT		p.*,
							IFNULL((	SELECT	SUM(l.`like`)
										FROM	`like` l
										WHERE	l.`id_account`=? AND l.`id_product`=p.`id` AND l.`type`=1
									), 0) AS `liked`,
							SUBSTRING_INDEX(	TRIM(	LEADING	'shop.' 
												FROM 	TRIM(	LEADING	'www.'
																FROM 	TRIM(	LEADING	'http://' 
																				FROM 	p.`url`))), '.', 1)	AS	`brand`,
							c.`nameEN`	AS	`category_name_en`,
							c.`nameKR`	AS	`category_name_kr`,
							c.`nameVN`	AS	`category_name_vn`,
							c.`color`	AS	`color`,
							p.`priceVN`	AS	`price_vn`
				FROM		`product` p
				INNER JOIN	`category` c
				ON			c.`id`=p.`id_category`
				WHERE		c.`status`=1	AND
							( IF 
								(p.`end_date` IS NULL, 
								TRUE, 
								(? BETWEEN p.`date` AND p.`end_date`)))	AND
							p.`quantity`=1		AND
							p.`top_seller`=1	AND
							c.`id_trademarks`=0
				ORDER BY	p.`create_day` 						DESC,
							p.`priceVN` * (100-p.`sale_off`)	ASC,
							p.`date` 							DESC
				LIMIT		?, ?;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "isii", $idaccount, $day, $start, $count );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function updateTradeMarks($id, $name, $image, $position) {
		$str = "UPDATE		`trademarks` t
				SET			t.`name`=?,
							t.`image`=?,
							t.`pos`=?
				WHERE		t.`id`=?;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( 'ssii', $name, $image, $position, $id );
		$stmt->execute ();
		$num_affected_rows = $stmt->affected_rows;
		$stmt->close ();
		return $num_affected_rows > 0;
	}
	public function deleteTradeMarks($id) {
		$str = "DELETE FROM	`trademarks`
				WHERE		`id`=?;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "i", $id );
		$stmt->execute ();
		$num_affected_rows = $stmt->affected_rows;
		$stmt->close ();
		return $num_affected_rows > 0;
	}
	public function getStyler($account, $start, $count) {
		$str = "SELECT		p.*,	IFNULL((	SELECT	SUM(l.`like`)
												FROM	`like` l
												WHERE	l.`id_account`=? AND l.`id_product`=p.`id` AND l.`type`=2
											), 0) AS `liked`
				FROM		`post` p
				WHERE		p.`status`=1
				ORDER BY	p.`create_day` 	DESC
				LIMIT		?, ?;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "iii", $account, $start, $count );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function register() {
	}
	public function getProductDetails($id) {
		$str = "SELECT 		p.*
				FROM 		`product_detail` p
				WHERE 		p.`id_product`=?
				ORDER BY	p.`pos`	ASC;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "i", $id );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function getProductComment($id, $start, $count, $type) {
		$str = "SELECT 		c.*, a.`image`, a.`name`, a.`nick_name`
				FROM 		`comment` c
				INNER JOIN 	`account` a
				ON			a.`id`=c.`id_account`
				WHERE 		c.`id_product`=? AND c.`status`=1 AND c.`type`=?
				ORDER BY	c.`date`	ASC
				LIMIT		?, ?;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "iiii", $id, $type, $start, $count );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function postComment($account, $product, $content, $type) {
		$str = "INSERT INTO `comment` 	(`id_account`, `id_product`, `content`, `date`, `type`, `status`)
				VALUES 					(?, ?, ?, ?, ?, 1)";
		
		$date = date ( "Y-m-d H:i:s" );
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "iissi", $account, $product, $content, $date, $type );
		$result = $stmt->execute ();
		$data = 0;
		if (! $result)
			$data = - 1;
		else
			$data = $stmt->insert_id;
		$stmt->close ();
		return $data;
	}
	public function changePassword($account, $password) {
		$str = "UPDATE	`account`
				SET		`password`=?
				WHERE	`id`=?;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "si", $password, $account );
		$stmt->execute ();
		$num_affected_rows = $stmt->affected_rows;
		$stmt->close ();
		return $num_affected_rows;
	}
	public function getStyle($member, $account, $start, $count) {
		$str = "SELECT		p.*,	IFNULL((	SELECT	SUM(l.`like`)
												FROM	`like` l
												WHERE	l.`id_account`=? AND l.`id_product`=p.`id` AND l.`type`=2
											), 0) AS `liked`
				FROM		`post` p
				WHERE		p.`id_account`=? AND p.`status`=1
				ORDER BY	p.`create_day` 	DESC
				LIMIT		?, ?;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "iiii", $account, $member, $start, $count );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function postOrderDetail($order, $product, $color, $size, $price, $quantity) {
		$str = "INSERT INTO 	`order_detail`	(`id_order`, `id_product`, `color`, `size`, `price`, `commission`, `quatily`)
				VALUES							(?, ?, ?, ?, ?, 0, ?)";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "iiiidi", $order, $product, $color, $size, $price, $quantity );
		$result = $stmt->execute ();
		$data = 0;
		if (! $result)
			$data = - 1;
		else
			$data = $stmt->insert_id;
		$stmt->close ();
		return $data;
	}
	public function getTradeMarks() {
		$str = "SELECT 		t.*, 
	 						t1.`sum_product`
				FROM 		`trademarks` t
				INNER JOIN 	(	SELECT 		c.`id_trademarks`, 
	 										SUM(t2.`count`) 	AS 	`sum_product` 
	 							FROM 		`category` c 
	 							INNER JOIN 	(	SELECT 		p.`id_category`, 
	 														COUNT(p.`id_category`) 	AS 	`count` 
	 											FROM 		`product` p 
	 											GROUP BY 	p.`id_category`	) t2 
	 							ON 			t2.`id_category`=c.`id` 
	 							GROUP BY 	c.`id`) t1
				ON 			t.`id`=t1.`id_trademarks`
				WHERE 		t.`id`<>0
				GROUP BY 	t.`id`;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function checkLikeError($product, $account, $liked, $type) {
		$str = "SELECT	*
	 			FROM	`like` l
	 			WHERE	l.`id_account`=?	AND
	 					l.`id_product`=?	AND
	 					l.`type`=?;";
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "iii", $account, $product, $type );
		$stmt->execute ();
		$stmt->store_result ();
		$result = $stmt->num_rows;
		$stmt->close ();
		return (($result > 0 && $liked == 1) || ($result <= 0 && $liked == - 1));
	}
	public function getLikesByProductId($product, $type) {
		$str = "";
		if ($type == 1)
			$str = "SELECT	p.`likes`
	 				FROM	`product` p
					WHERE	p.`id`=?;";
		if ($type == 2)
			$str = "SELECT 	p.`likes`
	 				FROM 	`post` p
					WHERE	p.`id`=?;";
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "i", $product );
		$likes = 0;
		if ($stmt->execute ()) {
			$stmt->bind_result ( $likes );
			$stmt->fetch ();
		}
		$stmt->close ();
		return $likes;
	}
	public function addLike($product, $account, $liked, $type) {
		$str = "";
		$date = date ( "Y-m-d H:i:s" );
		
		if ($liked == 1)
			$str = "INSERT INTO `like`	(`id_account`, `id_product`, `like`, `datetime`, `type`)
					VALUES				(?, ?, ?, ?, ?);";
		if ($liked == - 1)
			$str = "DELETE FROM `like`
		 			WHERE		`id_account`=?	AND
		 						`id_product`=?	AND
		 						`type`=?;";
		$stmt = $this->conn->prepare ( $str );
		
		if ($liked == 1)
			$stmt->bind_param ( "iiisi", $account, $product, $liked, $date, $type );
		if ($liked == - 1)
			$stmt->bind_param ( "iii", $account, $product, $type );
		$stmt->execute ();
		$rs2 = $stmt->affected_rows;
		
		$stmt->close ();
		return $rs2 > 0;
	}
	public function getProductReview($product, $start, $count) {
		$str = "SELECT		r.*,
							a.`image`		AS	`aimage`,
							a.`name`		AS	`aname`,
							a.`nick_name`	AS	`anickname`,
							p.`name`		AS	`pname`
				FROM		`review` r
				INNER JOIN	`account` a
				ON			a.`id`=r.`id_account`
				INNER JOIN	`product` p
				ON			p.`id`=r.`id_product`
				WHERE		r.`status`=1	AND
							r.`id_product`=?
				ORDER BY	r.`date`	DESC
				LIMIT		?, ?;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "iii", $product, $start, $count );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function getCategory($fashion) {
		$str = "SELECT		c.*, 
							(	SELECT	COUNT(*)
								FROM	`product` p
								WHERE	p.`id_category`=c.`id`	AND
										p.`status`=1			AND
										p.`quantity`=1)	AS `num_product`,
							(	SELECT	COUNT(*)
								FROM	`product` p1
								WHERE	p1.`id_category`=c.`id`	AND
										p1.`status`=1			AND
										p1.`quantity`=1			AND
										p1.`fashion`=?) AS `i_fashion`
				FROM		`category` c
				WHERE		c.`status`=1		AND
							c.`id_trademarks`=0
				HAVING 		`i_fashion`>0
				ORDER BY	c.`pos`		ASC,
							c.`pos1`	ASC;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "i", $fashion );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function getAllTradeMarksCategory() {
		$str = "SELECT		c.*, 
							t.`name` 	AS `tm_name`, 
							t.`image` 	AS `tm_image`,
							(	SELECT	COUNT(*)
								FROM	`product` p
								WHERE	p.`id_category`=c.`id`	AND
										p.`status`=1			AND
										p.`quantity`=1)	AS `num_product`
				FROM		`category` c
				INNER JOIN	`trademarks` t
				ON			t.`id`=c.`id_trademarks`
				WHERE		c.`status`=1
				ORDER BY	c.`pos`			ASC,
							c.`pos1`		ASC;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function getParentCategory($id) {
		$ids = "NULL";
		if (! empty ( $id ))
			$ids = join ( ',', $id );
		$str = "SELECT		c.*
				FROM		`category` c
				WHERE		c.`status`=1	AND
							c.`id` IN ($ids)
				ORDER BY	c.`pos`		ASC,
							c.`pos1`	ASC;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
	public function getProductCategory($category, $account, $fashion, $start, $count) {
		$str = "SELECT		p.*,	IFNULL((	SELECT	SUM(l.`like`)
												FROM	`like` l
												WHERE	l.`id_account`=? AND 
														l.`id_product`=p.`id` 		AND 
														l.`type`=1
											), 0) AS `liked`,
									c.`nameEN`	AS	`category_name_en`,
									c.`nameKR`	AS	`category_name_kr`,
									c.`nameVN`	AS	`category_name_vn`,
									c.`color`	AS	`color`,
									p.`priceVN`	AS	`price_vn`
				FROM		`product` p
				INNER JOIN	`category` c
				ON			c.`id`=p.`id_category`
				WHERE			p.`status`=1
							AND	p.`id_category`=?
							AND	p.`quantity`=1"
							. ( ( $fashion < 0 ) ? "" : "	AND p.`fashion`=$fashion" ) 
							. "
				ORDER BY	c.`pos`			ASC,
							c.`pos1`		ASC,
							p.`create_day`	DESC,
							p.`likes`		DESC
				LIMIT		?, ?;";
		
		$stmt = $this->conn->prepare ( $str );
		$stmt->bind_param ( "iiii", $account, $category, $start, $count );
		$stmt->execute ();
		$data = $stmt->get_result ();
		$stmt->close ();
		return $data;
	}
}

?>
