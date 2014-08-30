/**
 * Aug 7, 2014 2:46:46 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.ImageViewActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.ProductDetail;
import elitech.vietnam.myfashion.utilities.Utilities;
import elitech.vietnam.myfashion.widgets.rdimgview.RoundedImageView;

/**
 * @author Cong
 */
public class ProductDetailFragment extends AbstractFragment implements View.OnClickListener {

	RoundedImageView			mImage;
	TextView					mTxtName, mTxtPrice, mTxtSalePrice, mTxtBrand;
	Button						mBtnLike, mBtnCart;
	WebView						mWebDetail;

	ProductDetailCallback		mCallback;
	Product						mProduct;

	private int					fingerState			= FINGER_RELEASED;
	private final static int	FINGER_RELEASED		= 0;
	private final static int	FINGER_TOUCHED		= 1;
	private final static int	FINGER_DRAGGING		= 2;
	private final static int	FINGER_UNDEFINED	= 3;

	public ProductDetailFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
		// Get product from activity
		mCallback = mActivity.getController();
		mProduct = mCallback.getProduct();

		mImage = (RoundedImageView) view.findViewById(R.id.detail_imgProduct);
		mTxtName = (TextView) view.findViewById(R.id.detail_txtName);
		mTxtPrice = (TextView) view.findViewById(R.id.detail_txtPrice);
		mTxtSalePrice = (TextView) view.findViewById(R.id.detail_txtSalePrice);
		mTxtBrand = (TextView) view.findViewById(R.id.detail_txtBrand);
		mWebDetail = (WebView) view.findViewById(R.id.detail_webDetails);
		mBtnCart = (Button) view.findViewById(R.id.btnCart);
		mBtnLike = (Button) view.findViewById(R.id.btnLike);

		mWebDetail.getSettings().setUseWideViewPort(true);
		mWebDetail.getSettings().setLoadWithOverviewMode(true);
		mWebDetail.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		mBtnLike.setOnClickListener(this);
		mBtnCart.setOnClickListener(this);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		/*
		 * Get list details if not exists
		 */
		if (mProduct.DetailList.size() > 0)
			loadListDetail();
		else
			mActivity.getServices().getProductDetails(mProduct.Id, new Callback<List<ProductDetail>>() {
				@Override
				public void success(List<ProductDetail> arg0, Response arg1) {
					mProduct.DetailList.addAll(arg0);
					loadListDetail();
				}

				@Override
				public void failure(RetrofitError arg0) {
					Log.w("RetrofitError", arg0.getMessage());
				}
			});

		/*
		 * Load product data to fragment
		 */
		UrlImageViewHelper.setUrlDrawable(mImage, Const.SERVER_IMAGE_THUMB_URL + mProduct.Image);
		mTxtName.setText(mProduct.Name);
		mTxtPrice.setText(Utilities.numberFormat(mProduct.PriceVN) + Const.CURRENCY_VN);
		mTxtSalePrice.setText(Utilities.numberFormat(mProduct.PriceSale) + Const.CURRENCY_VN);
		if (mProduct.SaleOff > 0)
			mTxtPrice.setPaintFlags(mTxtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		else
			mTxtSalePrice.setVisibility(View.INVISIBLE);
		mTxtBrand.setText(mProduct.Brand);

		mBtnLike.setText(mProduct.Likes + "");
		mBtnLike.setCompoundDrawablesWithIntrinsicBounds(mProduct.Liked > 0 ? R.drawable.ic_fav_active
				: R.drawable.ic_fav_inactive, 0, 0, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLike:
			if (mActivity.getLoggedinUser() == null) {
				// TODO: 
			}
			else {
				mBtnLike.setEnabled(false);
				int liked = mProduct.Liked() ? -1 : 1;
				mActivity.getServices().doLikes(mProduct.Id, mActivity.getLoggedinUser().Id, liked, 1,
					new Callback<Integer>() {
						@Override
						public void success(Integer arg0, Response arg1) {
							if (arg0 >= 0) {
								mProduct.Liked = mProduct.Liked() ? 0 : 1;
								mProduct.Likes = arg0;
								mBtnLike.setText(mProduct.Likes + "");
								mBtnLike.setCompoundDrawablesWithIntrinsicBounds(
										mProduct.Liked() ? R.drawable.ic_fav_active : R.drawable.ic_fav_inactive, 0, 0,
										0);
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
		case R.id.btnCart:
			break;

		default:
			break;
		}
	}

	/**
	 * Method load data to webview
	 */
	private void loadListDetail() {
		if (mProduct.DetailList.size() <= 0)
			return;

		String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta name='viewport' content='width=device-width user-scalable=no'></head><body>";
		for (ProductDetail detail : mProduct.DetailList) {
			String str = Const.SERVER_IMAGE_URL + detail.Image;
			if (str.indexOf("post") != -1)
				str = str.replace("app/", "");
			if (str.indexOf("account") != -1)
				str = str.replace("app/", "");
			html += "<img src=\"" + str.replace("/app", "") + "\" width=\"99%\" />";
		}
		html += "</body></html>";
		mWebDetail.loadDataWithBaseURL(Const.SERVER_IMAGE_URL, html, "text/html", "utf-8", null);

		mWebDetail.setOnTouchListener(new WebTouchListener(html));
	}

	/**
	 * This class implement OnTouchListener for detail image webview
	 * 
	 * @author Cong
	 */
	class WebTouchListener implements View.OnTouchListener {
		String	mData;

		public WebTouchListener(String data) {
			mData = data;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (fingerState == FINGER_RELEASED)
					fingerState = FINGER_TOUCHED;
				else
					fingerState = FINGER_UNDEFINED;
				break;
			case MotionEvent.ACTION_UP:
				if (fingerState != FINGER_DRAGGING) {
					fingerState = FINGER_RELEASED;
					Intent i = new Intent(mActivity, ImageViewActivity.class);
					i.putExtra("Data", mData.replace(" user-scalable=no", "").replace(" width=\"99%\"", ""));
					mActivity.startActivity(i);
				} else if (fingerState == FINGER_DRAGGING)
					fingerState = FINGER_RELEASED;
				else
					fingerState = FINGER_UNDEFINED;
				break;
			case MotionEvent.ACTION_MOVE:
				if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING)
					fingerState = FINGER_DRAGGING;
				else
					fingerState = FINGER_UNDEFINED;
				break;
			default:
				fingerState = FINGER_UNDEFINED;
			}
			return false;
		}

	}
	
	public static interface ProductDetailCallback {
		Product getProduct();
		void setProduct(Product product);
	}
	//TODO: click thumb zoom image
}
