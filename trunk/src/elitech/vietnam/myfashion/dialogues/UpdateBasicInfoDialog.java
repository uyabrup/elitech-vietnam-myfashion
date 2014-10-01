/**
 * May 12, 2014 9:32:05 AM
 */
package elitech.vietnam.myfashion.dialogues;

import java.util.Arrays;

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
import android.widget.Spinner;
import android.widget.Toast;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.StringSpinnerAdapter;

/**
 * @author Cong
 *
 */
public class UpdateBasicInfoDialog extends AbstractDialogFragment implements View.OnClickListener {
	
	public static final String ARG_NICKNAME = "ARG_NICKNAME";
	public static final String ARG_GENDER = "ARG_GENDER";
	public static final String ARG_MEMBERID = "ARG_MEMBERID";
	
	BasicInfoDialogCallback mClick;
	
	EditText mEdtNickName;
	Spinner mSpinGender;
	StringSpinnerAdapter mAdapter;
	Button mBtnSave, mBtnCancel;
	String mNickName;
	int mRequest, mMemberId, mGender;
	
	String mGenders[];
	
	public static UpdateBasicInfoDialog newInstance(int memberId, String nickName, int gender, int request, Fragment target) {
		UpdateBasicInfoDialog dialog = new UpdateBasicInfoDialog();
		Bundle args = new Bundle();
		args.putInt(ARG_MEMBERID, memberId);
		args.putInt(ARG_GENDER, gender);
		args.putString(ARG_NICKNAME, nickName);
		dialog.setArguments(args);
		dialog.setTargetFragment(target, request);
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.status);
		mRequest = getTargetRequestCode();
		mClick = (BasicInfoDialogCallback) getTargetFragment();
		mMemberId = getArguments().getInt(ARG_MEMBERID);
		mNickName = getArguments().getString(ARG_NICKNAME);
		mGender = getArguments().getInt(ARG_GENDER);
		mGenders = new String[] {getString(R.string.gender_female), getString(R.string.gender_male), getString(R.string.gender_others)};
		View view = inflater.inflate(R.layout.dialog_updatebasicinfo, container, false);
		
		mEdtNickName = (EditText) view.findViewById(R.id.dialog_updatebasic_edtNickName);
		mSpinGender = (Spinner) view.findViewById(R.id.dialog_updatebasic_spinGender);
		mBtnSave = (Button) view.findViewById(R.id.dialog_updatebasic_btnSave);
		mBtnCancel = (Button) view.findViewById(R.id.dialog_updatebasic_btnCancel);
		mAdapter = new StringSpinnerAdapter(mActivity, Arrays.asList(mGenders));
		mSpinGender.setAdapter(mAdapter);
		
		mBtnSave.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mEdtNickName.setText(mNickName);
		mSpinGender.setSelection(mGender);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_updatebasic_btnSave:
			mBtnSave.setEnabled(false);
			mBtnCancel.setEnabled(false);
			final String nickName = mEdtNickName.getText().toString().trim();
			final int gender = mSpinGender.getSelectedItemPosition();
			mActivity.getServices().updateMemberBasicInfo(mMemberId, nickName, gender, new Callback<Integer>() {
				@Override
				public void success(Integer arg0, Response arg1) {
					if (arg0 > 0) {
						Toast.makeText(mActivity, R.string.profilesaved, Toast.LENGTH_SHORT).show();
						mClick.updateInfo(nickName, gender);
						dismiss();
					} else {
						Toast.makeText(mActivity, R.string.cantupdateprofile, Toast.LENGTH_SHORT).show();
						mBtnSave.setEnabled(true);
						mBtnCancel.setEnabled(false);
					}
				}
				@Override
				public void failure(RetrofitError arg0) {
					Log.w("RetrofitError", arg0.getMessage());
					mBtnSave.setEnabled(true);
					mBtnCancel.setEnabled(false);
				}
			});
			break;
		case R.id.dialog_updatebasic_btnCancel:
			dismiss();
			break;
		default:
			break;
		}
	}
	
	public interface BasicInfoDialogCallback {
		public void updateInfo(String nickName, int gender);
	}
}
