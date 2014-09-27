/**
 * Sep 26, 2014 5:26:55 PM
 */
package elitech.vietnam.myfashion.entities;

import com.google.gson.annotations.SerializedName;

/**
 * @author Cong
 *
 */
public class RestError {
	
	@SerializedName("code")
	public int Code;
	
	@SerializedName("message")
	public String Message;
}
