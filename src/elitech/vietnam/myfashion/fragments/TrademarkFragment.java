/**
 * Aug 7, 2014 2:03:25 PM
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.adapters.TradeMarkAdapter;

/**
 * @author Cong
 */
public class TrademarkFragment extends AbstractFragment implements OnItemClickListener {

	ListView mGrid;
	TradeMarkAdapter mAdapter;
	
	List<String> mTrademarks = new ArrayList<>();
	
	public TrademarkFragment() {
	}
	
	public static final TrademarkFragment newInstance() {
		TrademarkFragment frag = new TrademarkFragment();
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.trademarks);
		View view = inflater.inflate(R.layout.fragment_category_list, container, false);

		mGrid = (ListView) view.findViewById(R.id.category_grvCategory);
		mAdapter = new TradeMarkAdapter(mActivity, R.layout.item_category, mTrademarks);
		mGrid.setAdapter(mAdapter);
		
		mGrid.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mTrademarks.clear();
		getData();
	}
	
	private void getData() {
		mActivity.getServices().getTradeMarks(new Callback<List<String>>() {
			@Override
			public void success(List<String> arg0, Response arg1) {
				mTrademarks.addAll(arg0);
				mAdapter.notifyDataSetChanged();
			}
			@Override
			public void failure(RetrofitError arg0) {
				Log.w("RetrofitError", arg0.getMessage());
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mActivity.getController().onItemClick(mTrademarks.get(position));
	}
	
	/**
	 * This interface define callback methods for communication with activity
	 * Host activity must implement this interface
	 * @author Cong
	 *
	 */
	public static interface TradeMarkCallback {
		public void onItemClick(String item);
	}
}
