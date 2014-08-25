/**
 * Aug 15, 2014 3:19:07 PM
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
import elitech.vietnam.myfashion.widgets.CircularImageView;

/**
 * @author Cong
 *
 */
public class ReviewAdapter extends ArrayAdapter<Review> {

	MainActivity mActivity;

	public ReviewAdapter(Context context, int resource, List<Review> objects) {
		super(context, resource, objects);
		mActivity = (MainActivity) context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_review, parent, false);
			holder = new ViewHolder();
			holder.mAvatar = (CircularImageView) convertView.findViewById(R.id.detail_review_imgAvatar);
			holder.mTxtName = (TextView) convertView.findViewById(R.id.detail_review_txtName);
			holder.mTxtTitle = (TextView) convertView.findViewById(R.id.detail_review_txtTitle);
			holder.mRating = (RatingBar) convertView.findViewById(R.id.detail_review_rtbRating);
			holder.mTxtTime = (TextView) convertView.findViewById(R.id.detail_review_txtTime);
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder) convertView.getTag();
		
		Review item = getItem(position);
		UrlImageViewHelper.setUrlDrawable(holder.mAvatar, Const.SERVER_IMAGE_THUMB_URL + item.MemImage, R.drawable.no_avatar);
		String title = "%1$s: %2$s";
		holder.mTxtName.setText(String.format(title, (item.MemNickName.length()==0) ? item.MemName : item.MemNickName, item.ProductName));
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
		CircularImageView mAvatar;
		TextView mTxtName, mTxtTitle, mTxtTime;
		RatingBar mRating;
	}
}
