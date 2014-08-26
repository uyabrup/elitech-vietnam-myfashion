/**
 * Aug 21, 2014 1:54:43 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class CartTabHostFragment extends AbstractFragment {

	SlidingUpPanelLayout mSliding;
	RelativeLayout mLayoutCheckout;
	ListView mListCart;
	
	String[] txt = new String[] {"Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android"};
	
	public CartTabHostFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_shoppingcart, container, false);
		
		mSliding = (SlidingUpPanelLayout) view.findViewById(R.id.cart_layoutSliding);
		mLayoutCheckout = (RelativeLayout) view.findViewById(R.id.cart_layoutPurchase);
		mListCart = (ListView) view.findViewById(R.id.cart_lvMain);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_expandable_list_item_1, txt);
		mListCart.setAdapter(adapter);
		
		return view;
	}
}
