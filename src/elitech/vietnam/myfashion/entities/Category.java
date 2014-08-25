package elitech.vietnam.myfashion.entities;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Category {

	@SerializedName("child_categories")
	public List<Category> subCategories = new ArrayList<>();
	
	public boolean isTrademark = false;
	
	@SerializedName("id")
	public int Id;
	
	@SerializedName("id_parent")
	public int ParentID;
	
	@SerializedName("id_trademarks")
	public int TrademarksID;
	
	@SerializedName("tm_name")
	public String TrademarksName;
	
	@SerializedName("tm_image")
	public String TrademarksImage;
	
	@SerializedName("nameEN")
	public String NameEN;
	
	@SerializedName("nameKR")
	public String NameKR;
	
	@SerializedName("nameVN")
	public String NameVN;
	
	@SerializedName("commission")
	public double Commission;
	
	@SerializedName("image")
	public String Image;
	
	@SerializedName("content")
	public String Content;
	
	@SerializedName("color")
	public String Color;
	
	@SerializedName("status")
	public int Status;
	
	@SerializedName("num_product")
	public int ProductCount;
	
	@SerializedName("male_fashion")
	public int MaleCount;
	
	@SerializedName("female_fashion")
	public int FemaleCount;
	
	@SerializedName("office_fashion")
	public int OfficeCount;
}
