/**
 * Sep 23, 2014 3:42:42 PM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.widgets.CircularImageView;

/**
 * @author Cong
 *
 */
public class MemberFollowAdapter extends ArrayAdapter<Member> {

	MainActivity mActivity;
	
	int mMemberId;
	
	public MemberFollowAdapter(Context context, List<Member> objects, int memberId) {
		super(context, R.layout.item_member_follow, objects);
		mActivity = (MainActivity) context;
		mMemberId = memberId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_member_follow, parent, false);
			
			holder = new ViewHolder(convertView.findViewById(R.id.item_memberfollow_imgAvatar), 
					convertView.findViewById(R.id.item_memberfollow_txtName), 
					convertView.findViewById(R.id.item_memberfollow_txtFollowCount), 
					convertView.findViewById(R.id.item_memberfollow_btnFollow));
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		Member item = getItem(position);
		UrlImageViewHelper.setUrlDrawable(holder.mAvatar, Const.SERVER_IMAGE_THUMB_URL + item.Image, R.drawable.no_avatar);
		holder.mName.setText(item.NickName.length() == 0 ? item.Name : item.NickName);
		holder.mCount.setText(item.FollowCount + " " + mActivity.getString(item.FollowCount > 1 ? R.string.num_followers : R.string.num_follower));
		holder.mFollow.setVisibility(mActivity.getLoggedinUser() == null || mActivity.getLoggedinUser().Id == item.Id ? View.INVISIBLE : View.VISIBLE);
		holder.mFollow.setCompoundDrawablesWithIntrinsicBounds(item.Followed > 0 ? R.drawable.ic_following : R.drawable.ic_follow, 0, 0, 0);
		
		final ViewHolder dummyHolder = holder;
		final Member dummyItem = item;
		holder.mFollow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mActivity.getLoggedinUser() == null) {
					// TODO: 
				}
				else {
					dummyHolder.mFollow.setEnabled(false);
					if (!dummyItem.Followed())
						mActivity.getServices().follow(mActivity.getLoggedinUser().Id, dummyItem.Id, new Callback<Integer>() {
							@Override
							public void success(Integer arg0, Response arg1) {
								if (arg0 > 0) {
									dummyItem.Followed = 1;
									dummyHolder.mFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_following, 0, 0, 0);
									dummyItem.FollowCount += 1;
									dummyHolder.mCount.setText(dummyItem.FollowCount + " " + mActivity.getString(dummyItem.FollowCount > 1 ? R.string.num_followers : R.string.num_follower));
								}
								dummyHolder.mFollow.setEnabled(true);
							}
							@Override
							public void failure(RetrofitError arg0) {
								Log.w("RetrofitError", arg0.getMessage());
								dummyHolder.mFollow.setEnabled(true);
							}
						});
					else 
						mActivity.getServices().unFollow(mActivity.getLoggedinUser().Id, dummyItem.Id, new Callback<Integer>() {
							@Override
							public void success(Integer arg0, Response arg1) {
								if (arg0 > 0) {
									if (mMemberId == mActivity.getLoggedinUser().Id) {
										remove(dummyItem);
										notifyDataSetChanged();
									} else {
										dummyItem.Followed = 0;
										dummyHolder.mFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow, 0, 0, 0);
										dummyItem.FollowCount -= 1;
										dummyHolder.mCount.setText(dummyItem.FollowCount + " " + mActivity.getString(dummyItem.FollowCount > 1 ? R.string.num_followers : R.string.num_follower));
									}
								}
								dummyHolder.mFollow.setEnabled(true);
							}
							@Override
							public void failure(RetrofitError arg0) {
								Log.w("RetrofitError", arg0.getMessage());
								dummyHolder.mFollow.setEnabled(true);
							}
						});
				}
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder {
		CircularImageView mAvatar;
		TextView mName, mCount, mFollow;
		
		public ViewHolder(View avatar, View name, View count, View follow) {
			mAvatar = (CircularImageView) avatar;
			mName = (TextView) name;
			mCount = (TextView) count;
			mFollow = (TextView) follow;
		}
	}
}
