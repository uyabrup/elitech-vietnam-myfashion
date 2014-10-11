/**
 * Oct 7, 2014 2:53:34 PM
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
import android.widget.ProgressBar;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.ProductGridAdapter;
import elitech.vietnam.myfashion.controllers.AppController;
import elitech.vietnam.myfashion.controllers.AppController.SearchCallback;
import elitech.vietnam.myfashion.dialogues.AddToCartDialog.AddToCartCallBack;
import elitech.vietnam.myfashion.entities.Color;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.Size;

/**
 * @author Cong
 *
 */
public class SearchFragment extends AbstractFragment implements SearchCallback, AddToCartCallBack, OnItemClickListener {

	public static final String ARG_SEARCHQUERY = "ARG_SEARCHQUERY";
	
	GridView mGrid;
	ProgressBar mLoading;
	ProductGridAdapter mAdapter;
	AppController mController;
	
	List<Product> mProducts = new ArrayList<>();
	
	public static SearchFragment newInstance(String query) {
		Bundle args = new Bundle();
		args.putString(ARG_SEARCHQUERY, query);
		SearchFragment fragment = new SearchFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public SearchFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.search);
		mController = mActivity.getController();
		mController.setSearchCallback(this);
		View view = inflater.inflate(R.layout.fragment_search_simple, container, false);
		
		mGrid = (GridView) view.findViewById(R.id.search_gvContent);
		mLoading = (ProgressBar) view.findViewById(R.id.search_prgLoading);
		
		mAdapter = new ProductGridAdapter(mActivity, R.layout.item_bestofday, mProducts, this);
		mGrid.setAdapter(mAdapter);
		
		mGrid.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		String query = getArguments().getString(ARG_SEARCHQUERY, "");
		getArguments().remove(ARG_SEARCHQUERY);
		if (query.length() > 0)
			doSearch(query);
	}
	
	@Override
	public void onDestroyView() {
		mController.setSearchCallback(null);
		super.onDestroyView();
	}
	
	@Override
	public void doSearch(String query) {
		mLoading.setVisibility(View.VISIBLE);
		mGrid.setVisibility(View.GONE);
		int n = mActivity.getLoggedinUser() == null ? -1 : mActivity.getLoggedinUser().Id;
		mActivity.getServices().searchSimple(query, n, new Callback<List<Product>>() {
			@Override
			public void success(List<Product> arg0, Response arg1) {
				mProducts.clear();
				mProducts.addAll(arg0);
				mAdapter.notifyDataSetChanged();
				mLoading.setVisibility(View.GONE);
				mGrid.setVisibility(View.VISIBLE);
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
				mLoading.setVisibility(View.GONE);
				mGrid.setVisibility(View.VISIBLE);
			}
		});
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mController.onItemClick(mProducts.get(position));
	}
}
