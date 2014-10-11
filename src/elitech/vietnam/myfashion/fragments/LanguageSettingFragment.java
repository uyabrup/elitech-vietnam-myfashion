/**
 * Sep 25, 2014 4:04:21 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.gson.Gson;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Options;
import elitech.vietnam.myfashion.prefs.PrefsDefinition;

/**
 * @author Cong
 *
 */
public class LanguageSettingFragment extends AbstractFragment implements OnClickListener, OnCheckedChangeListener {

	RadioGroup mRgMain;
	RadioButton[] mRadio = new RadioButton[4];
	Options mOptions;
	
	public static LanguageSettingFragment newInstance() {
		Bundle args = new Bundle();
		LanguageSettingFragment fragment = new LanguageSettingFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public LanguageSettingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.language);
		mOptions = mActivity.getOptions();
		View view = inflater.inflate(R.layout.fragment_language, container, false);
		
		mRgMain = (RadioGroup) view.findViewById(R.id.mainLayout);
		mRadio[0] = (RadioButton) view.findViewById(R.id.stlang_auto);
		mRadio[1] = (RadioButton) view.findViewById(R.id.stlang_korea);
		mRadio[2] = (RadioButton) view.findViewById(R.id.stlang_eng);
		mRadio[3] = (RadioButton) view.findViewById(R.id.stlang_vi);

		mRadio[mOptions.mLanguage].setChecked(true);
		mRgMain.setOnCheckedChangeListener(this);

		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onClick(View v) {

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.stlang_auto:
			mOptions.mLanguage = 0;
			break;
		case R.id.stlang_korea:
			mOptions.mLanguage = 1;
			break;
		case R.id.stlang_eng:
			mOptions.mLanguage = 2;
			break;
		case R.id.stlang_vi:
			mOptions.mLanguage = 3;
			break;
		default:
			break;
		}
		mActivity.getPreferences().edit().putString(PrefsDefinition.OPTION_SETTINGS, new Gson().toJson(mOptions)).commit();
		Intent intent = new Intent(mActivity, MainActivity.class);
		mActivity.startActivity(intent);
		mActivity.finish();
	}

}
