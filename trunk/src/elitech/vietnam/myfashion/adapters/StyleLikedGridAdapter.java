/**
 * Sep 24, 2014 1:02:19 PM
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
import elitech.vietnam.myfashion.entities.Post;
import elitech.vietnam.myfashion.widgets.CircularImageView;
import elitech.vietnam.myfashion.widgets.ScaleImageView;

/**
 * @author Cong
 *
 */
public class StyleLikedGridAdapter extends ArrayAdapter<Post> {

	MainActivity mActivity;
	
	int mMemberId;
	
	public StyleLikedGridAdapter(Context context, List<Post> objects, int memberId) {
		super(context, R.layout.item_style, objects);
		mActivity = (MainActivity) context;
		mMemberId = memberId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_style, parent, false);
			
			holder = new ViewHolder(convertView.findViewById(R.id.item_style_imgPost),
					convertView.findViewById(R.id.item_style_imgAvatar),
					convertView.findViewById(R.id.item_style_txtName),
					convertView.findViewById(R.id.item_style_txtDate),
					convertView.findViewById(R.id.item_style_btnLike));
			
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder) convertView.getTag();
		
		Post item = getItem(position);
		UrlImageViewHelper.setUrlDrawable(holder.mAvatar, Const.SERVER_IMAGE_URL + item.Account.Image, R.drawable.no_avatar);
		UrlImageViewHelper.setUrlDrawable(holder.mImage, Const.SERVER_IMAGE_THUMB_URL + item.image_url);
		holder.mName.setText(item.Account.NickName.length() == 0 ? item.Account.Name : item.Account.NickName);
		holder.mDate.setText(item.Date);
		holder.mLike.setText(item.Likes + "");
		holder.mLike.setCompoundDrawablesWithIntrinsicBounds(0, (item.Liked > 0) ? R.drawable.ic_fav_active : R.drawable.ic_fav_inactive, 0, 0);
		
		final ViewHolder dummyHolder = holder;
		final Post dummyItem = item;
		holder.mLike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mActivity.getLoggedinUser() == null)
					return;	// TODO: 
				dummyHolder.mLike.setEnabled(false);
				int liked = dummyItem.Liked() ? -1 : 1;
				mActivity.getServices().doLikes(dummyItem.Id, mActivity.getLoggedinUser().Id, liked, 2, new Callback<Integer>() {
					@Override
					public void success(Integer arg0, Response arg1) {
						if (arg0 >= 0) {
							if (mMemberId == mActivity.getLoggedinUser().Id) {
								remove(dummyItem);
								notifyDataSetChanged();
							} else {
								dummyItem.Liked = dummyItem.Liked() ? 0 : 1;
								dummyItem.Likes = arg0;
								dummyHolder.mLike.setText(dummyItem.Likes + "");
								dummyHolder.mLike.setCompoundDrawablesWithIntrinsicBounds(0, (dummyItem.Liked > 0) ? R.drawable.ic_fav_active : R.drawable.ic_fav_inactive, 0, 0);
							}
						}
						dummyHolder.mLike.setEnabled(true);
					}
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						dummyHolder.mLike.setEnabled(true);
					}
				});
			}
		});
		
		return convertView;
	}
	
	private static class ViewHolder {

		CircularImageView mAvatar;
		TextView mName, mDate, mLike;
		ScaleImageView mImage;
		
		public ViewHolder(View image, View avatar, View name, View date, View like) {
			mImage = (ScaleImageView) image;
			mName = (TextView) name;
			mDate = (TextView) date;
			mLike = (TextView) like;
			mAvatar = (CircularImageView) avatar;
		}

	}
}
