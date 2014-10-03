/**
 * Sep 16, 2014 3:37:06 PM
 */
package elitech.vietnam.myfashion.fragments;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.dialogues.CommentDialog;
import elitech.vietnam.myfashion.dialogues.CommentDialog.CommentDialogCallback;
import elitech.vietnam.myfashion.dialogues.ConfirmDialog;
import elitech.vietnam.myfashion.dialogues.ConfirmDialog.ConfirmDialogClick;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.Post;
import elitech.vietnam.myfashion.widgets.CircularImageView;
import elitech.vietnam.myfashion.widgets.ScaleImageView;

/**
 * @author Cong
 */
public class StyleDetailFragment extends AbstractFragment implements View.OnClickListener, CommentDialogCallback, ConfirmDialogClick {

	public static final int	REQ_DIALOG_COMMENT	= 1;

	CircularImageView		mAvatar;
	TextView				mTxtName, mTxtSlogan, mBtnFollow, mTxtDate, mTxtContent;
	LinearLayout			mBtnLike, mBtnComment, mBtnShare;
	Button					mFBtnLike, mFBtnComment;
	Button					mFBtnShare;
	ScaleImageView			mImage;

	Post					mPost;
	StyleDetailCallback		mCallback;
	StyleDetailPopupMenu	mMenu;

	public StyleDetailFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mCallback = mActivity.getController();
		mPost = mCallback.getPost();
		View view = inflater.inflate(R.layout.fragment_style_detail, container, false);
		mMenu = new StyleDetailPopupMenu(mActivity);
		mMenu.setUp();

		mAvatar = (CircularImageView) view.findViewById(R.id.styledetail_imgAvatar);
		mTxtName = (TextView) view.findViewById(R.id.styledetail_txtName);
		mTxtSlogan = (TextView) view.findViewById(R.id.styledetail_txtSlogan);
		mBtnFollow = (TextView) view.findViewById(R.id.styledetail_btnFollow);
		mTxtDate = (TextView) view.findViewById(R.id.styledetail_txtDate);
		mTxtContent = (TextView) view.findViewById(R.id.styledetail_txtContent);
		mBtnLike = (LinearLayout) view.findViewById(R.id.styledetail_btnLike);
		mBtnComment = (LinearLayout) view.findViewById(R.id.styledetail_btnComment);
		mBtnShare = (LinearLayout) view.findViewById(R.id.styledetail_btnShare);
		mFBtnLike = (Button) view.findViewById(R.id.styledetail_btnFLike);
		mFBtnComment = (Button) view.findViewById(R.id.styledetail_btnFComment);
		mFBtnShare = (Button) view.findViewById(R.id.styledetail_btnFShare);
		mImage = (ScaleImageView) view.findViewById(R.id.styledetail_imgContent);

		mAvatar.setOnClickListener(this);
		mTxtName.setOnClickListener(this);
		mTxtSlogan.setOnClickListener(this);
		mBtnFollow.setOnClickListener(this);
		mBtnLike.setOnClickListener(this);
		mBtnComment.setOnClickListener(this);
		mBtnShare.setOnClickListener(this);
		mImage.setOnClickListener(this);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		int n = mActivity.getLoggedinUser() != null ? mActivity.getLoggedinUser().Id : -1;
		mBtnFollow.setEnabled(false);
		mBtnFollow.setVisibility(n == -1 ? View.GONE : View.VISIBLE);
		if (mPost.IdAccount != n)
			mActivity.getServices().getMemberById(mPost.IdAccount, n, new Callback<Member>() {
				@Override
				public void success(Member arg0, Response arg1) {
					mPost.Account = arg0;
					mBtnFollow.setCompoundDrawablesWithIntrinsicBounds(
							mPost.Account.Followed > 0 ? R.drawable.ic_following : R.drawable.ic_follow, 0, 0, 0);
					mBtnFollow.setEnabled(true);
				}

				@Override
				public void failure(RetrofitError arg0) {
					Log.w("RetrofitError", arg0.getMessage());
					mBtnFollow.setVisibility(View.INVISIBLE);
				}
			});
		else
			mBtnFollow.setVisibility(View.INVISIBLE);

		UrlImageViewHelper.setUrlDrawable(mAvatar, Const.SERVER_IMAGE_URL + mPost.Account.Image, R.drawable.no_avatar);
		UrlImageViewHelper.setUrlDrawable(mImage, Const.SERVER_IMAGE_URL + mPost.image_url);
		mTxtName.setText(mPost.Account.NickName.length() == 0 ? mPost.Account.Name : mPost.Account.NickName);
		mTxtSlogan.setText(mPost.Account.Profile);
		mFBtnLike.setText(mPost.Likes + "");
		mFBtnLike.setCompoundDrawablesWithIntrinsicBounds(mPost.Liked() ? R.drawable.ic_fav_active
				: R.drawable.ic_fav_inactive, 0, 0, 0);
		mFBtnComment.setText(mPost.Comments + "");
		mTxtDate.setText("Posted at: " + mPost.CreatedDate);
		mTxtContent.setText(mPost.Content);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		mCallback = mActivity.getController();
		int n = mActivity.getLoggedinUser() == null ? -1 : mActivity.getLoggedinUser().Id;
		if (n == mCallback.getPost().IdAccount) {
			inflater.inflate(R.menu.style_detail, menu);
			final MenuItem item = menu.findItem(R.id.action_styleDetail_more);
			View view = MenuItemCompat.getActionView(item).findViewById(R.id.menuitem_action_more);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v.getId() == R.id.menuitem_action_more)
						mMenu.showAsDropDown(v);
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.styledetail_btnLike:
			if (mActivity.getLoggedinUser() == null) {
				// TODO:
			} else {
				mBtnLike.setEnabled(false);
				int liked = mPost.Liked() ? -1 : 1;
				mActivity.getServices().doLikes(mPost.Id, mActivity.getLoggedinUser().Id, liked, 2,
						new Callback<Integer>() {
							@Override
							public void success(Integer arg0, Response arg1) {
								if (arg0 >= 0) {
									mPost.Liked = mPost.Liked() ? 0 : 1;
									mPost.Likes = arg0;
									mFBtnLike.setText(mPost.Likes + "");
									mFBtnLike.setCompoundDrawablesWithIntrinsicBounds(
											mPost.Liked() ? R.drawable.ic_fav_active : R.drawable.ic_fav_inactive, 0,
											0, 0);
								}
								mBtnLike.setEnabled(true);
							}

							@Override
							public void failure(RetrofitError arg0) {
								Log.w("RetrofitError", arg0.getMessage());
								mBtnLike.setEnabled(true);
							}
						});
			}
			break;
		case R.id.styledetail_btnComment:
			CommentDialog.newInstance(this, REQ_DIALOG_COMMENT).show(getFragmentManager());
			break;
		case R.id.styledetail_btnShare:
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg"); // might be text, sound, whatever
			share.putExtra(Intent.EXTRA_STREAM, Const.SERVER_IMAGE_URL + mPost.image_url);
			startActivity(Intent.createChooser(share, "share"));
			break;
		case R.id.styledetail_btnFollow:
			if (mActivity.getLoggedinUser() == null) {
				// TODO:
			} else {
				mBtnFollow.setEnabled(false);
				if (mPost.Account.Followed <= 0)
					mActivity.getServices().follow(mActivity.getLoggedinUser().Id, mPost.IdAccount,
							new Callback<Integer>() {
								@Override
								public void success(Integer arg0, Response arg1) {
									if (arg0 > 0) {
										mPost.Account.Followed = 1;
										mBtnFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_following, 0,
												0, 0);
									}
									mBtnFollow.setEnabled(true);
								}
								@Override
								public void failure(RetrofitError arg0) {
									Log.w("RetrofitError", arg0.getMessage());
									mBtnFollow.setEnabled(true);
								}
							});
				else
					mActivity.getServices().unFollow(mActivity.getLoggedinUser().Id, mPost.IdAccount,
							new Callback<Integer>() {
								@Override
								public void success(Integer arg0, Response arg1) {
									if (arg0 > 0) {
										mPost.Account.Followed = 0;
										mBtnFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow, 0, 0,
												0);
									}
									mBtnFollow.setEnabled(true);
								}
								@Override
								public void failure(RetrofitError arg0) {
									Log.w("RetrofitError", arg0.getMessage());
									mBtnFollow.setEnabled(true);
								}
							});
			}
			break;
		case R.id.styledetail_imgAvatar:
		case R.id.styledetail_txtName:
		case R.id.styledetail_txtSlogan:
			mCallback.openMemberPage(mPost.IdAccount);
			break;
		case R.id.styledetail_imgContent:
			break;
		default:
			break;
		}
	}

	public static interface StyleDetailCallback {
		Post getPost();

		void setPost(Post post);

		void openMemberPage(int member);
	}

	@Override
	public int getPostId() {
		return mPost.Id;
	}

	@Override
	public void updateCommentPost(int number) {
		mPost.Comments += number;
		mFBtnComment.setText(mPost.Comments + "");
	}

	class StyleDetailPopupMenu extends PopupWindow implements View.OnClickListener {

		Button	mBtnEdit, mBtnDelete;

		public StyleDetailPopupMenu(Context context) {
			super(context);
		}

		void setUp() {
			View view = LayoutInflater.from(mActivity)
					.inflate(R.layout.dialog_popup_styledetail, (ViewGroup) getView());
			setContentView(view);
			setWidth(mActivity.getConfig().getScreenWidth() / 3);
			setHeight(mActivity.getConfig().getScreenWidth() / 3);
			setFocusable(true);
			setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			mBtnEdit = (Button) view.findViewById(R.id.dialog_popupStyleDetail_btnEdit);
			mBtnDelete = (Button) view.findViewById(R.id.dialog_popupStyleDetail_btnDelete);
			mBtnEdit.setOnClickListener(this);
			mBtnDelete.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			dismiss();
			switch (v.getId()) {
			case R.id.dialog_popupStyleDetail_btnEdit:
				mActivity.getCurrentBase().replaceFragment(
						EditPostFragment.newInstance(), true);
				break;
			case R.id.dialog_popupStyleDetail_btnDelete:
				ConfirmDialog.newInstance(R.string.warning, R.string.areyousure, Const.REQUEST_CONFIRM_DELETEPOST,
						StyleDetailFragment.this).show(getFragmentManager());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void yesClick(int requestCode) {
		mActivity.getServices().deleteStyle(mPost.Id, new Callback<Integer>() {
			@Override
			public void success(Integer arg0, Response arg1) {
				if (arg0 > 0) {
					Toast.makeText(mActivity, R.string.deletepostsuccess, Toast.LENGTH_SHORT).show();
					mActivity.onBackPressed();
				} else {
					Toast.makeText(mActivity, R.string.deletepostfailed, Toast.LENGTH_SHORT).show();
				}
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
				Toast.makeText(mActivity, R.string.deletepostfailed, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void noClick(int requestCode) {
	}

}
