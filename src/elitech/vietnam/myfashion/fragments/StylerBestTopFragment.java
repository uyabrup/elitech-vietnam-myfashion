/**
 * Sep 12, 2014 5:14:52 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.etsy.android.grid.StaggeredGridView;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.StyleGridAdapter;
import elitech.vietnam.myfashion.entities.Post;

/**
 * @author Cong
 *
 */
public class StylerBestTopFragment extends AbstractFragment implements OnRefreshListener, OnItemClickListener {

	private static final int LOADMORE = 20;
	
	StaggeredGridView mGrid;
	SwipeRefreshLayout mRefresh;
	
	StyleGridAdapter mAdapter;
	List<Post> mPosts = new ArrayList<>();
	
	StylerBestCallback mCallBack;
	
	public StylerBestTopFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mCallBack = mActivity.getController();
		View view = inflater.inflate(R.layout.fragment_style_besttop, container, false);
		
		mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.stylerbest_refreshLayout);
		mGrid = (StaggeredGridView) view.findViewById(R.id.stylerbest_gridView);
		
		mAdapter = new StyleGridAdapter(mActivity, mPosts);
		mGrid.setAdapter(mAdapter);
		
		mRefresh.setColorSchemeResources(R.color.swipe_1, R.color.swipe_2, R.color.swipe_3, R.color.swipe_4);
		
		mRefresh.setOnRefreshListener(this);
		mGrid.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRefresh.setRefreshing(true);
		getData();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (mActivity.getLoggedinUser() != null) {
			inflater.inflate(R.menu.style, menu);
			final MenuItem camera = menu.findItem(R.id.action_createPost);
			View view = MenuItemCompat.getActionView(camera).findViewById(R.id.menuitem_action_takePhoto);
		    view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onOptionsItemSelected(camera);
				}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_createPost:
			mActivity.getCurrentBase().replaceFragment(CreatePostFragment.newInstance(mActivity.getLoggedinUser().Id), true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	private void getData() {
		int member = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		mActivity.getServices().getStylerBest(member, 0, LOADMORE, new Callback<List<Post>>() {
			@Override
			public void success(List<Post> arg0, Response arg1) {
				mPosts.clear();
				mPosts.addAll(arg0);
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

	@Override
	public void onRefresh() {
		getData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.stylerbest_gridView:
			mCallBack.onItemClick(mPosts.get(position));
			break;
		default:
			break;
		}
	}
	
	public static interface StylerBestCallback {
		public void onItemClick(Post post);
	}
}
