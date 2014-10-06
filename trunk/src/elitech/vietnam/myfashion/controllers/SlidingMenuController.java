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
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class SlidingMenuController implements OnChildClickListener, OnGroupClickListener, OnClickListener {
	
	MainActivity mActivity;
	SlidingMenu mMenu;

	ExpandableListView mMenuListView;
	View mMemberLayout;
	View mIntroLayout;
	SlidingMenuAdapter mMenuAdapter;
	
	Member mMember;
	
	List<TradeMark> mGroups;
	HashMap<Integer, List<TradeMark>> mChilds;
	List<TradeMark> mFashions, mStyles;
	
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
			mMenuListView.setIndicatorBounds(mMenuWidth - Utilities.dpiToPixel(mActivity, 50), mMenuWidth - Utilities.dpiToPixel(mActivity, 10));
		else
			mMenuListView.setIndicatorBoundsRelative(mMenuWidth - Utilities.dpiToPixel(mActivity, 50), mMenuWidth - Utilities.dpiToPixel(mActivity, 10));
		
//		setUpHeaders();
		
//		mMenuListView.addHeaderView((mActivity.getLoggedinUser() != null) ? mMemberLayout : mIntroLayout);
		mMenuListView.setOnGroupClickListener(this);
		mMenuListView.setOnChildClickListener(this);
		loadMenuData();
	}
	
	private void loadMenuData() {
		// Generate menu groups
		mGroups = new ArrayList<>();
		
		mGroups.add(new TradeMark(0, mActivity.getString(R.string.fashion), 0, ""));
		mGroups.add(new TradeMark(1, mActivity.getString(R.string.style), 0, ""));
		
		mFashions = new ArrayList<>();
		mFashions.add(new TradeMark(0, "Best top", 0, "menuicon_best"));
		mFashions.add(new TradeMark(1, "Women", 0, "menuicon_female"));
		mFashions.add(new TradeMark(2, "Office", 0, "menuicon_office"));
		mFashions.add(new TradeMark(3, "Men", 0, "menuicon_male"));
		mFashions.add(new TradeMark(4, "Winter", 0, "menuicon_female"));
		mFashions.add(new TradeMark(5, "My Pham", 0, "menuicon_best"));
		mFashions.add(new TradeMark(6, "Thuong hieu", 0, "menuicon_best"));
		
		mStyles = new ArrayList<>();
		mStyles.add(new TradeMark(7, "All style", 0, "menuicon_styler"));
		mStyles.add(new TradeMark(8, "My style", 0, "menuicon_mystyle"));
		mStyles.add(new TradeMark(9, "Review", 0, "menuicon_review"));
		
		// Generate child map
		mChilds = new HashMap<>();
		mChilds.put(0, mFashions);
		mChilds.put(1, mStyles);
		
		mMenuAdapter = new SlidingMenuAdapter(mActivity, mGroups, mChilds);
		mMenuListView.setAdapter(mMenuAdapter);
		// Get data from service
		mMenuListView.expandGroup(0);
		mMenuListView.expandGroup(1);
	}
	
	public void loadBrandData(List<TradeMark> tradeMarks) {
//		mBranches.clear();
//		mBranches.addAll(tradeMarks);
//		mMenuAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		int groupId = mGroups.get(groupPosition).Id;
		int childId = groupId == 0 ? mFashions.get(childPosition).Id : mStyles.get(childPosition).Id;
		Bundle args = null;
		switch (childId) {
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
		case 5:
//			My pham
			break;
		case 6:
//			Thuong hieu
			break;
		case 7:
			mActivity.changeBase(BaseFragment.TAG_STYLER, null);
			break;
		case 8:
			// my style
			break;
		case 9:
			// review
			break;
		case 10:
			mActivity.changeBase(BaseFragment.TAG_SETTINGS, null);
			break;
		default:
			break;
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
//		if (loggedIn) {
//			mMenuListView.removeHeaderView(mIntroLayout);
//			mMenuListView.addHeaderView(mMemberLayout);
//		} else {
//			mMenuListView.removeHeaderView(mMemberLayout);
//			mMenuListView.addHeaderView(mIntroLayout);
//		}
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		return true;
	}
}
