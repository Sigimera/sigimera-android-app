package org.sigimera.app.android.backend.auth;

import org.sigimera.app.android.R;

import android.app.Activity;
import android.os.Bundle;

public class AuthenticatorActivity extends Activity {
	public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
