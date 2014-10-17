<?php
require_once 'include/DbHandler.php';
require_once 'include/PassHash.php';
require_once 'include/MemoTracker.php';
require 'libs/Slim/Slim.php';

define ( 'API_KEY', '89a7f77b5a13635f5d6707d694c22a71' );
define ( 'GCM_KEY', 'AIzaSyC1tpZ_RPGgo9qtmCalRvLGO72hH9ozt-4' );
define ('DB_USERNAME', 'cam');
define ('DB_PASSWORD', '5678');
define ('DB_HOST', '127.0.0.1');
define ('DB_NAME', 'shopping');


class Notify {
	public $id;
	public $id_mem;
	public $id_sender;
	public $uid_sender;
	public $name_sender;
	public $id_owner;
	public $uid_owner;
	public $name_owner;
	public $content;
	public $id_post;
	public $image;
	public $type;
	public $cm_type;
	public $unread;
	public $date;
	public $status;
	
	public function __construct($id, $id_mem, $id_sender, $uid_sender, $name_sender, $id_owner, $uid_owner, $name_owner, $content, $id_post, $image, $type, $cm_type, $unread, $date, $status) {
		$this->id = $id;
		$this->id_mem = $id_mem;
		$this->id_sender = $id_sender;
		$this->uid_sender = $uid_sender;
		$this->name_sender = $name_sender;
		$this->id_owner = $id_owner;
		$this->uid_owner = $uid_owner;
		$this->name_owner = $name_owner;
		$this->content = $content;
		$this->id_post = $id_post;
		$this->image = $image;
		$this->type = $type;
		$this->cm_type = $cm_type;
		$this->unread = $unread;
		$this->date = $date;
		$this->status = $status;
	}
}


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

function SendNotification($message, $registrationIDs) {
	// Set POST variables
	$url = 'https://android.googleapis.com/gcm/send';
	
	$fields = array(
					'registration_ids'  => $registrationIDs,
					'data'              => array( "message" => $message ),
					);

	$headers = array( 
					'Authorization: key=' . GCM_KEY,
					'Content-Type: application/json'
					);

	// Open connection
	$ch = curl_init();

	// Set the url, number of POST vars, POST data
	curl_setopt( $ch, CURLOPT_URL, $url );

	curl_setopt( $ch, CURLOPT_POST, true );
	curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
	curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );

	curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields ) );

	curl_setopt ($ch, CURLOPT_CAINFO, dirname(__FILE__)."/cacert.pem");
	curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false);
	// Execute post
	$result = curl_exec($ch);

	// Close connection
	curl_close($ch);

	return $result;
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
		array_push ( $response, calculateSaleOff ( $row ) );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/inventory', function () use($app) {
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
	
	$result = $db->getInventory ( $account, $start, $count );
	
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, calculateSaleOff ( $row ) );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/cosmetics', function () {
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
	
	$result = $db->getStyler ( $account, $start, $count );
	
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
	$ip = $app->request->getIp();
	
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
		'account'
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
		'count'
	) );
	
	$account = $app->request->get ( 'account' );
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$db = new DbHandler ();
	$response = array ();
	$result = $db->getLikedProduct ( $id, $account, $start, $count );
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, calculateSaleOff ( $row ) );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/member/:id/likedStyle', function ($id) use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'account',
		'start',
		'count'
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
		'count'
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
		'count'
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
		'count'
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
	verifyRequiredParams ( array (
		'email',
		'password',
		'gcmid'
	) );
	$email = $app->request->post ( 'email' );
	$password = $app->request->post ( 'password' );
	$gcmid = $app->request->post ( 'gcmid' );
	
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
	
	$r4 = $db->updateGCMId($r3[0]['id'], $gcmid);
	if (false === $r4) {
		$response['code'] = 3;
		$response['message'] = 'Cannot update Google Cloud Message ID!';
		echoRespnse ( 400,  $response );
		return;
	}
	
	echoRespnse ( 200,  $r3[0] );
} );

$app->post ( '/register', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'name',
		'email',
		'password',
		'gcmid'
	) );
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

$app->post ( '/member/:id/status', function ($id) use($app) {
	authenticate ();
	$status = $app->request->post ( 'status' );
	
	$db = new DbHandler ();
	$rs = $db->updateMemberStatus($id, $status);
	echoRespnse ( 200,  $rs );
} );

$app->post ( '/member/:id/basicInfo', function ($id) use($app) {
	authenticate ();
	$nickname = $app->request->post ( 'nickname' );
	$gender = $app->request->post ( 'gender' );
	
	$db = new DbHandler ();
	$rs = $db->updateMemberBasicInfo($id, $nickname, $gender);
	echoRespnse ( 200,  $rs );
} );

$app->post ( '/member/:id/shippingAddress', function ($id) use($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'district',
		'city',
	) );
	$address = $app->request->post ( 'address' );
	$district = $app->request->post ( 'district' );
	$city = $app->request->post ( 'city' );
	$phone = $app->request->post ( 'phone' );
	
	$db = new DbHandler ();
	$rs = $db->updateMemberShippingAddress($id, $address, $district, $city, $phone);
	echoRespnse ( 200,  $rs );
} );

$app->put ( '/member/:id/avatar', function ($account) use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'image' 
	) );
	
	$image = $app->request->put ( 'image' );
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->changeAvatar ( $account, $image );
	
	echoRespnse ( 200, $result );
} );

$app->post ( '/member/:id/newStyle', function ($id) use($app) {
	authenticate ();
	verifyRequiredParams ( array (
		'title',
		'image'
	) );
	$title = $app->request->post ( 'title' );
	$image = $app->request->post ( 'image' );
	$content = $app->request->post ( 'content' );
	$ip = $_SERVER['REMOTE_ADDR'];
	$db = new DbHandler ();
	$rs = $db->newStyle($id, $title, $image, $content, $ip);
	
	echoRespnse ( 200,  $rs );
} );

$app->delete ( '/style/:id', function ($id) use($app) {
	authenticate ();
	
	$db = new DbHandler ();
	$rs = $db->deleteStyle($id);
	
	echoRespnse ( 200,  $rs );
} );

$app->put ( '/style/:id/content', function ($id) use($app) {
	authenticate ();
	
	$content = $app->request->put('content');
	
	$db = new DbHandler ();
	$rs = $db->updateStyleContent($id, $content);
	
	echoRespnse ( 200,  $rs );
} );

$app->get ( '/brands', function () use($app) {
	authenticate ();
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getBrands ();
	
	while ( $row = $result->fetch_assoc () ) {
		if ($row['brand'] != "")
			array_push ( $response, $row['brand'] );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/brand/:name/products', function ($name) use($app) {
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
	$result = $db->getBrandProduct ( $name, $account, $start, $count );
	
	// looping through result and preparing tasks array
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, calculateSaleOff ( $row ) );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/search', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'name',
			'account'
	) );
	
	$name = $app->request->get ( 'name' );
	$account = $app->request->get ( 'account' );
	
	$response = array ();
	$db = new DbHandler ();
	
	// fetching all best of day products
	$result = $db->searchProduct ( $name, $account );
	
	// looping through result and preparing tasks array
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, calculateSaleOff ( $row ) );
	}
	
	echoRespnse ( 200, $response );
} );

$app->get ( '/reviews', function () use($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'start',
			'count' 
	) );
	
	$start = $app->request->get ( 'start' );
	$count = $app->request->get ( 'count' );
	
	$response = array ();
	$db = new DbHandler ();
	
	$result = $db->getReviews ( $start, $count );
	
	while ( $row = $result->fetch_assoc () ) {
		array_push ( $response, $row );
	}
	
	echoRespnse ( 200, $response );
} );

$app->post ( '/forgotPassword', function () use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'email',
			'password' 
	) );
	$email = $app->request->post('email');
	
	$chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	$length = 6;
	
	$pwd1 = substr(str_shuffle($chars), 0, $length);
	
	$salt = "orchipro";
	$pwd2 = md5($pwd1 . $salt);
	
	$db = new DbHandler();
	$rs2 = $db->changePassword($email, $pwd1);
	
	$error = array();
	if ($rs2 < 1) {
		$error['code'] = 1;
		$error['message'] = 'Email not exists!';
		echoRespnse(400, $error);
		return;
	}
		
	$fields_string = "";
	$lang = "EN";
	$url = 'http://1.234.53.52/mail/shopping_forgetpass.php';
	$fields = array(
							'lang'  => urlencode($lang),
							'email' => urlencode($email),
							'pass' => urlencode($pwd1)
					);

	//url-ify the data for the POST
	foreach($fields as $key=>$value) { $fields_string .= $key.'='.$value.'&'; }
	rtrim($fields_string, '&');
	
	// Open connection
	$ch = curl_init();

	// Set the url, number of POST vars, POST data
	curl_setopt( $ch, CURLOPT_URL, $url );

	curl_setopt( $ch, CURLOPT_POST, true );
	// curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
	curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );

	curl_setopt( $ch, CURLOPT_POSTFIELDS, $fields_string );

	curl_setopt ($ch, CURLOPT_CAINFO, dirname(__FILE__)."/cacert.pem");
	
	curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false);
	// Execute post
	$result = curl_exec($ch);

	//close connection
	curl_close($ch);
	
	echoRespnse(200, 1);
} );

$app->post ('/notifyAddFriend', function () use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'member',
			'friend' 
	) );

	$member = $app->request->post('member');
	$friend = $app->request->post('friend');
	
	$conn = mysql_connect(DB_HOST, DB_USERNAME, DB_PASSWORD) or
		die("Khong ket noi dc Database !");
	mysql_select_db(DB_NAME);
	mysql_query("SET NAMES 'utf8'");
	
	$str = "SELECT		a.`id`			AS	`id_mem`,
						a.`gcm_id`,
						a1.`id`			AS	`id_sender`,
						a1.`name`		AS	`uid_sender`,
						a1.`nick_name`	AS	`name_sender`,
						a1.`image`		AS	`image`,
						a1.`id`			AS	`id_owner`,
						a1.`name`		AS	`uid_owner`,
						a1.`nick_name`	AS	`name_owner`,
						f.`create_date`	AS	`date`
			FROM		(	SELECT	*
							FROM	`follower`
							WHERE	`id_mem`=$member	AND
									`id_follow`=$friend	)	f
			INNER JOIN	`account` a
			ON 			a.`id`=f.`id_follow`
			INNER JOIN	`account` a1
			ON			a1.`id`=f.`id_mem`
			GROUP BY	a.`id`
			HAVING		a.`id`<>a1.`id`;";
	
	$rs1 = mysql_query($str);
	if (mysql_num_rows($rs1)>0) {
		$r = mysql_fetch_assoc($rs1);
		$id_mem = $r['id_mem'];
		$id_sender = $r['id_sender'];
		$uid_sender = $r['uid_sender'];
		$name_sender = $r['name_sender'];
		$id_owner = $r['id_owner'];
		$uid_owner = $r['uid_owner'];
		$name_owner = $r['name_owner'];
		$content = "";
		$id_post = 0;
		$image = $r['image'];
		$type = 4;
		$unread = 1;
		$date = $r['date'];
		$status = 1;
		
		$str = "INSERT INTO	`notify`	(`id_mem`, `id_sender`, `uid_sender`, `name_sender`, `id_owner`, `uid_owner`, `name_owner`, `content`, `id_post`, `image`, `type`, `cm_type`, `unread`, `date`, `status`)
				VALUES					('$id_mem', '$id_sender', '$uid_sender', '$name_sender', '$id_owner', '$uid_owner', '$name_owner', '$content', '$id_post', '$image', '$type', '0', '$unread', '$date', '$status');";
				
		$rs2 = mysql_query($str);
		if ($rs2==TRUE) {
			$ids = array();
			$ids[] = $r['gcm_id'];
			$id1 = mysql_insert_id();
			$notify = new Notify($id1, $id_mem, $id_sender, $uid_sender, $name_sender, $id_owner, $uid_owner, $name_owner, $content, $id_post, $image, $type, $cmtype, $unread, $date, $status);
			$mess = json_encode($notify);
			SendNotification($mess, $ids);
		}
	}
	
	mysql_close($conn);
	echoRespnse(200, 1);
} );

$app->post ( '/notifyPostFriend', function () use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'post'
	) );

	$idpost = $app->request->post('post');
	
	$conn = mysql_connect(DB_HOST, DB_USERNAME, DB_PASSWORD) or
		die("Khong ket noi dc Database !");
	mysql_select_db(DB_NAME);
	mysql_query("SET NAMES 'utf8'");
	
	$str = "SELECT		a.`id`			AS	`id_mem`,
						a.`gcm_id`,
						a1.`id`			AS	`id_sender`,
						a1.`name`		AS	`uid_sender`,
						a1.`nick_name`	AS	`name_sender`,
						a1.`image`	AS	`image`,
						a1.`id`			AS	`id_owner`,
						a1.`name`		AS	`uid_owner`,
						a1.`nick_name`	AS	`name_owner`,
						p.`create_day`	AS	`date`
			FROM		`post` p
			INNER JOIN 	`follower` f
			ON			f.`id_follow`=p.`id_account`
			INNER JOIN 	`account` a
			ON			a.`id`=f.`id_mem`
			INNER JOIN 	`account` a1
			ON			a1.`id`=p.`id_account`
			WHERE		p.`id`=$idpost
			GROUP BY	a.`id`
			HAVING		a.`id`<>a1.`id`;";
			
	$rs1 = mysql_query($str);
	if (mysql_num_rows($rs1)>0) {
		while ($r = mysql_fetch_assoc($rs1)) {
			$id_mem = $r['id_mem'];
			$id_sender = $r['id_sender'];
			$uid_sender = $r['uid_sender'];
			$name_sender = $r['name_sender'];
			$id_owner = $r['id_owner'];
			$uid_owner = $r['uid_owner'];
			$name_owner = $r['name_owner'];
			$content = "";
			$id_post = $idpost;
			$image = $r['image'];
			$type = 3;
			$unread = 1;
			$date = $r['date'];
			$status = 1;
			
			$str = "INSERT INTO	`notify`	(`id_mem`, `id_sender`, `uid_sender`, `name_sender`, `id_owner`, `uid_owner`, `name_owner`, `content`, `id_post`, `image`, `type`, `cm_type`, `unread`, `date`, `status`)
					VALUES					('$id_mem', '$id_sender', '$uid_sender', '$name_sender', '$id_owner', '$uid_owner', '$name_owner', '$content', '$id_post', '$image', '$type', '0', '$unread', '$date', '$status');";
					
			$rs2 = mysql_query($str);
			if ($rs2==TRUE) {
				$ids = array();
				$ids[] = $r['gcm_id'];
				$id1 = mysql_insert_id();
				$notify = new Notify($id1, $id_mem, $id_sender, $uid_sender, $name_sender, $id_owner, $uid_owner, $name_owner, $content, $id_post, $image, $type, $cmtype, $unread, $date, $status);
				$mess = json_encode($notify);
				SendNotification($mess, $ids);
			}
		}
	}
	
	mysql_close($conn);
	echoRespnse(200, 1);
} );

$app->post ( '/notifyComment', function () use ($app) {
	authenticate ();
	verifyRequiredParams ( array (
			'comment',
			'type'
	) );
	
	$idcomment = $app->request->post('comment');
	$cmtype = $app->request->post('type');
	
	$conn = mysql_connect(DB_HOST, DB_USERNAME, DB_PASSWORD) or
		die("Khong ket noi dc Database !");
	mysql_select_db(DB_NAME);
	mysql_query("SET NAMES 'utf8'");
	
	$str = "";
	if ($cmtype==1) {
		$str = "SELECT		c.`content`,
							a.`id`			AS	`id_mem`,
							a.`gcm_id`,
							a1.`id`			AS	`id_sender`,
							a1.`name`		AS	`uid_sender`,
							a1.`nick_name`	AS	`name_sender`,
							a1.`image`,
							p.`id_category`	AS	`id_owner`,
							p.`name`		AS	`uid_owner`,
							p.`name`		AS	`name_owner`,
							c1.`id_product`	AS	`id_post`,
							c.`date`
				FROM		`comment` c
				INNER JOIN	`product` p
				ON			p.`id`=c.`id_product`
				INNER JOIN	`comment` c1
				ON			c1.`id_product`=c.`id_product`	AND
							c1.`status`=1
				INNER JOIN	`account` a
				ON			a.`id`=c1.`id_account`	OR
							a.`id`=12
				INNER JOIN	`account` a1
				ON			a1.`id`=c.`id_account`
				WHERE		c.`id`=$idcomment
				GROUP BY	a.`id`
				HAVING		a.`id`<>a1.`id`;";
		
		$rs4 = mysql_query($str);
		if (mysql_num_rows($rs4)>0) {
			while ($rs5 = mysql_fetch_assoc($rs4)) {
				$id_mem = $rs5['id_mem'];
				$id_sender = $rs5['id_sender'];
				$uid_sender = $rs5['uid_sender'];
				$name_sender = $rs5['name_sender'];
				$id_owner = $rs5['id_owner'];
				$uid_owner = $rs5['uid_owner'];
				$name_owner = $rs5['name_owner'];
				$content = $rs5['content'];
				$id_post = $rs5['id_post'];
				$image = $rs5['image'];
				$type = 5;
				$unread = 1;
				$date = $rs5['date'];
				$status = 1;
				
				$str = "INSERT INTO	`notify`	(`id_mem`, `id_sender`, `uid_sender`, `name_sender`, `id_owner`, `uid_owner`, `name_owner`, `content`, `id_post`, `image`, `type`, `cm_type`, `unread`, `date`, `status`)
						VALUES					('$id_mem', '$id_sender', '$uid_sender', '$name_sender', '$id_owner', '$uid_owner', '$name_owner', '$content', '$id_post', '$image', '$type', '$cmtype', '$unread', '$date', '$status');";
						
				$rs6 = mysql_query($str);
				if ($rs6==TRUE) {
					$ids = array();
					$ids[] = $rs5['gcm_id'];
					$id1 = mysql_insert_id();
					$notify = new Notify($id1, $id_mem, $id_sender, $uid_sender, $name_sender, $id_owner, $uid_owner, $name_owner, $content, $id_post, $image, $type, $cmtype, $unread, $date, $status);
					$mess = json_encode($notify);
					SendNotification($mess, $ids);
				}
			}
		}
	}
	
	if ($cmtype==2) {
		$str = "SELECT		c1.`content`,
							a.`id`			AS	`id_mem`,
							a.`gcm_id`,
							a1.`id`			AS	`id_sender`,
							a1.`name`		AS	`uid_sender`,
							a1.`nick_name`	AS	`name_sender`,
							a1.`image`,
							a2.`id`			AS	`id_owner`,
							a2.`name`		AS	`uid_owner`,
							a2.`nick_name`	AS	`name_owner`,
							c1.`id_product`	AS	`id_post`,
							c.`date`
				FROM		`comment` c
				INNER JOIN	`comment` c1
				ON			c1.`id_product`=c.`id_product`
				INNER JOIN	`account` a1
				ON			a1.`id`=c1.`id_account`
				INNER JOIN	`post` p
				ON			p.`id`=c1.`id_product`
				INNER JOIN	`account` a2
				ON			a2.`id`=p.`id_account`
				INNER JOIN	`account` a
				ON			(a.`id`=p.`id_account`)
				WHERE		c1.`id`=$idcomment
				GROUP BY	a.`id`
				HAVING		a.`id`<>a1.`id`;";
				
		$rs4 = mysql_query($str);
		if (mysql_num_rows($rs4)>0) {
			$rs5 = mysql_fetch_assoc($rs4);
			$id_mem = $rs5['id_mem'];
			$id_sender = $rs5['id_sender'];
			$uid_sender = $rs5['uid_sender'];
			$name_sender = $rs5['name_sender'];
			$id_owner = $rs5['id_owner'];
			$uid_owner = $rs5['uid_owner'];
			$name_owner = $rs5['name_owner'];
			$content = $rs5['content'];
			$id_post = $rs5['id_post'];
			$image = $rs5['image'];
			$type = 1;
			$unread = 1;
			$date = $rs5['date'];
			$status = 1;
			
			$str = "INSERT INTO	`notify`	(`id_mem`, `id_sender`, `uid_sender`, `name_sender`, `id_owner`, `uid_owner`, `name_owner`, `content`, `id_post`, `image`, `type`, `cm_type`, `unread`, `date`, `status`)
					VALUES					('$id_mem', '$id_sender', '$uid_sender', '$name_sender', '$id_owner', '$uid_owner', '$name_owner', '$content', '$id_post', '$image', '$type', '$cmtype', '$unread', '$date', '$status');";
					
			$rs6 = mysql_query($str);
			if ($rs6==TRUE) {
				$ids = array();
				$ids[] = $rs5['gcm_id'];
				$id1 = mysql_insert_id();
				$notify = new Notify($id1, $id_mem, $id_sender, $uid_sender, $name_sender, $id_owner, $uid_owner, $name_owner, $content, $id_post, $image, $type, $cmtype, $unread, $date, $status);
				$mess = json_encode($notify);
				SendNotification($mess, $ids);
			}
		}
		
		$str = "SELECT		c.`content`,
							a.`id`			AS	`id_mem`,
							a.`gcm_id`,
							a1.`id`			AS	`id_sender`,
							a1.`name`		AS	`uid_sender`,
							a1.`nick_name`	AS	`name_sender`,
							a1.`image`,
							a2.`id`			AS	`id_owner`,
							a2.`name`		AS	`uid_owner`,
							a2.`nick_name`	AS	`name_owner`,
							c1.`id_product`	AS	`id_post`,
							c1.`date`
				FROM		`comment` c
				INNER JOIN	`post` p
				ON			p.`id`=c.`id_product`
				INNER JOIN	`comment` c1
				ON			c1.`id_product`=c.`id_product`	AND
							c1.`id_account`<>p.`id_account`	AND
							c1.`status`=1
				INNER JOIN	`account` a
				ON			a.`id`=c1.`id_account`
				INNER JOIN	`account` a1
				ON			a1.`id`=c.`id_account`
				INNER JOIN	`account` a2
				ON			a2.`id`=p.`id_account`
				WHERE		c.`id`=$idcomment
				GROUP BY	a.`id`
				HAVING		a.`id`<>a1.`id`;";
		
		$rs4 = mysql_query($str);
		if (mysql_num_rows($rs4)>0) {
			while ($rs5 = mysql_fetch_assoc($rs4)) {
				$id_mem = $rs5['id_mem'];
				$id_sender = $rs5['id_sender'];
				$uid_sender = $rs5['uid_sender'];
				$name_sender = $rs5['name_sender'];
				$id_owner = $rs5['id_owner'];
				$uid_owner = $rs5['uid_owner'];
				$name_owner = $rs5['name_owner'];
				$content = $rs5['content'];
				$id_post = $rs5['id_post'];
				$image = $rs5['image'];
				$type = 2;
				$unread = 1;
				$date = $rs5['date'];
				$status = 1;
				
				$str = "INSERT INTO	`notify`	(`id_mem`, `id_sender`, `uid_sender`, `name_sender`, `id_owner`, `uid_owner`, `name_owner`, `content`, `id_post`, `image`, `type`, `cm_type`, `unread`, `date`, `status`)
						VALUES					('$id_mem', '$id_sender', '$uid_sender', '$name_sender', '$id_owner', '$uid_owner', '$name_owner', '$content', '$id_post', '$image', '$type', '$cmtype', '$unread', '$date', '$status');";
						
				$rs6 = mysql_query($str);
				if ($rs6==TRUE) {
					$ids = array();
					$ids[] = $rs5['gcm_id'];
					$id1 = mysql_insert_id();
					$notify = new Notify($id1, $id_mem, $id_sender, $uid_sender, $name_sender, $id_owner, $uid_owner, $name_owner, $content, $id_post, $image, $type, $cmtype, $unread, $date, $status);
					$mess = json_encode($notify);
					SendNotification($mess, $ids);
				}
			}
		}
	}
	
	mysql_close($conn);
	echoRespnse ( 200, 1);
} );

$app->post ( '/addNotice', function () use ($app) {
	authenticate();
	verifyRequiredParams ( array (
			'product',
			'account'
	) );
	
	$id_product = $app->request->post ('product');
	$idaccount = $app->request->post ('account');
	
	$conn = mysql_connect(DB_HOST, DB_USERNAME, DB_PASSWORD) or
		die("Khong ket noi dc Database !");
	mysql_select_db(DB_NAME);
	mysql_query("SET NAMES 'utf8'");
	
	$sql="SELECT c.id_account from `comment` c WHERE c.id_product='$id_product' GROUP BY c.id_account";
	$result=mysql_query($sql);
	while ($row=mysql_fetch_array($result)){
		if($idaccount!=$row['id_account'])
			mysql_query("INSERT INTO `notice` (`id_account`, `id_product`, `datetime`, `id_writer`) VALUES ('".$row['id_account']."', '$id_product', NOW(), '$idaccount')");
	}
	mysql_close($conn);
	echoRespnse ( 200, 1);
} );


$app->post ( '/addMemoTrack', function () use ($app) {
	authenticate();
	
	$idaccount = $app->request->post ( 'idaccount' );
	$email = $app->request->post ( 'email' );
	$version = $app->request->post ( 'version' );
	$ip = $app->request->getIp();
	$date = date("Y-m-d H:i:s");
	
	$db = new MemoTracker();
	$res = $db->addTrack($idaccount, $email, $ip, $date, $version);
	echoRespnse(200, $res);
} );


/**
 * Test method
 */
$app->post ( '/test', function () use($app) {
	$db = new DbHandler ();
	
	echoRespnse ( 200, $db->updateStyleContent(896, 'no content'));
} );



$app->run ();
?>