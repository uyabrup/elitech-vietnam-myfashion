/**
 * Sep 22, 2014 5:26:05 PM
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
public class MemberBaseFragment extends ChildBaseFragment {

	int mMemberId;
	
	public static MemberBaseFragment newInstance(int member) {
		Bundle bundle = new Bundle();
		bundle.putInt(MemberInfoFragment.ARG_MEMBERID, member);
		MemberBaseFragment fragment = new MemberBaseFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public MemberBaseFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_base, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMemberId = getArguments().getInt(MemberInfoFragment.ARG_MEMBERID, 0);
		
		getChildFragmentManager().beginTransaction()
		.add(R.id.base_container, 
				MemberInfoFragment.newInstance(mMemberId), 
				MemberInfoFragment.class.getName())
		.commit();
	}
}
