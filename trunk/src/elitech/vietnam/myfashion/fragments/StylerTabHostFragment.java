/**
 * Sep 12, 2014 4:51:53 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class StylerTabHostFragment extends AbstractFragment {

	private static final String TABSPEC_BEST = "TABSPEC_BEST";
	private static final String TABSPEC_FOLLOWER = "TABSPEC_FOLLOWER";
	private static final String TABSPEC_FOLLOWING = "TABSPEC_FOLLOWING";
	private static final String TABSPEC_SELF = "TABSPEC_SELF";
	
	FragmentTabHost mTabHost;
	
	public StylerTabHostFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_category_tabshost, container, false);
		mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup(mActivity, getChildFragmentManager(), android.R.id.tabcontent);
		
		mTabHost.addTab(mTabHost.newTabSpec(TABSPEC_BEST).setIndicator(TABSPEC_BEST), StylerBestTopFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(TABSPEC_FOLLOWER).setIndicator(TABSPEC_FOLLOWER), AbstractFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(TABSPEC_FOLLOWING).setIndicator(TABSPEC_FOLLOWING), AbstractFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(TABSPEC_SELF).setIndicator(TABSPEC_SELF), AbstractFragment.class, null);
		
		return view;
	}
}
