/**
 * Jul 28, 2014 3:28:41 PM
 */
package elitech.vietnam.myfashion.entities;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Cong
 *
 */
public class Cosmetic {
	
	public Cosmetic(int id, String name, int groupId, String icon) {
		Id = id;
		Name = name;
		GroupId = groupId;
		Icon = icon;
	}

	@SerializedName("id")
	public int Id;
	
	@SerializedName("name")
	public String Name;
	
	public int GroupId = 1;
	
	public String Icon = "menuicon_best";

	@SerializedName("image")
	public String Image;
	
	@SerializedName("sum_product")
	public int NumProduct;
	
	@SerializedName("pos")
	public int Pos;
	
	@SerializedName("categories")
	public List<Category> Categories;
}
