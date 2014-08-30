package elitech.vietnam.myfashion.entities;

import com.google.gson.annotations.SerializedName;

public class City {

	@SerializedName("id")
	public int Id;
	
	@SerializedName("name")
	public String Name;
	
	@SerializedName("code")
	public String Code;
	
	@SerializedName("pos")
	public int Order;
	
}
