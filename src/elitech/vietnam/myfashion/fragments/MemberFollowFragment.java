/**
 * Sep 23, 2014 3:39:30 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.MemberFollowAdapter;
import elitech.vietnam.myfashion.entities.Member;
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

/**
 * @author Cong
 *
 */
public class MemberFollowFragment extends AbstractFragment implements OnItemClickListener, OnRefreshListener {

	private static final int LOADMORE = 20;
	public static final String ARG_MEMBERID = "ARG_MEMBERID";
	public static final String ARG_TYPE = "ARG_TYPE";
	
	SwipeRefreshLayout mRefresh;
	ListView mListView;
	
	List<Member> mMembers = new ArrayList<>();
	MemberFollowAdapter mAdapter;
	
	int mMemberId;
	FollowType mType;
	
	static enum FollowType {
		FOLLOWER,
		FOLLOWING
	}
	
	public static MemberFollowFragment newInstance(int memberId, FollowType type) {
		Bundle args = new Bundle();
		args.putInt(ARG_MEMBERID, memberId);
		args.putSerializable(ARG_TYPE, type);
		MemberFollowFragment fragment = new MemberFollowFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public MemberFollowFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mMemberId = getArguments().getInt(MemberInfoFragment.ARG_MEMBERID, 0);
		mType = (FollowType) getArguments().getSerializable(ARG_TYPE);
		if (mType == FollowType.FOLLOWER)
			mActivity.getActionBar().setTitle(R.string.follower);
		else
			mActivity.getActionBar().setTitle(R.string.following);
		View view = inflater.inflate(R.layout.fragment_member_follow, container, false);
		
		mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.memberfollow_layRefresh);
		mListView = (ListView) view.findViewById(R.id.memberfollow_lvContent);
		
		mRefresh.setColorSchemeResources(R.color.swipe_1, R.color.swipe_2, R.color.swipe_3, R.color.swipe_4);
		mAdapter = new MemberFollowAdapter(mActivity, mMembers, mMemberId);
		mListView.setAdapter(mAdapter);
		
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
		int n = mActivity.getLoggedinUser() == null ? -1 : mActivity.getLoggedinUser().Id;
		switch (mType) {
		case FOLLOWER:
			mActivity.getServices().getFollower(mMemberId, n, 0, LOADMORE, new Callback<List<Member>>() {
				@Override
				public void success(List<Member> arg0, Response arg1) {
					mMembers.clear();
					mMembers.addAll(arg0);
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
		case FOLLOWING:
			mActivity.getServices().getFollowing(mMemberId, n, 0, LOADMORE, new Callback<List<Member>>() {
				@Override
				public void success(List<Member> arg0, Response arg1) {
					mMembers.clear();
					mMembers.addAll(arg0);
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
		case R.id.memberfollow_lvContent:
			MemberInfoFragment fragment = MemberInfoFragment.newInstance(mMembers.get(position).Id);
			mActivity.getCurrentBase().replaceFragment(fragment, true);
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
