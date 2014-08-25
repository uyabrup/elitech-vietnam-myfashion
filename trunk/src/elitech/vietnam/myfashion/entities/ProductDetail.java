package elitech.vietnam.myfashion.entities;

import com.google.gson.annotations.SerializedName;

public class ProductDetail {

	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_product")
	public int IdProduct;
	
	@SerializedName("image")
	public String Image;
	
	@SerializedName("pos")
	public int Pos;
	
	@SerializedName("content")
	public String Content;
}
