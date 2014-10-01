/**
 * May 12, 2014 9:32:05 AM
 */
package elitech.vietnam.myfashion.dialogues;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
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
public class DeleteItemCartDialog extends AbstractDialogFragment implements View.OnClickListener {
	
	public static final String ARG_ITEMNAME = "ARG_ITEMNAME";
	public static final String ARG_ITEMPOSITION = "ARG_ITEMPOSITION";
	public static final String ARG_REQUEST = "ARG_REQUEST";
	
	DeleteItemDialogClick mClick;
	
	TextView mContent;
	Button mBtnYes, mBtnNo;
	
	int mRequest, mPosition;
	String mItemName;
	
	public static DeleteItemCartDialog newInstance(String itemName, int itemPosition, int request, Fragment target) {
		DeleteItemCartDialog dialog = new DeleteItemCartDialog();
		Bundle args = new Bundle();
		args.putString(ARG_ITEMNAME, itemName);
		args.putInt(ARG_ITEMPOSITION, itemPosition);
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
			mClick = (DeleteItemDialogClick) getTargetFragment();
		} else {
			mRequest = getArguments().getInt(ARG_REQUEST);
			mClick = (DeleteItemDialogClick) getActivity();
		}
		mItemName = getArguments().getString(ARG_ITEMNAME);
		mPosition = getArguments().getInt(ARG_ITEMPOSITION);
		if (mClick == null || mRequest <= 0) {
			throw new IllegalArgumentException("Must set call back and request code");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.removeitemtittle);
		View view = inflater.inflate(R.layout.dialog_confirm, container, false);
		
		mContent = (TextView) view.findViewById(R.id.confirmdialog_textMessage);
		mBtnYes = (Button) view.findViewById(R.id.confirmdialog_btnYes);
		mBtnNo = (Button) view.findViewById(R.id.confirmdialog_btnNo);
		
		mBtnYes.setOnClickListener(this);
		mBtnNo.setOnClickListener(this);
		
		mContent.setText(Html.fromHtml(String.format(getString(R.string.itemwilberemoved), mItemName)));
		return view;
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
	
	public interface DeleteItemDialogClick {
		public void yesClick(int requestCode, int position);
		public void noClick(int requestCode, int position);
	}
}
