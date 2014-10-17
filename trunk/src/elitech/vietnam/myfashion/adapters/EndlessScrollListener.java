/**
 * Oct 9, 2014 1:51:12 PM
 */
package elitech.vietnam.myfashion.adapters;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author Cong
 *
 */
public abstract class EndlessScrollListener implements OnScrollListener {
	
	private boolean isLoading = false, isEnd = false;
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (getItemCount() == 0 || isEnd)
			return;
		
		int l = visibleItemCount + firstVisibleItem;
	    if (l >= totalItemCount && !isLoading) {
	        isLoading = true;
	        onLoadmore();
	    }
	}

	public abstract void onLoadmore();
	
	public abstract int getItemCount();
	
	public void setLoading(boolean loading) {
		isLoading = loading;
	}
	
	public void setEnd(boolean ending) {
		isEnd = ending;
	}
	
	public boolean isLoading() {
		return isLoading;
	}
	
	public boolean isEnd() {
		return isEnd;
	}
}
