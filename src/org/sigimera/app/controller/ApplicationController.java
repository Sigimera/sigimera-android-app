package org.sigimera.app.controller;

import android.content.Context;

public class ApplicationController {
	public static ApplicationController instance = null;
	private Context context;
	
	private ApplicationController() {}
	
	public static ApplicationController getInstance() {
		if ( null == instance )
			instance = new ApplicationController();
		return instance;
	}
	
	public void setApplicationContext(Context _context) {
		this.context = _context;
	}
	
	public Context getApplicationContext() { return this.context; }
}
