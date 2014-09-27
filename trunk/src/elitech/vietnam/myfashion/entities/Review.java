/**
 * Jul 15, 2014 9:09:49 AM
 */
package elitech.vietnam.myfashion.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

/**
 * @author Cong
 *
 */
public class Review {

	public ArrayList<String> Images;
	
	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_product")
	public int ProductId;
	
	@SerializedName("id_account")
	public int AccountId;
	
	@SerializedName("title")
	public String Title;
	
	@SerializedName("content")
	public String Content;
	
	@SerializedName("rate")
	public int Rate;
	
	@SerializedName("views")
	public int View;
	
	@SerializedName("date")
	public String Date;
	
	@SerializedName("status")
	public int Status;
	
	@SerializedName("send_mail")
	public int SendMail;
	
	@SerializedName("new")
	public int New;
	
	@SerializedName("aname")
	public String MemName;
	
	@SerializedName("anickname")
	public String MemNickName;
	
	@SerializedName("aimage")
	public String MemImage;
	
	@SerializedName("pimage")
	public String ProductImage;
	
	@SerializedName("pname")
	public String ProductName;
	
	public Date Date() {
		if (Date == null || Date.equals(""))
			return null;
		try {
			return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(Date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
