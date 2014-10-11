/**
 * Sep 17, 2014 1:32:22 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.entities.Post;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.Review;
import elitech.vietnam.myfashion.fragments.MemberFollowFragment.FollowType;
import elitech.vietnam.myfashion.fragments.MemberStyleFragment.StyleType;
import elitech.vietnam.myfashion.widgets.CircularImageView;

/**
 * @author Cong
 *
 */
public class MemberInfoFragment extends AbstractFragment implements View.OnClickListener {

	public static final String ARG_MEMBERID = "ARG_MEMBERID";
	public static final int MAXITEMS = 7;
	
	int mMemberId = 0;
	Member mMember;
	
	List<Post> mPosts = new ArrayList<>();
	List<Product> mProducts = new ArrayList<>();
	List<Post> mSuggests = new ArrayList<>();
	List<Review> mReviews = new ArrayList<>();
	
	LinearLayout mLayStyle, mLayFavorite, mLaySuggest, mLayReview;
	TextView mTxtStyle, mTxtFavorite, mTxtSuggest, mTxtReview, mName, mSlogan, mAddress;
	Button mBtnFollower, mBtnFollowing, mBtnPurchased;
	ImageButton mBtnFollow, mBtnEdit;
	CircularImageView mAvatar;
	
	
	public static MemberInfoFragment newInstance(int member) {
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_MEMBERID, member);
		MemberInfoFragment fragment = new MemberInfoFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public MemberInfoFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_member_info, container, false);
		
		mLayStyle = (LinearLayout) view.findViewById(R.id.memInfo_layoutStyleContent);
		mLayFavorite = (LinearLayout) view.findViewById(R.id.memInfo_layoutFavoriteContent);
		mLayReview = (LinearLayout) view.findViewById(R.id.memInfo_layoutReviewContent);
		mLaySuggest = (LinearLayout) view.findViewById(R.id.memInfo_layoutSuggestContent);
		mTxtStyle = (TextView) view.findViewById(R.id.memInfo_txtStyleNoData);
		mTxtFavorite = (TextView) view.findViewById(R.id.memInfo_txtFavoriteNoData);
		mTxtReview = (TextView) view.findViewById(R.id.memInfo_txtReviewNoData);
		mTxtSuggest = (TextView) view.findViewById(R.id.memInfo_txtSuggestNoData);
		mAvatar = (CircularImageView) view.findViewById(R.id.memInfo_imgAvatar);
		mName = (TextView) view.findViewById(R.id.memInfo_txtName);
		mSlogan = (TextView) view.findViewById(R.id.memInfo_txtStatus);
		mAddress = (TextView) view.findViewById(R.id.memInfo_txtAddress);
		mBtnFollower = (Button) view.findViewById(R.id.memInfo_btnFollower);
		mBtnFollowing = (Button) view.findViewById(R.id.memInfo_btnFollowing);
		mBtnFollow = (ImageButton) view.findViewById(R.id.memInfo_btnFollow);
		mBtnEdit = (ImageButton) view.findViewById(R.id.memInfo_btnEdit);
		mBtnPurchased = (Button) view.findViewById(R.id.memInfo_btnPurchaseHistory);
		
		mBtnFollower.setOnClickListener(this);
		mBtnFollowing.setOnClickListener(this);
		mLayStyle.setOnClickListener(this);
		mLayFavorite.setOnClickListener(this);
		mLayReview.setOnClickListener(this);
		mLaySuggest.setOnClickListener(this);
		mBtnFollow.setOnClickListener(this);
		mBtnEdit.setOnClickListener(this);
		mBtnPurchased.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMemberId = getArguments().getInt(ARG_MEMBERID, 0);
		final int n = mActivity.getLoggedinUser() == null ? -1 : mActivity.getLoggedinUser().Id;
		mBtnFollow.setEnabled(false);
		mBtnFollow.setVisibility(n == -1 || mMemberId == n ? View.GONE : View.VISIBLE);
		mBtnEdit.setVisibility(n == mMemberId ? View.VISIBLE : View.GONE);
		mBtnPurchased.setVisibility(n == mMemberId ? View.VISIBLE : View.GONE);
		mActivity.getServices().getMemberById(mMemberId, n, new Callback<Member>() {
			@Override
			public void success(Member arg0, Response arg1) {
				mMember = arg0;
				
				UrlImageViewHelper.setUrlDrawable(mAvatar, Const.SERVER_IMAGE_URL + mMember.Image, R.drawable.no_avatar);
				mName.setText(mMember.NickName.length() == 0 ? mMember.Name : mMember.NickName);
				mActivity.getActionBar().setTitle(mMember.NickName.length() == 0 ? mMember.Name : mMember.NickName);
				
				if (mMember.Profile.length() > 0)
					mSlogan.setText(mMember.Profile);
				else
					mSlogan.setVisibility(View.GONE);
				
				if (mMember.District > 0 && mMember.City > 0)
					mAddress.setText(String.format("%s, %s", mActivity.getDatabase().getDistrictById(mMember.District).Name, mActivity.getDatabase().getCityById(mMember.City).Name));
				else
					mAddress.setVisibility(View.GONE);
				
				mBtnFollower.setText(mMember.FollowCount + "\n" + getString(mMember.FollowCount > 1 ? R.string.num_followers : R.string.num_follower));
				mBtnFollowing.setText(mMember.FollowingCount + "\n" + getString(mMember.FollowingCount > 1 ? R.string.num_followings : R.string.num_following));
				mBtnFollow.setEnabled(true);
				if (mMemberId != n)
					mBtnFollow.setImageResource(mMember.Followed() ? R.drawable.ic_following : R.drawable.ic_follow);
				else
					mBtnFollow.setVisibility(View.GONE);
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
			}
		});
		
		mActivity.getServices().getStyleById(mMemberId, n, 0, MAXITEMS, new Callback<List<Post>>() {
			@Override
			public void success(List<Post> arg0, Response arg1) {
				if (arg0.size() > 0) {
					mTxtStyle.setVisibility(View.GONE);
					mPosts.clear();
					mPosts.addAll(arg0);
					for (Post item : mPosts) {
						View view = LayoutInflater.from(mActivity).inflate(R.layout.item_member_detail, mLayStyle, false);
						UrlImageViewHelper.setUrlDrawable((ImageView) view, Const.SERVER_IMAGE_THUMB_URL + item.image_url);
						mLayStyle.addView(view);
					}
				}
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
			}
		});
		
		mActivity.getServices().getLikedProduct(mMemberId, n, 0, MAXITEMS, new Callback<List<Product>>() {
			@Override
			public void success(List<Product> arg0, Response arg1) {
				if (arg0.size() > 0) {
					mTxtFavorite.setVisibility(View.GONE);
					mProducts.clear();
					mProducts.addAll(arg0);
					for (Product item : mProducts) {
						View view = LayoutInflater.from(mActivity).inflate(R.layout.item_member_detail, mLayFavorite, false);
						UrlImageViewHelper.setUrlDrawable((ImageView) view, Const.SERVER_IMAGE_THUMB_URL + item.Image);
						mLayFavorite.addView(view);
					}
				}
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
			}
		});
		
		mActivity.getServices().getLikedStyle(mMemberId, n, 0, MAXITEMS, new Callback<List<Post>>() {
			@Override
			public void success(List<Post> arg0, Response arg1) {
				if (arg0.size() > 0) {
					mTxtSuggest.setVisibility(View.GONE);
					mSuggests.clear();
					mSuggests.addAll(arg0);
					for (Post item : mSuggests) {
						View view = LayoutInflater.from(mActivity).inflate(R.layout.item_member_detail, mLaySuggest, false);
						UrlImageViewHelper.setUrlDrawable((ImageView) view, Const.SERVER_IMAGE_THUMB_URL + item.image_url);
						mLaySuggest.addView(view);
					}
				}
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
			}
		});
		
		mActivity.getServices().getReviewById(mMemberId, 0, MAXITEMS, new Callback<List<Review>>() {
			@Override
			public void success(List<Review> arg0, Response arg1) {
				if (arg0.size() > 0) {
					mTxtReview.setVisibility(View.GONE);
					mReviews.clear();
					mReviews.addAll(arg0);
					for (Review item : mReviews) {
						View view = LayoutInflater.from(mActivity).inflate(R.layout.item_member_detail, mLayReview, false);
						UrlImageViewHelper.setUrlDrawable((ImageView) view, Const.SERVER_IMAGE_THUMB_URL + item.ProductImage);
						mLayReview.addView(view);
					}
				}
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.memInfo_btnFollower:
			if (mMember.FollowCount > 0)
				mActivity.getCurrentBase().replaceFragment(MemberFollowFragment.newInstance(mMemberId, FollowType.FOLLOWER), true);
			break;
		case R.id.memInfo_btnFollowing:
			if (mMember.FollowingCount > 0)
				mActivity.getCurrentBase().replaceFragment(MemberFollowFragment.newInstance(mMemberId, FollowType.FOLLOWING), true);
			break;
		case R.id.memInfo_layoutStyleContent:
			if (mPosts.size() > 0)
				mActivity.getCurrentBase().replaceFragment(MemberStyleFragment.newInstance(mMemberId, StyleType.STYLE), true);
			break;
		case R.id.memInfo_layoutFavoriteContent:
			if (mProducts.size() > 0)
				mActivity.getCurrentBase().replaceFragment(MemberFavoriteFragment.newInstance(mMemberId), true);
			break;
		case R.id.memInfo_layoutSuggestContent:
			if (mSuggests.size() > 0)
				mActivity.getCurrentBase().replaceFragment(MemberStyleFragment.newInstance(mMemberId, StyleType.SUGGEST), true);
			break;
		case R.id.memInfo_layoutReviewContent:
			if (mReviews.size() > 0)
				mActivity.getCurrentBase().replaceFragment(MemberReviewFragment.newInstance(mMemberId), true);
			break;
		case R.id.memInfo_btnEdit:
			mActivity.getCurrentBase().replaceFragment(AccountInfoFragment.newInstance(), true);
			break;
		case R.id.memInfo_btnPurchaseHistory:
			
			break;
		case R.id.memInfo_btnFollow:
			if (mActivity.getLoggedinUser() == null) {
				// TODO: 
			}
			else {
				mBtnFollow.setEnabled(false);
				if (!mMember.Followed())
					mActivity.getServices().follow(mActivity.getLoggedinUser().Id, mMember.Id, new Callback<Integer>() {
						@Override
						public void success(Integer arg0, Response arg1) {
							if (arg0 > 0) {
								mMember.Followed = 1;
								mBtnFollow.setImageResource(R.drawable.ic_following);
								mMember.FollowCount += 1;
								mBtnFollower.setText(mMember.FollowCount + "\n" + getString(mMember.FollowCount > 1 ? R.string.num_followers : R.string.num_follower));
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
					mActivity.getServices().unFollow(mActivity.getLoggedinUser().Id, mMember.Id, new Callback<Integer>() {
						@Override
						public void success(Integer arg0, Response arg1) {
							if (arg0 > 0) {
								mMember.Followed = 0;
								mBtnFollow.setImageResource(R.drawable.ic_follow);
								mMember.FollowCount -= 1;
								mBtnFollower.setText(mMember.FollowCount + "\n" + getString(mMember.FollowCount > 1 ? R.string.num_followers : R.string.num_follower));
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
		default:
			break;
		}
	}
}
