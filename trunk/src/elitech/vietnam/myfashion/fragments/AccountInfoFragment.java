/**
 * Sep 25, 2014 4:04:21 PM
 */
package elitech.vietnam.myfashion.fragments;

import elitech.vietnam.myfashion.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

/**
 * @author Cong
 *
 */
public class AccountInfoFragment extends AbstractFragment implements OnClickListener {

	public static AccountInfoFragment newInstance() {
		Bundle args = new Bundle();
		AccountInfoFragment fragment = new AccountInfoFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public AccountInfoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_account_info, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onClick(View v) {

	}

}
