package com.github.grimpy.gotifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.util.Log;

public class PhoneListener extends PhoneStateListener{
	private Context con;
	private ContentResolver content;
	private String[] projection;
	private static String TAG = "Gotifier";
	private SharedPreferences pref;
	
	public PhoneListener(Context con) {
		super();
		projection = new String[] {Calls.TYPE, Calls.NUMBER};
		this.con = con;
		this.pref = PreferenceManager.getDefaultSharedPreferences(con);
		this.content = con.getContentResolver();
		Log.i("PhoneManager", "Starting listener");
	}

	public int previousState;
	
	private int getRGBColor(){
		int value = 0xff000000;
		int r = this.pref.getBoolean("Red", false) ? 1 : 0;
		int g = this.pref.getBoolean("Green", false) ? 1 : 0;
		int b = this.pref.getBoolean("Blue", false) ? 1 : 0;
		value = (r & 0x00ff0000) | (g & 0x0000ff00) | (b & 0x000000ff);
		
		return value;
	}
	
	public void onCallStateChanged(int state, String incomingNumber){
		if (previousState == TelephonyManager.CALL_STATE_RINGING && state == TelephonyManager.CALL_STATE_IDLE){
			Cursor cur = content.query(Calls.CONTENT_URI, projection, null, null, Calls.DEFAULT_SORT_ORDER);
			if (cur.moveToFirst()){
				int calltype = cur.getInt(cur.getColumnIndex(Calls.TYPE));
				String number = cur.getString(cur.getColumnIndex(Calls.NUMBER));
				Log.i(TAG, String.format("Validate if we hanged up calltype %d and nr %s", calltype, number));
				if (calltype == Calls.MISSED_TYPE){
					Log.i("PhoneManager", "We have missed call!!");
			        NotificationManager mNotificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
			        Notification notification = new Notification();
			        notification.ledARGB = getRGBColor();
			        notification.ledOnMS = 300;
			        notification.ledOffMS = 300;
			        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			        mNotificationManager.notify(1, notification);		
				}
			}
			
		}
		Log.i(TAG, String.format("State changed to %d", state));
			
		previousState = state;
	}
}
