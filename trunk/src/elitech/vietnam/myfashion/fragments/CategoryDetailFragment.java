/**
 * Aug 19, 2014 10:36:35 AM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.Category;

/**
 * @author Cong
 *
 */
public class CategoryDetailFragment extends AbstractFragment {
	
	public static final String ARG_FASHION = "ARG_FASHION";
	
	FragmentTabHost mTabHost;
	
	CategoryDetailCallback mCallBack;
	int mFashion;
	Category mCategory;
	
	public CategoryDetailFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mCallBack = mActivity.getController();
		mCategory = mCallBack.getCategory();
		mActivity.getActionBar().setTitle(mCategory.NameVN);
		View view = inflater.inflate(R.layout.fragment_category_tabshost, container, false);

		mFashion = getArguments().getInt(ARG_FASHION);
		
		mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup(mActivity, getChildFragmentManager(), android.R.id.tabcontent);
		
		for (Category item : mCategory.subCategories) {
			Bundle bundle = new Bundle();
			bundle.putInt(CategoryContentFragment.ARG_ID, item.Id);
			bundle.putInt(CategoryContentFragment.ARG_FASHION, mFashion);
			mTabHost.addTab(mTabHost.newTabSpec(item.NameVN).setIndicator(item.NameVN), CategoryContentFragment.class, bundle);
		}
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	public static interface CategoryDetailCallback {
		void setCategory(Category category);
		Category getCategory();
		void onItemClick(Category category, int fashion);
	}
}
