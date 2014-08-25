/**
 * Aug 16, 2014 8:40:28 AM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.List;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.Category;
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
public class CategoryAdapter extends ArrayAdapter<Category> {

	MainActivity mActivity;
	int mFashion;
	
	public CategoryAdapter(Context context, int resource, List<Category> objects, int fashion) {
		super(context, resource, objects);
		mActivity = (MainActivity) context;
		mFashion = fashion;
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
		
		Category item = getItem(position);
		holder.mTxtName.setText(item.NameVN);
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView mTxtName;
	}
}
