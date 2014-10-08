/**
 * Sep 26, 2014 3:48:37 PM
 */
package elitech.vietnam.myfashion.fragments;

import com.google.gson.Gson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.RestError;
import elitech.vietnam.myfashion.prefs.PrefsDefinition;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class LoginAppLoginFragment extends AbstractFragment implements View.OnClickListener {

	EditText mEdtUsername, mEdtPassword;
	Button mBtnSubmit;
	TextView mBtnForgot, mBtnRegister;
	
	public static LoginAppLoginFragment newInstance() {
		LoginAppLoginFragment f = new LoginAppLoginFragment();
		return f;
	}
	
	public LoginAppLoginFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login_login, container, false);
		
		mEdtUsername = (EditText) view.findViewById(R.id.login_edtLoginEmail);
		mEdtPassword = (EditText) view.findViewById(R.id.login_edtLoginPassword);
		mBtnSubmit = (Button) view.findViewById(R.id.login_btnLoginSubmit);
		mBtnForgot = (TextView) view.findViewById(R.id.login_btnLoginForgotPassword);
		mBtnRegister = (TextView) view.findViewById(R.id.login_btnLoginRegister);
		
		mBtnSubmit.setOnClickListener(this);
		mBtnForgot.setOnClickListener(this);
		mBtnRegister.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btnLoginForgotPassword:
			break;
		case R.id.login_btnLoginRegister:
			mActivity.getCurrentBase().getCurrentChildBase().replaceFragment(LoginRegisterFragment.newInstance(), R.id.login_layoutContainer, true);
			break;
		case R.id.login_btnLoginSubmit:
			if (validate()) {
				String pass = Utilities.getMD5(mEdtPassword.getText().toString().trim() + "orchipro");
				mActivity.getServices().login(mEdtUsername.getText().toString().trim(), pass, new Callback<Member>() {
					@Override
					public void success(Member arg0, Response arg1) {
						mActivity.setLoggedinUser(arg0);
						mActivity.getMenuController().changeLoggedState(true);
						mActivity.getPreferences().edit().putString(PrefsDefinition.LOGGEDIN_MEMBER, new Gson().toJson(arg0)).commit();
						mActivity.onBackPressed();
					}
					@Override
					public void failure(RetrofitError arg0) {
						RestError error = (RestError) arg0.getBodyAs(RestError.class);
						switch (error.Code) {
						case 1:
							Toast.makeText(mActivity, R.string.emailnotexist, Toast.LENGTH_SHORT).show();
							break;
						case 2:
							Toast.makeText(mActivity, R.string.passwrong, Toast.LENGTH_SHORT).show();
							break;
						default:
							break;
						}
					}
				});
			}
			break;
		default:
			break;
		}
	}
	
	private boolean validate() {
		if (mEdtUsername.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.emailisempty, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtPassword.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.passblank, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!Utilities.checkEmailValid(mEdtUsername.getText().toString().trim())) {
			Toast.makeText(mActivity, R.string.emailinvalid, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
