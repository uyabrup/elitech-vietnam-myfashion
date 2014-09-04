/**
 * Sep 3, 2014 10:43:26 AM
 */
package elitech.vietnam.myfashion.entities;

import com.google.gson.annotations.SerializedName;

/**
 * @author Cong
 *
 */
public class Ship {

	@SerializedName("id")
	public int Id;
	
	@SerializedName("code")
	public String Code;
	
	@SerializedName("kg")
	public float Kg;
	
	@SerializedName("ship")
	public double Ship;
}
