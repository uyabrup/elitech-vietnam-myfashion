/**
 * Sep 24, 2014 10:14:15 AM
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
import elitech.vietnam.myfashion.adapters.ProductLikedGridAdapter;
import elitech.vietnam.myfashion.dialogues.AddToCartDialog.AddToCartCallBack;
import elitech.vietnam.myfashion.entities.Color;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.Size;

/**
 * @author Cong
 *
 */
public class MemberFavoriteFragment extends AbstractFragment implements OnRefreshListener, AddToCartCallBack, OnItemClickListener {

	private static final int	LOADMORE	= 20;
	
	SwipeRefreshLayout			mLayoutRefresh;
	GridView					mGrid;
	ProductLikedGridAdapter		mAdapter;

	int 						mMemberId;
	List<Product>				mProducts	= new ArrayList<>();
	
	public static MemberFavoriteFragment newInstance(int member) {
		Bundle bundle = new Bundle();
		bundle.putInt(MemberInfoFragment.ARG_MEMBERID, member);
		MemberFavoriteFragment fragment = new MemberFavoriteFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public MemberFavoriteFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bestofday, container, false);

		mLayoutRefresh = (SwipeRefreshLayout) view.findViewById(R.id.bod_layRefresh);
		mGrid = (GridView) view.findViewById(R.id.bod_gvMain);

		mLayoutRefresh.setColorSchemeResources(R.color.red, R.color.green, R.color.blue, R.color.orange);
		mAdapter = new ProductLikedGridAdapter(mActivity, R.layout.item_bestofday, mProducts, this, mMemberId);
		mGrid.setAdapter(mAdapter);

		mLayoutRefresh.setOnRefreshListener(this);
		mGrid.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMemberId = getArguments().getInt(MemberInfoFragment.ARG_MEMBERID);
		getData();
	}

	@Override
	public void onRefresh() {
		getData();
	}

	private void getData() {
		int member = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		mActivity.getServices().getLikedProduct(mMemberId, member, 0, LOADMORE,
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
		mActivity.getServices().getLikedProduct(mMemberId, member, mProducts.size(), LOADMORE,
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
		mActivity.getController().onItemClick(mProducts.get(position));
	}

	@Override
	public void yesClick(int request, int position, Color color, Size size, int quantity) {
		mActivity.getController().addToCart(mProducts.get(position), color, size, quantity);
	}

	@Override
	public void noClick(int request, int position) {
	}

	@Override
	public Product getData(int request, int position) {
		return mProducts.get(position);
	}
}	
