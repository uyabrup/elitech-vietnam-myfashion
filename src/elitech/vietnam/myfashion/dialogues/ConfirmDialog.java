/**
 * May 12, 2014 9:32:05 AM
 */
package elitech.vietnam.myfashion.dialogues;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class ConfirmDialog extends AbstractDialogFragment implements View.OnClickListener {
	
	public static final String ARG_MESSAGE = "ARG_MESSAGE";
	public static final String ARG_TITLE = "ARG_TITLE";
	public static final String ARG_REQUEST = "ARG_REQUEST";
	
	ConfirmDialogClick mClick;
	
	TextView mContent;
	Button mBtnYes, mBtnNo;
	
	int mMessage, mTitle, mRequest;
	
	public static ConfirmDialog newInstance(int titleResId, int messageResId, int request, Fragment target) {
		ConfirmDialog dialog = new ConfirmDialog();
		Bundle args = new Bundle();
		args.putInt(ARG_MESSAGE, messageResId);
		args.putInt(ARG_TITLE, titleResId);
		args.putInt(ARG_REQUEST, request);
		dialog.setArguments(args);
		dialog.setTargetFragment(target, request);
		return dialog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getTargetFragment() != null) {
			mRequest = getTargetRequestCode();
			mClick = (ConfirmDialogClick) getTargetFragment();
		} else {
			mRequest = getArguments().getInt(ARG_REQUEST);
			mClick = (ConfirmDialogClick) getActivity();
		}
		mMessage = getArguments().getInt(ARG_MESSAGE);
		mTitle = getArguments().getInt(ARG_TITLE);
		if (mClick == null || mRequest <= 0) {
			throw new IllegalArgumentException("Must set call back and request code");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(mTitle);
		View view = inflater.inflate(R.layout.dialog_confirm, container, false);
		
		mContent = (TextView) view.findViewById(R.id.confirmdialog_textMessage);
		mBtnYes = (Button) view.findViewById(R.id.confirmdialog_btnYes);
		mBtnNo = (Button) view.findViewById(R.id.confirmdialog_btnNo);
		
		mBtnYes.setOnClickListener(this);
		mBtnNo.setOnClickListener(this);
		
		mContent.setText(mMessage);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirmdialog_btnYes:
			if (mClick != null)
				mClick.yesClick(mRequest);
			break;
		case R.id.confirmdialog_btnNo:
			if (mClick != null)
				mClick.noClick(mRequest);
			break;
		default:
			break;
		}
		dismiss();
	}
	
	public interface ConfirmDialogClick {
		public void yesClick(int requestCode);
		public void noClick(int requestCode);
	}
}
