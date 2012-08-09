package org.sigimera.app.sigimera;

import org.sigimera.app.sigimera.controller.SessionHandler;
import org.sigimera.app.sigimera.exception.AuthenticationErrorException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
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
    		Context context = getApplicationContext();
    		CharSequence text = "Email or password were incorrect!";    		
    		int duration = Toast.LENGTH_SHORT;

    		Toast toast = Toast.makeText(context, text, duration);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    	}
    }
    
    public SharedPreferences getSessionSettings() {
    	String PREFS_NAME = "session_handler_preferences";
    	return getSharedPreferences(PREFS_NAME, 0);
    }
}
