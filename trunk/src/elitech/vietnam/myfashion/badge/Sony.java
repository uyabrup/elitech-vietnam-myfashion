/**
 * Feb 25, 2014 - 10:09:42 AM
 */
package elitech.vietnam.myfashion.badge;

import android.content.Context;
import android.content.Intent;

/**
 * @author Cong Vo
 *
 */
public class Sony {

	private static final String ACTION = "com.sonyericsson.home.action.UPDATE_BADGE";
	private static final String ACTIVITY = "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME";
	private static final String SHOWMESS = "com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE";
	private static final String MESSAGE = "com.sonyericsson.home.intent.extra.badge.MESSAGE";
	private static final String PACKAGE = "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME";
	
	private Context mContext;
	
	public Sony(Context context) {
		mContext = context;
	}
	
	public void updateBadge(int count) {
		Intent intent = new Intent();
		intent.setAction(ACTION);
		intent.putExtra(ACTIVITY, "elitech.vietnam.myfashion.MainActivity");
		if (count <= 0) {
			intent.putExtra(SHOWMESS, false);
			intent.putExtra(MESSAGE, "0");
		} else {
			intent.putExtra(SHOWMESS, true);
			intent.putExtra(MESSAGE, count + "");
		}
		intent.putExtra(PACKAGE, "elitech.vietnam.myfashion.MainActivity");
		mContext.sendBroadcast(intent);
	}
	
	public static void updateBadge(Context context, int number) {
		Intent intent = new Intent();
		intent.setAction(ACTION);
		intent.putExtra(ACTIVITY, "elitech.vietnam.myfashion.MainActivity");
		if (number <= 0) {
			intent.putExtra(SHOWMESS, false);
			intent.putExtra(MESSAGE, "0");
		} else {
			intent.putExtra(SHOWMESS, true);
			intent.putExtra(MESSAGE, number + "");
		}
		intent.putExtra(PACKAGE, "elitech.vietnam.myfashion.MainActivity");
		context.sendBroadcast(intent);
	}
}
