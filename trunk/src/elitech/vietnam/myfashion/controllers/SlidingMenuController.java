/**
 * Jul 29, 2014 11:30:00 AM
 */
package elitech.vietnam.myfashion.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.readystatesoftware.viewbadger.BadgeView;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.SlidingMenuAdapter;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.dialogues.WarningDialog;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.TradeMark;
import elitech.vietnam.myfashion.fragments.BaseFragment;
import elitech.vietnam.myfashion.fragments.CategoryFragment;
import elitech.vietnam.myfashion.utilities.Utilities;
import elitech.vietnam.myfashion.widgets.rdimgview.RoundedImageView;

/**
 * @author Cong
 *
 */
public class SlidingMenuController implements OnChildClickListener, OnGroupClickListener, OnClickListener {
	
	MainActivity mActivity;
	SlidingMenu mMenu;

	ExpandableListView mMenuListView;
	SlidingMenuAdapter mMenuAdapter;
	
	Member mMember;
	
	View mMemberView;
	View mLoginView;
	RoundedImageView mImgAvatar;
	TextView mTxtName, mTxtSettings;
	ImageButton mBtnNoti;
	BadgeView mBadge;
	
	List<TradeMark> mGroups;
	HashMap<Integer, List<TradeMark>> mChilds;
	List<TradeMark> mFashions, mStyles;
	
	int mMenuWidth, mBadgeCount;

	public SlidingMenuController(MainActivity activity) {
		mActivity = activity;
		mMember = mActivity.getLoggedinUser();
		mMenu = new SlidingMenu(mActivity);
		mMenu.attachToActivity(mActivity, SlidingMenu.SLIDING_WINDOW);
	}
	
	@SuppressLint("NewApi")
	public void setUp() {
		mMenu.setMode(SlidingMenu.LEFT);
		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mMenu.setShadowWidth(20);
		mMenu.setShadowDrawable(R.drawable.shadow);
		mMenu.setBehindWidth(mMenuWidth = mActivity.getConfig().getScreenWidth() * 75 / 100);
		mMenu.setBehindScrollScale(0.0f);
		mMenu.setFadeDegree(0.75f);
		mMenu.setMenu(R.layout.sliding_menu);
		
		mMenuListView = (ExpandableListView) mActivity.findViewById(R.id.slidemenu_explist);
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) 
			mMenuListView.setIndicatorBounds(mMenuWidth - Utilities.dpiToPixel(mActivity, 50), mMenuWidth - Utilities.dpiToPixel(mActivity, 10));
		else
			mMenuListView.setIndicatorBoundsRelative(mMenuWidth - Utilities.dpiToPixel(mActivity, 50), mMenuWidth - Utilities.dpiToPixel(mActivity, 10));
		
		setUpHeaders();
		
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
		mFashions.add(new TradeMark(0, mActivity.getString(R.string.best_top), 0, "menuicon_best"));
		mFashions.add(new TradeMark(1, mActivity.getString(R.string.women), 0, "menuicon_female"));
		mFashions.add(new TradeMark(2, mActivity.getString(R.string.office), 0, "menuicon_office"));
		mFashions.add(new TradeMark(3, mActivity.getString(R.string.men), 0, "menuicon_male"));
		mFashions.add(new TradeMark(4, mActivity.getString(R.string.autumn_winter), 0, "menuicon_female"));
		mFashions.add(new TradeMark(5, mActivity.getString(R.string.cosmetics), 0, "menuicon_best"));
		mFashions.add(new TradeMark(6, mActivity.getString(R.string.trademarks), 0, "menuicon_best"));
		mFashions.add(new TradeMark(7, mActivity.getString(R.string.inventory), 0, "menuicon_best"));
		
		mStyles = new ArrayList<>();
		mStyles.add(new TradeMark(8, mActivity.getString(R.string.all_style), 0, "menuicon_styler"));
		mStyles.add(new TradeMark(9, mActivity.getString(R.string.my_style), 0, "menuicon_mystyle"));
		mStyles.add(new TradeMark(10, mActivity.getString(R.string.review), 0, "menuicon_review"));
		
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
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		int groupId = mGroups.get(groupPosition).Id;
		int childId = groupId == 0 ? mFashions.get(childPosition).Id : mStyles.get(childPosition).Id;
		int memId = mActivity.getLoggedinUser() == null ? 0 : mActivity.getLoggedinUser().Id;
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
			mActivity.changeBase(BaseFragment.TAG_COSMETIC, null);
			break;
		case 6:
			mActivity.changeBase(BaseFragment.TAG_TRADEMARK, null);
		case 7:
			mActivity.changeBase(BaseFragment.TAG_INVENTORY, null);
			break;
		case 8:
			mActivity.changeBase(BaseFragment.TAG_STYLER, null);
			break;
		case 9:
			if (memId > 0)
				mActivity.changeBase(BaseFragment.TAG_MYSTYLE, null);
			else
				WarningDialog.newInstance(mActivity.getString(R.string.loginrequiremessage)).show(mActivity.getSupportFragmentManager());
			break;
		case 10:
			mActivity.changeBase(BaseFragment.TAG_REVIEW, null);
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
		case R.id.menu_txtSettings:
			mActivity.changeBase(BaseFragment.TAG_SETTINGS, null);
			break;
		case R.id.menu_txtLogin:
			mActivity.changeBase(BaseFragment.TAG_TPMLOGIN, null);
			break;
		case R.id.menu_imgAvatar:
		case R.id.menu_txtName:
			mActivity.changeBase(BaseFragment.TAG_MYPAGE, null);
			break;
		default:
			break;
		}
		mMenu.showContent();
	}
	
	private void setUpHeaders() {
		View view = LayoutInflater.from(mActivity).inflate(R.layout.sliding_menu_member, mMenuListView, false);
		mMenuListView.addHeaderView(view);
		
		mMemberView = view.findViewById(R.id.menu_layInfo);
		mLoginView = view.findViewById(R.id.menu_txtLogin);
		mImgAvatar = (RoundedImageView) view.findViewById(R.id.menu_imgAvatar);
		mTxtName = (TextView) view.findViewById(R.id.menu_txtName);
		mBtnNoti = (ImageButton) view.findViewById(R.id.menu_btnNotification);

		mBadge = new BadgeView(mActivity, mBtnNoti);
		mBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		
		mLoginView.setOnClickListener(this);
		mImgAvatar.setOnClickListener(this);
		mTxtName.setOnClickListener(this);
		mBtnNoti.setOnClickListener(this);
		
		changeLoggedState(mActivity.getLoggedinUser() != null);
		
		view = LayoutInflater.from(mActivity).inflate(R.layout.sliding_menu_footer, mMenuListView, false);
		mMenuListView.addFooterView(view);
		
		mTxtSettings = (TextView) view.findViewById(R.id.menu_txtSettings);
		mTxtSettings.setOnClickListener(this);
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
			mLoginView.setVisibility(View.GONE);
			mMemberView.setVisibility(View.VISIBLE);
			loadMemberData();
		} else {
			mLoginView.setVisibility(View.VISIBLE);
			mMemberView.setVisibility(View.GONE);
		}
	}
	
	private void loadMemberData() {
		mMember = mActivity.getLoggedinUser();
		if (mMember == null)
			return;
		UrlImageViewHelper.setUrlDrawable(mImgAvatar, Const.SERVER_IMAGE_THUMB_URL + mMember.Image, R.drawable.ic_user);
		mTxtName.setText(mMember.NickName.length() == 0 ? mMember.Name : mMember.NickName);
		updateNotiBadge(0);
	}

	public void updateNotiBadge(int number) {
		mBadge.setText(number + "");
		if (number > 0)
			mBadge.show();
		else
			mBadge.hide();
	}
	
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		return true;
	}
}
