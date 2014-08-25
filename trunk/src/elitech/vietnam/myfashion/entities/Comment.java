package elitech.vietnam.myfashion.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

public class Comment {

	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_account")
	public int AccountID;
	
	@SerializedName("name")
	public String Name;
	
	@SerializedName("nick_name")
	public String NickName;
	
	@SerializedName("image")
	public String Image;
	
	@SerializedName("id_product")
	public int ProductID;
	
	@SerializedName("content")
	public String Content;
	
	@SerializedName("date")
	public String Date;
	
	@SerializedName("type")
	public int Type;
	
	public Date Date() {
		if (Date == null || Date.equals(""))
			return null;
		try {
			return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault()).parse(Date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
