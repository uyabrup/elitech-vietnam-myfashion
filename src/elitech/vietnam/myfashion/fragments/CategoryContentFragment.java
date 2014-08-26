/**
 * Aug 19, 2014 1:29:37 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.ProductGridAdapter;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.fragments.BestOfTodayFragment.BestOfTodayCallback;

/**
 * @author Cong
 */
public class CategoryContentFragment extends AbstractFragment implements OnRefreshListener, OnItemClickListener {

	private static final int	LOADMORE	= 20;
	
	public static final String	ARG_ID		= "ARG_ID";
	public static final String	ARG_FASHION	= "ARG_FASHION";

	SwipeRefreshLayout			mLayoutRefresh;
	GridView					mGrid;
	ProductGridAdapter			mAdapter;

	int 						mId, mFashion;
	List<Product>				mProducts	= new ArrayList<>();

	public CategoryContentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bestofday, container, false);

		mLayoutRefresh = (SwipeRefreshLayout) view.findViewById(R.id.bod_layRefresh);
		mGrid = (GridView) view.findViewById(R.id.bod_gvMain);

		mLayoutRefresh.setColorSchemeResources(R.color.red, R.color.green, R.color.blue, R.color.orange);
		mAdapter = new ProductGridAdapter(mActivity, R.layout.item_bestofday, mProducts, this);
		mGrid.setAdapter(mAdapter);

		mLayoutRefresh.setOnRefreshListener(this);
		mGrid.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mId = getArguments().getInt(ARG_ID);
		mFashion = getArguments().getInt(ARG_FASHION);
		getData();
	}

	@Override
	public void onRefresh() {
		getData();
	}

	private void getData() {
		int member = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		mActivity.getServices().getCategoryProduct(mId, member, mFashion, mProducts.size(), LOADMORE,
				new Callback<List<Product>>() {
					@Override
					public void success(List<Product> arg0, Response arg1) {
						mProducts.clear();
						mProducts.addAll(arg0);
						mAdapter.notifyDataSetChanged();
						mLayoutRefresh.setRefreshing(false);
					}
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						mLayoutRefresh.setRefreshing(false);
					}
				});
	}

	private void getMoreData() {
		int member = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		mActivity.getServices().getCategoryProduct(mId, member, mFashion, mProducts.size(), LOADMORE,
				new Callback<List<Product>>() {
					@Override
					public void success(List<Product> arg0, Response arg1) {
						mProducts.addAll(arg0);
						mAdapter.notifyDataSetChanged();
						mLayoutRefresh.setRefreshing(false);
					}
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						mLayoutRefresh.setRefreshing(false);
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		((BestOfTodayCallback) mActivity).onItemClick(mProducts.get(position));
	}
}
