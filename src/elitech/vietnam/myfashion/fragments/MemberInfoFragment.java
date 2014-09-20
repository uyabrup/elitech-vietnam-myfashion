/**
 * Sep 17, 2014 1:32:22 PM
 */
package elitech.vietnam.myfashion.fragments;

import elitech.vietnam.myfashion.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Cong
 *
 */
public class MemberInfoFragment extends AbstractFragment {

	public static final String ARG_MEMBERID = "";
	
	public static MemberInfoFragment newInstance(int member) {
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_MEMBERID, member);
		MemberInfoFragment fragment = new MemberInfoFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public MemberInfoFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_member, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
}
