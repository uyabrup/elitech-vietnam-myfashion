package elitech.vietnam.myfashion;

import java.util.Locale;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.readystatesoftware.viewbadger.BadgeView;

import elitech.vietnam.myfashion.config.Config;
import elitech.vietnam.myfashion.controllers.SlidingMenuController;
import elitech.vietnam.myfashion.database.DBHandler;
import elitech.vietnam.myfashion.entities.Category;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.TradeMark;
import elitech.vietnam.myfashion.fragments.BaseFragment;
import elitech.vietnam.myfashion.fragments.BestOfTodayFragment.BestOfTodayCallback;
import elitech.vietnam.myfashion.fragments.CategoryDetailFragment;
import elitech.vietnam.myfashion.fragments.CategoryFragment.CategoryCallback;
import elitech.vietnam.myfashion.fragments.ProductTabHostFragment;
import elitech.vietnam.myfashion.wsclient.ServiceBuilder;
import elitech.vietnam.myfashion.wsclient.Services;

public class MainActivity extends ActionBarActivity implements BestOfTodayCallback, CategoryCallback {
	
	Config mConfig;
	SharedPreferences mPrefs;
	Services mServices;
	DBHandler mDBHandler;
	ActionBar mActionBar;
	
	BadgeView mCartBadge;
	
	SlidingMenuController mMenuController;
	
	Member mUser;
	
	String mBaseTag;
	
	Product mDetail;
	Category mCategory;
	TradeMark mTradeMark;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mConfig = new Config(this);
		mPrefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		mDBHandler = new DBHandler(getApplicationContext());
		
		mServices = new ServiceBuilder(this).build();
		
		/*
		 * Dummy user data
		 */
		mUser = new Member();
		mUser.Id = 8;

		mMenuController = new SlidingMenuController(this);
		mMenuController.setUp();
		
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		
		if (savedInstanceState == null) {
			mBaseTag = BaseFragment.TAG_BESTOFDAY;
			getSupportFragmentManager().beginTransaction().add(R.id.container, BaseFragment.newInstance(mBaseTag), mBaseTag).commit();
		} else {
			mBaseTag = savedInstanceState.getString(BaseFragment.TAG);
			Log.w("restoreSavedInstanceState", mBaseTag);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.w("onSaveInstanceState", mBaseTag);
		outState.putString(BaseFragment.TAG, mBaseTag);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		final MenuItem item = menu.findItem(R.id.action_settings);
	    View view = MenuItemCompat.getActionView(item).findViewById(R.id.menuitem_action_cart);
	    view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onOptionsItemSelected(item);
			}
		});
		mCartBadge = new BadgeView(this, view);
		mCartBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			changeBase(BaseFragment.TAG_MYSHOPPING, null);
			return true;
		}
		if (id == android.R.id.home) {
			mMenuController.showMenu();
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
	
	public Product getDetail() {
		return mDetail;
	}
	
	public void setDetail(Product product) {
		mDetail = product;
	}
	
	public Category getCategory() {
		return mCategory;
	}
	
	public void setCategory(Category category) {
		mCategory = category;
	}

	public Locale getCurrentLocale() {
		return getResources().getConfiguration().locale;
	}
	
	public TradeMark getTradeMark() {
		return mTradeMark;
	}
	
	public void setTradeMark(TradeMark tradeMark) {
		mTradeMark = tradeMark;
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
		if (mMenuController.onBackPressed())
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

	@Override
	public void onItemClick(Product product) {
		setDetail(product);
		getCurrentBase().replaceFragment(new ProductTabHostFragment(), true);
	}

	@Override
	public void onItemClick(Category category, int fashion) {
		setCategory(category);
		CategoryDetailFragment fragment = new CategoryDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(CategoryDetailFragment.ARG_FASHION, fashion);
		fragment.setArguments(bundle);
		getCurrentBase().replaceFragment(fragment, true);
	}
}
