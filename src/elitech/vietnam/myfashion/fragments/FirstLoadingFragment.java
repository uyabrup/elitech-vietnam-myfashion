/**
 * Aug 29, 2014 11:30:49 AM
 */
package elitech.vietnam.myfashion.fragments;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Options;
import elitech.vietnam.myfashion.entities.City;
import elitech.vietnam.myfashion.entities.District;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.Ship;
import elitech.vietnam.myfashion.entities.ShipMore;
import elitech.vietnam.myfashion.prefs.PrefsDefinition;
import elitech.vietnam.myfashion.utilities.Tracker;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class FirstLoadingFragment extends AbstractFragment {

	public static final String	SENDER_ID							= "793977106476";
	
	private volatile int mTask = 0;
	private Object mLock = new Object();
	private boolean mFailed = false;
	
	GoogleCloudMessaging gcm;
	
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
		new OneTimeStoreID().execute();
		if (mActivity.getDatabase().getShipCount() == 0) {
			mTask += 1;
			mActivity.getServices().getShip(new Callback<List<Ship>>() {
				@Override
				public void success(final List<Ship> arg0, Response arg1) {
					onLoadingCompleted(true);
					AsyncTask.execute(new Runnable() {
						@Override
						public void run() {
							mActivity.getDatabase().saveShip(arg0);
						}
					});
				}
				@Override
				public void failure(RetrofitError arg0) {
					Log.w("RetrofitError", arg0.getMessage());
					onLoadingCompleted(false);
				}
			});
		}
		if (mActivity.getDatabase().getShipMoreCount() == 0) {
			mTask += 1;
			mActivity.getServices().getShipMore(new Callback<List<ShipMore>>() {
				@Override
				public void success(final List<ShipMore> arg0, Response arg1) {
					onLoadingCompleted(true);
					AsyncTask.execute(new Runnable() {
						@Override
						public void run() {
							mActivity.getDatabase().saveShipMore(arg0);
						}
					});
				}
				@Override
				public void failure(RetrofitError arg0) {
					Log.w("RetrofitError", arg0.getMessage());
					onLoadingCompleted(false);
				}
			});
		}
		if (mActivity.getDatabase().getCityCount() == 0) {
			mTask += 1;
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
					Log.w("RetrofitError", arg0.getMessage());
					onLoadingCompleted(false);
				}
			});
		}
		if (mActivity.getDatabase().getDistrictCount() == 0) {
			mTask += 1;
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
					Log.w("RetrofitError", arg0.getMessage());
					onLoadingCompleted(false);
				}
			});
		}
		String user = mActivity.getPreferences().getString(PrefsDefinition.LOGGEDIN_MEMBER, "");
		if (user.length() > 0) {
			Member member = new Gson().fromJson(user, Member.class);
			if (member != null) {
				mTask += 1;
				mActivity.getServices().getMemberById(member.Id, member.Id, new Callback<Member>() {
					@Override
					public void success(final Member arg0, Response arg1) {
						onLoadingCompleted(true);
						final Member m = arg0;
						AsyncTask.execute(new Runnable() {
							@Override
							public void run() {
								mActivity.setLoggedinUser(arg0);
								mActivity.getPreferences().edit().putString(PrefsDefinition.LOGGEDIN_MEMBER, new Gson().toJson(m)).commit();
							}
						});
					}
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						onLoadingCompleted(false);
					}
				});
			}
		}
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				Options options = mActivity.getOptions();
				if (options == null) {
					options = new Options();
					mActivity.setOptions(options);
					mActivity.getPreferences().edit().putString(PrefsDefinition.OPTION_SETTINGS, 
							new Gson().toJson(options)).commit();
				}
			}
		});
	}
	
	private void onLoadingCompleted(boolean done) {
		synchronized (mLock) {
			if (!done)
				mFailed = true;
			mTask -= 1;
			if (mTask == 0) {
				if (!mFailed) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							mActivity.getActionBar().show();
							mActivity.changeBase(BaseFragment.TAG_BESTOFDAY, null);
							if (mActivity.getPreferences().getBoolean(PrefsDefinition.FIRST_LAUNCH, true)) {
								mActivity.getPreferences().edit().putBoolean(PrefsDefinition.FIRST_LAUNCH, false).commit();
								mActivity.getMenuController().showMenu();
							}
						}
					}, 1000);
				} else {
					// TODO: implement loading failed callback
					Log.e("Initialize", "Initialize failed! Now exitting. (1)");
					mActivity.finish();
				}
			}
		}
	}
	
	class OneTimeStoreID extends AsyncTask<Integer, Integer, Boolean> {
		SharedPreferences	pref;
		String				deviceId	= null;
		String				gcmId		= "";

		@Override
		protected void onPreExecute() {
			mTask += 1;
			pref = mActivity.getPreferences();
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			deviceId = pref.getString(PrefsDefinition.UNIQUE_DEVICE_ID, null);
			if (deviceId == null || deviceId.equals("")) {
				deviceId = Tracker.getDeviceID(mActivity.getApplicationContext());
				pref.edit().putString(PrefsDefinition.UNIQUE_DEVICE_ID, deviceId).commit();
			}

			gcmId = Utilities.getRegistrationId(mActivity.getApplicationContext());
			if (gcmId.equals("")) {
				try {
					Log.w("GCM", "Registering new GCM-ID");
					if (gcm == null)
						gcm = GoogleCloudMessaging.getInstance(mActivity.getApplicationContext());
					gcmId = gcm.register(SENDER_ID);
					Log.w("GCM", "New GCM-ID" + gcmId);
					Utilities.storeRegistrationId(mActivity.getApplicationContext(), gcmId);
					return true;
				} catch (IOException ex) {
					ex.printStackTrace();
					return false;
				}
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Log.e("Initialize", "Initialize failed! Now exitting.");
				mActivity.finish();
			} else {
				Log.w("Initialize", "Initialize success.");
				String version = "Unknown";
				try {
					version = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				int api = Build.VERSION.SDK_INT;
				Member m = new Gson().fromJson(pref.getString(PrefsDefinition.LOGGEDIN_MEMBER, ""), Member.class);
				String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
				mActivity.getServices().storeDevice(deviceId, version, api, (m != null) ? m.Email : "@null", date, gcmId, new Callback<Integer>() {
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						onLoadingCompleted(false);
					}
					@Override
					public void success(Integer arg0, Response arg1) {
						onLoadingCompleted(true);
					}
				});
//				mActivity.getServices().addMemoTrack((m != null) ? m.Id : 0, (m != null) ? m.Email : "no-id", version, new Callback<Integer>() {
//					@Override
//					public void failure(RetrofitError arg0) {
//					}
//					@Override
//					public void success(Integer arg0, Response arg1) {
//					}
//				});
			}
		}
	}
}
