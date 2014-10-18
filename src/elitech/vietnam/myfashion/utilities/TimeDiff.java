/**
 * Mar 28, 2014 3:46:03 PM
 */
package elitech.vietnam.myfashion.utilities;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import android.content.Context;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 */
public class TimeDiff {

	public static String getTimeDiffString(Date date, Context context) {
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
		// context.getResources().getConfiguration().locale);
		String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };

		DateTime now = new DateTime();
		DateTime fro = new DateTime(date);

		String result = "";
		Locale locale = context.getResources().getConfiguration().locale;
		long hour = Hours.hoursBetween(fro, now).getHours();
		long minu = Minutes.minutesBetween(fro, now).getMinutes();
		long seco = Seconds.secondsBetween(fro, now).getSeconds();

		if (seco < 60)
			result = context.getString(R.string.justnow);
		else if (minu < 60) {
			if (minu > 1)
				result = minu + context.getString(R.string.minutesago);
			else
				result = minu + context.getString(R.string.minuteago);
		} else if (hour < 24) {
			if (hour > 1)
				result = hour + context.getString(R.string.hoursago);
			else
				result = hour + context.getString(R.string.hourago);
		} else if (locale.equals(Locale.KOREA))
			result = fro.getMonthOfYear() + " 월 " + fro.getDayOfMonth() + " 일";
		else if (locale.getLanguage().trim().equals("vi_vn"))
			result = "Ngày " + fro.getDayOfMonth() + " Tháng " + fro.getMonthOfYear();
		else
			result = months[fro.getMonthOfYear() - 1] + " " + fro.getDayOfMonth();

		return result;
	}
}
