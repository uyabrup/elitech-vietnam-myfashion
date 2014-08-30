/**
 * Aug 27, 2014 9:06:47 AM
 */
package elitech.vietnam.myfashion.dialogues;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
public class WarningDialog extends DialogFragment implements View.OnClickListener {

	public static final String ARG_MESSAGE = "ARG_MESSAGE";
	
	TextView mTxtContent;
	Button mBtnOk;
	
	String mMessage;
	
	public static WarningDialog newInstance(String message) {
		Bundle args = new Bundle();
		args.putString(ARG_MESSAGE, message);
		WarningDialog dialog = new WarningDialog();
		dialog.setArguments(args);
		return dialog;
	}
	
	public WarningDialog() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().getAttributes().windowAnimations = R.style.SlideBottom;
		getDialog().setTitle(R.string.warning);
		View view = inflater.inflate(R.layout.dialog_warning, container, false);
		
		mTxtContent = (TextView) view.findViewById(R.id.dialog_warning_txtContent);
		mBtnOk = (Button) view.findViewById(R.id.dialog_warning_btnOk);
		
		mBtnOk.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMessage = getArguments().getString(ARG_MESSAGE);
		mTxtContent.setText(mMessage);
	}
	
	@Deprecated
	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
	}

	public void show(FragmentManager manager) {
		super.show(manager, "Dialog");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_warning_btnOk:
			dismiss();
			break;
		default:
			break;
		}
	}
}
