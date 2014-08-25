package elitech.vietnam.myfashion.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

public class Product {
	public String Discount;
	public int Sold;
	public Color Color;
	public Size Size;
	public List<Size> SizeList = new ArrayList<>();
	public List<Color> ColorList = new ArrayList<>();
	public List<ProductDetail> DetailList = new ArrayList<>();
	public List<Comment> CommentList = new ArrayList<>();
	
	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_category")
	public int CategoryID;
	
	@SerializedName("category_name_en")
	public String CategoryNameEN;
	
	@SerializedName("category_name_kr")
	public String CategoryNameKR;
	
	@SerializedName("category_name_vn")
	public String CategoryNameVN;
	
	@SerializedName("date")
	public String Date;
	
	@SerializedName("brand")
	public String Brand;
	
	@SerializedName("end_date")
	public String EndDate;
	
	@SerializedName("time")
	public int Time;
	
	@SerializedName("name")
	public String Name;
	
	@SerializedName("url")
	public String Url;
	
	@SerializedName("price")
	public int Price;
	
	@SerializedName("price_vn")
	public int PriceVN;
	
	@SerializedName("price_sale")
	public int PriceSale;
	
	@SerializedName("sale_off")
	public int SaleOff;
	
	@SerializedName("sale_start")
	public String SaleStart;
	
	@SerializedName("sale_end")
	public String SaleEnd;
	
	@SerializedName("quantity")
	public int Quantity;
	
	@SerializedName("image")
	public String Image;
	
	@SerializedName("content")
	public String Content;
	
	@SerializedName("promotion")
	public String Promotion;
	
	@SerializedName("create_day")
	public String CreatedDay;
	
	@SerializedName("likes")
	public int Likes;
	
	@SerializedName("liked")
	public int Liked;
	
	@SerializedName("comments")
	public int Comments;
	
	@SerializedName("color")
	public String CategoryColor;
	
	@SerializedName("status")
	public int Status;
	
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
		if (CreatedDay == null || CreatedDay.equals(""))
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(CreatedDay);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Date EndDate() {
		if (EndDate == null || EndDate.equals(""))
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(EndDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Date SaleStart() {
		if (SaleStart == null || SaleStart.equals(""))
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(SaleStart);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Date SaleEnd() {
		if (SaleEnd == null || SaleEnd.equals(""))
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(SaleEnd);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean Liked() {
		return Liked > 0;
	}
}
