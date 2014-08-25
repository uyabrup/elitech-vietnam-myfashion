package elitech.vietnam.myfashion.entities;

import com.google.gson.annotations.SerializedName;

public class Size {

	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_product")
	public int ProductID;
	
	@SerializedName("id_size")
	public int SizeID;
	
	@SerializedName("size")
	public String Size;
	
	@SerializedName("name")
	public String Name;
	
}
