/**
 * Sep 25, 2014 4:04:21 PM
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
public class LoginBaseFragment extends ChildBaseFragment {

	public static LoginBaseFragment newInstance() {
		LoginBaseFragment fragment = new LoginBaseFragment();
		return fragment;
	}
	
	public LoginBaseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login_base, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getChildFragmentManager().beginTransaction()
		.add(R.id.login_layoutContainer, 
				LoginSocialFragment.newInstance(), 
				LoginSocialFragment.class.getName())
		.commit();
	}
}
