/**
 * Sep 24, 2014 1:25:06 PM
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
import android.widget.ListView;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.EndlessScrollListener;
import elitech.vietnam.myfashion.adapters.MemberReviewAdapter;
import elitech.vietnam.myfashion.entities.Review;

/**
 * @author Cong
 *
 */
public class MemberReviewFragment extends AbstractFragment implements OnRefreshListener, OnItemClickListener {

	private static final int LOADMORE = 20;
	public static final String ARG_MEMBERID = "ARG_MEMBERID";
	
	SwipeRefreshLayout mRefresh;
	ListView mListView;
	
	List<Review> mReviews = new ArrayList<>();
	MemberReviewAdapter mAdapter;
	
	int mMemberId;
	
	public static MemberReviewFragment newInstance(int memberId) {
		Bundle args = new Bundle();
		args.putInt(ARG_MEMBERID, memberId);
		MemberReviewFragment fragment = new MemberReviewFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public MemberReviewFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.review);
		mMemberId = getArguments().getInt(ARG_MEMBERID, 0);
		View view = inflater.inflate(R.layout.fragment_member_follow, container, false);
		
		mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.memberfollow_layRefresh);
		mListView = (ListView) view.findViewById(R.id.memberfollow_lvContent);
		
		mRefresh.setColorSchemeResources(R.color.swipe_1, R.color.swipe_2, R.color.swipe_3, R.color.swipe_4);
		mAdapter = new MemberReviewAdapter(mActivity, mReviews);
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadmore() {
				mActivity.getServices().getReviewById(mMemberId, mReviews.size(), LOADMORE, new Callback<List<Review>>() {
					@Override
					public void success(List<Review> arg0, Response arg1) {
						mReviews.clear();
						mReviews.addAll(arg0);
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
				return mReviews.size();
			}
		});
		
		mRefresh.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mRefresh.setRefreshing(true);
		getData();
	}

	private void getData() {
		mActivity.getServices().getReviewById(mMemberId, 0, LOADMORE, new Callback<List<Review>>() {
			@Override
			public void success(List<Review> arg0, Response arg1) {
				mReviews.clear();
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
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}

	@Override
	public void onRefresh() {
		getData();
	}
}
