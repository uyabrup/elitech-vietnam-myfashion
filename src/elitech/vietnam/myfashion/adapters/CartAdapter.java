/**
 * Aug 27, 2014 10:57:42 AM
 */
package elitech.vietnam.myfashion.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.OrderDetail;
import elitech.vietnam.myfashion.fragments.ShoppingCartFragment;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class CartAdapter extends ArrayAdapter<OrderDetail> {
	
	ShoppingCartFragment mFragment;
	
	public CartAdapter(Context context, List<OrderDetail> objects, ShoppingCartFragment fragment) {
		super(context, R.layout.item_cartdetail, objects);
		mFragment = fragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cartdetail, parent, false);
			holder = new ViewHolder(
					convertView.findViewById(R.id.item_cartdetail_imgProduct),
					convertView.findViewById(R.id.item_cartdetail_txtName),
					convertView.findViewById(R.id.item_cartdetail_txtPrice),
					convertView.findViewById(R.id.item_cartdetail_txtSale),
					convertView.findViewById(R.id.item_cartdetail_spinColor),
					convertView.findViewById(R.id.item_cartdetail_spinSize),
					convertView.findViewById(R.id.item_cartdetail_spinQuantity),
					convertView.findViewById(R.id.item_cartdetail_btnDelete),
					convertView.findViewById(R.id.item_cartdetail_txtTotal),
					convertView.findViewById(R.id.item_cartdetail_layoutColor),
					convertView.findViewById(R.id.item_cartdetail_layoutSize));
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder) convertView.getTag();
		
		final OrderDetail item = getItem(position);
		UrlImageViewHelper.setUrlDrawable(holder.mImage, Const.SERVER_IMAGE_THUMB_URL + item.Image);
		item.Quantity = item.Quantity > holder.QUANTITIES.length ? holder.QUANTITIES.length : item.Quantity;
		holder.mQuantity.setSelection(item.Quantity - 1);
		holder.mName.setText(item.ProductName);
		if (item.SizeName == null || item.SizeName.length() == 0)
			holder.mLSize.setVisibility(View.GONE);
		else {
			holder.mSizes.clear();
			holder.mSizes.add(item.SizeName);
			holder.mSAdapter.notifyDataSetChanged();
		}
		if (item.ColorNameVN == null || item.ColorNameVN.length() == 0)
			holder.mLColor.setVisibility(View.GONE);
		else {
			holder.mColors.clear();
			holder.mColors.add(item.ColorNameVN);
			holder.mCAdapter.notifyDataSetChanged();
		}
		holder.mPrice.setText(Utilities.numberFormat(item.PriceVN) + Const.CURRENCY_VN);
		holder.mSale.setText(Utilities.numberFormat(item.SaleOff) + "");
		holder.mTotal.setText(Utilities.numberFormat(item.PriceVN * item.Quantity) + Const.CURRENCY_VN);
		
		final ViewHolder dummyHolder = holder;
		holder.mQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				int sub = (dummyHolder.QUANTITIES[position] - item.Quantity) * item.PriceVN;
				item.Quantity = dummyHolder.QUANTITIES[position];
				dummyHolder.mTotal.setText(Utilities.numberFormat(item.PriceVN * item.Quantity) + Const.CURRENCY_VN);
				mFragment.onItemQuantityChanged(sub);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder {
		final Integer[] QUANTITIES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
		List<String> mColors;
		List<String> mSizes;
		ImageView mImage;
		TextView mName, mPrice, mSale, mTotal;
		Spinner mColor, mSize, mQuantity;
		ImageButton mDelete;
		QuantitySpinnerAdapter mQAdapter;
		StringSpinnerAdapter mCAdapter, mSAdapter;
		LinearLayout mLColor, mLSize;
		
		public ViewHolder(View image, View name, View price, View sale, View color, View size, View quantity, View delete, View total, View lColor, View lSize) {
			mImage = (ImageView) image;
			mName = (TextView) name;
			mPrice = (TextView) price;
			mSale = (TextView) sale;
			mColor = (Spinner) color;
			mSize = (Spinner) size;
			mQuantity = (Spinner) quantity;
			mDelete = (ImageButton) delete;
			mTotal = (TextView) total;
			mLColor = (LinearLayout) lColor;
			mLSize = (LinearLayout) lSize;
			
			mQAdapter = new QuantitySpinnerAdapter(mImage.getContext(), R.layout.item_spinner, Arrays.asList(QUANTITIES));
			mQuantity.setAdapter(mQAdapter);
			mColors = new ArrayList<>();
			mSizes = new ArrayList<>();
			mColor.setEnabled(false);
			mSize.setEnabled(false);
			mCAdapter = new StringSpinnerAdapter(mImage.getContext(), mColors);
			mSAdapter = new StringSpinnerAdapter(mImage.getContext(), mSizes);
			mColor.setAdapter(mCAdapter);
			mSize.setAdapter(mSAdapter);
		}
	}
}
