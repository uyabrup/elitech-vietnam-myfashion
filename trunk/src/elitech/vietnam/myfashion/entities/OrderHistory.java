package elitech.vietnam.myfashion.entities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.annotations.SerializedName;

public class OrderHistory {

	@SerializedName("order")
	public Order Order;
	
	@SerializedName("list_detail")
	public ArrayList<OrderDetail> Details;
	
	
	public static ArrayList<OrderHistory> parseFromJSON(String json) {
		if (json==null)
			return null;
		
		if(json.equals(""))
			return new ArrayList<OrderHistory>();
		
		try {
			JSONArray array = new JSONArray(json);
			if((array.length() <= 0))
				return new ArrayList<OrderHistory>();
			
			ArrayList<OrderHistory> result = new ArrayList<OrderHistory>();
			
			for (int i=0; i<array.length(); i++) {
				OrderHistory orderHistory = new OrderHistory();
				JSONObject obj1 = array.getJSONObject(i);
				
				JSONObject obj2 = obj1.getJSONObject("order");
				orderHistory.Order = new Order();
				orderHistory.Order.Id = obj2.getInt("id");
				orderHistory.Order.OrderCode = obj2.getString("code_order");
				orderHistory.Order.AccountID = obj2.getInt("id_account");
				orderHistory.Order.Total = obj2.getDouble("total");
				orderHistory.Order.Address = obj2.getString("address");
				orderHistory.Order.City = obj2.getString("city");
				orderHistory.Order.State = obj2.getString("state");
				orderHistory.Order.Phone = obj2.getString("phone");
				orderHistory.Order.Payment = obj2.getInt("payment");
				orderHistory.Order.Ship = obj2.getDouble("ship");
				orderHistory.Order.date = obj2.getString("date");
				orderHistory.Order.Memo = obj2.getString("memo");
				orderHistory.Order.Status = obj2.getInt("status");
				
				JSONArray arr1 = obj1.getJSONArray("list_detail");
				orderHistory.Details = new ArrayList<OrderDetail>();
				for (int j=0; j<arr1.length(); j++) {
					OrderDetail detail = new OrderDetail();
					JSONObject obj3 = arr1.getJSONObject(j);
					
					detail.Id = obj3.getInt("id");
					detail.OrderID = obj3.getInt("id_order");
					detail.ProductID = obj3.getInt("id_product");
					detail.Color = obj3.getInt("color");
					detail.ColorNameEN = obj3.getString("name_color_en");
					detail.ColorNameKR = obj3.getString("name_color_kr");
					detail.ColorNameVN = obj3.getString("name_color_vn");
					detail.Size = obj3.getInt("size");
					detail.SizeName = obj3.getString("name_size");
					detail.Image = obj3.getString("product_image");
					detail.ProductName = obj3.getString("product_name");
					detail.CategoryNameEN = obj3.getString("category_name_en");
					detail.CategoryNameKR = obj3.getString("category_name_kr");
					detail.CategoryNameVN = obj3.getString("category_name_vn");
					detail.Price = obj3.getInt("price");
					detail.Commission = obj3.getDouble("commission");
					detail.Quantity = obj3.getInt("quantity");
					orderHistory.Details.add(detail);
				}
				result.add(orderHistory);
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
