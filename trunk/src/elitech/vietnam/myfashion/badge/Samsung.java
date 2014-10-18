/**
 * Feb 25, 2014 - 10:09:23 AM
 */
package elitech.vietnam.myfashion.badge;

import android.content.Context;
import android.util.Log;

/**
 * @author Cong Vo
 *
 */
public class Samsung {
	
	private Context mContext;
	
	public Samsung(Context context) {
		mContext = context;
		if (Badge.isBadgingSupported(mContext)) {
			Log.w("Badge", "Samsung Badge: Supported");
			Badge.deleteAllBadges(mContext);
			Badge badge = Badge.getBadge(mContext);
			if (badge == null) {
				badge = new Badge();
			    badge.mPackage = "elitech.vietnam.myfashion";
			    badge.mClass = "elitech.vietnam.myfashion.MainActivity";	// This should point to Activity declared as android.intent.action.MAIN
			    badge.mBadgeCount = 0;
			    try {
			    	badge.save(mContext);
			    } catch (Exception e) {
			    	e.printStackTrace();
			    	Log.w("Badge", "Samsung Badge: Save failed");
			    }
			}
			badge.mClass = "elitech.vietnam.myfashion.MainActivity";	// This should point to Activity declared as android.intent.action.MAIN
		    badge.update(mContext);
		} else 
			Log.w("Badge", "Samsung Badge: Unsupported");
	}

	public void updateBadge(int count) {
//		if (Badge.isBadgingSupported(mContext)) {
//			if (count != 0) {
//				Badge badge = new Badge();
//			    badge.mPackage = "elitech.vietnam.myfashion";
//			    badge.mClass = "elitech.vietnam.myfashion.SplashScreen";
//			    badge.mBadgeCount = count;
//			    badge.save(mContext);
//			} else {
//		    	Badge badge = Badge.getBadge(mContext);
//			    if (badge != null) {
//			        badge.mBadgeCount = count;
//			        badge.update(mContext);
//			    } else {
//			        // Nothing to do as this means you don't have a badge record with the BadgeProvider
//			        // Thus you shouldn't even have a badge count on your icon
//			    }
//			}
//		}
		if (Badge.isBadgingSupported(mContext)) {
	    	Badge badge = Badge.getBadge(mContext);
		    if (badge != null) {
		    	Log.w("Samung Update Badge", count + "");
		        badge.mBadgeCount = count;
		        badge.update(mContext);
		    } else {
		        // Nothing to do as this means you don't have a badge record with the BadgeProvider
		        // Thus you shouldn't even have a badge count on your icon
		    }
	    } else {
	    	
	    }
	}
	
	public static void increaseBadge(Context context, int amount) {
		if (Badge.isBadgingSupported(context)) {
			Log.w("Badge", "Samsung Badge: Supported");
			Badge.deleteAllBadges(context);
			Badge badge = Badge.getBadge(context);
			if (badge == null) {
				badge = new Badge();
			    badge.mPackage = "elitech.vietnam.myfashion";
			    badge.mClass = "elitech.vietnam.myfashion.MainActivity";	// This should point to Activity declared as android.intent.action.MAIN
			    badge.mBadgeCount = amount;
			    try {
			    	badge.save(context);
			    } catch (Exception e) {
			    	e.printStackTrace();
			    	Log.w("Badge", "Samsung Badge: Save failed");
			    }
			} else {
				badge.mClass = "elitech.vietnam.myfashion.MainActivity";	// This should point to Activity declared as android.intent.action.MAIN
				badge.mBadgeCount += amount;
				badge.update(context);
			}
		} else 
			Log.w("Badge", "Samsung Badge: Unsupported");
	}
	
	public static void updateBadge(Context context, int amount) {
		if (Badge.isBadgingSupported(context)) {
			Log.w("Badge", "Samsung Badge: Supported");
			Badge.deleteAllBadges(context);
			Badge badge = Badge.getBadge(context);
			if (badge == null) {
				badge = new Badge();
			    badge.mPackage = "elitech.vietnam.myfashion";
			    badge.mClass = "elitech.vietnam.myfashion.MainActivity";	// This should point to Activity declared as android.intent.action.MAIN
			    badge.mBadgeCount = (amount < 0) ? 0 : amount;
			    try {
			    	badge.save(context);
			    } catch (Exception e) {
			    	e.printStackTrace();
			    	Log.w("Badge", "Samsung Badge: Save failed");
			    }
			} else {
				badge.mClass = "elitech.vietnam.myfashion.MainActivity";	// This should point to Activity declared as android.intent.action.MAIN
				badge.mBadgeCount = amount;
				badge.update(context);
			}
		} else 
			Log.w("Badge", "Samsung Badge: Unsupported");
	}
}
