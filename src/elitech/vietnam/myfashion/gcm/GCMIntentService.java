package elitech.vietnam.myfashion.gcm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.entities.Notify;

public class GCMIntentService extends IntentService {
	public static int			NUMBER			= 0;

	public static final String	DATA_TAG		= "NOTIDATATAG";
	public static final int		NOTIFICATION_ID	= 1;
	private NotificationManager	mNotificationManager;
	NotificationCompat.Builder	builder;

	private File				cacheDir;

	public GCMIntentService() {
		super("GcmIntentService");
		// cacheDir = getCacheDir();
	}

	public static final String	TAG	= "GCM Demo";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				// sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				// sendNotification("Deleted messages on server: " +
				// extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				// This loop represents the service doing some work.
				Log.i(TAG, "Received: " + extras.toString());
				SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				boolean isNotiOn = mPrefs.getBoolean("GetNotification", true);
				boolean isNotiComent = mPrefs.getBoolean("GetNotiComent", true);
				boolean isNotiReply = mPrefs.getBoolean("GetNotiReply", true);
				boolean isNotiPost = mPrefs.getBoolean("GetNotiPost", true);
				boolean isNotiFollow = mPrefs.getBoolean("GetNotiFollow", true);
				boolean isLoggIn = mPrefs.getBoolean("LoggedIn", false);
				if (isNotiOn && isLoggIn) {
					String data = extras.getString("message");
					if (data != null && !data.equals("")) {
						Notify noti = new Gson().fromJson(data, Notify.class);
						noti.isNew = true;
						if ((noti.Type == 1 && isNotiComent) || (noti.Type == 2 && isNotiReply)
								|| (noti.Type == 3 && isNotiPost) || (noti.Type == 4 && isNotiFollow) || noti.Type == 5) {
							Log.i(TAG, "Received1: " + extras.toString());
							sendNotification(data, noti);
						}
					}
				}
				// Log.i(TAG, "Received: " + extras.toString());
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String data, Notify noti) {
		cacheDir = getCacheDir();
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		GCMEntity e = GCMEntity.newInstance(this, noti);

		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean isNotiSound = mPrefs.getBoolean("GetNotiSound", true);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(e.smallIcon)
				.setContentTitle(Html.fromHtml(e.title))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(e.bigtext)))
				.setContentText(e.content);

		String url = e.largeIcon;
		Bitmap bm = null;
		bm = readCache(url.replace("/", "").replace(".", "").replace(":", ""));
		if (bm == null) {
			try {
				InputStream in = new URL(url).openStream();
				bm = BitmapFactory.decodeStream(in);
				bm = Bitmap.createScaledBitmap(bm, 120, 120 * bm.getHeight() / bm.getWidth(), false);
				writeCache(bm, url.replace("/", "").replace(".", "").replace(":", ""));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (bm == null) {
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_avatar);
			bm = Bitmap.createScaledBitmap(bm, 120, 120 * bm.getHeight() / bm.getWidth(), false);
		}

		mBuilder.setLargeIcon(bm);

		Intent resultIntent = new Intent(this, MainActivity.class);

		resultIntent.putExtra(DATA_TAG, data);

		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// // Adds the back stack for the Intent (but not the Intent itself)
		// stackBuilder.addParentStack(MainActivity.class);
		// // Adds the Intent that starts the Activity to the top of the stack
		// stackBuilder.addNextIntent(resultIntent);

		// PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		if (isNotiSound) {
			Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(alarmSound);
		}

		mBuilder.setAutoCancel(true);
		mBuilder.setNumber((NUMBER += 1));

		// ///////////////////////////////////
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

		// Sets a title for the Inbox style big view
		inboxStyle.setBigContentTitle(Html.fromHtml("<b>" + getString(R.string.app_name) + "</b>: " + NUMBER
				+ getString(R.string.newmessages)));
		// Moves events into the big view
		String str = "";
		if (noti.Type == 1)
			str = getString(R.string.commentedonyourstyle_notibar);
		else if (noti.Type == 2)
			str = String.format(getString(R.string.alsoreplyonyourcomment_notibar),
					(noti.OwnerName.equals("")) ? noti.OwnerUID : noti.OwnerName);
		else if (noti.Type == 3)
			str = String.format(getString(R.string.uploadedanewstyle_notibar),
					(noti.SenderName.equals("")) ? noti.SenderUID : noti.SenderName);
		else if (noti.Type == 4)
			str = String.format(getString(R.string.addedyouasafriend_notibar),
					(noti.SenderName.equals("")) ? noti.SenderUID : noti.SenderName);
		else if (noti.Type == 5)
			str = String.format(getString(R.string.alsocommentonproduct_notibar),
					(noti.SenderName.equals("")) ? noti.SenderUID : noti.SenderName, noti.OwnerName);
		inboxStyle.addLine(str);
		// Moves the big view style object into the notification object.
		mBuilder.setStyle(inboxStyle);
		// ////////////////////////

		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//		((MyApplication) getApplicationContext()).mBadge.increaseBadge();
//		mPrefs.edit().putInt("mBadgeCount", ((MyApplication) getApplicationContext()).mBadge.mBadgeCount).commit();
		Intent intent = new Intent();
		intent.setAction("elitech.vietnam.myfashion.mNotifyReceiver");
		intent.putExtra("DATA", new Gson().toJson(noti));
		sendBroadcast(intent);
	}

	private Bitmap readCache(String Id) {
		File f = new File(cacheDir, Id);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	private void writeCache(Bitmap photo, String Id) {
		File f = new File(cacheDir, Id);
		try {
			FileOutputStream out = new FileOutputStream(f);
			photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static class GCMEntity {
		public String	title;
		public String	bigtext;
		public String	content;
		public String	largeIcon;
		public int		smallIcon;

		public static GCMEntity newInstance(Context context, Notify noti) {
			GCMEntity rs = new GCMEntity();
//			rs.smallIcon = R.drawable.logo_64;

			if (noti.Type == 1) {
				rs.title = context.getString(R.string.commentedonyourstyle);
				rs.bigtext = rs.title;
				rs.content = "";
				rs.largeIcon = Const.SERVER_IMAGE_URL + noti.Image;
			}
			if (noti.Type == 2) {
				rs.title = String.format(context.getString(R.string.alsoreplyonyourcomment),
						(noti.OwnerName.equals("")) ? noti.OwnerUID : noti.OwnerName);
				rs.bigtext = rs.title;
				rs.content = noti.Content;
				rs.largeIcon = Const.SERVER_IMAGE_URL + noti.Image;
			}
			if (noti.Type == 3) {
				rs.title = String.format(context.getString(R.string.uploadedanewstyle),
						(noti.SenderName.equals("")) ? noti.SenderUID : noti.SenderName);
				rs.bigtext = rs.title;
				rs.content = noti.Content;
				rs.largeIcon = Const.SERVER_IMAGE_URL + noti.Image;
			}
			if (noti.Type == 4) {
				rs.title = String.format(context.getString(R.string.addedyouasafriend),
						(noti.SenderName.equals("")) ? noti.SenderUID : noti.SenderName);
				rs.bigtext = rs.title;
				rs.content = "";
				rs.largeIcon = Const.SERVER_IMAGE_URL + noti.Image;
			}
			if (noti.Type == 5) {
				rs.title = String.format(context.getString(R.string.alsocommentonproduct),
						(noti.SenderName.equals("")) ? noti.SenderUID : noti.SenderName, noti.OwnerName);
				rs.bigtext = rs.title;
				rs.content = "";
				rs.largeIcon = Const.SERVER_IMAGE_URL + noti.Image;
			}

			return rs;
		}
	}
}
