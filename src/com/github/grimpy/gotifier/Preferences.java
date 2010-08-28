package com.github.grimpy.gotifier;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class Preferences extends PreferenceActivity {
	private static final String TAG = "Gotifier";
	private SharedPreferences prefs;
	private ActivityManager activityManager;

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		Log.i(TAG, "Resotring instance");
		super.onRestoreInstanceState(state);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		this.activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		bindButtons();

	}

	private void bindButtons() {
		CheckBoxPreference pref = (CheckBoxPreference) findPreference("checkboxEnabled");
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference p, Object arg1) {
				CheckBoxPreference mypref = (CheckBoxPreference) p;
				if (!mypref.isChecked()) {
					Preferences.this.startService(new Intent(Preferences.this.getBaseContext(), PhoneEventMngr.class));
				} else {
					Preferences.this.stopService(new Intent(Preferences.this.getBaseContext(), PhoneEventMngr.class));
				}
				return true;
			}
		});
		setServiceStatus(pref);
		String[] settings = { "Red", "Blue", "Green", "Boot" };
		for (String string : settings) {
			pref = (CheckBoxPreference) findPreference("checkbox" + string);
			pref.setChecked(prefs.getBoolean(string, false));
			pref.setOnPreferenceChangeListener(new CheckBoxPreferenceChangeListener(string, prefs));
		}
	}
	
	private void setServiceStatus(CheckBoxPreference pref){
		boolean isrunning = false;
		for (RunningServiceInfo servinfo : activityManager.getRunningServices(100)) {
			if (servinfo.service.getClassName().equals("com.github.grimpy.gotifier.PhoneEventMngr")) {
				Log.i(TAG, "found service running");
				isrunning = true;
				break;
			}
		}
		if(!isrunning){
			Log.i(TAG, "Service not running");
		}
		pref.setChecked(isrunning);
	}


	@Override
	protected void onRestart() {
		Log.i(TAG, "Restarted again");
		CheckBoxPreference pref = (CheckBoxPreference)findPreference("checkboxEnabled");
		setServiceStatus(pref);
		super.onRestart();
	}

}

class CheckBoxPreferenceChangeListener implements OnPreferenceChangeListener {
	private String key;
	private SharedPreferences sharedPref;

	public CheckBoxPreferenceChangeListener(String key, SharedPreferences sharedPref) {
		this.key = key;
		this.sharedPref = sharedPref;
	}

	@Override
	public boolean onPreferenceChange(Preference p, Object arg1) {
		CheckBoxPreference mypref = (CheckBoxPreference) p;
		Editor edit = sharedPref.edit();
		edit.putBoolean(key, !mypref.isChecked());
		edit.commit();
		return true;
	}
}