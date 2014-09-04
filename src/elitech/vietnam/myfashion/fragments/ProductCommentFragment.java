/**
 * Aug 14, 2014 2:46:14 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.CommentAdapter;
import elitech.vietnam.myfashion.dialogues.ConfirmDialog;
import elitech.vietnam.myfashion.dialogues.ConfirmDialog.ConfirmDialogClick;
import elitech.vietnam.myfashion.entities.Comment;
import elitech.vietnam.myfashion.entities.Product;

/**
 * @author Cong
 *
 */
public class ProductCommentFragment extends AbstractFragment implements ConfirmDialogClick, OnClickListener, OnItemClickListener, OnRefreshListener {
	
	private static final int LOADMORE = 20;
	
	SwipeRefreshLayout mRefresh;
	ListView mListView;
	TextView mNodata;
	EditText mEdtComment;
	ImageButton mBtnSend;
	
	CommentAdapter mAdapter;
	List<Comment> mComments = new ArrayList<>();
	
	Product mProduct;

	public ProductCommentFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_product_comment, container, false);
		
		mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.detail_comment_layRefresh);
		mListView = (ListView) view.findViewById(R.id.detail_comment_lvComment);
		mNodata = (TextView) view.findViewById(R.id.txtNodata);
		mEdtComment = (EditText) view.findViewById(R.id.detail_comment_edtComment);
		mBtnSend = (ImageButton) view.findViewById(R.id.detail_comment_btnSend);
		
		mAdapter = new CommentAdapter(mActivity, R.layout.item_comment, mComments);
		mListView.setAdapter(mAdapter);
		
		mRefresh.setColorSchemeResources(R.color.red, R.color.blue, R.color.green, R.color.orange);
		
		mBtnSend.setOnClickListener(this);
		mRefresh.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mProduct = mActivity.getController().getProduct();
		
		mRefresh.setRefreshing(true);
		onRefresh();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}

	@Override
	public void onRefresh() {
		mComments.clear();
		getData();
	}
	
	private void getData() {
		mActivity.getServices().getProductCommnets(mProduct.Id, mComments.size(), LOADMORE, 1, new Callback<List<Comment>>() {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.detail_comment_btnSend:
			if (mActivity.getLoggedinUser() == null) {
				// TODO: 
				ConfirmDialog.newInstance(R.string.login, R.string.loginsignup, 0, this).show(getFragmentManager());
			} else if (mEdtComment.getText().toString().length() == 0) {
				Toast.makeText(mActivity, R.string.commentblank, Toast.LENGTH_SHORT).show();
			} else {
				mBtnSend.setEnabled(false);
				final int uId = mActivity.getLoggedinUser().Id;
				final String content = mEdtComment.getText().toString(),
				image = mActivity.getLoggedinUser().Image,
				name = mActivity.getLoggedinUser().Name,
				nickname = mActivity.getLoggedinUser().NickName;
				mActivity.getServices().doComment(mProduct.Id, uId, content, 1, new Callback<Integer>() {
					@Override
					public void success(Integer arg0, Response arg1) {
						mEdtComment.setText("");
						Comment com = new Comment();
						com.Id = arg0;
						com.AccountID = uId;
						com.Content = content;
						com.Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mActivity.getCurrentLocale()).format(new Date());
						com.Image = image;
						com.Name = name;
						com.NickName = nickname;
						com.ProductID = mProduct.Id;
						com.Type = 1;
						mComments.add(com);
						mAdapter.notifyDataSetChanged();
						// TODO: notify comment
						mBtnSend.setEnabled(true);
					}
					@Override
					public void failure(RetrofitError arg0) {
						Toast.makeText(mActivity, "", Toast.LENGTH_SHORT).show();
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

	@Override
	public void yesClick(int requestCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noClick(int requestCode) {
		// TODO Auto-generated method stub
		
	}
}
