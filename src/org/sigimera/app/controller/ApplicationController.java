package org.sigimera.app.controller;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationController {
	public static ApplicationController instance = null;
	private Context context;
	private SharedPreferences settings;
	
	private ApplicationController() {}
	
	public static ApplicationController getInstance() {
		if ( null == instance )
			instance = new ApplicationController();
		return instance;
	}
	
	public void setApplicationContext(Context _context) {
		this.context = _context;
	}
	
	public void setSharedPreferences(SharedPreferences _settings) {
		this.settings = _settings;
	}
	
	public Context getApplicationContext() { return this.context; }
	public SharedPreferences getSharedPreferences() { return this.settings; }
}