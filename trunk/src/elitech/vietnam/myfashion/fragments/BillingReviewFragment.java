/**
 * Aug 29, 2014 11:16:45 AM
 */
package elitech.vietnam.myfashion.fragments;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.google.gson.Gson;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.controllers.AppController;
import elitech.vietnam.myfashion.dialogues.ThanksDialog;
import elitech.vietnam.myfashion.entities.Order;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class BillingReviewFragment extends AbstractFragment implements View.OnClickListener {

	AppController mController;
	Order mOrder;
	
	TextView mTxtName, mTxtEmail, mTxtPhone, mTxtAddress, mTxtCity, mTxtCount, mTxtWeight, mTxtPrice, mTxtShip, mTxtPay, mTxtTotal, mTxtNote, mBtnHotline, mBtnShipping, mBtnPayment;
	LinearLayout mLayoutNote;
	Button mBtnConfirm, mTxtSuccess;
	ProgressBar mPrgConfirm;
	
	public BillingReviewFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.title_revieworder);
		mController = mActivity.getController();
		mOrder = mController.getBill();
		View view = inflater.inflate(R.layout.fragment_billconfirm, container, false);
		
		mTxtName = (TextView) view.findViewById(R.id.bill_txtName);
		mTxtEmail = (TextView) view.findViewById(R.id.bill_txtEmail);
		mTxtPhone = (TextView) view.findViewById(R.id.bill_txtPhone);
		mTxtAddress = (TextView) view.findViewById(R.id.bill_txtAddress);
		mTxtCity = (TextView) view.findViewById(R.id.bill_txtCity);
		mTxtCount = (TextView) view.findViewById(R.id.bill_txtItemCount);
		mTxtWeight = (TextView) view.findViewById(R.id.bill_txtWeight);
		mTxtPrice = (TextView) view.findViewById(R.id.bill_txtPrice);
		mTxtShip = (TextView) view.findViewById(R.id.bill_txtShipPrice);
		mTxtPay = (TextView) view.findViewById(R.id.bill_txtPayType);
		mTxtTotal = (TextView) view.findViewById(R.id.bill_txtTotal);
		mTxtNote = (TextView) view.findViewById(R.id.bill_txtNote);
		mBtnHotline = (TextView) view.findViewById(R.id.bill_txtHotLine);
		mBtnShipping = (TextView) view.findViewById(R.id.bill_txtShippingInfo);
		mBtnPayment = (TextView) view.findViewById(R.id.bill_txtPaymentInfo);
		mLayoutNote = (LinearLayout) view.findViewById(R.id.bill_layoutNote);
		mBtnConfirm = (Button) view.findViewById(R.id.bill_btnCheckout);
		mTxtSuccess = (Button) view.findViewById(R.id.bill_txtSuccess);
		mPrgConfirm = (ProgressBar) view.findViewById(R.id.bill_prgConfirm);
		
		mBtnConfirm.setOnClickListener(this);
		mBtnShipping.setOnClickListener(this);
		mBtnPayment.setOnClickListener(this);
		mBtnHotline.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mTxtName.setText(mOrder.Name);
		mTxtEmail.setText(mOrder.Email);
		mTxtPhone.setText(mOrder.Phone);
		mTxtAddress.setText(mOrder.Address);
		mTxtCity.setText(mOrder.State + ", " + mOrder.City);
		mTxtCount.setText(mOrder.ListDetail.size() + " " + getString(mOrder.ListDetail.size() > 1 ? R.string.items : R.string.item));
		mTxtWeight.setText(mController.getTotalWeight() + " Kg");
		mTxtPrice.setText(Utilities.numberFormat(mController.getTotalPrice()) + Const.CURRENCY_VN);
		mTxtShip.setText(Utilities.numberFormat(mOrder.Ship) + Const.CURRENCY_VN);
		mTxtPay.setText(mOrder.Payment > 0 ? R.string.tienmat : R.string.chuyenkhoan);
		mTxtTotal.setText(Utilities.numberFormat(mController.getTotalPrice() + mOrder.Ship) + Const.CURRENCY_VN);
		mTxtNote.setText(mOrder.Memo);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_btnCheckout:
			mPrgConfirm.setVisibility(View.VISIBLE);
			mActivity.getServices().addOrder(
					mActivity.getLoggedinUser() != null ? mActivity.getLoggedinUser().Id : 0, 
					Utilities.encodeString(mOrder.Email), 
					Utilities.encodeString(mOrder.Name), 
					Utilities.encodeString(mOrder.Address), 
					Utilities.encodeString(mOrder.City), 
					Utilities.encodeString(mOrder.State), 
					mOrder.Phone, 
					mOrder.Payment, 
					mOrder.Ship, 
					mOrder.ShippingFee, 
					Utilities.encodeString(mOrder.Memo), 
					Utilities.encodeString(new Gson().toJson(mOrder.ListDetail)), 
					new Callback<Integer>() {
				@Override
				public void failure(RetrofitError arg0) {
					Log.w("RetrofitError", arg0.getMessage());
					mPrgConfirm.setVisibility(View.INVISIBLE);
				}
				@Override
				public void success(Integer arg0, Response arg1) {
					mTxtSuccess.setVisibility(View.VISIBLE);
					mController.resetCart();
					ThanksDialog.newInstance().show(getFragmentManager());
				}});
			break;
		case R.id.bill_txtHotLine:
			break;
		case R.id.bill_txtShippingInfo:
			break;
		case R.id.bill_txtPaymentInfo:
			break;
		default:
			break;
		}
	}
}


