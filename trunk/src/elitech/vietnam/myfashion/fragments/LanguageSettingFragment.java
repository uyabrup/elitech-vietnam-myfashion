/**
 * Sep 25, 2014 4:04:21 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

/**
 * @author Cong
 *
 */
public class LanguageSettingFragment extends AbstractFragment implements OnClickListener {

	public static LanguageSettingFragment newInstance() {
		Bundle args = new Bundle();
		LanguageSettingFragment fragment = new LanguageSettingFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public LanguageSettingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onClick(View v) {

	}

}
