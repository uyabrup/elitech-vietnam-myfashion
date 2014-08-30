/**
 * Aug 26, 2014 2:23:27 PM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.List;

import elitech.vietnam.myfashion.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Cong
 *
 */
public class QuantitySpinnerAdapter extends ArrayAdapter<Integer> {

	public QuantitySpinnerAdapter(Context context, int resource, List<Integer> objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner, parent, false);
		
		((TextView) convertView).setText(getItem(position) + "");
		return convertView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getDropDownView(position, convertView, parent);
		
		view.setText(getItem(position) + "");
		return view;
	}
}
