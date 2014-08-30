/**
 * Aug 7, 2014 2:03:25 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.CategoryAdapter;
import elitech.vietnam.myfashion.entities.Category;

/**
 * @author Cong
 */
public class CategoryFragment extends AbstractFragment implements OnItemClickListener {

	public static final String	TAG			= "CATEGORY_TAG";

	public static final String	TAG_WOMEN	= "TAG_WOMEN";
	public static final String	TAG_OFFICE	= "TAG_OFFICE";
	public static final String	TAG_MEN		= "TAG_MEN";
	public static final String	TAG_WINTER	= "TAG_WINTER";
	
	GridView mGrid;
	CategoryAdapter mAdapter;
	
	List<Category> mCategories = new ArrayList<>();
	String mTag;
	int mFashion;
	
	public CategoryFragment() {
	}
	
	public static final CategoryFragment newInstance(String tag) {
		Bundle bundle = new Bundle();
		bundle.putString(TAG, tag);
		CategoryFragment frag = new CategoryFragment();
		frag.setArguments(bundle);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mTag = getArguments().getString(TAG);
		if (mTag.equals(TAG_WOMEN))
			mFashion = 0;
		if (mTag.equals(TAG_MEN))
			mFashion = 1;
		if (mTag.equals(TAG_OFFICE))
			mFashion = 2;
		if (mTag.equals(TAG_WINTER))
			mFashion = 3;
		
		View view = inflater.inflate(R.layout.fragment_category, container, false);

		mGrid = (GridView) view.findViewById(R.id.category_grvCategory);
		mAdapter = new CategoryAdapter(mActivity, R.layout.item_category, mCategories, mFashion);
		mGrid.setAdapter(mAdapter);
		
		mGrid.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mCategories.clear();
		getData();
	}
	
	private void getData() {
		mActivity.getServices().getCategoryByFashion(mFashion, new Callback<List<Category>>() {
			@Override
			public void success(List<Category> arg0, Response arg1) {
				mCategories.addAll(arg0);
				mAdapter.notifyDataSetChanged();
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mActivity.getController().onItemClick(mCategories.get(position), mFashion);
	}
	
	/**
	 * This interface define callback methods for communication with activity
	 * Host activity must implement this interface
	 * @author Cong
	 *
	 */
	public static interface CategoryCallback {
		public void onItemClick(Category item, int fashion);
	}
}
