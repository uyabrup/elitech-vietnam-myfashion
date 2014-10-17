/**
 * Oct 16, 2014 8:58:50 AM
 */
package elitech.vietnam.myfashion.fragments;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.utilities.Utilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Cong
 *
 */
public class ForgotPasswordFragment extends AbstractFragment implements View.OnClickListener {

	private EditText mEdtEmail;
	private Button mBtnSubmit;
	
	public static ForgotPasswordFragment newInstance() {
		ForgotPasswordFragment fragment = new ForgotPasswordFragment();
		return fragment;
	}
	
	public ForgotPasswordFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login_forgotpassword, container, false);
		
		mEdtEmail = (EditText) view.findViewById(R.id.login_edtForgotEmail);
		mBtnSubmit = (Button) view.findViewById(R.id.login_btnForgotSubmit);
		
		mBtnSubmit.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btnForgotSubmit:
			if (validate()) {
				
			}
			break;
		default:
			break;
		}
	}
	
	private boolean validate() {
		if (mEdtEmail.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.emailisempty, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (Utilities.checkEmailValid(mEdtEmail.getText().toString().trim())) {
			Toast.makeText(mActivity, R.string.emailinvalid, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
