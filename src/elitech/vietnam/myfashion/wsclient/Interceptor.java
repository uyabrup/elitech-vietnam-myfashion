/**
 * Aug 5, 2014 2:17:01 PM
 */
package elitech.vietnam.myfashion.wsclient;

import elitech.vietnam.myfashion.MainActivity;
import retrofit.RequestInterceptor;

/**
 * @author Cong
 *
 */
public class Interceptor implements RequestInterceptor {

	private static final String API_KEY = "89a7f77b5a13635f5d6707d694c22a71";
	private MainActivity mActivity;
	
	public Interceptor(MainActivity activity) {
		mActivity = activity;
	}
	
	@Override
	public void intercept(RequestFacade request) {
		request.addHeader("Authorization", API_KEY);
		request.addHeader("CurrentUser", (mActivity.getLoggedinUser() == null ? -1 : mActivity.getLoggedinUser().Id) + "");
	}

}
