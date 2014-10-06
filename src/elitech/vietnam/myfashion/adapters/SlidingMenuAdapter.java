/**
 * Jul 28, 2014 3:28:26 PM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.HashMap;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.entities.TradeMark;

/**
 * @author Cong
 */
public class SlidingMenuAdapter extends BaseExpandableListAdapter {

	MainActivity					mActivity;

	List<TradeMark>					mGroups;
	HashMap<Integer, List<TradeMark>>	mMap;

	public SlidingMenuAdapter(MainActivity activity, List<TradeMark> groups, HashMap<Integer, List<TradeMark>> map) {
		super();
		mActivity = activity;
		mGroups = groups;
		mMap = map;
	}

	@Override
	public int getGroupCount() {
		return mGroups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mMap.get(mGroups.get(groupPosition).Id).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mMap.get(mGroups.get(groupPosition).Id).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		TradeMark item = (TradeMark) getGroup(groupPosition);
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.sliding_menu_group, parent, false);
		}

		TextView lblListHeader = (TextView) convertView;
		lblListHeader.setText(item.Name);

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		TradeMark item = (TradeMark) getChild(groupPosition, childPosition);
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.sliding_menu_child, parent, false);
		}

		TextView lblListHeader = (TextView) convertView;
		lblListHeader.setText(item.Name);
//		int icon = mActivity.getResources().getIdentifier(item.Icon, "drawable", mActivity.getPackageName());
//		lblListHeader.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
