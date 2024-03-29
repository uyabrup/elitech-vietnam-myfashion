/**
 * Aug 27, 2014 4:36:03 PM
 */
package elitech.vietnam.myfashion.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.entities.Category;
import elitech.vietnam.myfashion.entities.Color;
import elitech.vietnam.myfashion.entities.Cosmetic;
import elitech.vietnam.myfashion.entities.Order;
import elitech.vietnam.myfashion.entities.OrderDetail;
import elitech.vietnam.myfashion.entities.Post;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.Size;
import elitech.vietnam.myfashion.fragments.BaseFragment;
import elitech.vietnam.myfashion.fragments.BestOfTodayFragment.BestOfTodayCallback;
import elitech.vietnam.myfashion.fragments.CategoryDetailFragment;
import elitech.vietnam.myfashion.fragments.CategoryDetailFragment.CategoryDetailCallback;
import elitech.vietnam.myfashion.fragments.CosmeticDetailFragment;
import elitech.vietnam.myfashion.fragments.CosmeticDetailFragment.CosmeticDetailCallback;
import elitech.vietnam.myfashion.fragments.CosmeticFragment.CosmeticCallback;
import elitech.vietnam.myfashion.fragments.ProductDetailFragment.ProductDetailCallback;
import elitech.vietnam.myfashion.fragments.ProductTabHostFragment;
import elitech.vietnam.myfashion.fragments.ShoppingCartFragment.ShoppingCartCallback;
import elitech.vietnam.myfashion.fragments.StyleDetailFragment;
import elitech.vietnam.myfashion.fragments.StylerBestTopFragment.StylerBestCallback;
import elitech.vietnam.myfashion.fragments.TrademarkContentFragment;
import elitech.vietnam.myfashion.fragments.TrademarkFragment.TradeMarkCallback;

/**
 * @author Cong
 *
 */
public class AppController implements CategoryDetailCallback, ProductDetailCallback, ShoppingCartCallback, 
			CosmeticDetailCallback, TradeMarkCallback, BestOfTodayCallback, StylerBestCallback, 
			CosmeticCallback {

	MainActivity mActivity;
	
	Category mCategory;
	Product mProduct;
	HashMap<Integer, Post> mPosts = new HashMap<>();
	List<OrderDetail> mOrderDetails;
	Cosmetic mCosmetic;
	SearchCallback mSearch;
	
	Order mBill;
	double mTotal = 0;
	float mWeight = 0;
	
	public AppController(MainActivity activity) {
		mActivity = activity;
		mOrderDetails = new ArrayList<>();
	}

	@Override
	public void setCategory(Category category) {
		mCategory = category;
	}

	@Override
	public Category getCategory() {
		return mCategory;
	}
	
	@Override
	public void onItemClick(Category category, int fashion) {
		setCategory(category);
		CategoryDetailFragment fragment = new CategoryDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(CategoryDetailFragment.ARG_FASHION, fashion);
		fragment.setArguments(bundle);
		mActivity.getCurrentBase().replaceFragment(fragment, true);
	}

	@Override
	public Product getProduct() {
		return mProduct;
	}

	@Override
	public void setProduct(Product product) {
		mProduct = product;
	}

	@Override
	public List<OrderDetail> getOrders() {
		return mOrderDetails;
	}
	
	public double getTotalPrice() {
		return mTotal;
	}
	
	public void setTotalPrice(double value) {
		mTotal = value;
	}
	
	public float getTotalWeight() {
		return mWeight;
	}
	
	public void setTotalWeight(float weight) {
		mWeight = weight;
	}
	
	public Order getBill() {
		return mBill;
	}
	
	public void setBill(Order order) {
		mBill = order;
	}

	public void addToCart(Product product, Color color, Size size, int quantity) {
		product.Color = color;
		product.Size = size;
		product.Quantity = quantity;
		
		OrderDetail detail = new OrderDetail();
		detail.ProductID = product.Id;
		detail.ProductName = product.Name;
		detail.CategoryNameEN = product.CategoryNameEN;
		detail.CategoryNameVN = product.CategoryNameVN;
		detail.CategoryNameKR = product.CategoryNameKR;
		detail.Price = product.Price - product.Price
				* product.SaleOff / 100;
		detail.PriceVN = (product.SaleOff != 0) ? product.PriceSale : product.PriceVN;
		if (product.Color != null) {
			detail.ColorNameEN = product.Color.NameEN;
			detail.ColorNameKR = product.Color.NameKR;
			detail.ColorNameVN = product.Color.NameVN;
			detail.Color = product.Color.ColorID;
		} else {
			detail.Color = -1;
		}
		if (product.Size != null) {
			detail.SizeName = product.Size.Size;
			detail.Size = product.Size.SizeID;
		} else {
			detail.Size = -1;
		}
		detail.Quantity = product.Quantity;
		detail.Image = product.Image;
		detail.SaleOff = product.SaleOff;
		detail.Weight += product.Weight * product.Quantity;
		
		for (OrderDetail d : mOrderDetails) {
			if (d.ProductID == detail.ProductID
					&& d.Size == detail.Size && d.Color == detail.Color) {
				if (d.Quantity + detail.Quantity > 20) {
					detail.Quantity = 20 - d.Quantity;
					detail.Weight = product.Weight * detail.Quantity;
				}
				mTotal += detail.getAmountVN();
				mWeight += detail.Weight;
				d.Quantity = d.Quantity + detail.Quantity;
				d.Weight += detail.Weight;
				return;
			}
		}
		mTotal += detail.getAmountVN();
		mWeight += detail.Weight;
		mOrderDetails.add(detail);
		mActivity.updateCartBadge(mOrderDetails.size());
	}

	@Override
	public void onItemClick(Product product) {
		setProduct(product);
		mActivity.getCurrentBase().replaceFragment(new ProductTabHostFragment(), true);
	}

	@Override
	public Order createOrder(String address, String city, String district, String memo, String phone, String name,
			String email, double shipping, int payment, List<OrderDetail> details) {
		mBill = new Order();
		mBill.Address = address;
		mBill.City = city;
		mBill.State = district;
		mBill.Memo = memo;
		mBill.Phone = phone;
		mBill.Email = email;
		mBill.Name = name;
		mBill.Ship = shipping;
		mBill.Payment = payment;
		mBill.ListDetail = details;
		return mBill;
	}
	
	public void resetCart() {
		mOrderDetails = new ArrayList<>();
		mBill = null;
		mTotal = 0;
		mWeight = 0;
		mActivity.updateCartBadge(0);
	}

	@Override
	public void onItemClick(Post post) {
		setPost(post);
		mActivity.getCurrentBase().replaceFragment(StyleDetailFragment.newInstance(post.Id), true);
	}

	@Override
	public Post getPost(int postId) {
		return mPosts.get(postId);
	}

	@Override
	public void setPost(Post post) {
		if (mPosts.get(post.Id) != null)
			mPosts.remove(post.Id);
		mPosts.put(post.Id, post);
	}
	
	@Override
	public void onItemClick(Cosmetic item) {
		setCosmetic(item);
		CosmeticDetailFragment fragment = new CosmeticDetailFragment();
		mActivity.getCurrentBase().replaceFragment(fragment, true);
	}

	@Override
	public void setCosmetic(Cosmetic trademark) {
		mCosmetic = trademark;
	}

	@Override
	public Cosmetic getCosmetic() {
		return mCosmetic;
	}

	@Override
	public void onItemClick(String item) {
		TrademarkContentFragment fragment = TrademarkContentFragment.newInstance(item);
		mActivity.getCurrentBase().replaceFragment(fragment, true);
	}

	public void search(String arg0) {
		arg0 = arg0.trim();
		if (mSearch != null)
			mSearch.doSearch(arg0);
		else {
			Bundle args = new Bundle();
			args.putString(BaseFragment.ARG_SEARCHVALUE, arg0);
			mActivity.changeBase(BaseFragment.TAG_SEARCHSIMPLE, args);
		}
	}
	
	public static interface SearchCallback {
		void doSearch(String query);
	}
	
	public void setSearchCallback(SearchCallback callback) {
		mSearch = callback;
	}
	
	public SearchCallback getSearchCallback() {
		return mSearch;
	}
	
	public interface ResultListener {
		void onResult(int request, int result, Intent data);
	}
	
	private ResultListener mResultListener;

	public ResultListener getResultListener() {
		return mResultListener;
	}

	public void setResultListener(ResultListener mResultListener) {
		this.mResultListener = mResultListener;
	}
	
	public int mResult, mRequest; public Intent mData;
	public void storeResult(int request, int result, Intent data) {
		mRequest = request;
		mResult = result;
		mData = data;
	}
}
