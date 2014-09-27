/**
 * Jul 31, 2014 4:39:51 PM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.dialogues.AddToCartDialog;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class ProductLikedGridAdapter extends ArrayAdapter<Product> {
	
	public static final int REQ_ADDTOCART = 100;
	
	MainActivity mActivity;
	Fragment mFragment;
	int mImageHeight;
	int mMemberId;

	public ProductLikedGridAdapter(Context context, int resource, List<Product> objects, Fragment fragment, int memberId) {
		super(context, resource, objects);
		mActivity = (MainActivity) context;
		mFragment = fragment;
		mImageHeight = mActivity.getConfig().getScreenHeigh() / 5 * 4 / mActivity.getResources().getInteger(R.integer.grid_columns);
		mMemberId = memberId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bestofday, parent, false);
			
			holder = new ViewHolder();
			holder.mLaySaleOff = (RelativeLayout) convertView.findViewById(R.id.itembestofday_layoutSale);
			holder.mTxtSale = (TextView) convertView.findViewById(R.id.itembestofday_txtSale);
			holder.mImage = (ImageView) convertView.findViewById(R.id.itembestofday_image);
			holder.mTxtName = (TextView) convertView.findViewById(R.id.itembestofday_tvName);
			holder.mTxtPrice = (TextView) convertView.findViewById(R.id.itembestofday_tvPrice);
			holder.mBtnLikes = (Button) convertView.findViewById(R.id.btnLike);
			holder.mBtnCart = (Button) convertView.findViewById(R.id.btnCart);
			
			holder.mImage.getLayoutParams().height = mImageHeight;
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		Product item = getItem(position);
		UrlImageViewHelper.setUrlDrawable(holder.mImage, Const.SERVER_IMAGE_URL + item.Image);
		holder.mTxtName.setText(item.Name);
		holder.mBtnLikes.setText(item.Likes + "");
		holder.mTxtPrice.setText(Utilities.numberFormat((item.SaleOff != 0) ? item.PriceSale : item.PriceVN) + Const.CURRENCY_VN);
		holder.mBtnLikes.setCompoundDrawablesWithIntrinsicBounds((item.Liked > 0) ? R.drawable.ic_fav_active : R.drawable.ic_fav_inactive, 0, 0, 0);
		holder.mLaySaleOff.setVisibility((item.SaleOff > 0) ? View.VISIBLE : View.GONE);
		holder.mTxtSale.setText("-" + item.SaleOff + "%");
		
		final ViewHolder dummyHolder = holder;
		final Product dummyItem = item;
		holder.mBtnLikes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mActivity.getLoggedinUser() == null)
					return;	// TODO: 
				dummyHolder.mBtnLikes.setEnabled(false);
				int liked = dummyItem.Liked() ? -1 : 1;
				mActivity.getServices().doLikes(dummyItem.Id, mActivity.getLoggedinUser().Id, liked, 1, new Callback<Integer>() {
					@Override
					public void success(Integer arg0, Response arg1) {
						if (arg0 >= 0) {
							if (mMemberId == mActivity.getLoggedinUser().Id) {
								remove(dummyItem);
								notifyDataSetChanged();
							} else {
								dummyItem.Liked = dummyItem.Liked() ? 0 : 1;
								dummyItem.Likes = arg0;
								dummyHolder.mBtnLikes.setText(dummyItem.Likes + "");
								dummyHolder.mBtnLikes.setCompoundDrawablesWithIntrinsicBounds(dummyItem.Liked() ? R.drawable.ic_fav_active : R.drawable.ic_fav_inactive, 0, 0, 0);
							}
						}
						dummyHolder.mBtnLikes.setEnabled(true);
					}
					@Override
					public void failure(RetrofitError arg0) {
						Log.w("RetrofitError", arg0.getMessage());
						dummyHolder.mBtnLikes.setEnabled(true);
					}
				});
			}
		});
		
		final int dummyPos = position;
		holder.mBtnCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putInt(AddToCartDialog.ARG_POSITION, dummyPos);
				AddToCartDialog dialog = new AddToCartDialog();
				dialog.setArguments(args);
				dialog.setTargetFragment(mFragment, REQ_ADDTOCART);
				dialog.show(mFragment.getFragmentManager());
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder {
		RelativeLayout mLaySaleOff;
		ImageView mImage;
		TextView mTxtName, mTxtPrice, mTxtSalePrice, mTxtSale;
		Button mBtnLikes, mBtnCart;
	}
}
