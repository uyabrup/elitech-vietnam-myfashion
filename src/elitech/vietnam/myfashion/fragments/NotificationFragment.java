/**
 * Oct 18, 2014 9:20:32 AM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.EndlessScrollListener;
import elitech.vietnam.myfashion.adapters.NotificationAdapter;
import elitech.vietnam.myfashion.entities.Notify;
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
public class NotificationFragment extends AbstractFragment implements OnItemClickListener, OnRefreshListener {

	private static final int LOADMORE = 20;
	
	private ListView mList;
	private SwipeRefreshLayout mRefresh;
	private NotificationAdapter mAdapter;
	private List<Notify> mNotifies = new ArrayList<>();
	
	public static NotificationFragment newInstance() {
		NotificationFragment fragment = new NotificationFragment();
		return fragment;
	}
	
	public NotificationFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notifications, container, false);
		
		mList = (ListView) view.findViewById(R.id.notify_listView);
		mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.notify_layoutRefresh);
		
		mRefresh.setColorSchemeResources(R.color.swipe_1, R.color.swipe_2, R.color.swipe_3, R.color.swipe_4);
		mAdapter = new NotificationAdapter(mActivity, mNotifies);
		mList.setAdapter(mAdapter);
		
		mRefresh.setOnRefreshListener(this);
		mList.setOnItemClickListener(this);
		mList.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadmore() {
				mActivity.getServices().getMemberNotification(mActivity.getLoggedinUser().Id, mNotifies.size(), LOADMORE, new Callback<List<Notify>>() {
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						setLoading(false);
					}
					@Override
					public void success(List<Notify> arg0, Response arg1) {
						mNotifies.addAll(arg0);
						mAdapter.notifyDataSetChanged();
						setLoading(false);
						if (arg0.size() < 20)
							setEnd(true);
					}});
			}
			@Override
			public int getItemCount() {
				return mNotifies.size();
			}
		});
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRefresh.setRefreshing(true);
		getData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}

	@Override
	public void onRefresh() {
		getData();
	}
	
	private void getData() {
		mActivity.getServices().getMemberNotification(mActivity.getLoggedinUser().Id, 0, LOADMORE, new Callback<List<Notify>>() {
			@Override
			public void success(List<Notify> arg0, Response arg1) {
				mNotifies.clear();
				mNotifies.addAll(arg0);
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
