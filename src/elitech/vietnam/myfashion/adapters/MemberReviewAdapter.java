/**
 * Sep 24, 2014 3:07:37 PM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.Review;
import elitech.vietnam.myfashion.widgets.rdimgview.RoundedImageView;

/**
 * @author Cong
 *
 */
public class MemberReviewAdapter extends ArrayAdapter<Review> {

	MainActivity mActivity;
	
	public MemberReviewAdapter(Context context, List<Review> objects) {
		super(context, R.layout.item_member_review, objects);
		mActivity = (MainActivity) context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_member_review, parent, false);
			holder = new ViewHolder();
			holder.mAvatar = (RoundedImageView) convertView.findViewById(R.id.item_memberreview_imgProduct);
			holder.mTxtName = (TextView) convertView.findViewById(R.id.item_memberreview_txtName);
			holder.mTxtTitle = (TextView) convertView.findViewById(R.id.item_memberreview_txtTitle);
			holder.mRating = (RatingBar) convertView.findViewById(R.id.item_memberreview_rtbRating);
			holder.mTxtTime = (TextView) convertView.findViewById(R.id.item_memberreview_txtTime);
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder) convertView.getTag();
		
		Review item = getItem(position);
		UrlImageViewHelper.setUrlDrawable(holder.mAvatar, Const.SERVER_IMAGE_THUMB_URL + item.ProductImage, R.drawable.no_avatar);
		holder.mTxtName.setText(item.ProductName);
		holder.mTxtTitle.setText(item.Title);
		holder.mRating.setRating(item.Rate);
		holder.mTxtTime.setText(item.Date);
		
		if (item.Images == null || item.Images.size() == 0) {
			item.Images = new ArrayList<>();
			item.Images.addAll(extractImage(item.Content));
		}
		
		return convertView;
	}
	
	List<String> extractImage(String content) {
		List<String> result = new ArrayList<>();
		String str = content;
		while (str.contains("src=\"")) {
			str = str.substring(str.indexOf("src=\""), str.length());
			int i = str.indexOf("\"") + 1;
			int j = str.indexOf("\"", i);
			String url = str.substring(i, j);
			result.add(url.replace(" ", "%20"));
			str = str.substring(j + 1, str.length());
		}
		return result;
	}
	
	static class ViewHolder {
		RoundedImageView mAvatar;
		TextView mTxtName, mTxtTitle, mTxtTime;
		RatingBar mRating;
	}
}
