package elitech.vietnam.myfashion.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;

public class Utilities {

	public static int getColorId(int id) {
		int value = id % 10;
		switch (value) {
		case 0:
			return R.color.purple;
		case 1:
			return R.color.light_blue;
		case 2:
			return R.color.light_orange;
		case 3:
			return R.color.green;
		case 4:
			return R.color.red;
		case 5:
			return R.color.blue;
		case 6:
			return R.color.pink;
		case 7:
			return R.color.orange;
		case 8:
			return R.color.maroon;
		case 9:
			return R.color.darkblue;
		default:
			return 0;
		}
	}
/*
	public static int getSaleOffTagId(int value) {
		switch (value) {
		case 10:
			return R.drawable.sale_10;
		case 20:
			return R.drawable.sale_20;
		case 30:
			return R.drawable.sale_30;
		case 40:
			return R.drawable.sale_40;
		case 50:
			return R.drawable.sale_50;
		case 60:
			return R.drawable.sale_60;
		case 70:
			return R.drawable.sale_70;
		case 80:
			return R.drawable.sale_80;
		case 90:
			return R.drawable.sale_90;
		default:
			return 0;
		}
	}
*/
	public static String getMD5(String string) {
		MessageDigest md;
		StringBuffer sb = new StringBuffer();
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(string.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static boolean checkComment(String s) {
		String temp = "";
		for (int i = 0; i < s.length(); i++) {
			temp = temp + "#@2503#";
		}
		if (s.replace(" ", "#@2503#").equals(temp))
			return false;
		return true;
	}

	public static boolean checkEmailValid(String email) {

		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
				+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
				+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
				+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
		CharSequence inputStr = email;
		Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches())
			return true;
		else
			return false;
	}

	public static boolean checkValPass(String password) {
		if (password.length() < 4) {
			return false;
		}

		return true;
	}

	public static String getPath(Uri uri, Activity context) {
		if (uri == null) {
			return "";
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, projection,
				null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		return uri.getPath();
	}

	public static int getImageRotation(String path) {
		ExifInterface ei;
		try {
			ei = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_NORMAL);
		int rotate = 0;
		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotate = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotate = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotate = 270;
			break;
		// etc.
		default:
			break;
		}
		return rotate;
	}

	public static String numberFormat(double value) {
		DecimalFormat formatter = new DecimalFormat("#,###,###");
		return formatter.format(value);

	}

	public static boolean isNetworkEnabled(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null)
			return false;
		if (!i.isConnected())
			return false;
		if (!i.isAvailable())
			return false;
		return true;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static boolean createCropImage(String src, String des) {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(src);

			int rotate = getImageRotation(src);
			if (rotate != 0) {
				Matrix matrix = new Matrix();
				matrix.postRotate(rotate);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			}

			FileOutputStream out = new FileOutputStream(des);
			bitmap.compress(Bitmap.CompressFormat.JPEG, Const.IMAGE_COMPRESSION, out);
			out.close();
			bitmap.recycle();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); 
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    } 
	
	public static boolean createScaledImage(String src, String des, int desSize) {
		try {
			Options options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(src, options);
			int imageHeight = options.outHeight;
			int imageWidth = options.outWidth;

			float scale = ((float) ((imageWidth > desSize) ? desSize
					: imageWidth) / ((float) imageWidth));
			int rotate = getImageRotation(src);

			Bitmap bitmap = BitmapFactory.decodeFile(src);
			if (scale > 0) {
				imageHeight *= scale;
				imageWidth *= scale;
				bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth,
						imageHeight, false);
			}
			if (rotate != 0) {
				Matrix matrix = new Matrix();
				matrix.postRotate(rotate);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
			}

			FileOutputStream out = new FileOutputStream(des);
			bitmap.compress(Bitmap.CompressFormat.JPEG,
					Const.IMAGE_COMPRESSION, out);
			out.close();
			bitmap.recycle();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static String getRegistrationId(Context context) {
		final SharedPreferences prefs = context.getSharedPreferences(Const.MAIN_PREF, Context.MODE_PRIVATE);
		String registrationId = prefs.getString(Const.PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			Log.i("GCM", "Registration not found.");
			return "";
		}
		
		int registeredVersion = prefs.getInt(Const.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = Utilities.getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i("GCM", "App version changed.");
			return "";
		}
		return registrationId;
	}

	public static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = context.getSharedPreferences(Const.MAIN_PREF, Context.MODE_PRIVATE);
		int appVersion = Utilities.getAppVersion(context);
		Log.i("GCM", "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Const.PROPERTY_REG_ID, regId);
		editor.putInt(Const.PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	public static String downloadImage(Context context, String url, String destination) {
		Log.w("downloadImage", "Downloading Image...");
		InputStream is = null;
		OutputStream os = null;
		try {
			if (url.startsWith("http")) {
				URL mUrl = new URL(url);
				URLConnection conn = mUrl.openConnection();
				is = conn.getInputStream();
			} else
				is = context.getContentResolver().openInputStream(Uri.parse(url));
			os = new FileOutputStream(new File(destination));
			byte[] buffer = new byte[1024];
			int byteread;
			while ((byteread = is.read(buffer)) != -1) {
				os.write(buffer, 0, byteread);
			}
			is.close(); os.flush(); os.close();
			return destination;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String copyFile(String src, String des) {
		try {
			File sFile = new File(src), dFile = new File(des);
			FileInputStream inStream = new FileInputStream(sFile);
		    FileOutputStream outStream = new FileOutputStream(dFile);
		    FileChannel inChannel = inStream.getChannel();
		    FileChannel outChannel = outStream.getChannel();
		    inChannel.transferTo(0, inChannel.size(), outChannel);
		    inStream.close();
		    outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return des;
	}
	
	public static int getCategoryDrawable(int categoryId, int fashion) {
		int result = 0;
		/*
		 * Female category
		 */
		if (fashion == 0) {
			switch (categoryId) {
			default:
				break;
			}
			return result;
		}
		/*
		 * Male category
		 */
		if (fashion == 1) {
			switch (categoryId) {
			default:
				break;
			}
			return result;
		}
		/*
		 * Office category
		 */
		if (fashion == 2) {
			switch (categoryId) {
			default:
				break;
			}
			return result;
		}
		return result;
	}
}
