/**
 * Sep 23, 2014 1:06:27 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class ChildBaseFragment extends Fragment {

	public ChildBaseFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_base, container, false);
	}
	

	public void replaceFragment(Fragment fragment, int containerId, boolean addToBackStack) {
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		if (addToBackStack) {
			transaction.addToBackStack(fragment.getClass().getName());
		}
		transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		transaction.replace(containerId, fragment, fragment.getClass().getName());
		transaction.commit();
	}

	public boolean popFragment() {
		boolean isPop = false;
		if (getChildFragmentManager().getBackStackEntryCount() > 0) {
			isPop = true;
			getChildFragmentManager().popBackStack();
		}
		return isPop;
	}
	
	public boolean popAllFragment() {
		if (getChildFragmentManager().getBackStackEntryCount() > 0) {
			return getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		return false;
	}
}
