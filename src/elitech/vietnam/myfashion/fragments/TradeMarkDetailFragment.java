/**
 * Aug 19, 2014 4:19:53 PM
 */
package elitech.vietnam.myfashion.fragments;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.Category;
import elitech.vietnam.myfashion.entities.TradeMark;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Cong
 *
 */
public class TradeMarkDetailFragment extends AbstractFragment {

	FragmentTabHost mTabHost;
	
	TradeMark mItem;
	
	public TradeMarkDetailFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_category_tabshost, container, false);
		
		mItem = mActivity.getTradeMark();
		
		mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup(mActivity, getChildFragmentManager(), android.R.id.tabcontent);
		
		for (int i = 0; i < mItem.Categories.size(); i++) {
			Category item = mItem.Categories.get(i);
			Bundle bundle = new Bundle();
			bundle.putInt(TradeMarkContentFragment.ARG_CATEGORY_POSITION, i);
			mTabHost.addTab(mTabHost.newTabSpec(item.NameVN).setIndicator(item.NameVN), TradeMarkContentFragment.class, bundle);
		}
		return view;
	}
}
