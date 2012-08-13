package org.sigimera.app;

import org.sigimera.app.controller.SessionHandler;
import org.sigimera.app.exception.AuthenticationErrorException;
import org.sigimera.app.util.Config;

import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private SessionHandler session_handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * BEGIN: Google Cloud Messaging
		 */
		try {
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);
			final String regId = GCMRegistrar.getRegistrationId(this);
			if (regId.equals("")) {
				GCMRegistrar.register(this, Config.GCM_PROJECT_ID);
			} else {
				Log.v(Config.LOG_TAG, "Already registered");
			}
		} catch (Exception e) {
			Log.v(Config.LOG_TAG, "Device meets not the GCM requirements. Exception: " + e);
		}
		/**
		 * END: Google Cloud Messaging
		 */

		this.session_handler = SessionHandler.getInstance(getSessionSettings());
		try {
			String auth_token = this.session_handler.getAuthenticationToken();

			Intent listIntent = new Intent(MainActivity.this,
					CrisesListActivity.class);
			listIntent.putExtra("auth_token", auth_token);
			this.startActivity(listIntent);
		} catch (AuthenticationErrorException e) {
			Intent loginIntent = new Intent(MainActivity.this,
					LoginActivity.class);
			this.startActivity(loginIntent);
		} finally {
			this.finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public SharedPreferences getSessionSettings() {
		String PREFS_NAME = "session_handler_preferences";
		return getSharedPreferences(PREFS_NAME, 0);
	}
}
