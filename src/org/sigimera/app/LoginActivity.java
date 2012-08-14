package org.sigimera.app;

import org.sigimera.app.controller.SessionHandler;
import org.sigimera.app.exception.AuthenticationErrorException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private SessionHandler session_handler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.login);
        this.session_handler = SessionHandler.getInstance(getSessionSettings());
	}
	
	/**
     * On login Button click
     * @param view
	 * @throws AuthenticationErrorException 
     */
    public void login(View view) throws AuthenticationErrorException {
    	EditText emailView = (EditText)findViewById(R.id.email_input_field);
    	EditText passwordView = (EditText)findViewById(R.id.password_input_field);
    	if ( this.session_handler.login(emailView.getText().toString(), passwordView.getText().toString()) ) {
    		Intent listIntent = new Intent(LoginActivity.this, CrisesListActivity.class);
    		listIntent.putExtra("auth_token", this.session_handler.getAuthenticationToken());
    		this.startActivity(listIntent);
    	} else {    		
    		new Notification(getApplicationContext(), "Email or password were incorrect!", Toast.LENGTH_SHORT);
    	}
    }
    
    public SharedPreferences getSessionSettings() {
    	String PREFS_NAME = "session_handler_preferences";
    	return getSharedPreferences(PREFS_NAME, 0);
    }
}
