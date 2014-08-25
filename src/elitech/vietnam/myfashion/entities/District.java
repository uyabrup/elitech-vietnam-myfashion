package elitech.vietnam.myfashion.entities;

import com.google.gson.annotations.SerializedName;

public class District {

	@SerializedName("id")
	public int Id;
	
	@SerializedName("name")
	public String Name;
	
	@SerializedName("MaTinhThanh")
	public int ProvinceID;
	
	@SerializedName("id_city")
	public int Publish;
	
	@SerializedName("pos")
	public int Order;
	
	@SerializedName("ship")
	public double Ship;
	
}
