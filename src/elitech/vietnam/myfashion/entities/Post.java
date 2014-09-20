package elitech.vietnam.myfashion.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

public class Post {
	
	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_account")
	public int IdAccount;
	
	@SerializedName("date")
	public String Date;
	
	@SerializedName("title")
	public String Title;
	
	@SerializedName("account")
	public Member Account;
//	
//	@SerializedName("avatar")
//	public String Avatar;
//	
//	@SerializedName("profile")
//	public String Profile;
//
//	@SerializedName("name")
//	public String Name;
//
//	@SerializedName("nick_name")
//	public String NickName;
//	
	@SerializedName("image")
	public String image_url;
//	
//	@SerializedName("followed")
//	public int Followed;
//	
//	@SerializedName("follow_count")
//	public int FollowCount;
//	
	@SerializedName("content")
	public String Content;
	
	@SerializedName("create_day")
	public String CreatedDate;
		
	@SerializedName("likes")
	public int Likes;
	
	@SerializedName("liked")
	public int Liked;
	
	@SerializedName("comments")
	public int Comments;
	
	@SerializedName("ip")
	public String Ip;
	
	public Date Date() {
		if (Date == null || Date.equals(""))
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(Date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Date CreatedDate() {
		if (CreatedDate == null || CreatedDate.equals(""))
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(CreatedDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean Liked() {
		if (Liked == 1)
			return true;
		return false;
	}
}
