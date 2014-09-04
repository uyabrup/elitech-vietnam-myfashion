package elitech.vietnam.myfashion.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

public class Order {
//	public int TotalPrice;
	public List<OrderDetail> ListDetail = new ArrayList<OrderDetail>();
	public String Date;
	public double ShippingFee;
	
	@SerializedName("id")
	public int Id;
	
	@SerializedName("code_order")
	public String OrderCode;
	
	@SerializedName("id_account")
	public int AccountID;
	
	@SerializedName("total")
	public double Total;
	
	@SerializedName("email")
	public String Email;
	
	@SerializedName("name")
	public String Name;
	
	@SerializedName("address")
	public String Address;
	
	@SerializedName("city")
	public String City;
	
	@SerializedName("state")
	public String State;
	
	@SerializedName("phone")
	public String Phone;
	
	@SerializedName("payment")
	public int Payment;
	
	@SerializedName("ship")
	public double Ship;
	
	@SerializedName("memo")
	public String Memo;
	
	@SerializedName("date")
	public String date;

	@SerializedName("status")
	public int Status = -100;
	
	@SerializedName("is_delete")
	public int isDelete;
	
	public Date Date() {
		if (date == null || date.equals(""))
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
