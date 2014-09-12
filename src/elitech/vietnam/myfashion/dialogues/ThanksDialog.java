/**
 * Sep 11, 2014 2:32:46 PM
 */
package elitech.vietnam.myfashion.dialogues;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.fragments.BaseFragment;

/**
 * @author Cong
 *
 */
public class ThanksDialog extends DialogFragment implements View.OnClickListener {

	Button mBtnOk;
	
	String mMessage;
	
	public static ThanksDialog newInstance() {
		Bundle args = new Bundle();
		ThanksDialog dialog = new ThanksDialog();
		dialog.setArguments(args);
		dialog.setCancelable(false);
		return dialog;
	}
	
	public ThanksDialog() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.thankyou);
		View view = inflater.inflate(R.layout.dialog_thanks, container, false);
		
		mBtnOk = (Button) view.findViewById(R.id.thank_btnOk);
		
		mBtnOk.setOnClickListener(this);
		return view;
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
		case R.id.thank_btnOk:
			dismiss();
			((MainActivity) getActivity()).changeBase(BaseFragment.TAG_BESTOFDAY, null);
			break;
		default:
			break;
		}
	}
}
