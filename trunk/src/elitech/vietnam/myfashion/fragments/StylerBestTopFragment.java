/**
 * Sep 12, 2014 5:14:52 PM
 */
package elitech.vietnam.myfashion.fragments;

import elitech.vietnam.myfashion.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Cong
 *
 */
public class StylerBestTopFragment extends AbstractFragment {

	public StylerBestTopFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_styler_besttop, container, false);
		
		return view;
	}
}
