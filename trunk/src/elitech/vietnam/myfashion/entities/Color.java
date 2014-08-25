package elitech.vietnam.myfashion.entities;

import com.google.gson.annotations.SerializedName;

public class Color {

	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_product")
	public int ProductID;
	
	@SerializedName("id_color")
	public int ColorID;
	
	@SerializedName("color")
	public String Color;
	
	@SerializedName("nameEN")
	public String NameEN;
	
	@SerializedName("nameKR")
	public String NameKR;
	
	@SerializedName("nameVN")
	public String NameVN;
	
}
