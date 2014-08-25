/**
 * Mar 25, 2014 10:07:14 AM
 */
package elitech.vietnam.myfashion.utilities;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

/**
 * @author Cong
 *
 */
public class Tracker {

	public static String getDeviceID(Context context) {
		if (!(context instanceof Application))
			throw new IllegalArgumentException("You must pass in the Application Context");
		
		String res = null;
		WifiManager m_wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
		if (m_wm != null)
			return res = m_wm.getConnectionInfo().getMacAddress();
		
		res = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
		if (res != null && res.equals("9774d56d682e549c"))
			return res;
		
		res = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
		if (res != null && !res.equals(""))
			return res;
		
		res = UUID.randomUUID().toString();
		return getHash(res);
	}
	
	private static String getHash(String stringToHash) {
    	MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
 
    	byte[] result = null;
		try {
			result = digest.digest(stringToHash.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
 
    	StringBuilder sb = new StringBuilder();
    	for (byte b : result)
    	{
    	    sb.append(String.format("%02X", b));
    	}
 
    	String messageDigest = sb.toString();
    	return messageDigest;
    }
}
