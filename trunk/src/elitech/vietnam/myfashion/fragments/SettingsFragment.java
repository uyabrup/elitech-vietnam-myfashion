/**
 * Sep 25, 2014 10:46:32 AM
 */
package elitech.vietnam.myfashion.fragments;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.dialogues.ConfirmDialog;
import elitech.vietnam.myfashion.dialogues.ConfirmDialog.ConfirmDialogClick;
import elitech.vietnam.myfashion.prefs.PrefsDefinition;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author Cong
 *
 */
public class SettingsFragment extends AbstractFragment implements View.OnClickListener, ConfirmDialogClick {

	Button mBtnAccount, mBtnNotification, mBtnLanguage, mBtnAbout, mBtnLogin;
	
	public SettingsFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		
		mBtnAbout = (Button) view.findViewById(R.id.settings_btnAbout);
		mBtnAccount = (Button) view.findViewById(R.id.settings_btnAccount);
		mBtnNotification = (Button) view.findViewById(R.id.settings_btnNotifications);
		mBtnLanguage = (Button) view.findViewById(R.id.settings_btnLanguage);
		mBtnLogin = (Button) view.findViewById(R.id.settings_btnLoginSignup);
		
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
		mBtnLogin.setText(mActivity.getLoggedinUser() == null ? R.string.loginorsignup : R.string.signout);
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
//			mActivity.getCurrentBase().replaceFragment(AccountInfoFragment.newInstance(), true);
			break;
		case R.id.settings_btnLoginSignup:
			if (mActivity.getLoggedinUser() == null) {
				mActivity.getCurrentBase().replaceFragment(LoginBaseFragment.newInstance(), true);
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
		mBtnLogin.setText(R.string.loginorsignup);
		mActivity.getPreferences().edit().putString(PrefsDefinition.LOGGEDIN_MEMBER, "").commit();
		mActivity.getMenuController().changeLoggedState(false);
		Toast.makeText(mActivity, R.string.logoutsuccess, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void noClick(int requestCode) {
		
	}
}
