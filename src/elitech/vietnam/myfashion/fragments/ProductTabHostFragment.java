/**
 * Aug 14, 2014 9:12:08 AM
 */
package elitech.vietnam.myfashion.fragments;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author Cong
 *
 */
public class ProductTabHostFragment extends Fragment {
	
	public static final String ARG_PRODUCTID = "ARG_PRODUCTID";
	
	private static final String TABSPEC_MAIN = "TABSPEC_MAIN";
	private static final String TABSPEC_COMMENT = "TABSPEC_COMMENT";
	private static final String TABSPEC_REVIEW = "TABSPEC_REVIEW";
	
	MainActivity mActivity;
	
	FragmentTabHost mTabHost;
	
	public static ProductTabHostFragment newInstance(int productId) {
		ProductTabHostFragment frag = new ProductTabHostFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_PRODUCTID, productId);
		frag.setArguments(bundle);
		return frag;
	}

	public ProductTabHostFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_product_tabhost, container, false);
		mTabHost = (FragmentTabHost) view.findViewById(R.id.product_tabhost);
		mTabHost.setup(mActivity, getChildFragmentManager(), R.id.product_realtabcontent);
		
		mTabHost.addTab(mTabHost.newTabSpec(TABSPEC_MAIN).setIndicator(createTabView(R.drawable.ic_tabs_image)), ProductDetailFragment.class, getArguments());
		mTabHost.addTab(mTabHost.newTabSpec(TABSPEC_COMMENT).setIndicator(createTabView(R.drawable.ic_tabs_comments)), ProductCommentFragment.class, getArguments());
		mTabHost.addTab(mTabHost.newTabSpec(TABSPEC_REVIEW).setIndicator(createTabView(R.drawable.ic_tabs_star)), ProductReviewFragment.class, getArguments());
		
		return view;
	}
	
	private View createTabView(int resId) {
		View view = LayoutInflater.from(mActivity).inflate(R.layout.tabview_product, null);
		ImageView image = (ImageView) view.findViewById(R.id.tabview_product_img);
		image.setImageResource(resId);
		return view;
	}
}
