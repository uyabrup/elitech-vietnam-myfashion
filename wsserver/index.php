<?php
require_once 'include/DbHandler.php';
require_once 'include/PassHash.php';
require 'libs/Slim/Slim.php';

define ( 'API_KEY', '89a7f77b5a13635f5d6707d694c22a71' );

\Slim\Slim::registerAutoloader ();

$app = new \Slim\Slim ();

/**
 * Adding Middle Layer to authenticate every request
 * Checking if the request has valid api key in the 'Authorization' header
 */
function authenticate() {
	// Getting request headers
	$headers = apache_request_headers ();
	$response = array ();
	$app = \Slim\Slim::getInstance ();
	
	// Verifying Authorization Header
	if (isset ( $headers ['Authorization'] )) {
		
		// get the api key
		$api_key = $headers ['Authorization'];
		// validating api key
		if ($api_key != API_KEY) {
			// api key is not present in users table
			$response ["error"] = true;
			$response ["message"] = "Access Denied. Invalid Api key";
			echoRespnse ( 401, $response );
			$app->stop ();
		}
	} else {
		// api key is missing in header
		$response ["error"] = true;
		$response ["message"] = "Api key is misssing";
		echoRespnse ( 400, $response );
		$app->stop ();
	}
}

/**
 * ----------- METHODS WITHOUT AUTHENTICATION ---------------------------------
 */
/**
 * User Registration
 * url - /register
 * method - POST
 * params - name, email, password
 */
$app->post ( '/register', function () use($app) {
	// check for required params
	verifyRequiredParams ( array (
			'name',
			'email',
			'password' 
	) );
	
	$response = array ();
	
	// reading post params
	$name = $app->request->post ( 'name' );
	$email = $app->request->post ( 'email' );
	$password = $app->request->post ( 'password' );
	
	// validating email address
	validateEmail ( $email );
	
	$db = new DbHandler ();
	$res = $db->createUser ( $name, $email, $password );
	
	if ($res == USER_CREATED_SUCCESSFULLY) {
		$response ["error"] = false;
		$response ["message"] = "You are successfully registered";
	} else if ($res == USER_CREATE_FAILED) {
		$response ["error"] = true;
		$response ["message"] = "Oops! An error occurred while registereing";
	} else if ($res == USER_ALREADY_EXISTED) {
		$response ["error"] = true;
		$response ["message"] = "Sorry, this email already existed";
	}
	// echo json response
	echoRespnse ( 201, $response );
} );

/**
 * User Login
 * url - /login
 * method - POST
 * params - email, password
 */
$app->post ( '/login', function () use($app) {
	// check for required params
	verifyRequiredParams ( array (
			'email',
			'password' 
	) );
	
	// reading post params
	$email = $app->request ()->post ( 'email' );
	$password = $app->request ()->post ( 'password' );
	$response = array ();
	
	$db = new DbHandler ();
	// check for correct email and password
	if ($db->checkLogin ( $email, $password )) {
		// get the user by email
		$user = $db->getUserByEmail ( $email );
		
		if ($user != NULL) {
			$response ["error"] = false;
			$response ['name'] = $user ['name'];
			$response ['email'] = $user ['email'];
			$response ['apiKey'] = $user ['api_key'];
			$response ['createdAt'] = $user ['created_at'];
		} else {
			// unknown error occurred
			$response ['error'] = true;
			$response ['message'] = "An error occurred. Please try again";
		}
	} else {
		// user credentials are wrong
		$response ['error'] = true;
		$response ['message'] = 'Login failed. Incorrect credentials';
	}
	
	echoRespnse ( 200, $response );
} );

/*
 * ------------------------ METHODS WITH AUTHENTICATION ------------------------
 */

/**
 * Listing all tasks of particual user
 * method GET
 * url /tasks
 */
$app->get ( '/tasks', 'authenticate', function () {
	global $user_id;
	$response = array ();
	$db = new DbHandler ();
	
	// fetching all user tasks
	$result = $db->getAllUserTasks ( $user_id );
	
	$response ["error"] = false;
	$response ["tasks"] = array ();
	
	// looping through result and preparing tasks array
	while ( $task = $result->fetch_assoc () ) {
		$tmp = array ();
		$tmp ["id"] = $task ["id"];
		$tmp ["task"] = $task ["task"];
		$tmp ["status"] = $task ["status"];
		$tmp ["createdAt"] = $task ["created_at"];
		array_push ( $response ["tasks"], $tmp );
	}
	
	echoRespnse ( 200, $response );
} );

/**
 * Listing single task of particual user
 * method GET
 * url /tasks/:id
 * Will return 404 if the task doesn't belongs to user
 */
$app->get ( '/tasks/:id', 'authenticate', function ($task_id) {
	global $user_id;
	$response = array ();
	$db = new DbHandler ();
	
	// fetch task
	$result = $db->getTask ( $task_id, $user_id );
	
	if ($result != NULL) {
		$response ["error"] = false;
		$response ["id"] = $result ["id"];
		$response ["task"] = $result ["task"];
		$response ["status"] = $result ["status"];
		$response ["createdAt"] = $result ["created_at"];
		echoRespnse ( 200, $response );
	} else {
		$response ["error"] = true;
		$response ["message"] = "The requested resource doesn't exists";
		echoRespnse ( 404, $response );
	}
} );

/**
 * Creating new task in db
 * method POST
 * params - name
 * url - /tasks/
 */
$app->post ( '/tasks', function () use($app) {
	authenticate ();
	// check for required params
	verifyRequiredParams ( array (
			'task' 
	) );
	
	$response = array ();
	$task = $app->request->post ( 'task' );
	
	global $user_id;
	$db = new DbHandler ();
	
	// creating new task
	$task_id = $db->createTask ( $user_id, $task );
	
	if ($task_id != NULL) {
		$response ["error"] = false;
		$response ["message"] = "Task created successfully";
		$response ["task_id"] = $task_id;
		echoRespnse ( 201, $response );
	} else {
		$response ["error"] = true;
		$response ["message"] = "Failed to create task. Please try again";
		echoRespnse ( 200, $response );
	}
} );

/**
 * Updating existing task
 * method PUT
 * params task, status
 * url - /tasks/:id
 */
$app->put ( '/tasks/:id', 'authenticate', function ($task_id) use($app) {
	// check for required params
	verifyRequiredParams ( array (
			'task',
			'status' 
	) );
	
	global $user_id;
	$task = $app->request->put ( 'task' );
	$status = $app->request->put ( 'status' );
	
	$db = new DbHandler ();
	$response = array ();
	
	// updating task
	$result = $db->updateTask ( $user_id, $task_id, $task, $status );
	if ($result) {
		// task updated successfully
		$response ["error"] = false;
		$response ["message"] = "Task updated successfully";
	} else {
		// task failed to update
		$response ["error"] = true;
		$response ["message"] = "Task failed to update. Please try again!";
	}
	echoRespnse ( 200, $response );
} );

/**
 * Deleting task.
 * Users can delete only their tasks
 * method DELETE
 * url /tasks
 */
$app->delete ( '/tasks/:id', 'authenticate', function ($task_id) use($app) {
	global $user_id;
	
	$db = new DbHandler ();
	$response = array ();
	$result = $db->deleteTask ( $user_id, $task_id );
	if ($result) {
		// task deleted successfully
		$response ["error"] = false;
		$response ["message"] = "Task deleted succesfully";
	} else {
		// task failed to delete
		$response ["error"] = true;
		$response ["message"] = "Task failed to delete. Please try again!";
	}
	echoRespnse ( 200, $response );
} );

/**
 * Verifying required params posted or not
 */
function verifyRequiredParams($required_fields) {
	$error = false;
	$error_fields = "";
	$request_params = array ();
	$request_params = $_REQUEST;
	// Handling PUT request params
	if ($_SERVER ['REQUEST_METHOD'] == 'PUT') {
		$app = \Slim\Slim::getInstance ();
		parse_str ( $app->request ()->getBody (), $request_params );
	}
	foreach ( $required_fields as $field ) {
		if (! isset ( $request_params [$field] ) || strlen ( trim ( $request_params [$field] ) ) <= 0) {
			$error = true;
			$error_fields .= $field . ', ';
		}
	}
	
	if ($error) {
		// Required field(s) are missing or empty
		// echo error json and stop the app
		$response = array ();
		$app = \Slim\Slim::getInstance ();
		$response ["error"] = true;
		$response ["message"] = 'Required field(s) ' . substr ( $error_fields, 0, - 2 ) . ' is missing or empty';
		echoRespnse ( 400, $response );
		$app->stop ();
	}
}

/**
 * Validating email address
 */
function validateEmail($email) {
	$app = \Slim\Slim::getInstance ();
	if (! filter_var ( $email, FILTER_VALIDATE_EMAIL )) {
		$response ["error"] = true;
		$response ["message"] = 'Email address is not valid';
		echoRespnse ( 400, $response );
		$app->stop ();
	}
}

/**
 * Echoing json response to client
 *
 * @param String $status_code
 *        	Http response code
 * @param Int $response
 *        	Json response
 */
function echoRespnse($status_code, $response) {
	$app = \Slim\Slim::getInstance ();
	// Http response code
	$app->status ( $status_code );
	
	// setting response content type to json
	$app->contentType ( 'application/json' );
	
	echo json_encode ( $response );
}

/**
 * Calculate sale off
 */
function calculateSaleOff($r) {
	if ($r ['sale_start'] != null && $r ['sale_end'] != null && $r ['sale_start'] != '0000-00-00' && $r ['sale_end'] != '0000-00-00') {
		$now = date ( 'Y-m-d' );
		$start = date ( 'Y-m-d', strtotime ( $r ['sale_start'] ) );
		$end = date ( 'Y-m-d', strtotime ( $r ['sale_end'] ) );
		if ($now < $start || $now > $end)
			$r ['sale_off'] = '0';
	}
	$r ['price_sale'] = '0';
	$r ['price_sale'] = ( string ) (round ( ((( float ) $r ['price_vn']) - (( float ) $r ['price_vn']) * (( float ) $r ['sale_off']) / 100) / 10000 ) * 10000);
	$r ['price_vn'] = ( string ) (round ( (( float ) $r ['price_vn']) / 10000 ) * 10000);
	return $r;
}

/**
 * SHOPPING SERVICES METHODS DEFINITION
 */

/**
 * Listing product best of day by limit
 * method GET
 * url /bestOfDay
 */
$app->get ( '/bestOfDay', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'day',
			'account',
			'start',
			'count' 
	) );
	
	$day = $app->request->get ( 'day' );
	$account = $app->request->get ( 'account' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$response = array ();
	$db = new DbHandler ();
	
	// fetching all best of day products
	$result = $db->getBestOfDay ( $day, $account, $start, $count );
	
	// looping through result and preparing tasks array
	while ( $row = $result->fetch_assoc () ) {
		$row = calculateSaleOff ( $row );
		array_push ( $response, calculateSaleOff ( $row ) );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/trademarks', function () {
	authenticate ();
	
	$response = array ();
	$arr2 = array ();
	$db = new DbHandler ();
	
	$rs1 = $db->getTradeMarks ();
	$rs2 = $db->getAllTradeMarksCategory ();
	
	while ( $row = $rs2->fetch_assoc () ) {
		array_push ( $arr2, $row );
	}
	
	while ( $row = $rs1->fetch_assoc () ) {
		$row ['categories'] = array ();
		foreach ( $arr2 as $r1 ) {
			if ($r1 ['id_parent'] == 0 && $r1 ['id_trademarks'] == $row ['id']) {
				$r1 ['child_categories'] = array ();
				foreach ( $arr2 as $r2 ) {
					if ($r2 ['id_parent'] > 0 && $r1 ['id'] == $r2 ['id_parent'])
						array_push ( $r1 ['child_categories'], $r2 );
				}
				array_push ( $row ['categories'], $r1 );
			}
		}
		array_push ( $response, $row );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/styler', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'account',
			'start',
			'count' 
	) );
	
	$account = $app->request->get ( 'account' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$response = array ();
	$db = new DbHandler ();
	
	// fetching all best of day products
	$result = $db->getStyler ( $account, $start, $count );
	
	// looping through result and preparing tasks array
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, $row );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/product/:id/details', function ($id) {
	authenticate ();
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getProductDetails ( $id );
	
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, $row );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/product/:id/comments', function ($id) use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'start',
			'count',
			'type' 
	) );
	
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	$type = $app->request->get ( 'type' );
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getProductComment ( $id, $start, $count, $type );
	
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, $row );
	}
	
	echoRespnse ( 200, $response );
} );

$app->post ( '/doComment', function ($product) use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'product',
			'account',
			'content',
			'type' 
	) );
	
	$product = $app->request->post ( 'product' );
	$account = $app->request->post ( 'account' );
	$content = $app->request->post ( 'content' );
	$type = $app->request->post ( 'type' );
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->postComment ( $account, $product, $content, $type );
	
	$response = $result;
	
	echoRespnse ( 200, $response );
} );

$app->put ( '/changePassword', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'account',
			'password' 
	) );
	
	$account = $app->request->put ( 'account' );
	$password = $app->request->put ( 'password' );
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->changePassword ( $account, $password );
	
	$response = $result;
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/styler/:id', function ($member) use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'account',
			'start',
			'count' 
	) );
	
	$account = $app->request->get ( 'account' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getStyle ( $member, $account, $start, $count );
	
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, $row );
	}
	
	echoRespnse ( 200, $response );
} );

$app->post ( '/orders/:id/details', function ($order) use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'product',
			'colour',
			'size',
			'price',
			'quantity' 
	) );
	
	$product = $app->request->post ( 'product' );
	$colour = $app->request->post ( 'colour' );
	$size = $app->request->post ( 'size' );
	$price = $app->request->post ( 'price' );
	$quantity = $app->request->post ( 'quantity' );
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->postOrderDetail ( $order, $product, $colour, $size, $price, $quantity );
	
	$response = $result;
	
	echoRespnse ( 200, $response );
} );

$app->post ( '/doLikes', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'product',
			'account',
			'liked',
			'type' 
	) );
	
	$product = $app->request->post ( 'product' );
	$account = $app->request->post ( 'account' );
	$liked = $app->request->post ( 'liked' );
	$type = $app->request->post ( 'type' );
	
	$db = new DbHandler ();
	
	if ($db->checkLikeError ( $product, $account, $liked, $type )) {
		echoRespnse ( 200, - 1 );
		return;
	}
	
	if (! $db->addLike ( $product, $account, $liked, $type )) {
		echoRespnse ( 200, - 2 );
		return;
	}
	
	$response = $db->getLikesByProductId ( $product, $type );
	echoRespnse ( 200, $response );
} );

$app->get ( '/product/:id/review', function ($id) use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'start',
			'count' 
	) );
	
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getProductReview ( $id, $start, $count );
	
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, $row );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/category', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'fashion' 
	) );
	
	$fashion = $app->request->get ( 'fashion' );
	
	$ids = array ();
	$rs = array ();
	$response = array ();
	$db = new DbHandler ();
	
	$rs1 = $db->getCategory ( $fashion );
	while ( $row = $rs1->fetch_assoc () ) {
		array_push ( $ids, $row ['id_parent'] );
		array_push ( $rs, $row );
	}
	
	$rs2 = $db->getParentCategory ( $ids );
	while ( $row = $rs2->fetch_assoc () ) {
		$childs = array ();
		$ifashion = 0;
		$row ['i_fashion'] = 0;
		foreach ( $rs as $row1 ) {
			if ($row1 ['id_parent'] == $row ['id']) {
				array_push ( $childs, $row1 );
				$ifashion += $row1 ['i_fashion'];
			}
		}
		$row ['child_categories'] = $childs;
		$row ['i_fashion'] = $ifashion;
		array_push ( $response, $row );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/category/:id/products', function ($category) use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'account',
			'fashion',
			'start',
			'count' 
	) );
	
	$account = $app->request->get ( 'account' );
	$fashion = $app->request->get ( 'fashion' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getProductCategory ( $category, $account, $fashion, $start, $count );
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, calculateSaleOff ( $row ) );
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/product/:id/sizes', function ($product) {
	authenticate ();
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getProductSize ( $product );
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/product/:id/colors', function ($product) {
	authenticate ();
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getProductColor ( $product );
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/cities', function () {
	authenticate ();
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getCities ();
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/districts', function () {
	authenticate ();
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getDistricts ();
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->post ( '/devices', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'device',
			'version',
			'api',
			'user',
			'day',
			'gcmid' 
	) );
	
	$device = $app->request->post ( 'device' );
	$version = $app->request->post ( 'version' );
	$api = $app->request->post ( 'api' );
	$user = $app->request->post ( 'user' );
	$day = $app->request->post ( 'day' );
	$gcmid = $app->request->post ( 'gcmid' );
	
	$db = new DbHandler ();
	$result = $db->storeDevice ( $device, $gcmid );
	if ($result > 0) {
		$result = $db->appTrack ( $device, $version, $api, $user, $day );
	}
	
	echoRespnse ( 200, $result );
} );

/**
 * Test method
 */
$app->post ( '/test', function () use($app) {
	$product = $app->request->post ( 'product' );
	$account = $app->request->post ( 'account' );
	$liked = $app->request->post ( 'liked' );
	$type = $app->request->post ( 'type' );
	
	$db = new DbHandler ();
	
	echoRespnse ( 200, $db->checkLikeError ( $product, $account, $liked, $type ) );
} );

$app->run ();
?>