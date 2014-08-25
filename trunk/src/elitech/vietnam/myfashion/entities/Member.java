package elitech.vietnam.myfashion.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

public class Member {

	@SerializedName("id")
	public int Id;
	
	@SerializedName("name")
	public String Name;
	
	@SerializedName("nick_name")
	public String NickName;
	
	@SerializedName("email")
	public String Email;
	
	@SerializedName("password")
	public String Password;
	
	@SerializedName("image")
	public String Image;
	
	@SerializedName("profile")
	public String Profile;
	
	@SerializedName("area")
	public String Area;
	
	@SerializedName("address")
	public String Address;
	
	@SerializedName("city")
	public int City;
	
	@SerializedName("district")
	public int District;
	
	@SerializedName("phone")
	public String Phone;
	
	@SerializedName("join_day")
	public String JoinedDate;
	
	@SerializedName("follow_count")
	public int FollowCount;
	
	@SerializedName("followed")
	public int Followed;
	
	@SerializedName("gcm_id")
	public String GCMID;
	
	public Date JoinedDate() {
		if (JoinedDate == null || JoinedDate.equals(""))
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(JoinedDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean Followed() {
		return (Followed>0);
	}
}
