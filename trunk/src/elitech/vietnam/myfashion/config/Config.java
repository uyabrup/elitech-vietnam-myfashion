/**
 * Apr 25, 2014 2:55:39 PM
 */
package elitech.vietnam.myfashion.config;

import java.io.File;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * @author Cong
 * 
 */
public class Config {

	private Activity	mContext;

	private int				mWidth, mHeight;
	private String			mScreenType;
	private String			mVersionName	= "";
	private int				mVersionCode	= 0;
	private String			mImageTemp;
	private File 			mFile;
	
	public Config(Activity context) {
		mContext = context;

		/**
		 * Get version info
		 */
		try {
			PackageInfo mPInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			mVersionName = mPInfo.versionName;
			mVersionCode = mPInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		/**
		 * Get screen size
		 */
		DisplayMetrics metrics = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mWidth = metrics.widthPixels;
		mHeight = metrics.heightPixels;
		Log.w("Screen Size", mWidth + "x" + mHeight);

		/**
		 * Get screen type
		 */
		switch (mContext.getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			mScreenType = "LDPI";
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			mScreenType = "MDPI";
			break;
		case DisplayMetrics.DENSITY_HIGH:
			mScreenType = "HDPI";
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			mScreenType = "XHDPI";
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			mScreenType = "XXHDPI";
			break;
		default:
			break;
		}
		Log.w("Screen Type", mScreenType);
		
		/**
		 * Get temporary directory
		 */
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
			mFile = mContext.getExternalCacheDir();
		else
			mFile = mContext.getCacheDir();
		mFile.mkdirs();
		mImageTemp = mFile.getAbsolutePath() + File.separator + "shoppingtemp.jpg";
	}

	public int getScreenWidth() {
		return mWidth;
	}

	public int getScreenHeigh() {
		return mHeight;
	}

	public String getScreenType() {
		return mScreenType;
	}

	public String getVersionName() {
		return mVersionName;
	}

	public int getVersionCode() {
		return mVersionCode;
	}
	
	public String getImageTemp() {
		return mImageTemp;
	}
	
	public File getCacheDir() {
		return mFile;
	}
}
