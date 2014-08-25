/**
 * Aug 15, 2014 1:43:40 PM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.Comment;
import elitech.vietnam.myfashion.widgets.rdimgview.RoundedImageView;

/**
 * @author Cong
 *
 */
public class CommentAdapter extends ArrayAdapter<Comment> {
	
	MainActivity mActivity;

	public CommentAdapter(Context context, int resource, List<Comment> objects) {
		super(context, resource, objects);
		mActivity = (MainActivity) context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
			
			holder = new ViewHolder();
			holder.mImage = (RoundedImageView) convertView.findViewById(R.id.detail_comment_imgAvatar);
			holder.mTxtName = (TextView) convertView.findViewById(R.id.detail_comment_txtName);
			holder.mTxtContent = (TextView) convertView.findViewById(R.id.detail_comment_txtContent);
			holder.mTxtViewMore = (TextView) convertView.findViewById(R.id.detail_comment_txtViewMore);
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		Comment item = getItem(position);
		UrlImageViewHelper.setUrlDrawable(holder.mImage, Const.SERVER_IMAGE_THUMB_URL + item.Image, R.drawable.no_avatar);
		holder.mTxtName.setText((item.NickName.equals("")) ? item.Name : item.NickName);
		holder.mTxtContent.setText(Html.fromHtml(item.Content));
		
		return convertView;
	}
	
	static class ViewHolder {
		RoundedImageView mImage;
		TextView mTxtName, mTxtContent, mTxtViewMore;
	}
}
