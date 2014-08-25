/**
 * Aug 21, 2014 1:54:43 PM
 */
package elitech.vietnam.myfashion.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.widgets.QuickReturnListView;

/**
 * @author Cong
 *
 */
public class CartTabHostFragment extends AbstractFragment {

	RelativeLayout mLayoutCheckout;
	QuickReturnListView mListCart;
	
	String[] txt = new String[] {"Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android","Android"};
	
	public CartTabHostFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_shoppingcart, container, false);
		
		mLayoutCheckout = (RelativeLayout) view.findViewById(R.id.cart_layoutPurchase);
		mListCart = (QuickReturnListView) view.findViewById(R.id.cart_lvMain);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_expandable_list_item_1, txt);
		mListCart.setAdapter(adapter);
		
		mListCart.setQuickReturnView(mLayoutCheckout);
		
		return view;
	}
}
