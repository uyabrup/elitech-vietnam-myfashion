/**
 * Sep 30, 2014 2:21:26 PM
 */
package elitech.vietnam.myfashion.dialogues;

import elitech.vietnam.myfashion.MainActivity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * @author Cong
 *
 */
public class AbstractDialogFragment extends DialogFragment {

	protected MainActivity mActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
	}

	@Deprecated
	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
	}
	
	public void show(FragmentManager manager) {
		super.show(manager, "Dialog");
	}
}
