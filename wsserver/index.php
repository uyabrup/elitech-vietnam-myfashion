<?php
require_once 'include/DbHandler.php';
require_once 'include/PassHash.php';
require 'libs/Slim/Slim.php';

define ( 'API_KEY', '89a7f77b5a13635f5d6707d694c22a71' );

date_default_timezone_set ( "Asia/Saigon" );
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
		$r1 = new stdClass ();
		$r2 = new stdClass ();
		foreach ($row as $key => $value) {
			if ( substr ( $key, 0, 2 ) == 'a.' ) {
				$str = explode ( '.', $key );
				$var = $str[1];
				$r2->{$var} = $value;
			} else {
				$r1->{$key} = $value;
			}
		}
		$r1->account = $r2;
		array_push ( $response, $r1 );
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

$app->get ( '/member/:id/style', function ($member) use($app) {
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
		$r1 = new stdClass ();
		$r2 = new stdClass ();
		foreach ($row as $key => $value) {
			if ( substr ( $key, 0, 2 ) == 'a.' ) {
				$str = explode ( '.', $key );
				$var = $str[1];
				$r2->{$var} = $value;
			} else {
				$r1->{$key} = $value;
			}
		}
		$r1->account = $r2;
		array_push ( $response, $r1 );
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

$app->get ( '/ships', function () {
	authenticate ();
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getShip ();
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/shipmore', function () {
	authenticate ();
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getShipMore ();
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->post ( '/order', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'account',
			'email',
			'name',
			'address',
			'city',
			'state',
			'phone',
			'payment',
			'ship',
			'shipprice',
			'detail' 
	) );
	
	$account = $app->request->post ( 'account' );
	$email = urldecode ( $app->request->post ( 'email' ) );
	$name = urldecode ( $app->request->post ( 'name' ) );
	$address = urldecode ( $app->request->post ( 'address' ) );
	$city = urldecode ( $app->request->post ( 'city' ) );
	$state = urldecode ( $app->request->post ( 'state' ) );
	$phone = $app->request->post ( 'phone' );
	$payment = $app->request->post ( 'payment' );
	$ship = $app->request->post ( 'ship' );
	$shipprice = $app->request->post ( 'shipprice' );
	$memo = urldecode ( $app->request->post ( 'memo' ) );
	$detail = urldecode ( $app->request->post ( 'detail' ) );
	$details = json_decode ( $detail, true );
	
	$db = new DbHandler ();
	$code = 'OD_APP_' . $db->getOrderCode ();
	$date = date ( "Y-m-d H:i:s" );
	$orderid = $db->addOrder ( $code, $account, $address, $city, $state, $phone, $payment, $shipprice, $memo, $date, $email, $name );
	if ($orderid > 0)
		$result = $db->addOrderDetails ( $orderid, $details );
	else
		$result = - 1;
	
	echoRespnse ( 200, $result );
} );

$app->post ( '/follow', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'idmem',
			'idfollow' 
	) );
	
	$idmem = $app->request->post ( 'idmem' );
	$idfollow = $app->request->post ( 'idfollow' );
	
	$db = new DbHandler ();
	$result = $db->follow ( $idmem, $idfollow );
	
	echoRespnse ( 200, $result );
} );

$app->post ( '/unfollow', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'idmem',
			'idfollow'
	) );

	$idmem = $app->request->post ( 'idmem' );
	$idfollow = $app->request->post ( 'idfollow' );

	$db = new DbHandler ();
	$result = $db->unFollow ( $idmem, $idfollow );

	echoRespnse ( 200, $result );
} );

$app->get ( '/member/:id', function ($id) use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'account',
	) );
	
	$account = $app->request->get ( 'account' );
	
	$db = new DbHandler ();
	$result = $db->getMemberById ( $id, $account );
	
	echoRespnse ( 200, $result );
} );

$app->get ( '/member/:id/likedProduct', function ($id) use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'account',
		'start',
		'count',
	) );
	
	$account = $app->request->get ( 'account' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$db = new DbHandler ();
	$response = array ();
	$result = $db->getLikedProduct ( $id, $account, $start, $count );
	while ( $row = $result->fetch_assoc () ) {
		$row = calculateSaleOff ( $row );
		array_push ( $response, calculateSaleOff ( $row ) );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/member/:id/likedStyle', function ($id) use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'account',
		'start',
		'count',
	) );
	
	$account = $app->request->get ( 'account' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$db = new DbHandler ();
	$response = array ();
	$result = $db->getLikedStyle ( $id, $account, $start, $count );
	
	while ( $row = $result->fetch_assoc () ) {
		$r1 = new stdClass ();
		$r2 = new stdClass ();
		foreach ($row as $key => $value) {
			if ( substr ( $key, 0, 2 ) == 'a.' ) {
				$str = explode ( '.', $key );
				$var = $str[1];
				$r2->{$var} = $value;
			} else {
				$r1->{$key} = $value;
			}
		}
		$r1->account = $r2;
		array_push ( $response, $r1 );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/member/:id/reviews', function ($id) use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'start',
		'count',
	) );
	
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$db = new DbHandler ();
	$response = array ();
	$result = $db->getReviewById ( $id, $start, $count );
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/member/:id/follower', function ($id) use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'account',
		'start',
		'count',
	) );
	
	$account = $app->request->get ( 'account' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$db = new DbHandler ();
	$response = array ();
	$result = $db->getFollower ( $id, $account, $start, $count );
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/member/:id/following', function ($id) use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'account',
		'start',
		'count',
	) );
	
	$account = $app->request->get ( 'account' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$db = new DbHandler ();
	$response = array ();
	$result = $db->getFollowing ( $id, $account, $start, $count );
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/member/:id/likedProduct', function ($id) use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'account',
		'start',
		'count',
	) );
	
	$account = $app->request->get ( 'account' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$db = new DbHandler ();
	$response = array ();
	$result = $db->getFollowing ( $id, $account, $start, $count );
	while ( $row = $result->fetch_assoc () )
		array_push ( $response, $row );
	
	echoRespnse ( 200, $response );
} );

$app->post ( '/login', function () use($app) {
	authenticate ();
	$email = $app->request->post ( 'email' );
	$password = $app->request->post ( 'password' );
	
	$db = new DbHandler ();
	$response = array();
	
	$r1 = $db->isUserExists($email);
	if (false === $r1) {
		$response['code'] = 1;
		$response['message'] = 'Email not exists!';
		echoRespnse ( 400,  $response );
		return;
	}
	
	$r2 = $db->getUserLogin( $email, $password );
	$r3 = array();
	while ( $row = $r2->fetch_assoc () )
		array_push ( $r3, $row );
	if (empty($r3)) {
		$response['code'] = 2;
		$response['message'] = 'Password incorrect!';
		echoRespnse ( 400,  $response );
		return;
	}
	
	echoRespnse ( 200,  $r3[0] );
} );

$app->post ( '/register', function () use($app) {
	authenticate ();
	$name = $app->request->post ( 'name' );
	$email = $app->request->post ( 'email' );
	$password = $app->request->post ( 'password' );
	$gcmid = $app->request->post ( 'gcmid' );
	
	$db = new DbHandler ();
	$response = array();
	
	$r1 = $db->isUserExists($email);
	if (true === $r1) {
		$response['code'] = 3;
		$response['message'] = 'Email exists!!';
		echoRespnse ( 400,  $response );
		return;
	}
	
	$r2 = $db->registerUser ($name, $email, $password, $gcmid);
	echoRespnse ( 200,  $r2 );
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