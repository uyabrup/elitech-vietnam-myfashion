/**
 * Sep 26, 2014 3:48:37 PM
 */
package elitech.vietnam.myfashion.fragments;

import com.facebook.widget.LoginButton;

import elitech.vietnam.myfashion.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * @author Cong
 *
 */
public class LoginSocialFragment extends AbstractFragment implements View.OnClickListener {

	LoginButton mFbLogin;
	Button mGgLogin, mBtnLogin, mBtnSignup;
	
	public static LoginSocialFragment newInstance() {
		LoginSocialFragment f = new LoginSocialFragment();
		return f;
	}
	
	public LoginSocialFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.login);
		View view = inflater.inflate(R.layout.fragment_login_social, container, false);
		
		mFbLogin = (LoginButton) view.findViewById(R.id.facebook_btnLogin);
		mGgLogin = (Button) view.findViewById(R.id.google_btnLogin);
		mBtnLogin = (Button) view.findViewById(R.id.login_btnLogin);
		mBtnSignup = (Button) view.findViewById(R.id.login_btnSignup);
		
		mGgLogin.setOnClickListener(this);
		mBtnLogin.setOnClickListener(this);
		mBtnSignup.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.google_btnLogin:
			break;
		case R.id.login_btnLogin:
			mActivity.getCurrentBase().getCurrentChildBase().replaceFragment(LoginAppLoginFragment.newInstance(), R.id.login_layoutContainer, true);
			break;
		case R.id.login_btnSignup:
			mActivity.getCurrentBase().getCurrentChildBase().replaceFragment(LoginRegisterFragment.newInstance(), R.id.login_layoutContainer, true);
			break;
		default:
			break;
		}
	}
}
