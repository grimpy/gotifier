package com.github.grimpy.gotifier;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneEventMngr extends Service{
	private static String TAG = "Gotifier";
	
	public boolean register(){
		TelephonyManager phnMngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		PhoneListener lst = new PhoneListener(this);
		phnMngr.listen(lst, PhoneStateListener.LISTEN_CALL_STATE);
		return true;
	}
	
    @Override
    public void onCreate() {
    	Log.i(TAG, "Service got created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        register();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
