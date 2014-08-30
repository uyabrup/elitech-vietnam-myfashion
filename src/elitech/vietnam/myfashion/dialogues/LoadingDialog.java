/**
 * Aug 29, 2014 10:50:48 AM
 */
package elitech.vietnam.myfashion.dialogues;

import elitech.vietnam.myfashion.R;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * @author Cong
 *
 */
public class LoadingDialog extends DialogFragment {

	public static final String ARG_TITLE = "ARG_TITLE";
	public static final String ARG_MESSAGE = "ARG_MESSAGE";
	
	public static LoadingDialog newInstance(String title, String message) {
		Bundle args = new Bundle();
		args.putString(ARG_TITLE, title);
		args.putString(ARG_MESSAGE, message);
		LoadingDialog dialog = new LoadingDialog();
		dialog.setArguments(args);
		return dialog;
	}
	
	public LoadingDialog() {
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.getWindow().getAttributes().windowAnimations = R.style.SlideBottom;
		
		dialog.setTitle(getArguments().getString(ARG_TITLE));
		dialog.setMessage(getArguments().getString(ARG_MESSAGE));
		dialog.setCancelable(false);
		
		return dialog;
	}
	
	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, "Dialog");
	}
}
