/**
 * Aug 7, 2014 1:27:50 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 */
public class BaseFragment extends Fragment {

	public static final String	TAG					= "BASETAG";
	public static final String	ARG_POSITION		= "ARG_POSITION";

	public static final String	TAG_SPLASH			= "TAG_SPLASH";
	public static final String	TAG_BESTOFDAY		= "TAG_BESTOFDAY";
	public static final String	TAG_WOMENFASHION	= "TAG_WOMENFASHION";
	public static final String	TAG_OFFICEFASHION	= "TAG_OFFICEFASHION";
	public static final String	TAG_MENFASHION		= "TAG_MENFASHION";
	public static final String	TAG_WINTERFASHION	= "TAG_WINTERFASHION";
	public static final String	TAG_BRANCHES		= "TAG_BRANCHES";
	public static final String	TAG_MYSHOPPING		= "TAG_MYSHOPPING";
	public static final String	TAG_STYLER			= "TAG_STYLER";
	public static final String	TAG_MYSTYLE			= "TAG_MYSTYLE";
	public static final String	TAG_REVIEW			= "TAG_REVIEW";
	public static final String	TAG_SETTINGS		= "TAG_SETTINGS";

	MainActivity				mActivity;

	String						mTag;
	int							mPos;
	
	public static BaseFragment newInstance(String tag) {
		Bundle bundle = new Bundle();
		bundle.putString(TAG, tag);
		BaseFragment fragment = new BaseFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public static BaseFragment newInstance(String tag, Bundle args) {
		args.putString(TAG, tag);
		BaseFragment fragment = new BaseFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public BaseFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_base, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mTag = getArguments().getString(TAG, "");
		mPos = getArguments().getInt(ARG_POSITION, -1);
		Log.w("BaseFragment", "onViewCreated");

		if (savedInstanceState == null) {
			Log.w("savedInstanceState", "NULL");
			Fragment fragment = null;
			if (mTag.equals(TAG_SPLASH))
				fragment = new FirstLoadingFragment();
			if (mTag.equals(TAG_BESTOFDAY))
				fragment = new BestOfTodayFragment();
			if (mTag.equals(TAG_WOMENFASHION))
				fragment = CategoryFragment.newInstance(CategoryFragment.TAG_WOMEN);
			if (mTag.equals(TAG_OFFICEFASHION))
				fragment = CategoryFragment.newInstance(CategoryFragment.TAG_OFFICE);
			if (mTag.equals(TAG_MENFASHION))
				fragment = CategoryFragment.newInstance(CategoryFragment.TAG_MEN);
			if (mTag.equals(TAG_WINTERFASHION))
				fragment = CategoryFragment.newInstance(CategoryFragment.TAG_WINTER);
			if (mTag.equals(TAG_BRANCHES + mPos))
				fragment = new TradeMarkDetailFragment();
			if (mTag.equals(TAG_MYSHOPPING))
				fragment = new ShoppingCartFragment();
			if (mTag.equals(TAG_STYLER))
				fragment = new StylerBestTopFragment();
			
			getChildFragmentManager().beginTransaction().add(R.id.base_container, fragment, mTag).commit();
		} else
			Log.w("savedInstanceState", "NOT NULL");
	}
	
	public boolean popFragment() {
		boolean isPop = false;
		if (getChildFragmentManager().getBackStackEntryCount() > 0) {
			isPop = true;
			getChildFragmentManager().popBackStack();
		}
		return isPop;
	}
	
	public boolean popAllFragment() {
		if (getChildFragmentManager().getBackStackEntryCount() > 0) {
			return getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		return false;
	}
	
	public void replaceFragment(Fragment fragment, boolean addToBackStack) {
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		if (addToBackStack) {
			transaction.addToBackStack(fragment.getClass().getName());
		}
		transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		transaction.replace(R.id.base_container, fragment, fragment.getClass().getName());
		transaction.commit();
	}
}
