/**
 * May 12, 2014 9:32:05 AM
 */
package elitech.vietnam.myfashion.dialogues;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.CitySpinnerAdapter;
import elitech.vietnam.myfashion.adapters.DistrictSpinnerAdapter;
import elitech.vietnam.myfashion.entities.City;
import elitech.vietnam.myfashion.entities.District;

/**
 * @author Cong
 *
 */
public class UpdateShippingDialog extends AbstractDialogFragment implements View.OnClickListener, OnItemSelectedListener {
	
	public static final String ARG_ADDRESS = "ARG_ADDRESS";
	public static final String ARG_DISTRICT = "ARG_DISTRICT";
	public static final String ARG_CITY = "ARG_CITY";
	public static final String ARG_PHONE = "ARG_PHONE";
	public static final String ARG_MEMBERID = "ARG_MEMBERID";
	
	ShippingDialogCallback mClick;
	
	EditText mEdtAddress, mEdtPhone;
	Spinner mSpinDistrict, mSpinCity;
	CitySpinnerAdapter mCityAdapter;
	DistrictSpinnerAdapter mDistrictAdapter;
	Button mBtnSave, mBtnCancel;
	String mAddress, mPhone;
	int mRequest, mMemberId, mDistrict, mCity;

	List<City> mCities = new ArrayList<>();
	List<District> mDistricts = new ArrayList<>();
	
	String mGenders[];
	
	public static UpdateShippingDialog newInstance(int memberId, String address, int district, int city, String phone, int request, Fragment target) {
		UpdateShippingDialog dialog = new UpdateShippingDialog();
		Bundle args = new Bundle();
		args.putInt(ARG_MEMBERID, memberId);
		args.putInt(ARG_DISTRICT, district);
		args.putInt(ARG_CITY, city);
		args.putString(ARG_ADDRESS, address);
		args.putString(ARG_PHONE, phone);
		dialog.setArguments(args);
		dialog.setTargetFragment(target, request);
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.status);
		mRequest = getTargetRequestCode();
		mClick = (ShippingDialogCallback) getTargetFragment();
		mMemberId = getArguments().getInt(ARG_MEMBERID);
		mAddress = getArguments().getString(ARG_ADDRESS);
		mDistrict = getArguments().getInt(ARG_DISTRICT);
		mCity = getArguments().getInt(ARG_CITY);
		mPhone = getArguments().getString(ARG_PHONE);
		View view = inflater.inflate(R.layout.dialog_updateshippingaddress, container, false);
		
		mEdtAddress = (EditText) view.findViewById(R.id.dialog_updateshipping_edtAddress);
		mSpinDistrict = (Spinner) view.findViewById(R.id.dialog_updateshipping_spinDistrict);
		mSpinCity = (Spinner) view.findViewById(R.id.dialog_updateshipping_spinCity);
		mEdtPhone = (EditText) view.findViewById(R.id.dialog_updateshipping_edtPhone);
		mBtnSave = (Button) view.findViewById(R.id.dialog_updateshipping_btnSave);
		mBtnCancel = (Button) view.findViewById(R.id.dialog_updateshipping_btnCancel);

		mDistrictAdapter = new DistrictSpinnerAdapter(mActivity, mDistricts);
		mCityAdapter = new CitySpinnerAdapter(mActivity, mCities);
		mSpinDistrict.setAdapter(mDistrictAdapter);
		mSpinCity.setAdapter(mCityAdapter);
		
		mBtnSave.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mSpinCity.setOnItemSelectedListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mEdtAddress.setText(mAddress);
		mEdtPhone.setText(mPhone);
		
		mCities.clear();
		mCities.addAll(mActivity.getDatabase().loadCities());
		mCityAdapter.notifyDataSetChanged();
		for (int i=0; i<mCities.size(); i++) {
			if (mCities.get(i).Id == mCity) {
				mSpinCity.setSelection(i, false);
				break;
			}
		}
		
		mDistricts.clear();
		mDistricts.addAll(mActivity.getDatabase().getDistrictByCity(mCities.get(mSpinCity.getSelectedItemPosition()).Id));
		mDistrictAdapter.notifyDataSetChanged();
		for (int i=0; i<mDistricts.size(); i++) {
			if (mDistricts.get(i).Id == mDistrict) {
				mSpinDistrict.setSelection(i, false);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_updateshipping_btnSave:
			mBtnSave.setEnabled(false);
			mBtnCancel.setEnabled(false);
			final String address = mEdtAddress.getText().toString().trim();
			final int district = mDistricts.get(mSpinDistrict.getSelectedItemPosition()).Id;
			final int city = mCities.get(mSpinCity.getSelectedItemPosition()).Id;
			final String phone = mEdtPhone.getText().toString().trim();
			mActivity.getServices().updateMemberShippingAddress(mMemberId, address, district, city, phone, new Callback<Integer>() {
				@Override
				public void success(Integer arg0, Response arg1) {
					if (arg0 > 0) {
						Toast.makeText(mActivity, R.string.profilesaved, Toast.LENGTH_SHORT).show();
						mClick.updateShipping(address, city, district, phone);
						dismiss();
					} else {
						Toast.makeText(mActivity, R.string.cantupdateprofile, Toast.LENGTH_SHORT).show();
						mBtnSave.setEnabled(true);
						mBtnCancel.setEnabled(false);
					}
				}
				@Override
				public void failure(RetrofitError arg0) {
					Log.w("RetrofitError", arg0.getMessage());
					mBtnSave.setEnabled(true);
					mBtnCancel.setEnabled(false);
				}
			});
			break;
		case R.id.dialog_updateshipping_btnCancel:
			dismiss();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.dialog_updateshipping_spinCity:
			mDistricts.clear();
			mDistricts.addAll(mActivity.getDatabase().getDistrictByCity(mCities.get(position).Id));
			mDistrictAdapter.notifyDataSetChanged();
			mSpinDistrict.setSelection(0);
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	public interface ShippingDialogCallback {
		public void updateShipping(String address, int city, int district, String phone);
	}
}
