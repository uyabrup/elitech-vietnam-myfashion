/**
 * Aug 21, 2014 1:54:43 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.CartAdapter;
import elitech.vietnam.myfashion.adapters.CitySpinnerAdapter;
import elitech.vietnam.myfashion.adapters.DistrictSpinnerAdapter;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.City;
import elitech.vietnam.myfashion.entities.District;
import elitech.vietnam.myfashion.entities.OrderDetail;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class ShoppingCartFragment extends AbstractFragment implements View.OnClickListener {

	SlidingUpPanelLayout mSliding;
	RelativeLayout mLayoutCheckout;
	LinearLayout mLayoutEmpty;
	ListView mListCart;
	Button mBtnGoShopping, mBtnCheckout;
	ImageButton mBtnCheckoutIcon;
	TextView mTxtHeaderCount, mTxtHeaderTotal;
	Spinner mSpinCity, mSpinDistrict;
	
	ShoppingCartCallback mCallback;
	List<OrderDetail> mOrders;
	List<City> mCities = new ArrayList<>();
	List<District> mDistricts = new ArrayList<>();
	
	CartAdapter mAdapter;
	CitySpinnerAdapter mCityAdapter;
	DistrictSpinnerAdapter mDistrictAdapter;
	
	public ShoppingCartFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_shoppingcart, container, false);
		mCallback = mActivity.getController();
		mOrders = mCallback.getOrders();
		
		mSliding = (SlidingUpPanelLayout) view.findViewById(R.id.cart_layoutSliding);
		mLayoutCheckout = (RelativeLayout) view.findViewById(R.id.cart_layoutPurchase);
		mLayoutEmpty = (LinearLayout) view.findViewById(R.id.cart_layoutEmpty);
		mListCart = (ListView) view.findViewById(R.id.cart_lvMain);
		mBtnGoShopping = (Button) view.findViewById(R.id.cart_btnGoShopping);
		mBtnCheckout = (Button) view.findViewById(R.id.cart_btnCheckout);
		mBtnCheckoutIcon = (ImageButton) view.findViewById(R.id.cart_btnCheckoutIcon);
		mTxtHeaderCount = (TextView) view.findViewById(R.id.cart_header_txtCount);
		mTxtHeaderTotal = (TextView) view.findViewById(R.id.cart_header_txtTotal);
		mSpinCity = (Spinner) view.findViewById(R.id.cart_spinnerCity);
		mSpinDistrict = (Spinner) view.findViewById(R.id.cart_spinnerDistrict);
		
		mAdapter = new CartAdapter(mActivity, mOrders, this);
		mListCart.setAdapter(mAdapter);
		
		mBtnGoShopping.setOnClickListener(this);
		mBtnCheckout.setOnClickListener(this);
		mBtnCheckoutIcon.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (mOrders.size() == 0)
			return;
		
		mSliding.setVisibility(View.VISIBLE);
		mLayoutEmpty.setVisibility(View.GONE);
		mAdapter.notifyDataSetChanged();
		loadCheckoutView();
	}
	
	public void loadCheckoutView() {
		double price = mActivity.getController().getTotalPrice();
		String str = getString(mOrders.size() > 1 ? R.string.items : R.string.item);
		mTxtHeaderCount.setText(mOrders.size() + " " + str);
		mTxtHeaderTotal.setText(Utilities.numberFormat(price) + Const.CURRENCY_VN);
		// TODO: calculate shipping price
		mCities.clear();
		mCities.addAll(mActivity.getDatabase().loadCities());
		mCityAdapter = new CitySpinnerAdapter(mActivity, mCities);
		mSpinCity.setAdapter(mCityAdapter);
		
		mDistricts.clear();
		mDistricts.addAll(mActivity.getDatabase().getDistrictByCity(mCities.get(mSpinCity.getSelectedItemPosition()).Id));
		mDistrictAdapter = new DistrictSpinnerAdapter(mActivity, mDistricts);
		mSpinDistrict.setAdapter(mDistrictAdapter);
	}
	
	public void onItemQuantityChanged(double subPrice) {
		mActivity.getController().setTotalPrice(mActivity.getController().getTotalPrice() + subPrice);
		loadCheckoutView();
	}
	
	public void onItemDeleted() {
		loadCheckoutView();
	}
	
	public static interface ShoppingCartCallback {
		List<OrderDetail> getOrders();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cart_btnGoShopping:
			mActivity.changeBase(BaseFragment.TAG_BESTOFDAY, null);
			break;

		default:
			break;
		}
	}
}
