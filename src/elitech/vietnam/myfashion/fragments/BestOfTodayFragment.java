/**
 * Jul 29, 2014 5:03:49 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.ProductGridAdapter;
import elitech.vietnam.myfashion.dialogues.AddToCartDialog.AddToCartCallBack;
import elitech.vietnam.myfashion.entities.Product;

/**
 * @author Cong
 *
 */
public class BestOfTodayFragment extends AbstractFragment implements OnRefreshListener, OnItemClickListener, AddToCartCallBack {

	private static final int LOADMORE = 20;
	
	SwipeRefreshLayout mLayoutRefresh;
	GridView mGrid;
	ProductGridAdapter mAdapter;

	List<Product> mBest = new ArrayList<>();
	
	public BestOfTodayFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bestofday, container, false);
		
		mLayoutRefresh = (SwipeRefreshLayout) view.findViewById(R.id.bod_layRefresh);
		mGrid = (GridView) view.findViewById(R.id.bod_gvMain);
		
		mLayoutRefresh.setColorSchemeResources(R.color.red, R.color.green, R.color.blue, R.color.orange);
		mAdapter = new ProductGridAdapter(mActivity, R.layout.item_bestofday, mBest, this);
		mGrid.setAdapter(mAdapter);
		
		mLayoutRefresh.setOnRefreshListener(this);
		mGrid.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getData();
	}

	@Override
	public void onRefresh() {
		getData();
	}

	private void getData() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		int member = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		mActivity.getServices().getBestOfDay(format.format(new Date()), member, 0, LOADMORE, new Callback<List<Product>>() {
			@Override
			public void success(List<Product> arg0, Response arg1) {
				mBest.clear();
				mBest.addAll(arg0);
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		int member = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		mActivity.getServices().getBestOfDay(format.format(new Date()), member, mBest.size(), LOADMORE, new Callback<List<Product>>() {
			@Override
			public void success(List<Product> arg0, Response arg1) {
				mBest.addAll(arg0);
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
		((BestOfTodayCallback) mActivity).onItemClick(mBest.get(position));
	}
	
	
	public static interface BestOfTodayCallback {
		public void onItemClick(Product product);
	}


	@Override
	public void yesClick(int request, int position) {
		Toast.makeText(mActivity, "Yes", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void noClick(int request, int position) {
		Toast.makeText(mActivity, "No", Toast.LENGTH_SHORT).show();
	}
}
