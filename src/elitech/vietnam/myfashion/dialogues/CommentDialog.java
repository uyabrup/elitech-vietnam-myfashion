/**
 * Sep 17, 2014 5:24:17 PM
 */
package elitech.vietnam.myfashion.dialogues;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.CommentDialogAdapter;
import elitech.vietnam.myfashion.entities.Comment;
import elitech.vietnam.myfashion.fragments.StyleDetailFragment;

/**
 * @author Cong
 *
 */
public class CommentDialog extends DialogFragment implements View.OnClickListener, OnRefreshListener {

	private static final int LOADMORE = 20;
	
	MainActivity mActivity;
	CommentDialogCallback mCallback;
	
	SwipeRefreshLayout mRefresh;
	Button mBtnNoComment;
	ImageButton mBtnSend;
	ListView mList;
	ProgressBar mLoading;
	EditText mEdtComment;
	
	List<Comment> mComments = new ArrayList<>();
	CommentDialogAdapter mAdapter;
	int mPostId;
	
	public static CommentDialog newInstance(StyleDetailFragment fragment, int requestCode) {
		CommentDialog dialog = new CommentDialog();
		dialog.setTargetFragment(fragment, requestCode);
		return dialog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
		mCallback = (CommentDialogCallback) getTargetFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.comments);
		View view = inflater.inflate(R.layout.dialog_comment, container, false);
		
		mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.dialog_comment_layoutRefresh);
		mEdtComment = (EditText) view.findViewById(R.id.dialog_comment_edtComment);
		mBtnNoComment = (Button) view.findViewById(R.id.dialog_comment_btnFirstComment);
		mBtnSend = (ImageButton) view.findViewById(R.id.dialog_comment_send);
		mList = (ListView) view.findViewById(R.id.dialog_comment_listView);
		mLoading = (ProgressBar) view.findViewById(R.id.dialog_comment_prgLoading);
		
		mRefresh.setColorSchemeResources(R.color.swipe_1, R.color.swipe_2, R.color.swipe_3, R.color.swipe_4);
		mAdapter = new CommentDialogAdapter(mActivity, mComments);
		mList.setAdapter(mAdapter);
		
		mRefresh.setOnRefreshListener(this);
		mBtnSend.setOnClickListener(this);
		mBtnNoComment.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mPostId = mCallback.getPostId();
		getData();
	}
	
	@Deprecated
	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
	}
	
	public void show(FragmentManager manager) {
		super.show(manager, "Dialog");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_comment_btnFirstComment:
			break;
		case R.id.dialog_comment_send:
			if (mActivity.getLoggedinUser() == null) {
				Toast.makeText(mActivity, R.string.requestlogin, Toast.LENGTH_SHORT).show();
			} else if (mEdtComment.getText().toString().length() == 0) {
				Toast.makeText(mActivity, R.string.commentblank, Toast.LENGTH_SHORT).show();
			} else {
				mBtnSend.setEnabled(false);
				final int uId = mActivity.getLoggedinUser().Id;
				final String content = mEdtComment.getText().toString(),
				image = mActivity.getLoggedinUser().Image,
				name = mActivity.getLoggedinUser().Name,
				nickname = mActivity.getLoggedinUser().NickName;
				mActivity.getServices().doComment(mPostId, uId, content, 2, new Callback<Integer>() {
					@Override
					public void success(Integer arg0, Response arg1) {
						if (mComments.size() == 0) {
							mBtnNoComment.setVisibility(View.GONE);
							mList.setVisibility(View.VISIBLE);
							mLoading.setVisibility(View.GONE);
						}
						mEdtComment.setText("");
						Comment com = new Comment();
						com.Id = arg0;
						com.AccountID = uId;
						com.Content = content;
						com.Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mActivity.getCurrentLocale()).format(new Date());
						com.Image = image;
						com.Name = name;
						com.NickName = nickname;
						com.ProductID = mPostId;
						com.Type = 2;
						mComments.add(com);
						mAdapter.notifyDataSetChanged();
						// TODO: notify comment
						mBtnSend.setEnabled(true);
						mCallback.updateCommentPost(1);
					}
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						mBtnSend.setEnabled(true);
					}
				});
				InputMethodManager im = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(mEdtComment.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			break;
		default:
			break;
		}
	}
	
	public static interface CommentDialogCallback {
		int getPostId();
		void updateCommentPost(int number);
	}

	@Override
	public void onRefresh() {
		getData();
	}
	
	private void getData() {
		mActivity.getServices().getProductCommnets(mPostId, 0, LOADMORE, 2, new Callback<List<Comment>>() {
			@Override
			public void success(List<Comment> arg0, Response arg1) {
				mComments.clear();
				mComments.addAll(arg0);
				mAdapter.notifyDataSetChanged();
				mRefresh.setRefreshing(false);
				mBtnNoComment.setVisibility(arg0.size() > 0 ? View.GONE : View.VISIBLE);
				mList.setVisibility(arg0.size() > 0 ? View.VISIBLE : View.GONE);
				mLoading.setVisibility(View.GONE);
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
				mRefresh.setRefreshing(false);
			}
		});
	}
	
	private void getMoreData() {
		mActivity.getServices().getProductCommnets(mPostId, mComments.size(), LOADMORE, 2, new Callback<List<Comment>>() {
			@Override
			public void success(List<Comment> arg0, Response arg1) {
				mComments.addAll(arg0);
				mAdapter.notifyDataSetChanged();
				mRefresh.setRefreshing(false);
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
				mRefresh.setRefreshing(false);
			}
		});
	}
}
