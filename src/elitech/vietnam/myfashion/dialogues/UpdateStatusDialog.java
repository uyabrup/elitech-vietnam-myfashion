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

/**
 * @author Cong
 *
 */
public class UpdateStatusDialog extends AbstractDialogFragment implements View.OnClickListener {
	
	public static final String ARG_CONTENT = "ARG_CONTENT";
	public static final String ARG_MEMBERID = "ARG_MEMBERID";
	
	StatusDialogCallback mClick;
	
	EditText mEdtStatus;
	Button mBtnSave, mBtnCancel;
	String mStatus;
	int mTitle, mRequest, mMemberId;
	
	public static UpdateStatusDialog newInstance(int memberId, String status, int request, Fragment target) {
		UpdateStatusDialog dialog = new UpdateStatusDialog();
		Bundle args = new Bundle();
		args.putString(ARG_CONTENT, status);
		args.putInt(ARG_MEMBERID, memberId);
		dialog.setArguments(args);
		dialog.setTargetFragment(target, request);
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.status);
		mRequest = getTargetRequestCode();
		mClick = (StatusDialogCallback) getTargetFragment();
		mStatus = getArguments().getString(ARG_CONTENT);
		mMemberId = getArguments().getInt(ARG_MEMBERID);
		View view = inflater.inflate(R.layout.dialog_updatestatus, container, false);
		
		mEdtStatus = (EditText) view.findViewById(R.id.statusdialog_edtStatus);
		mBtnSave = (Button) view.findViewById(R.id.statusdialog_btnSave);
		mBtnCancel = (Button) view.findViewById(R.id.statusdialog_btnCancel);
		
		mBtnSave.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mEdtStatus.setText(mStatus);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.statusdialog_btnSave:
			mBtnSave.setEnabled(false);
			mBtnCancel.setEnabled(false);
			final String status = mEdtStatus.getText().toString().trim();
			mActivity.getServices().updateMemberStatus(mMemberId, status, new Callback<Integer>() {
				@Override
				public void success(Integer arg0, Response arg1) {
					if (arg0 > 0) {
						Toast.makeText(mActivity, R.string.your_status_was_updated, Toast.LENGTH_SHORT).show();
						mClick.updateStatus(status);
						dismiss();
					} else {
						Toast.makeText(mActivity, R.string.can_not_update_status, Toast.LENGTH_SHORT).show();
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
			break;
		case R.id.statusdialog_btnCancel:
			dismiss();
			break;
		default:
			break;
		}
	}
	
	public interface StatusDialogCallback {
		public void updateStatus(String status);
	}
}
