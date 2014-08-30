/**
 * Aug 29, 2014 11:30:49 AM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.City;
import elitech.vietnam.myfashion.entities.District;

/**
 * @author Cong
 *
 */
public class FirstLoadingFragment extends AbstractFragment {

	private static final int TASKS = 2;
	
	private int mTask = 0;
	private boolean mDone = true;
	private Object mLock = new Object();
	
	public FirstLoadingFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_firstloading, container, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mActivity.getActionBar().hide();
		mActivity.getServices().getCities(new Callback<List<City>>() {
			@Override
			public void success(final List<City> arg0, Response arg1) {
				onLoadingCompleted(true);
				AsyncTask.execute(new Runnable() {
					@Override
					public void run() {
						mActivity.getDatabase().saveCities(arg0);
					}
				});
			}
			@Override
			public void failure(RetrofitError arg0) {
				onLoadingCompleted(false);
			}
		});
		mActivity.getServices().getDistricts(new Callback<List<District>>() {
			@Override
			public void success(final List<District> arg0, Response arg1) {
				onLoadingCompleted(true);
				AsyncTask.execute(new Runnable() {
					@Override
					public void run() {
						mActivity.getDatabase().saveDistricts(arg0);
					}
				});
			}
			@Override
			public void failure(RetrofitError arg0) {
				onLoadingCompleted(false);
			}
		});
	}
	
	private void onLoadingCompleted(boolean done) {
		synchronized (mLock) {
			mTask += 1;
			if (!done)
				done = false;
			if (mTask >= TASKS) {
				if (done) {
					mActivity.getDatabase().updateFirstLaunch(false);
					mActivity.getActionBar().show();
					mActivity.changeBase(BaseFragment.TAG_BESTOFDAY, null);
				} else {
					// TODO: implement first loading failed task
					mActivity.finish();
				}
			}
		}
	}
}
