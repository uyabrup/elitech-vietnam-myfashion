/**
 * Aug 20, 2014 10:37:32 AM
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
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.ProductGridAdapter;
import elitech.vietnam.myfashion.dialogues.AddToCartDialog.AddToCartCallBack;
import elitech.vietnam.myfashion.entities.Category;
import elitech.vietnam.myfashion.entities.Color;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.Size;
import elitech.vietnam.myfashion.widgets.QuickReturnGridView;

/**
 * @author Cong
 */
public class TradeMarkContentFragment extends AbstractFragment implements OnRefreshListener, AddToCartCallBack, OnCheckedChangeListener, OnItemClickListener {

	private static final int	LOADMORE				= 20;
	public static final String	ARG_CATEGORY_POSITION	= "ARG_CATEGORY_POSITION";

	SwipeRefreshLayout			mRefresh;
	QuickReturnGridView			mGrid;
	HorizontalScrollView		mQuickLayout;
	RadioGroup					mQuickItem;

	Category					mItem;
	SparseIntArray				mMap;
	List<Product>				mProducts				= new ArrayList<>();
	ProductGridAdapter			mAdapter;

	int							mCurrent;

	public TradeMarkContentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_trademarks_content, container, false);

		mItem = mActivity.getController().getTradeMark().Categories.get(getArguments().getInt(ARG_CATEGORY_POSITION));
		mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.trademarks_layRefresh);
		mGrid = (QuickReturnGridView) view.findViewById(R.id.trademarks_gvMain);
		mQuickLayout = (HorizontalScrollView) view.findViewById(R.id.trademarks_layQuickReturn);
		mQuickItem = (RadioGroup) view.findViewById(R.id.trademarks_layQuickItem);

		mRefresh.setColorSchemeResources(R.color.red, R.color.green, R.color.blue, R.color.orange);

		mAdapter = new ProductGridAdapter(mActivity, R.layout.item_bestofday, mProducts, this);
		mGrid.setAdapter(mAdapter);

		mGrid.setQuickReturnView(mQuickLayout);
		
		mGrid.setOnItemClickListener(this);
		mRefresh.setOnRefreshListener(this);
		
		if (mItem.subCategories.size() > 0) {
			mMap = new SparseIntArray();
			for (Category item : mItem.subCategories) {
				RadioButton t = (RadioButton) LayoutInflater.from(mActivity).inflate(R.layout.item_trademark_quickreturn,
						mQuickItem, false);
				t.setText(item.NameVN);
				mQuickItem.addView(t);
				mMap.put(t.getId(), item.Id);
			}
			mQuickItem.setOnCheckedChangeListener(this);
		} else {
			mQuickLayout.setVisibility(View.GONE);
		}
		
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (mItem.subCategories.size() > 0) {
			mQuickItem.getChildAt(0).performClick();
		} else {
			mCurrent = mItem.Id;
			mRefresh.setRefreshing(true);
			getData();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		mCurrent = mMap.get(checkedId);
		mRefresh.setRefreshing(true);
		mProducts.clear();
		mAdapter.notifyDataSetChanged();
		getData();
	}

	private void getData() {
		int account = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		mActivity.getServices().getCategoryProduct(mCurrent, account, -1, 0, LOADMORE, new Callback<List<Product>>() {
			@Override
			public void success(List<Product> arg0, Response arg1) {
				mProducts.clear();
				mProducts.addAll(arg0);
				mAdapter.notifyDataSetChanged();
				mRefresh.setRefreshing(false);
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
				mRefresh.setRefreshing(false);
			}
		});
	}

	private void getMore() {
		int account = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		mActivity.getServices().getCategoryProduct(mCurrent, account, -1, mProducts.size(), LOADMORE,
			new Callback<List<Product>>() {
				@Override
				public void success(List<Product> arg0, Response arg1) {
					mProducts.addAll(arg0);
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
		mActivity.getController().onItemClick(mProducts.get(position));
	}

	@Override
	public void onRefresh() {
		getData();
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
