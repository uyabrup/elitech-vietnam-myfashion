/**
 * Aug 29, 2014 11:16:45 AM
 */
package elitech.vietnam.myfashion.fragments;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.controllers.AppController;
import elitech.vietnam.myfashion.entities.Order;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Cong
 *
 */
public class BillingReviewFragment extends AbstractFragment implements View.OnClickListener {

	AppController mController;
	Order mOrder;
	
	TextView mTxtName, mTxtEmail, mTxtPhone, mTxtAddress, mTxtCity, mTxtCount, mTxtWeight, mTxtPrice, mTxtShip, mTxtPay, mTxtTotal, mTxtNote, mBtnHotline, mBtnShipping, mBtnPayment;
	LinearLayout mLayoutNote;
	Button mBtnConfirm;
	
	public BillingReviewFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		
		mBtnConfirm.setOnClickListener(this);
		mBtnShipping.setOnClickListener(this);
		mBtnPayment.setOnClickListener(this);
		mBtnHotline.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 1:
			
			break;

		default:
			break;
		}
	}
}
