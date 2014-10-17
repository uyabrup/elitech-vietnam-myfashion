/**
 * Aug 21, 2014 1:54:43 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.CartAdapter;
import elitech.vietnam.myfashion.adapters.CitySpinnerAdapter;
import elitech.vietnam.myfashion.adapters.DistrictSpinnerAdapter;
import elitech.vietnam.myfashion.adapters.StringSpinnerAdapter;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.controllers.AppController;
import elitech.vietnam.myfashion.dialogues.DeleteItemCartDialog.DeleteItemDialogClick;
import elitech.vietnam.myfashion.entities.City;
import elitech.vietnam.myfashion.entities.District;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.Order;
import elitech.vietnam.myfashion.entities.OrderDetail;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class ShoppingCartFragment extends AbstractFragment implements DeleteItemDialogClick, View.OnClickListener, OnItemSelectedListener {

	SlidingUpPanelLayout mSliding;
	RelativeLayout mLayoutCheckout;
	LinearLayout mLayoutEmpty;
	ListView mListCart;
	Button mBtnGoShopping, mBtnCheckout;
	ImageButton mBtnCheckoutIcon;
	TextView mTxtHeaderCount, mTxtHeaderTotal, mTxtShip;
	Spinner mSpinCity, mSpinDistrict, mSpinPayment;
	EditText mEdtAddress, mEdtFullName, mEdtEmail, mEdtPhone, mEdtNote;
	
	AppController mController;
	List<OrderDetail> mOrders;
	List<City> mCities = new ArrayList<>();
	List<District> mDistricts = new ArrayList<>();
	List<String> mPays = new ArrayList<>();
	
	CartAdapter mAdapter;
	CitySpinnerAdapter mCityAdapter;
	DistrictSpinnerAdapter mDistrictAdapter;
	StringSpinnerAdapter mPayAdapter;
	
	double mShipping = 0;
	
	public ShoppingCartFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.title_shopping_cart);
		View view = inflater.inflate(R.layout.fragment_shoppingcart, container, false);
		mController = mActivity.getController();
		mOrders = mController.getOrders();
		
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
		mSpinPayment = (Spinner) view.findViewById(R.id.cart_spinnerPayment);
		mTxtShip = (TextView) view.findViewById(R.id.cart_txtShippingPrice);
		mEdtAddress = (EditText) view.findViewById(R.id.cart_edtAddress);
		mEdtFullName = (EditText) view.findViewById(R.id.cart_edtFullName);
		mEdtEmail = (EditText) view.findViewById(R.id.cart_edtEmail);
		mEdtPhone = (EditText) view.findViewById(R.id.cart_edtPhone);
		mEdtNote = (EditText) view.findViewById(R.id.cart_edtNote);
		
		mAdapter = new CartAdapter(mActivity, mOrders, this);
		mListCart.setAdapter(mAdapter);
		mCityAdapter = new CitySpinnerAdapter(mActivity, mCities);
		mSpinCity.setAdapter(mCityAdapter);
		mDistrictAdapter = new DistrictSpinnerAdapter(mActivity, mDistricts);
		mSpinDistrict.setAdapter(mDistrictAdapter);
		mPayAdapter = new StringSpinnerAdapter(mActivity, mPays);
		mSpinPayment.setAdapter(mPayAdapter);
		
		mSpinCity.setTag("init tag");
		mSpinDistrict.setTag("init tag");
		mBtnGoShopping.setOnClickListener(this);
		mBtnCheckout.setOnClickListener(this);
		mBtnCheckoutIcon.setOnClickListener(this);
		mSpinCity.setOnItemSelectedListener(this);
		mSpinDistrict.setOnItemSelectedListener(this);
		
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
		
		mCities.clear();
		mCities.addAll(mActivity.getDatabase().loadCities());
		mCityAdapter.notifyDataSetChanged();

		mPays.clear();
		mPays.add(getString(R.string.chuyenkhoan));
		mPays.add(getString(R.string.tienmat));
		mPayAdapter.notifyDataSetChanged();

		loadCheckoutView();
	}
	
	private void loadCheckoutView() {
		double price = mController.getTotalPrice();
		String str = getString(mOrders.size() > 1 ? R.string.items : R.string.item);
		mTxtHeaderCount.setText(mOrders.size() + " " + str);
		mTxtHeaderTotal.setText(Utilities.numberFormat(price) + Const.CURRENCY_VN);

		if (mActivity.getLoggedinUser() != null) {
			Member member = mActivity.getLoggedinUser();
			mEdtAddress.setText(member.Address);
			mEdtFullName.setText(member.Name);
			mEdtEmail.setText(member.Email);
			mEdtPhone.setText(member.Phone);
			
			for (int i=0; i<mCities.size(); i++)
				if (mCities.get(i).Id == member.City) {
					mSpinCity.setTag("select tag");
					mSpinCity.setSelection(i);
					break;
				}
			
			mDistricts.clear();
			mDistricts.addAll(mActivity.getDatabase().getDistrictByCity(mCities.get(mSpinCity.getSelectedItemPosition()).Id));
			mDistrictAdapter.notifyDataSetChanged();
			for (int i=0; i<mDistricts.size(); i++)
				if (mDistricts.get(i).Id == member.District) {
					mSpinDistrict.setSelection(i);
					break;
				}
		} else {
			mSpinCity.setTag("select tag");
			mSpinCity.setSelection(0);
			mDistricts.clear();
			mDistricts.addAll(mActivity.getDatabase().getDistrictByCity(mCities.get(mSpinCity.getSelectedItemPosition()).Id));
			mDistrictAdapter.notifyDataSetChanged();
			mSpinDistrict.setSelection(0);
		}
		mSpinPayment.setSelection(0);
		
		setShippingText(mDistricts.get(mSpinDistrict.getSelectedItemPosition()).Id, mCities.get(mSpinCity.getSelectedItemPosition()).Code);
	}
	
	private void setShippingText(final int districtId, final String cityCode) {
		new AsyncTask<Integer, Integer, Double>() {
			@Override
			protected void onPreExecute() {
			}
			@Override
			protected Double doInBackground(Integer... params) {
				float weight = mController.getTotalWeight();
				double price = mController.getTotalPrice();
				if (price >= 1500000 && mCities.get(mSpinCity.getSelectedItemPosition()).Id == 3) {
					mShipping = 0;
				}
				else {
					mShipping = mActivity.getDatabase().getShipping(cityCode, weight, districtId);
					if (weight > 2) {
						double extraPrice = mActivity.getDatabase().getShippingMore(cityCode);
						float extraWeight = ((float) Math.ceil((weight - 2.0) * 2.0)) / 2.0f;
						extraPrice *= (extraWeight / 0.5);
						mShipping += extraPrice;
					}
					mShipping = (mShipping < 20000) ? 20000 : mShipping;
				}
				return null;
			}
			@Override
			protected void onPostExecute(Double result) {
				if (mShipping == 0)
					mTxtShip.setText("Free");
				else
					mTxtShip.setText(String.format("%s   (%.2f kg)", Utilities.numberFormat(mShipping) + Const.CURRENCY_VN, mController.getTotalWeight()));
			}
		}.execute();
	}
	
	public void onItemQuantityChanged(double subPrice, float subWeight) {
		mController.setTotalPrice(mController.getTotalPrice() + subPrice);
		mController.setTotalWeight(mController.getTotalWeight() + subWeight);
		changeCheckoutPrice();
	}
	
	public void onItemDeleted(int position) {
		OrderDetail item = mOrders.get(position);
		mController.setTotalPrice(mController.getTotalPrice() - item.getAmountVN());
		mController.setTotalWeight(mController.getTotalWeight() - item.Weight);
		mOrders.remove(position);
		mActivity.updateCartBadge(mOrders.size());
		if (mOrders.size() == 0) {
			mSliding.setVisibility(View.GONE);
			mLayoutEmpty.setVisibility(View.VISIBLE);
		} else {
			mAdapter.notifyDataSetChanged();
			changeCheckoutPrice();
		}
	}
	
	private void changeCheckoutPrice() {
		double price = mController.getTotalPrice();
		String str = getString(mOrders.size() > 1 ? R.string.items : R.string.item);
		mTxtHeaderCount.setText(mOrders.size() + " " + str);
		mTxtHeaderTotal.setText(Utilities.numberFormat(price) + Const.CURRENCY_VN);
		setShippingText(mDistricts.get(mSpinDistrict.getSelectedItemPosition()).Id, mCities.get(mSpinCity.getSelectedItemPosition()).Code);
	}
	
	public static interface ShoppingCartCallback {
		List<OrderDetail> getOrders();
		Order createOrder(String address, String city, String district, String memo, String phone, String name, String email, double shipping, int payment, List<OrderDetail> details);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cart_btnGoShopping:
			mActivity.changeBase(BaseFragment.TAG_BESTOFDAY, null);
			break;
		case R.id.cart_btnCheckout:
		case R.id.cart_btnCheckoutIcon:
			if (!mSliding.isPanelExpanded())
				mSliding.expandPanel();
			else if (validate()) {
				mController.createOrder(mEdtAddress.getText().toString().trim(), 
						mCities.get(mSpinCity.getSelectedItemPosition()).Name, 
						mDistricts.get(mSpinDistrict.getSelectedItemPosition()).Name, 
						mEdtNote.getText().toString(), 
						mEdtPhone.getText().toString().trim(), 
						mEdtFullName.getText().toString().trim(), 
						mEdtEmail.getText().toString().trim(), 
						mShipping, 
						mSpinPayment.getSelectedItemPosition(), 
						mOrders);
				mActivity.getCurrentBase().replaceFragment(new BillingReviewFragment(), true);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getTag() != null) {
			parent.setTag(null);
			return;
		}
		switch (parent.getId()) {
		case R.id.cart_spinnerCity:
			mDistricts.clear();
			mDistricts.addAll(mActivity.getDatabase().getDistrictByCity(mCities.get(position).Id));
			mDistrictAdapter.notifyDataSetChanged();
			mSpinDistrict.setSelection(0);
			setShippingText(mDistricts.get(mSpinDistrict.getSelectedItemPosition()).Id, mCities.get(position).Code);
			break;
		case R.id.cart_spinnerDistrict:
			setShippingText(mDistricts.get(position).Id, mCities.get(mSpinCity.getSelectedItemPosition()).Code);
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
	private boolean validate() {
		if (mEdtAddress.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.addressisempty, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtFullName.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.nameisempty, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtEmail.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.emailisempty, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!Utilities.checkEmailValid(mEdtEmail.getText().toString().trim())) {
			Toast.makeText(mActivity, R.string.emailisinvalid, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtPhone.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.phoneisempty, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void yesClick(int requestCode, int position) {
		onItemDeleted(position);
	}
	
	@Override
	public void noClick(int requestCode, int position) {
		
	}
}
