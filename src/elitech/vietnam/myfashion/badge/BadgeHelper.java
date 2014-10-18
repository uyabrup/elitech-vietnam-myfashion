/**
 * Feb 25, 2014 - 10:08:40 AM
 */
package elitech.vietnam.myfashion.badge;

import android.content.Context;

/**
 * @author Cong Vo
 *
 */
public class BadgeHelper {
	
	public static void updateBadge(Context context, int number) {
		Samsung.updateBadge(context, number);
		Sony.updateBadge(context, number);
	}
}
