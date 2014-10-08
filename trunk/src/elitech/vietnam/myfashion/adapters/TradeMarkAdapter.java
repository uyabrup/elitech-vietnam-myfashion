/**
 * Aug 16, 2014 8:40:28 AM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class TradeMarkAdapter extends ArrayAdapter<String> {

	MainActivity mActivity;
	
	public TradeMarkAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		mActivity = (MainActivity) context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_category, parent, false);
			holder = new ViewHolder();
			
			holder.mTxtName = (TextView) convertView;
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		String item = getItem(position);
		holder.mTxtName.setText(item);
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView mTxtName;
	}
}
