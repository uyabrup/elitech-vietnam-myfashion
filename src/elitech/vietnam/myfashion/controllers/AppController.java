/**
 * Aug 27, 2014 4:36:03 PM
 */
package elitech.vietnam.myfashion.controllers;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.entities.Category;
import elitech.vietnam.myfashion.entities.Color;
import elitech.vietnam.myfashion.entities.OrderDetail;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.Size;
import elitech.vietnam.myfashion.entities.TradeMark;
import elitech.vietnam.myfashion.fragments.BestOfTodayFragment.BestOfTodayCallback;
import elitech.vietnam.myfashion.fragments.CategoryDetailFragment;
import elitech.vietnam.myfashion.fragments.CategoryDetailFragment.CategoryDetailCallback;
import elitech.vietnam.myfashion.fragments.ProductDetailFragment.ProductDetailCallback;
import elitech.vietnam.myfashion.fragments.ProductTabHostFragment;
import elitech.vietnam.myfashion.fragments.ShoppingCartFragment.ShoppingCartCallback;
import elitech.vietnam.myfashion.fragments.TradeMarkDetailFragment.TrademarkDetailCallback;

/**
 * @author Cong
 *
 */
public class AppController implements CategoryDetailCallback, ProductDetailCallback, ShoppingCartCallback, 
			TrademarkDetailCallback, BestOfTodayCallback {

	MainActivity mActivity;
	
	Category mCategory;
	Product mProduct;
	List<OrderDetail> mOrderDetails;
	TradeMark mTradeMark;
	
	double mTotal = 0;
	
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
		
		mTotal += detail.Quantity * detail.PriceVN;
		for (OrderDetail d : mOrderDetails) {
			if (d.ProductID == detail.ProductID
					&& d.Size == detail.Size && d.Color == detail.Color) {
				d.Quantity += detail.Quantity;
				return;
			}
		}
		mOrderDetails.add(detail);
		mActivity.updateCartBadge(mOrderDetails.size());
	}

	@Override
	public void setTradeMark(TradeMark trademark) {
		mTradeMark = trademark;
	}

	@Override
	public TradeMark getTradeMark() {
		return mTradeMark;
	}

	@Override
	public void onItemClick(Product product) {
		setProduct(product);
		mActivity.getCurrentBase().replaceFragment(new ProductTabHostFragment(), true);
	}
}
