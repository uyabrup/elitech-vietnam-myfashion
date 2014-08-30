/**
 * Aug 26, 2014 2:41:14 PM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.List;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.Size;
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
public class SizeSpinnerAdapter extends ArrayAdapter<Size> {

	public SizeSpinnerAdapter(Context context, List<Size> objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner, parent, false);
		
		((TextView) convertView).setText(getItem(position).Size);
		return convertView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getDropDownView(position, convertView, parent);
		
		view.setText(getItem(position).Size);
		return view;
	}
}
