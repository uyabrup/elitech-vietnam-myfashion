/**
 * Sep 26, 2014 3:48:37 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class LoginRegisterFragment extends AbstractFragment implements View.OnClickListener {
	public static LoginRegisterFragment newInstance() {
		LoginRegisterFragment f = new LoginRegisterFragment();
		return f;
	}
	
	public LoginRegisterFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login_signup, container, false);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
}
