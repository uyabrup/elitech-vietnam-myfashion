/**
 * May 12, 2014 9:32:05 AM
 */
package elitech.vietnam.myfashion.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class AddToCartDialog extends DialogFragment implements View.OnClickListener {
	
	public static final String ARG_MESSAGE = "ARG_MESSAGE";
	public static final String ARG_TITLE = "ARG_TITLE";
	public static final String ARG_REQUEST = "ARG_REQUEST";
	public static final String ARG_POSITION = "ARG_POSITION";
	
	AddToCartCallBack mClick;
	
	TextView mContent;
	Button mBtnYes, mBtnNo;
	
	String mMessage, mTitle;
	int mRequest, mPosition;
	
	public static AddToCartDialog newInstance() {
		return null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getTargetFragment() != null) {
			mRequest = getTargetRequestCode();
			mClick = (AddToCartCallBack) getTargetFragment();
		} else {
			mRequest = getArguments().getInt(ARG_REQUEST);
			mClick = (AddToCartCallBack) getActivity();
		}
		mMessage = getArguments().getString(ARG_MESSAGE);
		mTitle = getArguments().getString(ARG_TITLE);
		mPosition = getArguments().getInt(ARG_POSITION);
		if (mClick == null || mRequest < 0) {
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
		return new AlertDialog.Builder(getActivity())
		.setTitle("Add to cart")
		.setMessage("Lorem ispu dollo reman nesta")
		.setPositiveButton(R.string.addtocart, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mClick != null)
					mClick.yesClick(mRequest, mPosition);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mClick != null)
					mClick.noClick(mRequest, mPosition);
			}
		}).create();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirmdialog_btnYes:
			if (mClick != null)
				mClick.yesClick(mRequest, mPosition);
			break;
		case R.id.confirmdialog_btnNo:
			if (mClick != null)
				mClick.noClick(mRequest, mPosition);
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
		super.show(manager, getClass().getName());
	}
	
	public interface AddToCartCallBack {
		public void yesClick(int request, int position);
		public void noClick(int request, int position);
	}
}
