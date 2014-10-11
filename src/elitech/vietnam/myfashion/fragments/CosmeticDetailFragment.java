/**
 * Aug 19, 2014 4:19:53 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.Category;
import elitech.vietnam.myfashion.entities.Cosmetic;

/**
 * @author Cong
 *
 */
public class CosmeticDetailFragment extends AbstractFragment {

	FragmentTabHost mTabHost;
	
	CosmeticDetailCallback mCallback;
	Cosmetic mItem;
	
	public CosmeticDetailFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mCallback = mActivity.getController();
		mItem = mCallback.getCosmetic();
		mActivity.getActionBar().setTitle(mItem.Name);
		View view = inflater.inflate(R.layout.fragment_category_tabshost, container, false);
		
		mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup(mActivity, getChildFragmentManager(), android.R.id.tabcontent);
		
		for (int i = 0; i < mItem.Categories.size(); i++) {
			Category item = mItem.Categories.get(i);
			Bundle bundle = new Bundle();
			bundle.putInt(CosmeticContentFragment.ARG_CATEGORY_POSITION, i);
			mTabHost.addTab(mTabHost.newTabSpec(item.NameVN).setIndicator(item.NameVN), CosmeticContentFragment.class, bundle);
		}
		return view;
	}
	
	public static interface CosmeticDetailCallback {
		void setCosmetic(Cosmetic trademark);
		Cosmetic getCosmetic();
	}
}
