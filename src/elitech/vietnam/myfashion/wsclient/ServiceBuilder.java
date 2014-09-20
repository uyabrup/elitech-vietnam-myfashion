/**
 * Aug 5, 2014 2:58:50 PM
 */
package elitech.vietnam.myfashion.wsclient;

import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.config.Const;

/**
 * @author Cong
 */
public class ServiceBuilder {

	MainActivity	mActivity;

	public ServiceBuilder(MainActivity activity) {
		mActivity = activity;
	}

	public Services build() {
		OkHttpClient okHttpClient = new OkHttpClient();
		try {
			okHttpClient.setCache(new Cache(mActivity.getConfig().getCacheDir(), 10 * 1024 * 1024));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new RestAdapter.Builder()
				.setEndpoint(Const.SERVICE_NAMESPACE)
				.setRequestInterceptor(new Interceptor(mActivity))
				.setLogLevel(LogLevel.FULL)
				.setClient(new OkClient(okHttpClient)).build().create(Services.class);
	}
}
