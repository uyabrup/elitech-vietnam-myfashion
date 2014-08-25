package elitech.vietnam.myfashion.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

public class Notify {

	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_mem")
	public int MemID;
	
	@SerializedName("id_sender")
	public int SenderID;
	
	@SerializedName("uid_sender")
	public String SenderUID;
	
	@SerializedName("name_sender")
	public String SenderName;
	
	@SerializedName("id_owner")
	public int OwnerID;
	
	@SerializedName("uid_owner")
	public String OwnerUID;
	
	@SerializedName("name_owner")
	public String OwnerName;
	
	@SerializedName("content")
	public String Content;
	
	@SerializedName("id_post")
	public int PostID;
	
	@SerializedName("image")
	public String Image;
	
	@SerializedName("type")
	public int Type;

	@SerializedName("cm_type")
	public int CommentType;
	
	@SerializedName("unread")
	public int Unread;
	
	@SerializedName("date")
	public String Date;
	
	@SerializedName("status")
	public int Status;

	public boolean	isNew;
	
	public Date Date() {
		if (Date == null || Date.equals(""))
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(Date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean Unread() {
		return (Unread>0);
	}
	
	public boolean Status() {
		return (Status>0);
	}
}
