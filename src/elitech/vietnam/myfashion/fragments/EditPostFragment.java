/**
 * Sep 25, 2014 4:04:21 PM
 */
package elitech.vietnam.myfashion.fragments;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.Post;
import elitech.vietnam.myfashion.widgets.ScaleImageView;

/**
 * @author Cong
 */
public class EditPostFragment extends AbstractFragment {

	public static final String	ARG_IMAGE		= "ARG_IMAGE";
	public static final String	ARG_CONTENT		= "ARG_CONTENT";
	public static final String	ARG_MEMBERID	= "ARG_MEMBERID";
	public static final String	ARG_POSTID		= "ARG_POSTID";

	ScaleImageView				mImage;
	EditText					mEdtDes;
	Button						mBtnTake;
	View						mLayoutMain;

	Post mPost;

	public static EditPostFragment newInstance(int postId) {
		Bundle args = new Bundle();
		args.putInt(ARG_POSTID, postId);
		EditPostFragment fragment = new EditPostFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public EditPostFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mPost = mActivity.getController().getPost(getArguments().getInt(ARG_POSTID));
		View view = inflater.inflate(R.layout.fragment_createpost, container, false);

		mLayoutMain = view.findViewById(R.id.createpost_scrMainView);
		mBtnTake = (Button) view.findViewById(R.id.createpost_btnTakePhoto);
		mEdtDes = (EditText) view.findViewById(R.id.createpost_edtContent);
		mImage = (ScaleImageView) view.findViewById(R.id.createpost_imgPhoto);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		UrlImageViewHelper.setUrlDrawable(mImage, Const.SERVER_IMAGE_URL + mPost.image_url);
		mEdtDes.setText(mPost.Content);

		mLayoutMain.setVisibility(View.VISIBLE);
		mBtnTake.setVisibility(View.GONE);
		
		mEdtDes.requestFocus();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		for (int i = 0; i < menu.size(); i++)
			menu.getItem(i).setVisible(false);

		inflater.inflate(R.menu.create_post, menu);

		final MenuItem save = menu.findItem(R.id.action_savePost);

		View view = null;
		view = MenuItemCompat.getActionView(save).findViewById(R.id.menuitem_action_savePost);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onOptionsItemSelected(save);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_savePost:
			savePost();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void savePost() {
		final String content = mEdtDes.getText().toString().trim();
		mActivity.getServices().updateStyleContent(mPost.Id, content, new Callback<Integer>() {
			@Override
			public void success(Integer arg0, Response arg1) {
				if (arg0 > 0) {
					mPost.Content = content;
					Toast.makeText(mActivity, R.string.your_style_was_changed, Toast.LENGTH_SHORT).show();
					mActivity.onBackPressed();
				} else {
					Toast.makeText(mActivity, R.string.servererror, Toast.LENGTH_SHORT).show();
				}
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
				Toast.makeText(mActivity, R.string.servererror, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
