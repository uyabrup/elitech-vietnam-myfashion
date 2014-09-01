/**
 * Sep 1, 2014 5:11:22 PM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.District;

/**
 * @author Cong
 *
 */
public class DistrictSpinnerAdapter extends ArrayAdapter<District> {

	public DistrictSpinnerAdapter(Context context, List<District> objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner, parent, false);
		
		((TextView) convertView).setText(getItem(position).Name);
		return convertView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getDropDownView(position, convertView, parent);
		
		view.setText(getItem(position).Name);
		return view;
	}
}
