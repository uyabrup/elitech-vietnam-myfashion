package elitech.vietnam.myfashion;

import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.readystatesoftware.viewbadger.BadgeView;

import elitech.vietnam.myfashion.badge.BadgeHelper;
import elitech.vietnam.myfashion.config.Config;
import elitech.vietnam.myfashion.config.Options;
import elitech.vietnam.myfashion.controllers.AppController;
import elitech.vietnam.myfashion.controllers.SlidingMenuController;
import elitech.vietnam.myfashion.database.DBHandler;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.Notify;
import elitech.vietnam.myfashion.fragments.BaseFragment;
import elitech.vietnam.myfashion.gcm.GCMIntentService;
import elitech.vietnam.myfashion.prefs.PrefsDefinition;
import elitech.vietnam.myfashion.wsclient.ServiceBuilder;
import elitech.vietnam.myfashion.wsclient.Services;

public class MainActivity extends ActionBarActivity {
	
	Config mConfig;
	SharedPreferences mPrefs;
	Services mServices;
	DBHandler mDBHandler;
	ActionBar mActionBar;
	
	BadgeView mCartBadge;
	
	SlidingMenuController mSlideMenuController;
	AppController mController;
	
	Member mUser;
	Options mOptions;
	
	String mBaseTag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mConfig = new Config(this);
		mPrefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		mDBHandler = new DBHandler(getApplicationContext());
		mServices = new ServiceBuilder(this).build();

		mUser = new Gson().fromJson(mPrefs.getString(PrefsDefinition.LOGGEDIN_MEMBER, ""), Member.class);
		mOptions = new Gson().fromJson(mPrefs.getString(PrefsDefinition.OPTION_SETTINGS, ""), Options.class);
		
		mController = new AppController(this);
		mSlideMenuController = new SlidingMenuController(this);
		
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		// Setting up action bar title style
		TextView mTitle = (TextView) findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));
		mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		mTitle.setTypeface(Typeface.SERIF);

		changeLanguage(mOptions != null ? mOptions.mLanguage : 2);

		mSlideMenuController.setUp();
		
		if (savedInstanceState == null) {
			mBaseTag = BaseFragment.TAG_SPLASH;
			getSupportFragmentManager().beginTransaction().add(R.id.container, BaseFragment.newInstance(mBaseTag), mBaseTag).commit();
		} else {
			mBaseTag = savedInstanceState.getString(BaseFragment.TAG);
			Log.w("restoreSavedInstanceState", mBaseTag);
		}
		
		handleIntent(getIntent());
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.w("onSaveInstanceState", mBaseTag);
		outState.putString(BaseFragment.TAG, mBaseTag);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView search = (SearchView) MenuItemCompat.getActionView(searchItem);
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String arg0) {
				search.clearFocus();
				mController.search(arg0);
				searchItem.collapseActionView();
				return false;
			}
			@Override
			public boolean onQueryTextChange(String arg0) {
				return false;
			}
		});
		
		final MenuItem cartItem = menu.findItem(R.id.action_settings);
		View view = MenuItemCompat.getActionView(cartItem).findViewById(R.id.menuitem_action_cart);
	    view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onOptionsItemSelected(cartItem);
			}
		});
		mCartBadge = new BadgeView(this, view);
		mCartBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		updateCartBadge(mController.getOrders().size());
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			changeBase(BaseFragment.TAG_MYSHOPPING, null);
			return true;
		}
		if (id == android.R.id.home) {
			mSlideMenuController.showMenu();
			return true;
		}
		return false;
	}
	
	public Config getConfig() {
		return mConfig;
	}
	
	public SharedPreferences getPreferences() {
		return mPrefs;
	}
	
	public Services getServices() {
		return mServices;
	}
	
	public DBHandler getDatabase() {
		return mDBHandler;
	}
	
	public Member getLoggedinUser() {
		return mUser;
	}
	
	public void setLoggedinUser(Member member) {
		mUser = member;
	}
	
	public Locale getCurrentLocale() {
		return getResources().getConfiguration().locale;
	}
	
	public AppController getController() {
		return mController;
	}
	
	public SlidingMenuController getMenuController() {
		return mSlideMenuController;
	}
	
	public boolean changeBase(String tag, Bundle args) {
		if (tag.equals(mBaseTag))
			return false;
		BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
		if (fragment == null)
			fragment = args != null ? BaseFragment.newInstance(tag, args) : BaseFragment.newInstance(tag);
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		trans.replace(R.id.container, fragment, tag).commit();
		mBaseTag = tag;
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if (mSlideMenuController.onBackPressed())
			return;
		if (getCurrentBase().popFragment())
			return;
		if (!mBaseTag.equals(BaseFragment.TAG_BESTOFDAY))
			changeBase(BaseFragment.TAG_BESTOFDAY, null);
		else
			super.onBackPressed();
	}
	
	public BaseFragment getCurrentBase() {
		return (BaseFragment) getSupportFragmentManager().findFragmentByTag(mBaseTag);
	}
	
	public void updateCartBadge(int number) {
		mCartBadge.setText(number + "");
		if (number > 0)
			mCartBadge.show();
		else
			mCartBadge.hide();
	}
	
	public Options getOptions() {
		return mOptions;
	}
	
	public void setOptions(Options options) {
		mOptions = options;
	}

	/**
	 * Get the image path from uri. Used after received result from ACTION_PICK intent
	 * @param contentUri
	 * @return
	 */
	public String getRealPathFromURI(Uri contentUri) {
		String path = null;
		String[] proj = { MediaStore.MediaColumns.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		if (cursor.moveToFirst()) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
			path = cursor.getString(column_index);
		}
		cursor.close();
		return path;
	}

	@Override
	protected void onActivityResult(final int arg0, final int arg1, final Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * Change the app's language (locale) by language number
	 * @param languageOption
	 */
	public void changeLanguage(int languageOption) {
		Locale locale = Locale.KOREA;
		switch (languageOption) {
		case 1:
			locale = Locale.KOREA;
			break;
		case 2:
			locale = Locale.ENGLISH;
			break;
		case 0:
		case 3:
			locale = new Locale("vi_VN");
			break;
		default:
			break;
		}
		Locale.setDefault(locale);
        Configuration appConfig = getApplicationContext().getResources().getConfiguration();
        appConfig.locale = locale;
        getApplicationContext().getResources().updateConfiguration(appConfig,
        		getApplicationContext().getResources().getDisplayMetrics());
        
        mSlideMenuController.setUp();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		final Bundle extras = intent.getExtras();
		if (extras != null && extras.containsKey(GCMIntentService.DATA_TAG) && !extras.getString(GCMIntentService.DATA_TAG).equals("")) {
			Log.w("handleIntent", GCMIntentService.DATA_TAG + " extra");
			mPrefs.edit().putInt(PrefsDefinition.BADGE_COUNT, 0).commit();
			BadgeHelper.updateBadge(getApplicationContext(), 0);
//			TODO: Change notification button badge
			final Notify noti = new Gson().fromJson(extras.getString(GCMIntentService.DATA_TAG), Notify.class);
			mServices.readNotification(noti.Id, 0, new Callback<Integer>() {
				@Override
				public void success(Integer arg0, Response arg1) {
				}
				@Override
				public void failure(RetrofitError arg0) {
				}
			});
//			Runnable runnable = null;
//			if (noti.Type == 1 || noti.Type == 2 || noti.Type == 3) {
//				runnable = new Runnable() {
//					@Override
//					public void run() {
////						TODO: Open style notified
//					}
//				};
//			}
//			if (noti.Type == 4) {
//				runnable = new Runnable() {
//					@Override
//					public void run() {
////						TODO: Open follow list
//					}
//				};
//			}
//			if (noti.Type == 5) {
//				runnable = new Runnable() {
//					@Override
//					public void run() {
////						TODO: Open product
//					}
//				};
//			}
//			if (runnable != null)
//				new Handler().postDelayed(runnable, 400);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					changeBase(BaseFragment.TAG_NOTIFICATION, null);
				}
			}, 400);

			GCMIntentService.NUMBER = 0;
		}
	}
}
