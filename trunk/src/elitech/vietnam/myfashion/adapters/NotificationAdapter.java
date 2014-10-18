/**
 * Oct 18, 2014 9:23:12 AM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.Notify;
import elitech.vietnam.myfashion.gcm.GCMIntentService.GCMEntity;
import elitech.vietnam.myfashion.utilities.TimeDiff;
import elitech.vietnam.myfashion.widgets.rdimgview.RoundedImageView;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Cong
 *
 */
public class NotificationAdapter extends ArrayAdapter<Notify> {

	public NotificationAdapter(Context context, List<Notify> objects) {
		super(context, R.layout.item_notification, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notification, parent, false);
			
			holder = new ViewHolder(convertView.findViewById(R.id.item_noti_imgAvatar), 
					convertView.findViewById(R.id.item_noti_txtContent), 
					convertView.findViewById(R.id.item_noti_txtTime));
			
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder) convertView.getTag();
		
		Notify item = getItem(position);
		GCMEntity e = GCMEntity.newInstance(getContext(), item);
		
		UrlImageViewHelper.setUrlDrawable(holder.mImage, Const.SERVER_IMAGE_THUMB_URL + item.Image, R.drawable.no_avatar);
		holder.mTxtTime.setText(TimeDiff.getTimeDiffString(item.Date(), getContext()));
		holder.mTxtContent.setText(Html.fromHtml(e.bigtext));
		
		if (item.Unread()) 
			convertView.setBackgroundResource(R.drawable.bg_item_noti_u);
		else
			convertView.setBackgroundResource(R.drawable.bg_item_noti_r);
		
		return convertView;
	}
	
	static class ViewHolder {
		
		public ViewHolder(View image, View content, View time) {
			mImage = (RoundedImageView) image;
			mTxtContent = (TextView) content;
			mTxtTime = (TextView) time;
		}
		
		RoundedImageView mImage;
		TextView mTxtContent, mTxtTime;
	}
}
