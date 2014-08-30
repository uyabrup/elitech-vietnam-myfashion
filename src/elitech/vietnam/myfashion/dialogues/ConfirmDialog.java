/**
 * May 12, 2014 9:32:05 AM
 */
package elitech.vietnam.myfashion.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class ConfirmDialog extends DialogFragment implements View.OnClickListener {
	
	public static final String ARG_MESSAGE = "ARG_MESSAGE";
	public static final String ARG_TITLE = "ARG_TITLE";
	public static final String ARG_REQUEST = "ARG_REQUEST";
	
	ConfirmDialogClick mClick;
	
	TextView mContent;
	Button mBtnYes, mBtnNo;
	
	String mMessage, mTitle;
	int mRequest;
	
	public static Builder createBuilder() {
		return new Builder();
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
		mMessage = getArguments().getString(ARG_MESSAGE);
		mTitle = getArguments().getString(ARG_TITLE);
		if (mClick == null || mRequest <= 0) {
			throw new IllegalArgumentException("Must set call back and request code");
		}
	}
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		getDialog().setTitle(mTitle);
//		View view = inflater.inflate(R.layout.dialog_confirm, container, false);
//		
//		mContent = (TextView) view.findViewById(R.id.confirmdialog_textMessage);
//		mBtnYes = (Button) view.findViewById(R.id.confirmdialog_btnYes);
//		mBtnNo = (Button) view.findViewById(R.id.confirmdialog_btnNo);
//		
//		mBtnYes.setOnClickListener(this);
//		mBtnNo.setOnClickListener(this);
//		
//		mContent.setText(mMessage);
//		return view;
//	}
//	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity(), R.style.Theme_Base_AppCompat_Dialog_Light_FixedSize)
		.setTitle(mTitle)
		.setMessage(mMessage)
		.setPositiveButton(R.string.addtocart, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mClick != null)
					mClick.yesClick(mRequest);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mClick != null)
					mClick.noClick(mRequest);
			}
		}).create();
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
	
	@Deprecated
	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
	}
	
	public void show(FragmentManager manager) {
		super.show(manager, "Dialog");
	}
	
	public interface ConfirmDialogClick {
		public void yesClick(int requestCode);
		public void noClick(int requestCode);
	}
	
	public static class Builder {
		String mMessage, mTitle;
		ConfirmDialogClick mCallBack;
		int mRequest;
		
		private Builder() {}
		
		public Builder setMessage(String message) {
			mMessage = message;
			return this;
		}
		
		public Builder setTitle(String title) {
			mTitle = title;
			return this;
		}
		
		public Builder setRequestCode(int requestCode) {
			mRequest = requestCode;
			return this;
		}
		
		public Builder setCallBack(ConfirmDialogClick callBack) {
			mCallBack = callBack;
			return this;
		}
		
		public ConfirmDialog build() {
			ConfirmDialog fragment = new ConfirmDialog();
			Bundle bundle = new Bundle();
			bundle.putString(ARG_MESSAGE, mMessage);
			bundle.putString(ARG_TITLE, mTitle);
			bundle.putInt(ARG_REQUEST, mRequest);
			if (mCallBack instanceof Fragment)
				fragment.setTargetFragment((Fragment) mCallBack, mRequest);
			fragment.setArguments(bundle);
			return fragment;
		}
	}
}
