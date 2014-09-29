/**
 * Sep 26, 2014 3:48:37 PM
 */
package elitech.vietnam.myfashion.fragments;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.RestError;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class LoginRegisterFragment extends AbstractFragment implements View.OnClickListener {
	
	EditText mEdtName, mEdtEmail, mEdtPass, mEdtPassConfirm;
	Button mBtnSubmit;
	
	public static LoginRegisterFragment newInstance() {
		LoginRegisterFragment f = new LoginRegisterFragment();
		return f;
	}
	
	public LoginRegisterFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login_signup, container, false);
		
		mEdtName = (EditText) view.findViewById(R.id.login_edtRegisterUser);
		mEdtEmail = (EditText) view.findViewById(R.id.login_edtRegisterEmail);
		mEdtPass = (EditText) view.findViewById(R.id.login_edtRegisterPassword);
		mEdtPassConfirm = (EditText) view.findViewById(R.id.login_edtRegisterPasswordRetype);
		mBtnSubmit = (Button) view.findViewById(R.id.login_btnRegisterSubmit);
		
		mBtnSubmit.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btnRegisterSubmit:
			if (validate()) 
				mActivity.getServices().register(
						mEdtName.getText().toString().trim(),
						mEdtEmail.getText().toString().trim(),
						Utilities.getMD5(mEdtPass.getText().toString() + "orchipro"),
						Utilities.getRegistrationId(mActivity.getApplicationContext()),
						new Callback<Integer>() {
							@Override
							public void success(Integer arg0, Response arg1) {
								Toast.makeText(mActivity, R.string.signupsuccessful, Toast.LENGTH_SHORT).show();
								mActivity.onBackPressed();
							}
							@Override
							public void failure(RetrofitError arg0) {
								RestError error = (RestError) arg0.getBodyAs(RestError.class);
								switch (error.Code) {
								case 3:
									Toast.makeText(mActivity, R.string.emailexist, Toast.LENGTH_SHORT).show();
									break;
								default:
									break;
								}
							}
						});
			break;
		default:
			break;
		}
	}
	
	public boolean validate() {
		if (mEdtEmail.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.emailisempty, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtName.getText().toString().trim().length() == 0){
			Toast.makeText(mActivity, R.string.nameisempty, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtPass.getText().toString().length() == 0){
			Toast.makeText(mActivity, R.string.passblank, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtPassConfirm.getText().toString().length() == 0){
			Toast.makeText(mActivity, R.string.passblank, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!Utilities.checkEmailValid(mEdtEmail.getText().toString().trim())) {
			Toast.makeText(mActivity, R.string.emailinvalid, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtPass.getText().toString().length() <= 4) {
			Toast.makeText(mActivity, R.string.passwordlengthgreaterthanfour, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!mEdtPass.getText().toString().equals(mEdtPassConfirm.getText().toString())) {
			Toast.makeText(mActivity, R.string.passwordconfirmdoesnotmatch, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
