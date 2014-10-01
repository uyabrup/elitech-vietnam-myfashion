/**
 * Aug 29, 2014 10:50:48 AM
 */
package elitech.vietnam.myfashion.dialogues;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class LoadingDialog extends AbstractDialogFragment {

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
}
