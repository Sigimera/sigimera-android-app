package org.sigimera.app.sigimera;

import org.sigimera.app.sigimera.controller.SessionHandler;
import org.sigimera.app.sigimera.exception.AuthenticationErrorException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class MainActivity extends Activity {
	private SessionHandler session_handler;		
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        
    	this.session_handler = SessionHandler.getInstance(getSessionSettings());
		try {
			String auth_token = this.session_handler.getAuthenticationToken();			
			
			Intent listIntent = new Intent(MainActivity.this, CrisesListActivity.class);
			listIntent.putExtra("auth_token", auth_token);
			this.startActivity(listIntent);			
		} catch (AuthenticationErrorException e) {
			Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
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
