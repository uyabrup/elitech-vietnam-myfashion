/**
 * Jul 29, 2014 11:30:00 AM
 */
package elitech.vietnam.myfashion.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.SlidingMenuAdapter;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.TradeMark;
import elitech.vietnam.myfashion.fragments.BaseFragment;
import elitech.vietnam.myfashion.fragments.CategoryFragment;

/**
 * @author Cong
 *
 */
public class SlidingMenuController implements OnGroupClickListener, OnChildClickListener, OnClickListener {
	
	private static final int BRANDS_ID = 99;
	
	MainActivity mActivity;
	SlidingMenu mMenu;

	ExpandableListView mMenuListView;
	View mMemberLayout;
	View mIntroLayout;
	SlidingMenuAdapter mMenuAdapter;
	
	Member mMember;
	
	List<TradeMark> mGroups;
	HashMap<Integer, List<TradeMark>> mChilds;
	List<TradeMark> mBranches, mEmpty;
	
	int mMenuWidth;

	public SlidingMenuController(MainActivity activity) {
		mActivity = activity;
		mMember = mActivity.getLoggedinUser();
	}
	
	@SuppressLint("NewApi")
	public void setUp() {
		mMenu = new SlidingMenu(mActivity);
		mMenu.setMode(SlidingMenu.LEFT);
		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mMenu.setShadowWidth(20);
		mMenu.setShadowDrawable(R.drawable.shadow);
		mMenu.setBehindWidth(mMenuWidth = mActivity.getConfig().getScreenWidth() * 75 / 100);
		mMenu.setBehindScrollScale(0.0f);
		mMenu.setFadeDegree(0.75f);
		mMenu.attachToActivity(mActivity, SlidingMenu.SLIDING_WINDOW);
		mMenu.setMenu(R.layout.sliding_menu);
		
		mMenuListView = (ExpandableListView) mActivity.findViewById(R.id.slidemenu_explist);
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) 
			mMenuListView.setIndicatorBounds(mMenuWidth - dpiToPixel(50), mMenuWidth - dpiToPixel(10));
		else
			mMenuListView.setIndicatorBoundsRelative(mMenuWidth - dpiToPixel(50), mMenuWidth - dpiToPixel(10));
		
		setUpHeaders();
		
		mMenuListView.addHeaderView((mActivity.getLoggedinUser() != null) ? mMemberLayout : mIntroLayout);
		mMenuListView.setOnGroupClickListener(this);
		mMenuListView.setOnChildClickListener(this);
		loadMenuData();
	}
	
	private void loadMenuData() {
		// Generate menu groups
		mGroups = new ArrayList<>();
		
		mGroups.add(new TradeMark(0, mActivity.getString(R.string.bestoftoday), 0, R.drawable.menuicon_best));
		mGroups.add(new TradeMark(1, mActivity.getString(R.string.womenfashion), 0, R.drawable.menuicon_female));
		mGroups.add(new TradeMark(2, mActivity.getString(R.string.officefashion), 0, R.drawable.menuicon_office));
		mGroups.add(new TradeMark(3, mActivity.getString(R.string.menfashion), 0, R.drawable.menuicon_male));
		mGroups.add(new TradeMark(4, mActivity.getString(R.string.winterfashion), 0, R.drawable.menuicon_best));
		mGroups.add(new TradeMark(BRANDS_ID, mActivity.getString(R.string.menugroup_brands), 0, R.drawable.menuicon_best));
		mBranches = new ArrayList<>();
		
		mGroups.add(new TradeMark(5, mActivity.getString(R.string.myshopping), 2, R.drawable.menuicon_cart));
		mGroups.add(new TradeMark(6, mActivity.getString(R.string.styler), 2, R.drawable.menuicon_styler));
		mGroups.add(new TradeMark(7, mActivity.getString(R.string.mystyle), 2, R.drawable.menuicon_mystyle));
		mGroups.add(new TradeMark(8, mActivity.getString(R.string.review), 2, R.drawable.menuicon_review));
		mGroups.add(new TradeMark(9, mActivity.getString(R.string.setting), 2, R.drawable.menuicon_settings));
		// Dummy data for no-child groups
		mEmpty = new ArrayList<>();
		// Generate child map
		mChilds = new HashMap<>();
		for (TradeMark item : mGroups)
			mChilds.put(item.Id, item.Id == BRANDS_ID ? mBranches : mEmpty);
		
		mMenuAdapter = new SlidingMenuAdapter(mActivity, mGroups, mChilds);
		mMenuListView.setAdapter(mMenuAdapter);
		// Get data from service
		
		mMenuListView.setSelection(0);
	}
	
	public void loadBrandData(List<TradeMark> tradeMarks) {
		mBranches.clear();
		mBranches.addAll(tradeMarks);
		mMenuAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		// Define click event for static menu item
		if (mGroups.get(groupPosition).Id != BRANDS_ID) {
			Bundle args = null;
			switch (groupPosition) {
			case 0:
				mActivity.changeBase(BaseFragment.TAG_BESTOFDAY, null);
				break;
			case 1:
				args = new Bundle();
				args.putString(CategoryFragment.TAG, CategoryFragment.TAG_WOMEN);
				mActivity.changeBase(BaseFragment.TAG_WOMENFASHION, args);
				break;
			case 2:
				args = new Bundle();
				args.putString(CategoryFragment.TAG, CategoryFragment.TAG_OFFICE);
				mActivity.changeBase(BaseFragment.TAG_OFFICEFASHION, args);
				break;
			case 3:
				args = new Bundle();
				args.putString(CategoryFragment.TAG, CategoryFragment.TAG_MEN);
				mActivity.changeBase(BaseFragment.TAG_MENFASHION, args);
				break;
			case 4:
				args = new Bundle();
				args.putString(CategoryFragment.TAG, CategoryFragment.TAG_WINTER);
				mActivity.changeBase(BaseFragment.TAG_WINTERFASHION, args);
				break;
			case 6:
				mActivity.changeBase(BaseFragment.TAG_MYSHOPPING, null);
				break;
			case 7:
				mActivity.changeBase(BaseFragment.TAG_STYLER, null);
				break;
			default:
				break;
			}
			mMenu.showContent();
		}
		return false;
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		if (mGroups.get(groupPosition).Id == BRANDS_ID) {
			mActivity.getController().setTradeMark(mBranches.get(childPosition));
			Bundle args = new Bundle();
			args.putInt(BaseFragment.ARG_POSITION, childPosition);
			mActivity.changeBase(BaseFragment.TAG_BRANCHES + childPosition, args);
		}
		mMenu.showContent();
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_imgLogo:
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mActivity.getString(R.string.homepageurl)));
			mActivity.startActivity(i);
			break;

		default:
			break;
		}
	}
	
	private int dpiToPixel(int dpi) {
		final float scale = mActivity.getResources().getDisplayMetrics().density;
	    return (int) (dpi * scale + 0.5f);
	}
	
	private void setUpHeaders() {
		// Inflate header views
		mMemberLayout = LayoutInflater.from(mActivity).inflate(R.layout.sliding_menu_member, mMenuListView, false);
		mIntroLayout = LayoutInflater.from(mActivity).inflate(R.layout.sliding_menu_intro, mMenuListView, false);
		
		// TODO: Get header controls
		
		// TODO: Setting up listener
		mIntroLayout.findViewById(R.id.menu_imgLogo).setOnClickListener(this);
	}

	public boolean onBackPressed() {
		if (!mMenu.isMenuShowing())
			return false;
		mMenu.showContent();
		return true;
	}
	
	public void showMenu() {
		if (!mMenu.isMenuShowing())
			mMenu.showMenu();
	}
	
	public void changeLoggedState(boolean loggedIn) {
		if (loggedIn) {
			mMenuListView.removeHeaderView(mIntroLayout);
			mMenuListView.addHeaderView(mMemberLayout);
		} else {
			mMenuListView.removeHeaderView(mMemberLayout);
			mMenuListView.addHeaderView(mIntroLayout);
		}
	}
}
