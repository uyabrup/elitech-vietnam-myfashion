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
public class NotificationSettingFragment extends AbstractFragment implements OnClickListener {

	public static NotificationSettingFragment newInstance() {
		Bundle args = new Bundle();
		NotificationSettingFragment fragment = new NotificationSettingFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public NotificationSettingFragment() {
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
