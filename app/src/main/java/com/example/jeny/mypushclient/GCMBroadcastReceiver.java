package com.example.jeny.mypushclient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.net.URLDecoder;

/**
 * 푸시 메시지를 받는 Receiver 정의
 * 
 * @author Mike
 *
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
	private static final String TAG = "GCMBroadcastReceiver";
	public static final int NOTIFICATION_ID = 1;
	
	@Override
	public void onReceive(Context context, Intent intent) {		//상대방이 메시지 보낼때  intent의 부가적인 정보로 사용
		String action = intent.getAction();
		Log.d(TAG, "action : " + action);
		
		if (action != null) {
			if (action.equals("com.google.android.c2dm.intent.RECEIVE")) { // 푸시 메시지 수신 시
				String from = intent.getStringExtra("from");
				String command = intent.getStringExtra("command");		// 서버에서 보낸 command 라는 키의 value 값
				String type = intent.getStringExtra("type");		// 서버에서 보낸 type 라는 키의 value 값
				String rawData = intent.getStringExtra("data");		// 서버에서 보낸 data 라는 키의 value 값
				String data = "";
				try {
					data = URLDecoder.decode(rawData, "UTF-8");
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
				Log.v(TAG, "from : " + from + ", command : " + command + ", type : " + type + ", data : " + data);
				
				// 액티비티로 전달
				sendToActivity(context, from, command, type, data);
				
			} else {
				Log.d(TAG, "Unknown action : " + action);
			}
		} else {
			Log.d(TAG, "action is null.");
		}
		
	}

	/**
	 * 메인 액티비티로 수신된 푸시 메시지의 데이터 전달
	 * 
	 * @param context
	 * @param command
	 * @param type
	 * @param data
	 */
	private void sendToActivity(Context context, String from, String command, String type, String data) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra("from", from);
		intent.putExtra("command", command);
		intent.putExtra("type", type);
		intent.putExtra("data", data);
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		//context.startActivity(intent);

		Toast.makeText(context, data, Toast.LENGTH_LONG).show();

		sendNotification(context, intent);

	}


	/**
	 * Send a sample notification using the NotificationCompat API.
	 */
	public void sendNotification(Context context, Intent intent) {

		// BEGIN_INCLUDE(build_action)
		/** Create an intent that will be fired when the user clicks the notification.
		 * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
		 * notification service can fire it on our behalf.
		 */
		//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
		//PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		// END_INCLUDE(build_action)

		// BEGIN_INCLUDE (build_notification)
		/**
		 * Use NotificationCompat.Builder to set up our notification.
		 */
		//NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		/** Set the icon that will appear in the notification bar. This icon also appears
		 * in the lower right hand corner of the notification itself.
		 *
		 * Important note: although you can use any drawable as the small icon, Android
		 * design guidelines state that the icon should be simple and monochrome. Full-color
		 * bitmaps or busy images don't render well on smaller screens and can end up
		 * confusing the user.
		 */
		builder.setSmallIcon(R.drawable.ic_stat_notification);

		// Set the intent that will fire when the user taps the notification.
		builder.setContentIntent(pendingIntent);

		// Set the notification to auto-cancel. This means that the notification will disappear
		// after the user taps it, rather than remaining until it's explicitly dismissed.
		builder.setAutoCancel(true);

		/**
		 *Build the notification's appearance.
		 * Set the large icon, which appears on the left of the notification. In this
		 * sample we'll set the large icon to be the same as our app icon. The app icon is a
		 * reasonable default if you don't have anything more compelling to use as an icon.
		 */
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.ic_launcher));

		/**
		 * Set the text of the notification. This sample sets the three most commononly used
		 * text areas:
		 * 1. The content title, which appears in large type at the top of the notification
		 * 2. The content text, which appears in smaller text below the title
		 * 3. The subtext, which appears under the text on newer devices. Devices running
		 *    versions of Android prior to 4.2 will ignore this field, so don't use it for
		 *    anything vital!
		 */
		builder.setContentTitle("BetheRacer");
		builder.setContentText("Time to Drive");
		builder.setSubText("Tap to Race");

		// END_INCLUDE (build_notification)

		// BEGIN_INCLUDE(send_notification)
		/**
		 * Send the notification. This will immediately display the notification icon in the
		 * notification bar.
		 */
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, builder.build());
		// END_INCLUDE(send_notification)
	}

}
