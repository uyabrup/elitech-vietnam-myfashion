/**
 * Aug 5, 2014 10:54:11 AM
 */
package elitech.vietnam.myfashion.wsclient;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import elitech.vietnam.myfashion.entities.Category;
import elitech.vietnam.myfashion.entities.City;
import elitech.vietnam.myfashion.entities.Color;
import elitech.vietnam.myfashion.entities.Comment;
import elitech.vietnam.myfashion.entities.District;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.Post;
import elitech.vietnam.myfashion.entities.Ship;
import elitech.vietnam.myfashion.entities.ShipMore;
import elitech.vietnam.myfashion.entities.Size;
import elitech.vietnam.myfashion.entities.TradeMark;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.ProductDetail;
import elitech.vietnam.myfashion.entities.Review;

/**
 * @author Cong
 */
public interface Services {

	@GET("/bestOfDay")
	void getBestOfDay(@Query("day") String day, @Query("account") int account, @Query("start") int start,
			@Query("count") int count, Callback<List<Product>> callback);
	
	@GET("/trademarks")
	void getTradeMarks(Callback<List<TradeMark>> callback);
	
	@FormUrlEncoded
	@POST("/doLikes")
	void doLikes(@Field("product") int product, @Field("account") int account, @Field("liked") int liked,
			@Field("type") int type, Callback<Integer> callback);
	
	@FormUrlEncoded
	@POST("/login")
	void login(@Field("email") String username, @Field("password") String password, Callback<Member> callback);
	
	@GET("/product/{id}/details")
	void getProductDetails(@Path("id") int id, Callback<List<ProductDetail>> callback);
	
	@GET("/product/{id}/comments")
	void getProductCommnets(@Path("id") int id, @Query("start") int start, @Query("count") int count, @Query("type") int type, Callback<List<Comment>> callback);
	
	@GET("/product/{id}/review")
	void getProductReviews(@Path("id") int id, @Query("start") int start, @Query("count") int count, Callback<List<Review>> callback);
	
	@GET("/category")
	void getCategoryByFashion(@Query("fashion") int fashion, Callback<List<Category>> callback);
	
	@FormUrlEncoded
	@POST("/doComment")
	void doComment(@Field("product") int product, @Field("account") int account, @Field("content") String content,
			@Field("type") int type, Callback<Integer> callback);
	
	@GET("/category/{id}/products")
	void getCategoryProduct(@Path("id") int category, @Query("account") int account, @Query("fashion") int fashion, @Query("start") int start, @Query("count") int count, Callback<List<Product>> callback);
	
	@GET("/product/{id}/sizes")
	void getProductSize(@Path("id") int product, Callback<List<Size>> callback);
	
	@GET("/product/{id}/colors")
	void getProductColor(@Path("id") int product, Callback<List<Color>> callback);
	
	@GET("/cities")
	void getCities(Callback<List<City>> callback);
	
	@GET("/districts")
	void getDistricts(Callback<List<District>> callback);
	
	@FormUrlEncoded
	@POST("/devices")
	void storeDevice(@Field("device") String device, @Field("version") String version, @Field("api") int api, @Field("user") String user, @Field("day") String day, @Field("gcmid") String gcm, Callback<Integer> callback);
	
	@GET("/ships")
	void getShip(Callback<List<Ship>> callback);
	
	@GET("/shipmore")
	void getShipMore(Callback<List<ShipMore>> callback);
	
	@FormUrlEncoded
	@POST("/order")
	void addOrder(@Field("account") int account, @Field("email") String email, @Field("name") String name, @Field("address") String address, @Field("city") String city, @Field("state") String state, @Field("phone") String phone, @Field("payment") int payment, @Field("ship") double ship, @Field("shipprice") double shipprice, @Field("memo") String memo, @Field("detail") String detail, Callback<Integer> callback);
	
	@GET("/styler")
	void getStylerBest(@Query("account") int account, @Query("start") int start, @Query("count") int count, Callback<List<Post>> callback);
	
	@FormUrlEncoded
	@POST("/follow")
	void follow(@Field("idmem") int member, @Field("idfollow") int follower, Callback<Integer> callback);
	
	@FormUrlEncoded
	@POST("/unfollow")
	void unFollow(@Field("idmem") int member, @Field("idfollow") int follower, Callback<Integer> callback);
	
	@GET("/member/{id}")
	void getMemberById(@Path("id") int member, @Query("account") int account, Callback<Member> callback);
	
	@GET("/member/{id}/style")
	void getStyleById(@Path("id") int member, @Query("account") int account, @Query("start") int start, @Query("count") int count, Callback<List<Post>> callback);
	
	@GET("/member/{id}/likedProduct")
	void getLikedProduct(@Path("id") int member, @Query("account") int account, @Query("start") int start, @Query("count") int count, Callback<List<Product>> callback);
	
	@GET("/member/{id}/likedStyle")
	void getLikedStyle(@Path("id") int member, @Query("account") int account, @Query("start") int start, @Query("count") int count, Callback<List<Post>> callback);
	
	@GET("/member/{id}/reviews")
	void getReviewById(@Path("id") int member, @Query("start") int start, @Query("count") int count, Callback<List<Review>> callback);
	
	@GET("/member/{id}/follower")
	void getFollower(@Path("id") int member, @Query("account") int account, @Query("start") int start, @Query("count") int count, Callback<List<Member>> callback);
	
	@GET("/member/{id}/following")
	void getFollowing(@Path("id") int member, @Query("account") int account, @Query("start") int start, @Query("count") int count, Callback<List<Member>> callback);
}
