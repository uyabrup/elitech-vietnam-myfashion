/**
 * Aug 14, 2014 2:46:14 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.ReviewAdapter;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.Review;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Cong
 *
 */
public class ProductReviewFragment extends AbstractFragment implements OnItemClickListener, OnRefreshListener {
	
	private static final int LOADMORE = 20;

	SwipeRefreshLayout mRefresh;
	ListView mListView;
	TextView mNodata;
	
	ReviewAdapter mAdapter;
	List<Review> mReviews = new ArrayList<>();

	Product mProduct;

	public ProductReviewFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_product_review, container, false);
		
		mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.detail_review_layRefresh);
		mListView = (ListView) view.findViewById(R.id.detail_review_lvReview);
		mNodata = (TextView) view.findViewById(R.id.txtNodata);
		
		mAdapter = new ReviewAdapter(mActivity, R.layout.fragment_product_review, mReviews);
		mListView.setAdapter(mAdapter);

		mRefresh.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mProduct = mActivity.getController().getProduct();
		
		mRefresh.setRefreshing(true);
		onRefresh();
	}

	@Override
	public void onRefresh() {
		mReviews.clear();
		getData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}
	
	private void getData() {
		mActivity.getServices().getProductReviews(mProduct.Id, mReviews.size(), LOADMORE, new Callback<List<Review>>() {
			@Override
			public void success(List<Review> arg0, Response arg1) {
				mReviews.addAll(arg0);
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
}
