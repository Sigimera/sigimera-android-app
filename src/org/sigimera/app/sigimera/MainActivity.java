package org.sigimera.app.sigimera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.sigimera.controller.CrisesController;
import org.sigimera.app.sigimera.controller.SessionHandler;
import org.sigimera.app.sigimera.exception.AuthenticationErrorException;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	private SessionHandler session_handler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	this.session_handler = SessionHandler.getInstance(getSessionSettings());
		try {
			String auth_token = this.session_handler.getAuthenticationToken();
			JSONArray crises = CrisesController.getInstance().getCrises(auth_token, 1);
			for ( int count = 0; count < crises.length(); count++ ) {
				try {
					JSONObject crisis = (JSONObject) crises.get(count);
					System.out.println("[" + count + "] " + crisis.getString("dc_title"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    		// 1. Get Crises List
    		// 1a. If crises list null set auth_token null and render R.layout.login
    		// 1b. If not null build the crises list in the activity_main
    		setContentView(R.layout.activity_main);
		} catch (AuthenticationErrorException e) {
    		setContentView(R.layout.login);
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void login(View view) {
    	EditText emailView = (EditText)findViewById(R.id.email_input_field);
    	EditText passwordView = (EditText)findViewById(R.id.password_input_field);
    	this.session_handler.login(emailView.getText().toString(), passwordView.getText().toString());
    }
    
    public SharedPreferences getSessionSettings() {
    	String PREFS_NAME = "session_handler_preferences";
    	return getSharedPreferences(PREFS_NAME, 0);
    }
}
