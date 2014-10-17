/**
 * Sep 25, 2014 10:46:32 AM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Options;
import elitech.vietnam.myfashion.dialogues.AboutDialog;
import elitech.vietnam.myfashion.dialogues.ConfirmDialog;
import elitech.vietnam.myfashion.dialogues.ConfirmDialog.ConfirmDialogClick;
import elitech.vietnam.myfashion.prefs.PrefsDefinition;

/**
 * @author Cong
 *
 */
public class SettingsFragment extends AbstractFragment implements View.OnClickListener, ConfirmDialogClick {

	RelativeLayout mBtnAccount, mBtnNotification, mBtnLanguage, mBtnAbout, mBtnLogin;
	TextView mTxtLogin, mTxtLangValue;
	View mDividerAccount;
	
	final int[] mLangs = new int[] {R.string.lang_auto, R.string.lang_korea, R.string.lang_english, R.string.lang_vietnam};
	
	public static SettingsFragment newInstance() {
		SettingsFragment fragment = new SettingsFragment();
		return fragment;
	}
	
	public SettingsFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.title_settings);
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		
		mBtnAbout = (RelativeLayout) view.findViewById(R.id.settings_btnAbout);
		mBtnAccount = (RelativeLayout) view.findViewById(R.id.settings_btnAccount);
		mBtnNotification = (RelativeLayout) view.findViewById(R.id.settings_btnNotifications);
		mBtnLanguage = (RelativeLayout) view.findViewById(R.id.settings_btnLanguage);
		mBtnLogin = (RelativeLayout) view.findViewById(R.id.settings_btnLoginSignup);
		mTxtLogin = (TextView) view.findViewById(R.id.settings_txtLoginSignup);
		mTxtLangValue = (TextView) view.findViewById(R.id.settings_txtLanguageValue);
		mDividerAccount = view.findViewById(R.id.settings_dividerAccount);
		
		mBtnAbout.setOnClickListener(this);
		mBtnAccount.setOnClickListener(this);
		mBtnNotification.setOnClickListener(this);
		mBtnLanguage.setOnClickListener(this);
		mBtnLogin.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Options option = mActivity.getOptions();
		mTxtLogin.setText(mActivity.getLoggedinUser() == null ? R.string.loginorsignup : R.string.signout);
		mTxtLangValue.setText(mLangs[option.mLanguage]);
		mBtnAccount.setVisibility(mActivity.getLoggedinUser() == null ? View.GONE : View.VISIBLE);
		mDividerAccount.setVisibility(mActivity.getLoggedinUser() == null ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settings_btnAccount:
			if (mActivity.getLoggedinUser() != null)
				mActivity.getCurrentBase().replaceFragment(AccountInfoFragment.newInstance(), true);
			else
				Toast.makeText(mActivity, R.string.requestlogin, Toast.LENGTH_SHORT).show();
			break;
		case R.id.settings_btnNotifications:
			mActivity.getCurrentBase().replaceFragment(NotificationSettingFragment.newInstance(), true);
			break;
		case R.id.settings_btnLanguage:
			mActivity.getCurrentBase().replaceFragment(LanguageSettingFragment.newInstance(), true);
			break;
		case R.id.settings_btnAbout:
			AboutDialog.newInstance().show(getFragmentManager());
			break;
		case R.id.settings_btnLoginSignup:
			if (mActivity.getLoggedinUser() == null) {
				mActivity.changeBase(BaseFragment.TAG_TPMLOGIN, null);
			} else {
				ConfirmDialog.newInstance(R.string.logout, R.string.logoutconfirm, 1, this).show(getFragmentManager());
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void yesClick(int requestCode) {
		mActivity.setLoggedinUser(null);
		mTxtLogin.setText(R.string.loginorsignup);
		mActivity.getPreferences().edit().putString(PrefsDefinition.LOGGEDIN_MEMBER, "").commit();
		mActivity.getMenuController().changeLoggedState(false);
		Toast.makeText(mActivity, R.string.logoutsuccess, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void noClick(int requestCode) {
		
	}
}
