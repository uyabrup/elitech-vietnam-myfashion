/**
 * Sep 25, 2014 4:04:21 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.gson.Gson;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Options;
import elitech.vietnam.myfashion.prefs.PrefsDefinition;

/**
 * @author Cong
 *
 */
public class NotificationSettingFragment extends AbstractFragment implements OnCheckedChangeListener {

	CheckBox mCbEnable, mCbFriendPost, mCbMyStyle, mCbReply, mCbFriendAddMe, mCbSound;
	Options mOptions;
	
	public static NotificationSettingFragment newInstance() {
		Bundle args = new Bundle();
		NotificationSettingFragment fragment = new NotificationSettingFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public NotificationSettingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.notification);
		mOptions = mActivity.getOptions();
		View view = inflater.inflate(R.layout.fragment_notification_settings, container, false);
		mCbEnable = (CheckBox) view.findViewById(R.id.stnoti_cbEnable);
		mCbFriendPost = (CheckBox) view.findViewById(R.id.stnoti_cbFriendPost);
		mCbMyStyle = (CheckBox) view.findViewById(R.id.stnoti_cbMystyleComment);
		mCbReply = (CheckBox) view.findViewById(R.id.stnoti_cbReplyComment);
		mCbFriendAddMe = (CheckBox) view.findViewById(R.id.stnoti_cbFriendAddMe);
		mCbSound = (CheckBox) view.findViewById(R.id.stnoti_cbEnableSound);
		
		mCbEnable.setChecked(mOptions.mNotiEnable);
		mCbFriendPost.setChecked(mOptions.mNotiFriendPost);
		mCbMyStyle.setChecked(mOptions.mNotiComment);
		mCbReply.setChecked(mOptions.mNotiRepComment);
		mCbFriendAddMe.setChecked(mOptions.mNotiAddFriend);
		mCbSound.setChecked(mOptions.mNotiSound);
		
		mCbFriendPost.setEnabled(mOptions.mNotiEnable);
		mCbMyStyle.setEnabled(mOptions.mNotiEnable);
		mCbReply.setEnabled(mOptions.mNotiEnable);
		mCbFriendAddMe.setEnabled(mOptions.mNotiEnable);
		
		mCbEnable.setOnCheckedChangeListener(this);
		mCbFriendPost.setOnCheckedChangeListener(this);
		mCbMyStyle.setOnCheckedChangeListener(this);
		mCbReply.setOnCheckedChangeListener(this);
		mCbFriendAddMe.setOnCheckedChangeListener(this);
		mCbSound.setOnCheckedChangeListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) { 
		case R.id.stnoti_cbEnable: 
			mOptions.mNotiEnable = isChecked;
			mCbFriendPost.setEnabled(isChecked);
			mCbMyStyle.setEnabled(isChecked);
			mCbReply.setEnabled(isChecked);
			mCbFriendAddMe.setEnabled(isChecked);
			break;
		case R.id.stnoti_cbEnableSound:
			mOptions.mNotiSound = isChecked;			
			break;
		case R.id.stnoti_cbFriendPost:
			mOptions.mNotiFriendPost = isChecked;
			break;
		case R.id.stnoti_cbMystyleComment:
			mOptions.mNotiComment = isChecked;
			break;
		case R.id.stnoti_cbReplyComment:
			mOptions.mNotiRepComment = isChecked;
			break;
		case R.id.stnoti_cbFriendAddMe:
			mOptions.mNotiAddFriend = isChecked;
			break;
		default:
			break;
		}
		mActivity.getPreferences().edit().putString(PrefsDefinition.OPTION_SETTINGS, new Gson().toJson(mOptions)).commit();
	}

}
