/**
 * Sep 24, 2014 9:30:38 AM
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

import com.etsy.android.grid.StaggeredGridView;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.EndlessScrollListener;
import elitech.vietnam.myfashion.adapters.StyleLikedGridAdapter;
import elitech.vietnam.myfashion.entities.Post;
import elitech.vietnam.myfashion.fragments.StylerBestTopFragment.StylerBestCallback;

/**
 * @author Cong
 *
 */
public class MemberStyleFragment extends AbstractFragment implements OnRefreshListener, OnItemClickListener {

	private static final int LOADMORE = 20;
	public static final String ARG_TYPE = "ARG_TYPE";
	
	StaggeredGridView mGrid;
	SwipeRefreshLayout mRefresh;
	
	StyleLikedGridAdapter mAdapter;
	List<Post> mPosts = new ArrayList<>();
	int mMemberId;
	StyleType mType;

	static enum StyleType {
		STYLE,
		SUGGEST
	}
	
	StylerBestCallback mCallBack;
	
	public static MemberStyleFragment newInstance(int memberId, StyleType type) {
		Bundle args = new Bundle();
		args.putInt(MemberInfoFragment.ARG_MEMBERID, memberId);
		args.putSerializable(ARG_TYPE, type);
		MemberStyleFragment fragment = new MemberStyleFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public MemberStyleFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.style);
		mCallBack = mActivity.getController();
		mMemberId = getArguments().getInt(MemberInfoFragment.ARG_MEMBERID, 0);
		mType = (StyleType) getArguments().getSerializable(ARG_TYPE);
		View view = inflater.inflate(R.layout.fragment_style_besttop, container, false);
		
		mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.stylerbest_refreshLayout);
		mGrid = (StaggeredGridView) view.findViewById(R.id.stylerbest_gridView);
		
		mAdapter = new StyleLikedGridAdapter(mActivity, mPosts, mMemberId);
		mGrid.setAdapter(mAdapter);
		mGrid.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadmore() {
				int account = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
				switch (mType) {
				case STYLE:
					mActivity.getServices().getStyleById(mMemberId, account, mPosts.size(), LOADMORE, new Callback<List<Post>>() {
						@Override
						public void success(List<Post> arg0, Response arg1) {
							mPosts.addAll(arg0);
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
					break;
				case SUGGEST:
					mActivity.getServices().getLikedStyle(mMemberId, account, mPosts.size(), LOADMORE, new Callback<List<Post>>() {
						@Override
						public void success(List<Post> arg0, Response arg1) {
							mPosts.addAll(arg0);
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
					break;
				default:
					break;
				}
			}
			@Override
			public int getItemCount() {
				return mPosts.size();
			}
		});
		
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

	private void getData() {
		int account = (mActivity.getLoggedinUser() != null) ? mActivity.getLoggedinUser().Id : -1;
		switch (mType) {
		case STYLE:
			mActivity.getServices().getStyleById(mMemberId, account, 0, LOADMORE, new Callback<List<Post>>() {
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
			break;
		case SUGGEST:
			mActivity.getServices().getLikedStyle(mMemberId, account, 0, LOADMORE, new Callback<List<Post>>() {
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
			break;
		default:
			break;
		}
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

	@Override
	public void onRefresh() {
		getData();
	}
}
