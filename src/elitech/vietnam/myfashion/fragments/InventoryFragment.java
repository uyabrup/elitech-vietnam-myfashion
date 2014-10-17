/**
 * Jul 29, 2014 5:03:49 PM
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
import elitech.vietnam.myfashion.adapters.EndlessScrollListener;
import elitech.vietnam.myfashion.adapters.ProductGridAdapter;
import elitech.vietnam.myfashion.controllers.AppController;
import elitech.vietnam.myfashion.dialogues.AddToCartDialog.AddToCartCallBack;
import elitech.vietnam.myfashion.entities.Color;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.Size;

/**
 * @author Cong
 *
 */
public class InventoryFragment extends AbstractFragment implements OnRefreshListener, OnItemClickListener, AddToCartCallBack {

	private static final int LOADMORE = 20;
	
	AppController mCallback;
	
	SwipeRefreshLayout mLayoutRefresh;
	GridView mGrid;
	ProductGridAdapter mAdapter;

	List<Product> mProducts = new ArrayList<>();
	
	public static InventoryFragment newInstance() {
		InventoryFragment fragment = new InventoryFragment();
		return fragment;
	}
	
	public InventoryFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.inventory);
		View view = inflater.inflate(R.layout.fragment_bestofday, container, false);
		mCallback = mActivity.getController();
		
		mLayoutRefresh = (SwipeRefreshLayout) view.findViewById(R.id.bod_layRefresh);
		mGrid = (GridView) view.findViewById(R.id.bod_gvMain);
		
		mLayoutRefresh.setColorSchemeResources(R.color.red, R.color.green, R.color.blue, R.color.orange);
		mAdapter = new ProductGridAdapter(mActivity, R.layout.item_bestofday, mProducts, this);
		mGrid.setAdapter(mAdapter);
		mGrid.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadmore() {
				int member = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
				mActivity.getServices().getInventory(member, mProducts.size(), LOADMORE, new Callback<List<Product>>() {
					@Override
					public void success(List<Product> arg0, Response arg1) {
						mProducts.addAll(arg0);
						mAdapter.notifyDataSetChanged();
						setLoading(false);
						if (arg0.size() < 20)
							setEnd(true);
					}
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						setLoading(false);
					}
				});
			}
			@Override
			public int getItemCount() {
				return mProducts.size();
			}
		});
		
		mLayoutRefresh.setOnRefreshListener(this);
		mGrid.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLayoutRefresh.setRefreshing(true);
		getData();
	}

	@Override
	public void onRefresh() {
		getData();
	}

	private void getData() {
		int member = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		mActivity.getServices().getInventory(member, 0, LOADMORE, new Callback<List<Product>>() {
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mCallback.onItemClick(mProducts.get(position));
	}
	
	@Override
	public Product getData(int request, int position) {
		return mProducts.get(position);
	}

	@Override
	public void noClick(int request, int position) {
		
	}

	@Override
	public void yesClick(int request, int position, Color color, Size size, int quantity) {
		mActivity.getController().addToCart(mProducts.get(position), color, size, quantity);
	}
}
