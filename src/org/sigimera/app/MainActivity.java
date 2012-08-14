package org.sigimera.app;

import org.sigimera.app.controller.SessionHandler;
import org.sigimera.app.exception.AuthenticationErrorException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MainActivity extends Activity {
	private SessionHandler session_handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if ( activeNetwork != null && activeNetwork.isConnected() ) {	
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
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG);
			toast.show();
		}
	}

	public SharedPreferences getSessionSettings() {
		String PREFS_NAME = "session_handler_preferences";
		return getSharedPreferences(PREFS_NAME, 0);
	}
}
