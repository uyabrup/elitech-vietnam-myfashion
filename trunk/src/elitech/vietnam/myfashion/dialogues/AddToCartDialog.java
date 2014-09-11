/**
 * May 12, 2014 9:32:05 AM
 */
package elitech.vietnam.myfashion.dialogues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.ColorSpinnerAdapter;
import elitech.vietnam.myfashion.adapters.QuantitySpinnerAdapter;
import elitech.vietnam.myfashion.adapters.SizeSpinnerAdapter;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.Color;
import elitech.vietnam.myfashion.entities.Product;
import elitech.vietnam.myfashion.entities.Size;
import elitech.vietnam.myfashion.utilities.Utilities;

/**
 * @author Cong
 *
 */
public class AddToCartDialog extends DialogFragment implements View.OnClickListener {
	
	private static final int TASKS = 2;
	
	public static final String ARG_REQUEST = "ARG_REQUEST";
	public static final String ARG_POSITION = "ARG_POSITION";
	
	final Integer[] QUANTITY = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
	private Object lock = new Object();
	
	MainActivity mActivity;
	AddToCartCallBack mClick;
	
	List<Size> mSizes = new ArrayList<>();
	List<Color> mColors = new ArrayList<>();
	
	ImageView mImgProduct;
	TextView mPrice, mSale, mName;
	Spinner mQuantity, mSize, mColor;
	LinearLayout mLSize, mLColor, mLayoutMain;
	ProgressBar mLoading;
	Button mBtnYes, mBtnNo;
	
	QuantitySpinnerAdapter mQAdapter;
	ColorSpinnerAdapter mCAdapter;
	SizeSpinnerAdapter mSAdapter;
	
	int mRequest, mPosition, mTask = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
		mRequest = getTargetRequestCode();
		mClick = (AddToCartCallBack) getTargetFragment();
		mPosition = getArguments().getInt(ARG_POSITION);
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().getAttributes().windowAnimations = R.style.SlideBottom;
		getDialog().setTitle(R.string.addtocart);
		View view = inflater.inflate(R.layout.dialog_addtocart, container, false);
		
		mLayoutMain = (LinearLayout) view.findViewById(R.id.atc_layoutMain);
		mLoading = (ProgressBar) view.findViewById(R.id.prgLoading);
		mImgProduct = (ImageView) view.findViewById(R.id.atc_imgProduct);
		mLColor = (LinearLayout) view.findViewById(R.id.atc_layoutColor);
		mLSize = (LinearLayout) view.findViewById(R.id.atc_layoutSize);
		mPrice = (TextView) view.findViewById(R.id.atc_imgProductPrice);
		mSale = (TextView) view.findViewById(R.id.atc_imgProductSale);
		mName = (TextView) view.findViewById(R.id.atc_imgProductName);
		mQuantity = (Spinner) view.findViewById(R.id.atc_spinQuantity);
		mColor = (Spinner) view.findViewById(R.id.atc_spinColor);
		mSize = (Spinner) view.findViewById(R.id.atc_spinSize);
		mBtnYes = (Button) view.findViewById(R.id.atc_btnOk);
		mBtnNo = (Button) view.findViewById(R.id.atc_btnCancel);
		
		mLayoutMain.setVisibility(View.INVISIBLE);
		mLoading.setVisibility(View.VISIBLE);

		mQAdapter = new QuantitySpinnerAdapter(getActivity(), R.layout.item_spinner, Arrays.asList(QUANTITY));
		mQuantity.setAdapter(mQAdapter);
		mSAdapter = new SizeSpinnerAdapter(mActivity, mSizes);
		mSize.setAdapter(mSAdapter);
		mCAdapter = new ColorSpinnerAdapter(mActivity, mColors);
		mColor.setAdapter(mCAdapter);
		
		mBtnYes.setOnClickListener(this);
		mBtnNo.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		final Product item = mClick.getData(mRequest, mPosition);
		
		UrlImageViewHelper.setUrlDrawable(mImgProduct, Const.SERVER_IMAGE_THUMB_URL + item.Image);
		mName.setText(item.Name);
		mPrice.setText(Utilities.numberFormat(item.PriceVN) + Const.CURRENCY_VN);
		mSale.setText(Utilities.numberFormat(item.PriceSale) + Const.CURRENCY_VN);
		if (item.SaleOff > 0) {
			mSale.setVisibility(View.VISIBLE);
			mSale.setPaintFlags(mSale.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}
		
		if (item.SizeList.size() <= 0 || item.ColorList.size() <= 0) {
			mActivity.getServices().getProductSize(item.Id, new Callback<List<Size>>() {
				@Override
				public void success(List<Size> arg0, Response arg1) {
					if (arg0.size() > 0) {
						mSizes.clear();
						mSizes.addAll(arg0);
						mSAdapter.notifyDataSetChanged();
					} else
						mLSize.setVisibility(View.GONE);
					onLoadingCompleted();
				}
				@Override
				public void failure(RetrofitError arg0) {
					Log.w("RetrofitError", arg0.getMessage());
					onLoadingCompleted();
				}
			});
			
			mActivity.getServices().getProductColor(item.Id, new Callback<List<Color>>() {
				@Override
				public void success(List<Color> arg0, Response arg1) {
					if (arg0.size() > 0) {
						mColors.clear();
						mColors.addAll(arg0);
						mCAdapter.notifyDataSetChanged();
					} else 
						mLColor.setVisibility(View.GONE);
					onLoadingCompleted();
				}
				@Override
				public void failure(RetrofitError arg0) {
					Log.w("RetrofitError", arg0.getMessage());
					onLoadingCompleted();
				}
			});
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.atc_btnOk:
			if (mClick != null) {
				Color color = mColors.size() > 0 ? mColors.get(mColor.getSelectedItemPosition()) : null;
				Size size = mSizes.size() > 0 ? mSizes.get(mColor.getSelectedItemPosition()) : null;
				mClick.yesClick(mRequest, mPosition, color, size, QUANTITY[mQuantity.getSelectedItemPosition()]);
			}
			break;
		case R.id.atc_btnCancel:
			if (mClick != null)
				mClick.noClick(mRequest, mPosition);
			break;
		default:
			break;
		}
		dismiss();
	}
	
	private void onLoadingCompleted() {
		synchronized (lock) {
			mTask += 1;
			if (mTask == TASKS) {
				mLayoutMain.setVisibility(View.VISIBLE);
				mLoading.setVisibility(View.GONE);
			}
		}
	}
	
	@Deprecated
	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
	}
	
	public void show(FragmentManager manager) {
		super.show(manager, getClass().getName());
	}
	
	public interface AddToCartCallBack {
		void yesClick(int request, int position, Color color, Size size, int quantity);
		void noClick(int request, int position);
		Product getData(int request, int position);
	}
}