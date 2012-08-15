package org.sigimera.app;

import org.json.JSONArray;
import org.sigimera.app.controller.ApplicationController;
import org.sigimera.app.controller.CrisesController;
import org.sigimera.app.controller.LocationController;
import org.sigimera.app.controller.SessionHandler;
import org.sigimera.app.exception.AuthenticationErrorException;
import org.sigimera.app.model.SigimeraConstants;
import org.sigimera.app.util.Config;

import com.google.android.gcm.GCMRegistrar;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	private SessionHandler session_handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ApplicationController appController = ApplicationController.getInstance();
		appController.setApplicationContext(getApplicationContext());
		appController.setSharedPreferences(getSessionSettings());
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if ( activeNetwork != null && activeNetwork.isConnected() ) {						
			
			this.session_handler = SessionHandler.getInstance(getSessionSettings());
			try {
				String auth_token = this.session_handler.getAuthenticationToken();
	
				//TODO: extract this block in a separate method
				JSONArray nearCrises = CrisesController.getInstance().getNearCrises(auth_token, 1, LocationController.getInstance().getLastKnownLocation());
	    		if ( nearCrises != null &&  nearCrises.length() > 0 ){
	    			//TODO: show context menu with the near crises
	    			Log.i(SigimeraConstants.LOG_TAG_SIGIMERA_APP, nearCrises.toString());
	    			new Notification(getApplicationContext(), "Found " + nearCrises.length() + "crises near you.\n TODO: show near crises", Toast.LENGTH_LONG);
	    		} else {
	    			Intent listIntent = new Intent(MainActivity.this, CrisesListActivity.class);
					listIntent.putExtra("auth_token", auth_token);
					this.startActivity(listIntent);
	    		}
				/**
				 * BEGIN: Google Cloud Messaging
				 */
				if ( Config.getInstance().getGcmProjectId() != null ) {
					try {
						GCMRegistrar.checkDevice(this); GCMRegistrar.checkManifest(this);
						final String regId = GCMRegistrar.getRegistrationId(this);
						if (regId.equals("")) GCMRegistrar.register(this, Config.getInstance().getGcmProjectId());
					} catch (Exception e) {
						Log.v(SigimeraConstants.LOG_TAG_SIGIMERA_APP, "Device meets not the GCM requirements. Exception: " + e);
					}
				}
				/**
				 * END: Google Cloud Messaging
				 */
				
			} catch (AuthenticationErrorException e) {
				Intent loginIntent = new Intent(MainActivity.this,
						LoginActivity.class);
				this.startActivity(loginIntent);
			} finally {
				this.finish();
			}
		} else {
			new Notification(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG);
		}
	}

	public SharedPreferences getSessionSettings() {
		String PREFS_NAME = "session_handler_preferences";
		return getSharedPreferences(PREFS_NAME, 0);
	}
}
