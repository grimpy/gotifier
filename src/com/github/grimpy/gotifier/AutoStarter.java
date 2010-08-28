package com.github.grimpy.gotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AutoStarter extends BroadcastReceiver {

	@Override
    public void onReceive(Context context, Intent intent) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		if (sharedPref.getBoolean("Boot", false)){
			context.startService(new Intent(context, PhoneEventMngr.class));
		}
	}

}
