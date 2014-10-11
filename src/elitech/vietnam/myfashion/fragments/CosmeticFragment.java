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
import elitech.vietnam.myfashion.adapters.CosmeticAdapter;
import elitech.vietnam.myfashion.entities.Cosmetic;

/**
 * @author Cong
 */
public class CosmeticFragment extends AbstractFragment implements OnItemClickListener {

	ListView mGrid;
	CosmeticAdapter mAdapter;
	
	List<Cosmetic> mCosmetics = new ArrayList<>();
	
	public CosmeticFragment() {
	}
	
	public static final CosmeticFragment newInstance() {
		CosmeticFragment frag = new CosmeticFragment();
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getActionBar().setTitle(R.string.title_cosmetics);
		View view = inflater.inflate(R.layout.fragment_category_list, container, false);

		mGrid = (ListView) view.findViewById(R.id.category_grvCategory);
		mAdapter = new CosmeticAdapter(mActivity, R.layout.item_category, mCosmetics);
		mGrid.setAdapter(mAdapter);
		
		mGrid.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mCosmetics.clear();
		getData();
	}
	
	private void getData() {
		mActivity.getServices().getCosmetics(new Callback<List<Cosmetic>>() {
			@Override
			public void success(List<Cosmetic> arg0, Response arg1) {
				mCosmetics.addAll(arg0);
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
		mActivity.getController().onItemClick(mCosmetics.get(position));
	}
	
	/**
	 * This interface define callback methods for communication with activity
	 * Host activity must implement this interface
	 * @author Cong
	 *
	 */
	public static interface CosmeticCallback {
		public void onItemClick(Cosmetic item);
	}
}
