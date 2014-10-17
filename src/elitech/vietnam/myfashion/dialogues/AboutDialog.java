/**
 * Oct 14, 2014 11:13:43 AM
 */
package elitech.vietnam.myfashion.dialogues;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class AboutDialog extends AbstractDialogFragment implements OnClickListener {

	private TextView mTxtVersion;
	private Button mBtnLegal, mBtnOk;
	
	public static AboutDialog newInstance() {
		AboutDialog dialog = new AboutDialog();
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(getString(R.string.about) + " " + getString(R.string.app_name));
		View view = inflater.inflate(R.layout.dialog_about, container, false);
		
		mBtnOk = (Button) view.findViewById(R.id.dialog_about_btnLegal);
		mBtnLegal = (Button) view.findViewById(R.id.dialog_about_btnOk);
		mTxtVersion = (TextView) view.findViewById(R.id.dialog_about_txtVersionNCopyRight);
		
		mBtnOk.setOnClickListener(this);
		mBtnLegal.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		String version = "1.0";
		try {
			PackageInfo info = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		mTxtVersion.setText(String.format(getString(R.string.about_version_copyright), version));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_about_btnLegal:
			break;
		case R.id.dialog_about_btnOk:
			break;
		default:
			break;
		}
		dismiss();
	}
	
}
