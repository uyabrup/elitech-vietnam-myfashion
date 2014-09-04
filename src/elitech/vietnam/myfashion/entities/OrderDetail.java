package elitech.vietnam.myfashion.entities;

import com.google.gson.annotations.SerializedName;

public class OrderDetail {
	
	public int SaleOff;
	public double getAmountVN(){
		return PriceVN*Quantity;
	}
	public double getAmount(){
		return Price*Quantity;
	}
	public String CategotyEN;
	
	@SerializedName("product_image")
	public String Image;
	
	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_order")
	public int OrderID;
	
	@SerializedName("id_product")
	public int ProductID;
	
	@SerializedName("product_name")
	public String ProductName;
	
	@SerializedName("color")
	public int Color;

	@SerializedName("name_color_en")
	public String ColorNameEN;
	
	@SerializedName("name_color_kr")
	public String ColorNameKR;
	
	@SerializedName("name_color_vn")
	public String ColorNameVN;

	@SerializedName("size")
	public int Size;
	
	@SerializedName("name_size")
	public String SizeName;

	@SerializedName("price")
	public int Price;
	
	@SerializedName("priceVN")
	public int PriceVN;
	
	@SerializedName("commission")
	public double Commission;
	
	@SerializedName("quantity")
	public int Quantity;
	
	@SerializedName("category_name_en")
	public String CategoryNameEN;
	
	@SerializedName("category_name_kr")
	public String CategoryNameKR;
	
	@SerializedName("category_name_VN")
	public String CategoryNameVN;
	
	@SerializedName("status")
	public int Status = 1;
	
	public float Weight;
}
