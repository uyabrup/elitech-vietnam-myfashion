/**
 * May 12, 2014 9:32:05 AM
 */
package elitech.vietnam.myfashion.dialogues;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class ChangePasswordDialog extends AbstractDialogFragment implements View.OnClickListener {
	
	private static final String ARG_MEMBERID = "ARG_MEMBERID";
	private static final String ARG_PASSWORD = "ARG_PASSWORD";
	
	EditText mEdtOld, mEdtNew, mEdtConf;
	Button mBtnSave, mBtnCancel;
	
	int mRequest, mMemberId;
	String mPassword;
	
	public static ChangePasswordDialog newInstance(int memberId, String password, int request, Fragment target) {
		ChangePasswordDialog dialog = new ChangePasswordDialog();
		Bundle args = new Bundle();
		args.putInt(ARG_MEMBERID, memberId);
		args.putString(ARG_PASSWORD, password);
		dialog.setArguments(args);
		dialog.setTargetFragment(target, request);
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.changepassword);
		mRequest = getTargetRequestCode();
		mMemberId = getArguments().getInt(ARG_MEMBERID);
		mPassword = getArguments().getString(ARG_PASSWORD);
		View view = inflater.inflate(R.layout.dialog_changepassword, container, false);
		
		mEdtOld = (EditText) view.findViewById(R.id.dialog_changepass_edtOldPass);
		mEdtNew = (EditText) view.findViewById(R.id.dialog_changepass_edtNewPass);
		mEdtConf = (EditText) view.findViewById(R.id.dialog_changepass_edtConfirmPass);
		mBtnSave = (Button) view.findViewById(R.id.dialog_updateshipping_btnSave);
		mBtnCancel = (Button) view.findViewById(R.id.dialog_updateshipping_btnCancel);

		mBtnSave.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_updateshipping_btnSave:
			if (validate()) {
				mBtnSave.setEnabled(false);
				mBtnCancel.setEnabled(false);
				final String newPass = Utilities.getMD5(mEdtNew.getText().toString().trim() + "orchipro");
				mActivity.getServices().changePassword(mMemberId, newPass, new Callback<Integer>() {
					@Override
					public void success(Integer arg0, Response arg1) {
						if (arg0 > 0) {
							Toast.makeText(mActivity, R.string.changepasssuccess, Toast.LENGTH_SHORT).show();
							dismiss();
						} else {
							Toast.makeText(mActivity, R.string.changepassfailed, Toast.LENGTH_SHORT).show();
							mBtnSave.setEnabled(true);
							mBtnCancel.setEnabled(true);
						}
					}
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						mBtnSave.setEnabled(true);
						mBtnCancel.setEnabled(true);
					}
				});
			}
			break;
		case R.id.dialog_updateshipping_btnCancel:
			dismiss();
			break;
		default:
			break;
		}
	}
	
	private boolean validate() {
		if (mEdtOld.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.passblank, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtNew.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.passblank, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtConf.getText().toString().trim().length() == 0) {
			Toast.makeText(mActivity, R.string.passblank, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtOld.getText().toString().trim().length() < 5) {
			Toast.makeText(mActivity, R.string.passwordlengthgreaterthanfour, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtNew.getText().toString().trim().length() < 5) {
			Toast.makeText(mActivity, R.string.passwordlengthgreaterthanfour, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEdtConf.getText().toString().trim().length() < 5) {
			Toast.makeText(mActivity, R.string.passwordlengthgreaterthanfour, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!mPassword.equals(Utilities.getMD5(mEdtOld.getText().toString().trim() + "orchipro"))) {
			Toast.makeText(mActivity, R.string.passwrong, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!mEdtNew.getText().toString().trim().equals(mEdtConf.getText().toString().trim())) {
			Toast.makeText(mActivity, R.string.passwordconfirmdoesnotmatch, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
